package com.github.ryanholdren.typesafesql;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaClassWriter extends AbstractJavaClassWriter {

	public static class Builder extends AbstractBuilder {
		@Override
		protected JavaClassWriter createClassWriter() throws IOException {
			return new JavaClassWriter(this);
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	private JavaClassWriter(Builder builder) throws IOException {
		super(builder);
	}

	@Override
	public void writeClass() throws IOException {
		writeNamespace();
		writeImports();
		writeStartOfClass();
		writeSQLConstant();
		if (sql.needsResultClass()) {
			writeResultInterface();
			writeResultClass();
			writeResultStreamExecutable();
		}
		writeParameterInterfaces();
		writeStartPreparedClass();
		writeParameterSetters();
		writeEndPreparedClass();
		writeUsingMethods();
		writeEndOfClass();
	}

	@Override
	protected void forEachImport(Consumer<String> action) {
		super.forEachImport(action);
		sql.forEachRequiredImport(action, true);
		action.accept("com.github.ryanholdren.typesafesql." + sql.getClassNameOfExecutableParentClass());
		action.accept("static java.lang.String.join");
		action.accept("static java.lang.System.lineSeparator");
		if (sql.needsResultClass()) {
			action.accept("com.github.ryanholdren.typesafesql.ColumnPosition");
			action.accept("java.sql.ResultSet");
			action.accept("java.sql.SQLException");
		}
	}

	private void writeSQLConstant() throws IOException {
		final Iterator<String> iterator = sql.getLines().iterator();
		if (iterator.hasNext()) {
			writer.writeLine("public static final String SQL = join(");
			writer.writeLine("lineSeparator(),");
			while (true) {
				final String line = iterator.next();
				final String precedingWhitespace = findPrecedingWhitespaceIn(line);
				writer.write(precedingWhitespace);
				writer.write('"');
				final String escaped = escape(line.trim());
				writer.write(escaped);
				writer.write('"');
				if (iterator.hasNext()) {
					writer.writeLine(',');
				} else {
					writer.writeLine();
					break;
				}
			}
			writer.writeLine(");");
			writer.writeEmptyLine();
		}
	}

	private static String escape(String line) {
		if (line.indexOf('\\') < 0) {
			return line;
		} else {
			return line.replace("\\", "\\\\");
		}
	}

	protected static final Pattern PRECEDING_WHITESPACE = Pattern.compile("^\\s*");

	protected static String findPrecedingWhitespaceIn(String line) {
		final Matcher matcher = PRECEDING_WHITESPACE.matcher(line);
		if (matcher.find()) {
			return matcher.group();
		}
		throw new IllegalStateException("Should always match something, even if it's an empty string!");
	}

	public void writeResultInterface() throws IOException {
		writer.writeLine("public interface Result {");
		writer.writeEmptyLine();
		sql.forEachColumn(column -> {
			column.writeGetterDefinitionTo(writer);
			writer.writeEmptyLine();
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	public void writeResultClass() throws IOException {
			writer.writeLine("private static final class ResultImpl implements Result {");
		writer.writeEmptyLine();
		sql.forEachColumn(column -> {
			column.writeFieldTo(writer);
		});
		writer.writeEmptyLine();
		writer.writeLine("private ResultImpl(ResultSet results) throws SQLException {");
		sql.forEachColumn(column -> {
			column.writeSetFieldTo(writer);
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
		sql.forEachColumn(column -> {
			column.writeGetterTo(writer);
			writer.writeEmptyLine();
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	public void writeResultStreamExecutable() throws IOException {
		writer.writeLine("public static class ResultStreamExecutable extends ObjectStreamExecutable<Result> {");
		writer.writeEmptyLine();
		writer.writeLine("private ResultStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {");
		writer.writeLine("super(sql, connection, handling);");
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine("@Override");
		writer.writeLine("protected final Result read(ResultSet results) throws SQLException {");
		writer.writeLine("return new ResultImpl(results);");
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	private void writeParameterInterfaces() throws IOException {
		sql.forEachParameter((parameter, nextParameter) -> {
			final String returnType = nextParameter == null ? sql.getClassNameOfExecutable() : nextParameter.getNameOfInterface();
			parameter.writeInterfaceTo(writer, returnType);
		});
	}

	private void writeStartPreparedClass() throws IOException {
		writer.write("private static final class Prepared extends ");
		writer.write(sql.getClassNameOfExecutable());
		if (sql.hasParameters()) {
			writer.write(" implements ");
			sql.forEachParameter((parameter, nextParameter) -> {
				final String nameOfInterface = parameter.getNameOfInterface();
				writer.write(nameOfInterface);
				if (nextParameter != null) {
					writer.write(", ");
				}
			});
		}
		writer.writeLine(" {");
		writer.writeEmptyLine();
		writer.writeLine("private Prepared(Connection connection, ConnectionHandling handling) {");
		writer.writeLine("super(SQL, connection, handling);");
		writer.writeLine("}");
		writer.writeEmptyLine();
	}

	private void writeParameterSetters() throws IOException {
		sql.forEachParameter((parameter, nextParameter) -> {
			final String returnType = nextParameter == null ? sql.getClassNameOfExecutable() : nextParameter.getNameOfInterface();
			parameter.writeImplementationOfInterfaceTo(writer, returnType);
		});
	}

	private void writeEndPreparedClass() throws IOException {
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	private void writeUsingMethods() throws IOException {
		final String nameOfFirstInterface = getNameOfFirstInterface();
		writer.writeLine("public static final ", nameOfFirstInterface, " using(Connection connection, ConnectionHandling handling) {");
		writer.writeLine("return new Prepared(connection, handling);");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

}