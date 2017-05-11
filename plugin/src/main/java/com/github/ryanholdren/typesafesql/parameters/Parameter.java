package com.github.ryanholdren.typesafesql.parameters;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import com.github.ryanholdren.typesafesql.RequiresImports;
import java.io.IOException;
import java.util.HashSet;
import java.util.function.Consumer;

public abstract class Parameter implements RequiresImports {

	public static String capitalize(String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	private final String name;
	private final String capitalizedName;
	private final HashSet<Integer> positions = new HashSet<>();
	private final String argumentType = getArgumentType();
	private final String nameOfJDBCConstant = getNameOfJDBCConstant();
	private final boolean isNotAllowedToBeNull = isNullable() == false;

	public Parameter(String argumentName) {
		this.name = argumentName;
		this.capitalizedName = capitalize(argumentName);
	}

	public void addPosition(int position) {
		positions.add(position);
	}

	public String getName() {
		return name;
	}

	public String getCapitalizedName() {
		return capitalizedName;
	}

	public String getNameOfInterface() {
		return "Needs" + capitalizedName;
	}

	public String getNameOfMock() {
		return "needs" + capitalizedName;
	}

	public boolean needsCasting() {
		return false;
	}

	public String getCast() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		if (isNotMocking) {
			action.accept("java.sql.Types");
		}
	}

	public void writeInterfaceTo(AutoIndentingWriter writer, String returnType) throws IOException {
		final String nameOfInterface = getNameOfInterface();
		writer.writeLine("public interface ", nameOfInterface, " {");
		writer.writeLine(returnType, " without", capitalizedName, "();");
		writer.writeLine(returnType, " with", capitalizedName, '(', argumentType, ' ', name, ");");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	public void writeImplementationOfInterfaceTo(AutoIndentingWriter writer, String returnType) throws IOException {
		final String nameOfStatement = name.equals("statement") ? "sqlStatement" : "statement";
		writer.writeLine("@Override");
		writer.writeLine("public final ", returnType, " without", capitalizedName, "() {");
		writer.writeLine("return safelyUseStatement(", nameOfStatement, " -> {");
		for (int position : positions) {
			writer.writeLine(nameOfStatement, ".setNull(", position, ", Types.", nameOfJDBCConstant, ");");
		}
		writer.writeLine("return this;");
		writer.writeLine("});");
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine("@Override");
		writer.writeLine("public final ", returnType, " with", capitalizedName, "(", argumentType, " ", name, ") {");
		writer.writeLine("return safelyUseStatement(", nameOfStatement, " -> {");
		if (isNotAllowedToBeNull) {
			writeStatementSettersTo(nameOfStatement, writer);
		} else {
			writer.writeLine("if (", name, " == null) {");
			writer.writeLine("return without", capitalizedName, "();");
			writer.writeLine("} else {");
			writeStatementSettersTo(nameOfStatement, writer);
			writer.writeLine("}");
		}
		writer.writeLine("return this;");
		writer.writeLine("});");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	protected void writeStatementSettersTo(final String nameOfStatement, AutoIndentingWriter writer) throws IOException {
		for (int position : positions) {
			writer.writeLine(nameOfStatement, ".", getSetter(position, name), ";");
		}
	}

	protected String getSetter(int position, String nameOfVariable) {
		return getNameOfMethodInPreparedStatement() + "(" + position + ", " + nameOfVariable + ")";
	}

	protected abstract boolean isNullable();
	protected abstract String getNameOfMethodInPreparedStatement();
	protected abstract String getNameOfJDBCConstant();
	public abstract String getArgumentType();

}
