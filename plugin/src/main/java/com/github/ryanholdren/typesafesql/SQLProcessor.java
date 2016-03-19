package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.columns.ResultColumns;
import com.github.ryanholdren.typesafesql.parameters.Parameter;
import com.github.ryanholdren.typesafesql.parameters.Parameters;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
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
		private AutoIndentingWriter writer;
		private String className;
		private String namespace;

		public Builder setReader(Path file) throws IOException {
			this.reader = Files.newBufferedReader(file, UTF8);
			return this;
		}

		public Builder setReader(BufferedReader input) {
			this.reader = input;
			return this;
		}

		public Builder setReader(Reader input) {
			this.reader = new BufferedReader(input);
			return this;
		}

		public Builder setWriter(Path file) throws IOException {
			Files.createDirectories(file.getParent());
			final BufferedWriter writer = Files.newBufferedWriter(file, UTF8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			return setWriter(writer);
		}

		public Builder setWriter(BufferedWriter output) {
			this.writer = new AutoIndentingWriter(output);
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
			try {
				try {
					new SQLProcessor(this).preprocess();
				} finally {
					writer.close();
				}
			} finally {
				reader.close();
			}
		}

	}

	public static Builder newBuilder() {
		return new Builder();
	}

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Pattern PRECEDING_WHITESPACE = Pattern.compile("^\\s*");
	private static final Pattern PARAMETER = Pattern.compile("\\{(?<direction>in|out):(?<type>[A-Z][A-Z_]+):(?<name>[a-z][a-zA-Z0-9]*)\\}");

	private final BufferedReader reader;
	private final AutoIndentingWriter writer;
	private final String className;
	private final String namespace;
	private final Parameters arguments = new Parameters();
	private final ResultColumns columns = new ResultColumns();

	public SQLProcessor(Builder builder) {
		this.namespace = builder.namespace;
		this.className = builder.className;
		this.reader = builder.reader;
		this.writer = builder.writer;
	}

	public void preprocess() throws IOException {
		writeNamespace();
		writeImports();
		writeStartOfClass();
		writeSQLConstant();
		writeResultClass();
		writeParameterInterfaces();
		writeStartPreparedClass();
		writeParameterSetters();
		writeEndPreparedClass();
		writeUsingMethods();
		writeEndOfClass();
	}

	private void writeNamespace() throws IOException {
		writer.writeLine("package ", namespace, ";");
		writer.writeEmptyLine();
	}

	private final String[] IMPORTS = {
		"java.sql.*",
		"java.time.*",
		"java.util.*",
		"com.github.ryanholdren.typesafesql.*"
	};

	private void writeImports() throws IOException {
		for (String classNameOfImport : IMPORTS) {
			writer.writeLine("import ", classNameOfImport, ';');
		}
		writer.writeEmptyLine();
	}

	private void writeStartOfClass() throws IOException {
		writer.writeLine("public final class ", className, " {");
		writer.writeEmptyLine();
	}

	private void writeSQLConstant() throws IOException {
		writer.writeLine("public static final String SQL = String.join(");
		writer.writeLine("System.lineSeparator(),");
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
				writer.write(",");
				writer.writeLineBreak();
			}
			writer.write(precedingWhitespace);
			writer.write('"');
			final Matcher matcher = PARAMETER.matcher(escaped);
			if (matcher.find()) {
				int end = 0;
				do {
					final int start = matcher.start();
					final String before = escaped.substring(end, start);
					writer.write(before);
					final String direction = matcher.group("direction");
					final String nameOfType = matcher.group("type");
					final String name = matcher.group("name");
					if (direction.equals("in")) {
						arguments.add(name, nameOfType);
						writer.write('?');
					} else {
						columns.add(name, nameOfType);
						writer.write(name);
					}
					end = matcher.end();
				} while (matcher.find());
				final String after = escaped.substring(end, escaped.length());
				writer.write(after);
			} else {
				writer.write(escaped);
			}
			writer.write('"');
		}
		writer.writeLineBreak();
		writer.writeLine(");");
		writer.writeEmptyLine();
	}

	private void writeResultClass() throws IOException {
		columns.writeResultClassToIfNecessary(writer);
	}

	private void writeParameterInterfaces() throws IOException {
		final String lastReturnType = columns.getNameOfExecutorClass();
		arguments.writeInterfacesTo(writer, lastReturnType);
	}

	private void writeStartPreparedClass() throws IOException {
		writer.write("private static final class Prepared extends ");
		writer.write(columns.getNameOfExecutorClass());
		final Iterator<Parameter> iterator = arguments.iterator();
		if (iterator.hasNext()) {
			writer.write(" implements ");
			while (true) {
				final Parameter parameter = iterator.next();
				final String nameOfInterface = parameter.getNameOfInterface();
				writer.write(nameOfInterface);
				if (iterator.hasNext()) {
					writer.write(", ");
				} else {
					break;
				}
			}
		}
		writer.writeLine(" {");
		writer.writeEmptyLine();
		writer.writeLine("private Prepared(Connection connection, ConnectionHandling handling) {");
		writer.writeLine("super(SQL, connection, handling);");
		writer.writeLine("}");
		writer.writeEmptyLine();
	}

	private void writeParameterSetters() throws IOException {
		final String lastReturnType = columns.getNameOfExecutorClass();
		arguments.writeImplemenationOfInterfacesTo(writer, lastReturnType);
	}

	private void writeEndPreparedClass() throws IOException {
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	private void writeUsingMethods() throws IOException {
		final String nameOfFirstInterface = getNameOfFirstInterface();
		writer.writeLine("public static final ", nameOfFirstInterface, " using(Connection connection) {");
		writer.writeLine("return new Prepared(connection, ConnectionHandling.CLOSE_WHEN_DONE);");
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine("public static final ", nameOfFirstInterface, " using(Connection connection, ConnectionHandling handling) {");
		writer.writeLine("return new Prepared(connection, handling);");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	private String getNameOfFirstInterface() {
		final Iterator<Parameter> iterator = arguments.iterator();
		if (iterator.hasNext()) {
			final Parameter first = iterator.next();
			return first.getNameOfInterface();
		} else {
			return columns.getNameOfExecutorClass();
		}
	}

	private void writeEndOfClass() throws IOException {
		writer.writeLine("}");
	}

	private String findPrecedingWhitespaceIn(String line) {
		final Matcher matcher = PRECEDING_WHITESPACE.matcher(line);
		if (matcher.find()) {
			return matcher.group();
		}
		throw new IllegalStateException("Should always match something, even if it's an empty string!");
	}

}