package com.github.ryanholdren.typesafesql.pgasync;

import com.github.ryanholdren.typesafesql.JavaClassWriter;
import com.github.ryanholdren.typesafesql.columns.ResultColumn;
import static com.github.ryanholdren.typesafesql.pgasync.ToObjectFromParameters.TO_OBJECT_FROM_PARAMETERS;
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
		action.accept("static com.github.ryanholdren.typesafesql.Utilities.*");
		switch (sql.getNumberOfColumns()) {
			case 0:
				action.accept("reactor.core.publisher.Mono");
				action.accept("com.github.pgasync.ResultSet");
				break;
			case 1:
				action.accept("reactor.core.publisher.Flux");
				break;
			default:
				action.accept("reactor.core.publisher.Flux");
				action.accept("com.github.pgasync.Row");
				break;
		}
	}

	@Override
	protected void writeMethodsOfParameterInterface() throws IOException {
		writer.writeEmptyLine();
		writer.writeLine("QueryExecutor getQueryExecutor();");
		super.writeMethodsOfParameterInterface();
		writer.writeLine("default ", getReturnTypeOfExecute(), " execute() {");
		writer.writeLine("final Object[] objects = new Object[", sql.getNumberOfParameters(), "];");
		sql.forEachParameter(parameter -> {
			for (int position : parameter.getPositions()) {
				writer.writeLine("objects[", position - 1, "] = ", parameter.accept(TO_OBJECT_FROM_PARAMETERS), ";");
			}
		});
		writer.write("return toFlux(getQueryExecutor().queryRows(SQL, objects))");
		writeRowMapper();
		writer.writeLine("}");
		writer.writeEmptyLine();
	}

	@Override
	protected void writeFactoryMethod() throws IOException {
		writer.writeLine("QueryExecutor getQueryExecutor();");
		writer.writeEmptyLine();
		if (sql.needsParameterInterface()) {
			writer.writeLine("default Immutable", this.className, ".Parameters.", sql.firstParameter().get().getCapitalizedName(), "BuildStage ", uncapitalize(this.className), "() {");
			writer.writeLine("return Immutable", this.className, ".Parameters.builder().withQueryExecutor(getQueryExecutor());");
			writer.writeLine('}');
			writer.writeEmptyLine();
			return;
		} else {
			writer.writeLine("default ", getReturnTypeOfExecute(), ' ', uncapitalize(this.className) + "() {");
			writer.write("return toFlux(getQueryExecutor().queryRows(SQL))");
			writeRowMapper();
			writer.writeLine('}');
			writer.writeEmptyLine();
		}
	}

	private void writeRowMapper() throws IOException {
		final ReturnFromRow extractor = new ReturnFromRow(writer);
		switch (sql.getNumberOfColumns()) {
			case 0:
				writer.writeLine(".then();");
				break;
			case 1:
				writer.writeLine(".map(row -> {");
				sql.forEachColumn(column -> {
					column.accept(extractor);
				});
				writer.writeLine("});");
				break;
			default:
				writer.writeLine(".map(row -> Immutable" + this.className + ".Result.copyOf(new Result() {");
				sql.forEachColumn(column -> {
					writer.writeLine("@Override");
					writer.writeLine("public final ", column.getReturnType(), " get", column.getCapitalizedName(), "() {");
					column.accept(extractor);
					writer.writeLine('}');
					writer.writeEmptyLine();
				});
				writer.writeLine("}));");
				break;
		}
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

}
