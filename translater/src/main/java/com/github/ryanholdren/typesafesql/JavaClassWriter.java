package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.pgasync.PgAsyncJavaClassWriter;
import java.io.IOException;
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
		if (sql.needsParameterInterface()) {
			writeParameterInterface();
		}
		if (sql.needsResultInterface()) {
			writeResultInterface();
		}
		writeFactoryMethod();
		writeEndOfClass();
	}

	@Override
	protected void writeStartOfClass() throws IOException {
		if (sql.needsParameterInterface() || sql.needsResultInterface()) {
			writer.writeLine("@Enclosing");
			writer.writeLine("@Style(stagedBuilder = true, init = \"with*\", build = \"prepare\")");
		}
		super.writeStartOfClass();
	}

	@Override
	protected void forEachImport(Consumer<String> action) {
		sql.forEachRequiredImport(action, true);
		action.accept("static java.lang.String.join");
		action.accept("static java.lang.System.lineSeparator");
		if (sql.needsParameterInterface() || sql.needsResultInterface()) {
			action.accept("org.immutables.value.Value.Immutable");
			action.accept("org.immutables.value.Value.Style");
			action.accept("org.immutables.value.Value.Enclosing");
		}
		if (sql.needsResultInterface()) {
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

	protected void writeParameterInterface() throws IOException {
		writer.writeLine("@Immutable");
		writer.writeLine("public interface Parameters {");
		this.writeMethodsOfParameterInterface();
		writer.writeLine("}");
		writer.writeEmptyLine();
	}

	protected void writeMethodsOfParameterInterface() throws IOException {
		writer.writeEmptyLine();
		sql.forEachParameter(parameter -> {
			writer.writeLine(parameter.getArgumentType(), " get", parameter.getCapitalizedName(), "();");
			writer.writeEmptyLine();
		});
	}

	protected void writeResultInterface() throws IOException {
		writer.writeLine("@Immutable");
		writer.writeLine("public interface Result {");
		this.writeMethodsOfResultInterface();
		writer.writeLine("}");
		writer.writeEmptyLine();
	}

	private void writeMethodsOfResultInterface() throws IOException {
		writer.writeEmptyLine();
		sql.forEachColumn(column -> {
			final String nameOfJavaType = column.getReturnType();
			final String capitalizedName = capitalize(column.getName());
			writer.writeLine("@ColumnPosition(", column.getPosition(), ")");
			writer.writeLine(nameOfJavaType, " get", capitalizedName, "();");
			writer.writeEmptyLine();
		});
	}

	protected abstract void writeFactoryMethod() throws IOException;

}
