package com.github.ryanholdren.typesafesql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLProcessor {

	public static class Builder {

		private BufferedReader reader;
		private BufferedWriter writer;
		private String className;
		private String namespace;

		public Builder setInput(Reader input) {
			this.reader = new BufferedReader(input);
			return this;
		}

		public Builder setInput(InputStream input) {
			this.reader = new BufferedReader(new InputStreamReader(input));
			return this;
		}

		public Builder setInput(Path file) throws IOException {
			this.reader = Files.newBufferedReader(file, UTF8);
			return this;
		}

		public Builder setInput(BufferedReader input) {
			this.reader = input;
			return this;
		}

		public Builder setOutput(Writer output) {
			this.writer = new BufferedWriter(output);
			return this;
		}

		public Builder setOutput(BufferedWriter output) {
			this.writer = output;
			return this;
		}

		public Builder setOutput(Path file) throws IOException {
			Files.createDirectories(file.getParent());
			this.writer = Files.newBufferedWriter(file, UTF8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			return this;
		}

		public Builder setClassName(String className) {
			this.className = className;
			return this;
		}

		public Builder setNamespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public void preprocess() throws IOException {
			new SQLProcessor(this).preprocess();
		}

	}

	public static Builder newBuilder() {
		return new Builder();
	}

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Pattern PRECEDING_WHITESPACE = Pattern.compile("^\\s*");
	private static final Pattern PARAMETER = Pattern.compile("\\{(?<direction>in|out):(?<type>ARRAY|BIGINT|BINARY|BIT|BLOB|BOOLEAN|CHAR|CLOB|DATALINK|DATE|DECIMAL|DISTINCT|DOUBLE|FLOAT|INTEGER|JAVA_OBJECT|LONGNVARCHAR|LONGVARBINARY|LONGVARCHAR|NCHAR|NCLOB|NULL|NUMERIC|NVARCHAR|OTHER|REAL|REF|ROWID|SMALLINT|SQLXML|STRUCT|TIME|TIMESTAMP|TINYINT|VARBINARY|VARCHAR):(?<name>[a-z][a-zA-Z0-9]*)\\}");

	private final BufferedReader reader;
	private final BufferedWriter writer;
	private final String className;
	private final String namespace;
	private final SQLParameters arguments = new SQLParameters();
	private final SQLParameters columns = new SQLParameters();

	public SQLProcessor(Builder builder) {
		this.namespace = builder.namespace;
		this.className = builder.className;
		this.reader = builder.reader;
		this.writer = builder.writer;
	}

	public void preprocess() throws IOException {
		try {
			writeNamespace();
			writeImports();
			writeStartOfClass();
			writeSQLConstant();
			writeResultClass();
			writeParameterInterfaces();
			writeStartPrepareClass();
			writeParameterSetters();
			writeExecuteMethod();
			writeEndPreparedClass();
			writePrepareMethod();
			writeEndOfClass();
		} finally {
			writer.flush();
		}
	}

	private void writeNamespace() throws IOException {
		writeLine("package ", namespace, ";");
		writeLine();
	}

	private final String[] IMPORTS = {
		"java.sql.*",
		"java.time.*",
		"com.github.ryanholdren.typesafesql.*"
	};

	private void writeImports() throws IOException {
		for (String classNameOfImport : IMPORTS) {
			writeLine("import ", classNameOfImport, ";");
		}
		writeLine();
	}

	private void writeStartOfClass() throws IOException {
		writeLine("public final class ", className, " {");
		writeLine();
	}

	private void writeSQLConstant() throws IOException {
		writeLine("	public static final String SQL = String.join(");
		writeLine("		System.lineSeparator(),");
		boolean first = true;
		while (true) {
			final String line = reader.readLine();
			if (line == null) {
				break;
			}
			final String precedingWhitespace = findPrecedingWhitespaceIn(line);
			final String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				continue;
			}
			final String escaped;
			if (trimmed.indexOf('\\') < 0) {
				escaped = trimmed;
			} else {
				escaped = trimmed.replace("\\", "\\\\");
			}
			if (first) {
				first = false;
			} else {
				writeLine(",");
			}
			write(precedingWhitespace);
			write("		", '"');
			final Matcher matcher = PARAMETER.matcher(escaped);
			if (matcher.find()) {
				int end = 0;
				do {
					final int start = matcher.start();
					final CharSequence before = escaped.subSequence(end, start);
					write(before);
					final String direction = matcher.group("direction");
					final SQLParameterType type = SQLParameterType.valueOf(matcher.group("type"));
					final String name = matcher.group("name");
					if (direction.equals("in")) {
						arguments.add(name, type);
						write('?');
					} else {
						if (columns.add(name, type).getPositions().size() == 1) {
							write(name);
						} else {
							throw new IllegalArgumentException("Output names must be unique!");
						}
					}
					end = matcher.end();
				} while (matcher.find());
				final CharSequence after = escaped.subSequence(end, escaped.length());
				write(after);
			} else {
				write(escaped);
			}
			write('"');
		}
		writeLine();
		writeLine("	);");
		writeLine();
	}

	private void writeResultClass() throws IOException {
		if (columns.size() < 2) {
			return;
		}
		writeLine("	public static final class Result {");
		writeLine();
		for (SQLParameter column : columns) {
			final String definition = column.getDefinition();
			writeLine("		private ", definition, ";");
		}
		writeLine();
		writeLine("		private Result(");
		final Iterator<SQLParameter> iterator = columns.iterator();
		while (true) {
			final SQLParameter column = iterator.next();
			final String definition = column.getDefinition();
			if (iterator.hasNext()) {
				writeLine("			", definition, ",");
			} else {
				writeLine("			", definition);
				break;
			}
		}
		writeLine("		) {");
		for (SQLParameter column : columns) {
			final String name = column.getNameInLowerCamelCase();
			writeLine("			this.", name, " = ", name, ";");
		}
		writeLine("		}");
		writeLine();
		for (SQLParameter column : columns) {
			final String nameInLowerCamel = column.getNameInLowerCamelCase();
			final String nameInUpperCamel = column.getNameInUpperCamelCase();
			final String type = column.getType().getNameOfJavaType();
			writeLine("		public final ", type, " get", nameInUpperCamel, "() {");
			writeLine("			return ", nameInLowerCamel, ";");
			writeLine("		}");
			writeLine();
		}
		writeLine("	}");
		writeLine();
	}

	private String getNameOfExecutorClass() {
		final int numberOfColumns = columns.size();
		switch (numberOfColumns) {
			case 0:
				return "UpdateExecutable";
			case 1:
				return columns.iterator().next().getType().getNameOfSingleParameterExecutor();
			default:
				return "ObjectStreamExecutable<Result>";
		}
	}

	private void writeParameterInterfaces() throws IOException {
		final Iterator<SQLParameter> iterator = arguments.iterator();
		if (iterator.hasNext()) {
			SQLParameter current = iterator.next();
			do {
				final String argumentName = current.getNameInLowerCamelCase();
				final SQLParameterType type = current.getType();
				final String argumentType = type.getNameOfJavaType();
				writeLine("	public interface ", current.getNameOfInterface(), " {");
				final String returnType;
				final SQLParameter next;
				if (iterator.hasNext()) {
					next = iterator.next();
					returnType = next.getNameOfInterface();
				} else {
					next = null;
					returnType = getNameOfExecutorClass();
				}
				if (type.canBeNull() == false) {
					final String withoutMethodName = current.getNameOfWithoutMethod();
					writeLine("		", returnType, " ", withoutMethodName, "();");
				}
				final String withMethodName = current.getNameOfWithMethod();
				writeLine("		", returnType, " ", withMethodName, "(", argumentType, " ", argumentName, ");");
				writeLine("	}");
				writeLine();
				current = next;
			} while (current != null);
		}
	}

	public void writeStartPrepareClass() throws IOException {
		write("	private static final class Prepared extends ", getNameOfExecutorClass());
		final Iterator<SQLParameter> iterator = arguments.iterator();
		if (iterator.hasNext()) {
			write(" implements ");
			while (true) {
				final SQLParameter parameter = iterator.next();
				final String nameOfInterface = parameter.getNameOfInterface();
				write(nameOfInterface);
				if (iterator.hasNext()) {
					write(", ");
				} else {
					break;
				}
			}
		}
		writeLine(" {");
		writeLine();
		writeLine("		private Prepared(Connection connection, ConnectionHandling handling) {");
		writeLine("			super(SQL, connection, handling);");
		writeLine("		}");
		writeLine();
	}

	public void writeParameterSetters() throws IOException {
		final Iterator<SQLParameter> iterator = arguments.iterator();
		if (iterator.hasNext()) {
			SQLParameter current = iterator.next();
			SQLParameter next;
			do {
				final String returnType;
				if (iterator.hasNext()) {
					next = iterator.next();
					returnType = next.getNameOfInterface();
				} else {
					next = null;
					returnType = getNameOfExecutorClass();
				}
				final String argumentName = current.getNameInLowerCamelCase();
				final String withMethodName = current.getNameOfWithMethod();
				final SQLParameterType type = current.getType();
				final String argumentType = type.getNameOfJavaType();
				if (type.canBeNull() == false) {
					final String withoutMethodName = current.getNameOfWithoutMethod();
					writeLine("		@Override");
					writeLine("		public final ", returnType, " ", withoutMethodName, "() {");
					writeLine("			return safelyUseStatement(statement -> {");
					for (int position : current.getPositions()) {
						writeLine("				statement.setNull(", position, ", Types." + type.name(), ");");
					}
					writeLine("				return this;");
					writeLine("			});");
					writeLine("		}");
					writeLine();
				}
				writeLine("		@Override");
				writeLine("		public final ", returnType, " ", withMethodName, "(", argumentType, " ", argumentName, ") {");
				writeLine("			return safelyUseStatement(statement -> {");
				if (type.canBeNull()) {
					writeLine("				if (", argumentName, " == null) {");
					for (int position : current.getPositions()) {
						writeLine("					statement.setNull(", position, ", Types." + type.name(), ");");
					}
					writeLine("				} else {");
					for (int position : current.getPositions()) {
						writeLine("					statement.", type.getSetter(position, argumentName), ";");
					}
					writeLine("				}");
				} else {
					for (int position : current.getPositions()) {
						writeLine("					statement.", type.getSetter(position, argumentName), ";");
					}
				}
				writeLine("				return this;");
				writeLine("			});");
				writeLine("		}");
				writeLine();
				current = next;
			} while (current != null);
		}
	}

	public void writeExecuteMethod() throws IOException {
		final int numberOfColumns = columns.size();
		if (numberOfColumns < 2) {
			return;
		}
		writeLine("		@Override");
		writeLine("		protected final Result read(ResultSet results) throws SQLException {");
		for (SQLParameter column : columns) {
			final SQLParameterType type = column.getType();
			final String nameInLowerCamel = column.getNameInLowerCamelCase();
			final String nameOfType = type.getNameOfJavaType();
			final int position = column.getPositions().iterator().next();
			final String getter = type.getGetter(position);
			writeLine("			final ", nameOfType, " ", nameInLowerCamel, " = results.", getter, ";");
		}
		writeLine("			return new Result(");
		final Iterator<SQLParameter> columnIterator = columns.iterator();
		while (columnIterator.hasNext()) {
			final SQLParameter column = columnIterator.next();
			final String nameInLowerCamel = column.getNameInLowerCamelCase();
			write("					", nameInLowerCamel);
			if (columnIterator.hasNext()) {
				writeLine(",");
			} else {
				writeLine();
			}
		}
		writeLine("			);");
		writeLine("		}");
		writeLine();
	}

	public void writeEndPreparedClass() throws IOException {
		writeLine("	}");
		writeLine();
	}

	public void writePrepareMethod() throws IOException {
		final String nameOfFirstInterface = getNameOfFirstInterface();
		writeLine("	public static final ", nameOfFirstInterface, " using(Connection connection, ConnectionHandling handling) {");
		writeLine("		return new Prepared(connection, handling);");
		writeLine("	}");
		writeLine();
	}

	private String getNameOfFirstInterface() {
		final Iterator<SQLParameter> iterator = arguments.iterator();
		if (iterator.hasNext()) {
			final SQLParameter first = iterator.next();
			return first.getNameOfInterface();
		} else {
			return getNameOfExecutorClass();
		}
	}

	private void writeEndOfClass() throws IOException {
		writeLine("}");
	}

	private void write(Object ... contentsOfLine) throws IOException {
		for (Object value : contentsOfLine) {
			writer.append(value.toString());
		}
	}

	private void writeLine(Object ... contentsOfLine) throws IOException {
		write(contentsOfLine);
		writer.newLine();
	}

	private void writeLine() throws IOException {
		writer.newLine();
	}

	private String findPrecedingWhitespaceIn(String line) {
		final Matcher matcher = PRECEDING_WHITESPACE.matcher(line);
		if (matcher.find()) {
			return matcher.group();
		}
		throw new IllegalStateException("Should always match something, even if it's an empty string!");
	}

}