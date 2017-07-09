package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.SQL.ResultColumnConsumer;
import com.github.ryanholdren.typesafesql.columns.ResultColumn;
import com.github.ryanholdren.typesafesql.jdbc.JDBCClassWriter;
import com.github.ryanholdren.typesafesql.pgasync.PgAsyncJavaClassWriter;
import java.io.IOException;
import static java.lang.String.join;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class JavaClassWriter extends AbstractJavaClassWriter {

	public static class Builder extends AbstractBuilder {
		@Override
		protected AbstractJavaClassWriter createClassWriter() throws IOException {
			switch (api) {
				case PGASYNC:
					return new PgAsyncJavaClassWriter(this);
				case JDBC:
					return new JDBCClassWriter(this);
			}
			throw new IllegalArgumentException();
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	protected JavaClassWriter(AbstractBuilder builder) throws IOException {
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
			writeResultForwarder();
			writeResultClass();
		}
		writeParameterInterfaces();
		writeRestOfClass();
		writeEndOfClass();
	}

	@Override
	protected void forEachImport(Consumer<String> action) {
		sql.forEachRequiredImport(action, true);
		action.accept("static java.lang.String.join");
		action.accept("static java.lang.System.lineSeparator");
		if (sql.needsResultClass()) {
			action.accept("com.github.ryanholdren.typesafesql.ColumnPosition");
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
		return line.replace("\\", "\\\\").replace("\"", "\\\"");
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
		if (sql.hasInterfaces()) {
			final String interfaces = join(", ", sql.getInterfaces());
			writer.writeLine("public interface Result extends ", interfaces, " {");
		} else {
			writer.writeLine("public interface Result {");
		}
		writer.writeEmptyLine();
		sql.forEachColumn(column -> {
			final String nameOfJavaType = column.getReturnType();
			final String capitalizedName = capitalize(column.getName());
			writer.writeLine("@ColumnPosition(", column.getPosition(), ")");
			writer.writeLine(nameOfJavaType, " get", capitalizedName, "();");
			writer.writeEmptyLine();
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	private void writeResultForwarder() throws IOException {
		writer.writeLine("public interface ForwardingResult extends Result {");
		writer.writeEmptyLine();
		writer.writeLine("Result getDelegate();");
		writer.writeEmptyLine();
		sql.forEachColumn(column -> {
			final String nameOfJavaType = column.getReturnType();
			final String capitalizedName = capitalize(column.getName());
			writer.writeLine("@Override");
			writer.writeLine("default ", nameOfJavaType, " get", capitalizedName, "() {");
			writer.writeLine("return getDelegate().get", capitalizedName, "();");
			writer.writeLine('}');
			writer.writeEmptyLine();
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	public void writeResultClass() throws IOException {
		writer.writeLine("public static class BasicResult implements Result {");
		writer.writeEmptyLine();
		sql.forEachColumn(column -> {
			final String nameOfJavaType = column.getReturnType();
			writer.writeLine("private final ", nameOfJavaType, ' ', column.getName(), ';');
		});
		writer.writeEmptyLine();
		writer.writeLine("public BasicResult(Result result) {");
		sql.forEachColumn(column -> {
			final String name = column.getName();
			final String capitalizedName = capitalize(name);
			writer.writeLine("this.", name, " = result.get", capitalizedName, "();");
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine("public BasicResult(");
		sql.forEachColumn(new ResultColumnConsumer() {

			@Override
			public void accept(ResultColumn column) throws Exception {
				final String nameOfJavaType = column.getReturnType();
				writer.writeLine("final ", nameOfJavaType, ' ', column.getName(), ',');
			}

			@Override
			public void acceptLast(ResultColumn column) throws Exception {
				final String nameOfJavaType = column.getReturnType();
				writer.writeLine("final ", nameOfJavaType, ' ', column.getName());
			}

		});
		writer.writeLine(") {");
		sql.forEachColumn(column -> {
			writer.writeLine("this.", column.getName(), " = ", column.getName(), ';');
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
		writeBasicResultConstructor();
		sql.forEachColumn(column -> {
			final String nameOfJavaType = column.getReturnType();
			final String capitalizedName = capitalize(column.getName());
			writer.writeLine("@Override");
			writer.writeLine("public final ", nameOfJavaType, " get", capitalizedName, "() {");
			writer.writeLine("return ", column.getName(), ';');
			writer.writeLine('}');
			writer.writeEmptyLine();
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	protected abstract void writeBasicResultConstructor() throws IOException;

	private void writeParameterInterfaces() throws IOException {
		sql.forEachParameter((parameter, nextParameter) -> {
			final String returnType = nextParameter == null ? getClassNameOfExecutable() : nextParameter.getNameOfInterface();
			final String nameOfInterface = parameter.getNameOfInterface();
			writer.writeLine("public interface ", nameOfInterface, " {");
			writer.writeLine(returnType, " without", parameter.getCapitalizedName(), "();");
			writer.writeLine(returnType, " with", parameter.getCapitalizedName(), '(', parameter.getArgumentType(), ' ', parameter.getName(), ");");
			writer.writeLine('}');
			writer.writeEmptyLine();
		});
	}

	protected abstract void writeRestOfClass() throws IOException;

}
