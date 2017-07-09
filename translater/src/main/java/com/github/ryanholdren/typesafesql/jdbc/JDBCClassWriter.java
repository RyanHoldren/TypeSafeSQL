package com.github.ryanholdren.typesafesql.jdbc;

import com.github.ryanholdren.typesafesql.*;
import com.github.ryanholdren.typesafesql.columns.ResultColumn;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

public class JDBCClassWriter extends JavaClassWriter {

	public JDBCClassWriter(AbstractBuilder builder) throws IOException {
		super(builder);
	}

	@Override
	protected void forEachImport(Consumer<String> action) {
		action.accept("java.sql.Connection");
		action.accept("com.github.ryanholdren.typesafesql." + getClassNameOfExecutableParentClass());
		action.accept("com.github.ryanholdren.typesafesql.RuntimeSQLException");
		action.accept("com.github.ryanholdren.typesafesql.ConnectionSupplier");
		action.accept("com.github.ryanholdren.typesafesql.ConnectionHandling");
		super.forEachImport(action);
		action.accept("java.sql.SQLException");
		action.accept("java.sql.Types");
		if (sql.needsResultClass()) {
			action.accept("java.sql.ResultSet");
		}
	}

	@Override
	public void writeResultClass() throws IOException {
		super.writeResultClass();
		writeResultStreamExecutable();
	}

	@Override
	protected void writeBasicResultConstructor() throws IOException {
		writer.writeLine("private BasicResult(ResultSet results) throws SQLException {");
		final ExtractFromResultSet extractor = new ExtractFromResultSet(writer);
		sql.forEachColumn(column -> {
			column.accept(extractor);
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
		writer.writeLine("return new BasicResult(results);");
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	@Override
	protected void writeRestOfClass() throws IOException {
		writeStartPreparedClass();
		writeParameterSetters();
		writeEndPreparedClass();
		writeUsingMethods();
	}

	private void writeStartPreparedClass() throws IOException {
		writer.write("private static final class Prepared extends ");
		writer.write(getClassNameOfExecutable());
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
			final String returnType = nextParameter == null ? getClassNameOfExecutable() : nextParameter.getNameOfInterface();
			final String nameOfStatement = parameter.getName().equals("statement") ? "sqlStatement" : "statement";
			writer.writeLine("@Override");
			writer.writeLine("public final ", returnType, " without", parameter.getCapitalizedName(), "() {");
			writer.writeLine("return safelyUseStatement(", nameOfStatement, " -> {");
			for (int position : parameter.getPositions()) {
				writer.writeLine(nameOfStatement, ".setNull(", position, ", Types.", parameter.accept(NameOfTypesConstant.VISITOR), ");");
			}
			writer.writeLine("return this;");
			writer.writeLine("});");
			writer.writeLine('}');
			writer.writeEmptyLine();
			writer.writeLine("@Override");
			writer.writeLine("public final ", returnType, " with", parameter.getCapitalizedName(), "(", parameter.getArgumentType(), " ", parameter.getName(), ") {");
			writer.writeLine("return safelyUseStatement(", nameOfStatement, " -> {");
			if (parameter.isNullable()) {
				writer.writeLine("if (", parameter.getName(), " == null) {");
				writer.writeLine("return without", parameter.getCapitalizedName(), "();");
				writer.writeLine("} else {");
				parameter.accept(new SetToPreparedStatement(writer, nameOfStatement));
				writer.writeLine("}");
			} else {
				parameter.accept(new SetToPreparedStatement(writer, nameOfStatement));
			}
			writer.writeLine("return this;");
			writer.writeLine("});");
			writer.writeLine('}');
			writer.writeEmptyLine();
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
		writer.writeLine("public static final ", nameOfFirstInterface, " using(ConnectionSupplier connectionSupplier, ConnectionHandling handling) {");
		writer.writeLine("try {");
		writer.writeLine("final Connection connection = connectionSupplier.openConnection();");
		writer.writeLine("return new Prepared(connection, handling);");
		writer.writeLine("} catch (SQLException exception) {");
		writer.writeLine("throw new RuntimeSQLException(exception);");
		writer.writeLine('}');
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	protected String getClassNameOfExecutable() {
		final Iterator<ResultColumn> iterator = sql.getColumns().iterator();
		if (iterator.hasNext()) {
			final ResultColumn column = iterator.next();
			if (iterator.hasNext()) {
				return "ResultStreamExecutable";
			} else {
				return column.accept(NameOfSpecializedExecutor.VISITOR);
			}
		} else {
			return "UpdateExecutable";
		}
	}

	private String getClassNameOfExecutableParentClass() {
		final Iterator<ResultColumn> iterator = sql.getColumns().iterator();
		if (iterator.hasNext()) {
			final ResultColumn column = iterator.next();
			if (iterator.hasNext()) {
				return "ObjectStreamExecutable";
			} else {
				return column.accept(NameOfSpecializedExecutor.VISITOR);
			}
		} else {
			return "UpdateExecutable";
		}
	}

}
