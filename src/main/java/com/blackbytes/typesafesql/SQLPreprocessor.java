package com.blackbytes.typesafesql;

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

public class SQLPreprocessor {

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
			new SQLPreprocessor(this).preprocess();
		}

	}

	public static Builder newBuilder() {
		return new Builder();
	}

	private static final String NAME_OF_EXECUTABLE_INTERFACE = "ReadyToExecute";
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Pattern PRECEDING_WHITESPACE = Pattern.compile("^\\s*");
	private static final Pattern PARAMETER = Pattern.compile("\\{(?<type>ARRAY|BIGINT|BINARY|BIT|BLOB|BOOLEAN|CHAR|CLOB|DATALINK|DATE|DECIMAL|DISTINCT|DOUBLE|FLOAT|INTEGER|JAVA_OBJECT|LONGNVARCHAR|LONGVARBINARY|LONGVARCHAR|NCHAR|NCLOB|NULL|NUMERIC|NVARCHAR|OTHER|REAL|REF|ROWID|SMALLINT|SQLXML|STRUCT|TIME|TIMESTAMP|TINYINT|VARBINARY|VARCHAR):(?<name>[a-z][_a-zA-Z0-9]*)\\}");

	private final BufferedReader reader;
	private final BufferedWriter writer;
	private final String className;
	private final String namespace;
	private final SQLParameters parameters = new SQLParameters();

	public SQLPreprocessor(Builder builder) {
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
			writeConstant();
			writeResultSetHandlerInterface();
			writeParameterInterfaces();
			writeExecutableInterface();
			writePrepareClass();
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
		"java.sql.Connection",
		"java.sql.PreparedStatement",
		"java.sql.ResultSet",
		"java.sql.SQLException",
		"java.sql.Types",
	};

	private void writeImports() throws IOException {
		for (String classNameOfImport : IMPORTS) {
			writeLine("import ", classNameOfImport, ";");
		}
		writeLine();
	}

	private void writeStartOfClass() throws IOException {
		writeLine("public class ", className, " {");
		writeLine();
	}

	private void writeResultSetHandlerInterface() throws IOException {
		writeLine("	@FunctionalInterface");
		writeLine("	public interface ResultSetHandler<T> {");
		writeLine("		T handle(ResultSet results) throws SQLException;");
		writeLine("	}");
		writeLine();
	}

	private void writeConstant() throws IOException {
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
				writer.append(",\n");
			}
			writer.append(precedingWhitespace);
			writer.append("\t\t\"");
			final Matcher matcher = PARAMETER.matcher(escaped);
			if (matcher.find()) {
				int end = 0;
				do {
					final int start = matcher.start();
					final CharSequence before = escaped.subSequence(end, start);
					writer.append(before);
					final String type = matcher.group("type");
					final String name = matcher.group("name");
					parameters.add(name, SQLParameterType.valueOf(type));
					writer.append('?');
					end = matcher.end();
				} while (matcher.find());
				final CharSequence after = escaped.subSequence(end, escaped.length());
				writer.append(after);
			} else {
				writer.append(escaped);
			}
			writer.append('"');
		}
		writer.write("\n\t);\n\n");
	}

	private void writeParameterInterfaces() throws IOException {
		final Iterator<SQLParameter> iterator = parameters.iterator();
		if (iterator.hasNext()) {
			SQLParameter current = iterator.next();
			do {
				final String nameInUpperCamel = current.getNameInUpperCamelCase();
				final String parameterType = current.getType().getNameOfJavaClass();
				final String nameInLowerCamel = current.getNameInLowerCamelCase();
				writeLine("	public interface Needs", nameInUpperCamel, " {");
				final String returnType;
				final SQLParameter next;
				if (iterator.hasNext()) {
					next = iterator.next();
					returnType = "Needs" + next.getNameInUpperCamelCase();
				} else {
					next = null;
					returnType = NAME_OF_EXECUTABLE_INTERFACE;
				}
				writeLine("		", returnType, " with", nameInUpperCamel, "(", parameterType, " ", nameInLowerCamel, ") throws SQLException;");
				writeLine("	}");
				writeLine();
				current = next;
			} while (current != null);
		}
	}

	private void writeExecutableInterface() throws IOException {
		writer.append("\tpublic interface ");
		writer.append(NAME_OF_EXECUTABLE_INTERFACE);
		writer.append(" {\n\t\tint execute() throws SQLException;\n\t\t<T> T execute(ResultSetHandler<T> handler) throws SQLException;\n\t}\n\n");
	}

	public void writePrepareClass() throws IOException {
		writer.append("\tprivate static class Prepared implements ");
		for (SQLParameter parameter : parameters) {
			writer.append("Needs");
			writer.append(parameter.getNameInUpperCamelCase());
			writer.append(", ");
		}
		writer.append(NAME_OF_EXECUTABLE_INTERFACE);
		writer.append(" {\n\n\t\tprivate final PreparedStatement statement;\n\n\t\tpublic Prepared(Connection connection) throws SQLException {\n\t\t\tstatement = connection.prepareStatement(SQL);\n\t\t}\n\n");
		final Iterator<SQLParameter> iterator = parameters.iterator();
		if (iterator.hasNext()) {
			SQLParameter current = iterator.next();
			SQLParameter next;
			do {
				writer.append("\t\t@Override\n\t\tpublic ");
				if (iterator.hasNext()) {
					next = iterator.next();
					writer.append("Needs");
					writer.append(next.getNameInUpperCamelCase());
				} else {
					next = null;
					writer.append("ReadyToExecute");
				}
				writer.append(" with");
				writer.append(current.getNameInUpperCamelCase());
				writer.append('(');
				writer.append(current.getType().getNameOfJavaClass());
				writer.append(' ');
				writer.append(current.getNameInLowerCamelCase());
				writer.append(") throws SQLException {\n\t\t\t");
				for (int position : current.getPositions()) {
					writer.append("statement.");
					final String nameOfSetterMethod = current.getType().getNameOfMethodInPreparedStatement();
					writer.append(nameOfSetterMethod);
					writer.append('(');
					writer.append(Integer.toString(position));
					writer.append(", ");
					writer.append(current.getNameInLowerCamelCase());
					writer.append(");\n\t\t\t");
				}
				writer.append("return this;\n\t\t}\n\n");
				current = next;
			} while (current != null);
		}

		writeLine("		@Override");
		writeLine("		public int execute() throws SQLException {");
		writeLine("			int totalUpdateCount = 0;");
		writeLine("			boolean isResultSet = statement.execute();");
		writeLine("			while (true) {");
		writeLine("				if (isResultSet) {");
		writeLine("					continue;");
		writeLine("				}");
		writeLine("				int updateCount = statement.getUpdateCount();");
		writeLine("				if (updateCount < 0) {");
		writeLine("					return totalUpdateCount;");
		writeLine("				}");
		writeLine("				totalUpdateCount += updateCount;");
		writeLine("				isResultSet = statement.getMoreResults();");
		writeLine("			}");
		writeLine("		}");
		writeLine();
		writeLine("		@Override");
		writeLine("		public <T> T execute(ResultSetHandler<T> handler) throws SQLException {");
		writeLine("			boolean isResultSet = statement.execute();");
		writeLine("			do {");
		writeLine("				if (isResultSet) {");
		writeLine("					try (final ResultSet results = statement.getResultSet()) {");
		writeLine("						return handler.handle(results);");
		writeLine("					}");
		writeLine("				}");
		writeLine("				isResultSet = statement.getMoreResults();");
		writeLine("			} while (isResultSet || statement.getUpdateCount() != -1);");
		writeLine("			throw new IllegalArgumentException(\"Statement does not return any results!\");");
		writeLine("		}");
		writeLine();
		writeLine("	}");
		writeLine();
	}

	public void writePrepareMethod() throws IOException {
		final String nameOfFirstInterface = getNameOfFirstInterface();
		writeLine("	public static ", nameOfFirstInterface, " using(Connection connection) throws SQLException {");
		writeLine("		return new Prepared(connection);");
		writeLine("	}");
		writeLine();
	}

	private String getNameOfFirstInterface() {
		final Iterator<SQLParameter> iterator = parameters.iterator();
		if (iterator.hasNext()) {
			final SQLParameter first = iterator.next();
			return "Needs" + first.getNameInUpperCamelCase();
		} else {
			return NAME_OF_EXECUTABLE_INTERFACE;
		}
	}

	private void writeEndOfClass() throws IOException {
		writeLine("}");
	}

	private void writeLine(String ... contentsOfLine) throws IOException {
		for (String value : contentsOfLine) {
			writer.append(value);
		}
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