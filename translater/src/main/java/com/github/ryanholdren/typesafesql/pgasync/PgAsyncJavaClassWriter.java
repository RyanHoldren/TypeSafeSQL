package com.github.ryanholdren.typesafesql.pgasync;

import com.github.ryanholdren.typesafesql.JavaClassWriter;
import com.github.ryanholdren.typesafesql.columns.ResultColumn;
import java.io.IOException;
import java.util.function.Consumer;

public class PgAsyncJavaClassWriter extends JavaClassWriter {

	public PgAsyncJavaClassWriter(AbstractBuilder builder) throws IOException {
		super(builder);
	}

	@Override
	protected void forEachImport(Consumer<String> action) {
		super.forEachImport(action);
		action.accept("com.github.pgasync.QueryExecutor");
		switch (sql.getNumberOfColumns()) {
			case 0:
				action.accept("static com.github.ryanholdren.typesafesql.RxJavaToReactor.toMono");
				action.accept("reactor.core.publisher.Mono");
				action.accept("com.github.pgasync.ResultSet");
				break;
			case 1:
				action.accept("static com.github.ryanholdren.typesafesql.RxJavaToReactor.toFlux");
				action.accept("reactor.core.publisher.Flux");
				break;
			default:
				action.accept("static com.github.ryanholdren.typesafesql.RxJavaToReactor.toFlux");
				action.accept("reactor.core.publisher.Flux");
				action.accept("com.github.pgasync.Row");
				break;
		}
	}

	@Override
	protected void writeBasicResultConstructor() throws IOException {
		writer.writeLine("private BasicResult(Row row) {");
		final ExtractFromRow extractor = new ExtractFromRow(writer);
		sql.forEachColumn(column -> {
			column.accept(extractor);
		});
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	@Override
	protected void writeRestOfClass() throws IOException {
		writeExecutableInterface();
		writeStartParameterClass();
		writeParameterSetters();
		writeExecuteMethod();
		writeEndPreparedClass();
		writeBeginMethod();
	}

	private void writeExecutableInterface() throws IOException {
		writer.writeLine("public interface Executable {");
		final String returnType = getReturnTypeOfExecute();
		writer.writeLine(returnType, " executeIn(QueryExecutor executor);");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	private void writeStartParameterClass() throws IOException {
		writer.write("private static final class Parameters implements ");
		sql.forEachParameter((parameter, nextParameter) -> {
			final String nameOfInterface = parameter.getNameOfInterface();
			writer.write(nameOfInterface);
			writer.write(", ");
		});
		writer.writeLine("Executable {");
		writer.writeEmptyLine();
		if (sql.hasParameters()) {
			final int size = sql.getParameters().stream().mapToInt(parameter -> parameter.getPositions().size()).sum();
			writer.writeLine("private final Object[] parameters = new Object[", size, "];");
			writer.writeEmptyLine();
		}
	}

	private void writeParameterSetters() throws IOException {
		sql.forEachParameter((parameter, nextParameter) -> {
			final String returnType = nextParameter == null ? getClassNameOfExecutable() : nextParameter.getNameOfInterface();
			final String nameOfStatement = parameter.getName().equals("statement") ? "sqlStatement" : "statement";
			writer.writeLine("@Override");
			writer.writeLine("public final ", returnType, " without", parameter.getCapitalizedName(), "() {");
			for (int position : parameter.getPositions()) {
				writer.writeLine("this.parameters[", position - 1, "] = null;");
			}
			writer.writeLine("return this;");
			writer.writeLine('}');
			writer.writeEmptyLine();
			writer.writeLine("@Override");
			writer.writeLine("public final ", returnType, " with", parameter.getCapitalizedName(), "(", parameter.getArgumentType(), " ", parameter.getName(), ") {");
			for (int position : parameter.getPositions()) {
				writer.writeLine("this.parameters[", position - 1, "] = ", parameter.accept(Setter.INSTANCE), ";");
			}
			writer.writeLine("return this;");
			writer.writeLine('}');
			writer.writeEmptyLine();
		});
	}

	private void writeExecuteMethod() throws IOException {
		writer.writeLine("@Override");
		writer.writeLine("public final ", getReturnTypeOfExecute(), " executeIn(QueryExecutor executor) {");
		if (sql.hasParameters()) {
			switch (sql.getNumberOfColumns()) {
				case 0:
					writer.writeLine("return toMono(executor.querySet(SQL, parameters)).then();");
					break;
				case 1:
					final ReturnFromRow extractor = new ReturnFromRow(writer);
					writer.writeLine("return toFlux(executor.queryRows(SQL, parameters).map(row -> {");
					sql.forEachColumn(column -> {
						column.accept(extractor);
					});
					writer.writeLine("}));");
					break;
				default:
					writer.writeLine("return toFlux(executor.queryRows(SQL, parameters).map(BasicResult::new));");
					break;
			}
		} else {
			switch (sql.getNumberOfColumns()) {
				case 0:
					writer.writeLine("return toMono(executor.querySet(SQL)).then();");
					break;
				case 1:
					final ReturnFromRow extractor = new ReturnFromRow(writer);
					writer.writeLine("return toFlux(executor.queryRows(SQL).map(row -> {");
					sql.forEachColumn(column -> {
						column.accept(extractor);
					});
					writer.writeLine("}));");
					break;
				default:
					writer.writeLine("return toFlux(executor.queryRows(SQL).map(BasicResult::new));");
					break;
			}
		}
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	private void writeEndPreparedClass() throws IOException {
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	@Override
	protected String getClassNameOfExecutable() {
		return "Executable";
	}

	protected String getReturnTypeOfExecute() {
		switch (sql.getNumberOfColumns()) {
			case 0:
				return "Mono<Void>";
			case 1:
				final ResultColumn onlyColumn = sql.getColumns().iterator().next();
				return "Flux<" + onlyColumn.getBoxedReturnType() + '>';
			default:
				return "Flux<Result>";
		}
	}

	private void writeBeginMethod() throws IOException {
		final String nameOfFirstInterface = getNameOfFirstInterface();
		writer.writeLine("public static final ", nameOfFirstInterface, " prepare() {");
		writer.writeLine("return new Parameters();");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

}
