package com.github.ryanholdren.typesafesql.parameters;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.HashSet;

public abstract class Parameter {

	public static String capitalize(String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	private final String name;
	private final String capitalizedName;
	private final HashSet<Integer> positions =  new HashSet<>();

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

	public String getNameOfInterface() {
		return "Needs" + capitalizedName;
	}

	public void writeInterfaceTo(AutoIndentingWriter writer, String returnType) throws IOException {
		final String nameOfInterface = getNameOfInterface();
		writer.writeLine("public interface ", nameOfInterface, " {");
		if (isNotAllowedToBeNull) {
			writer.writeLine(returnType, " without", capitalizedName, "();");
		}
		writer.writeLine(returnType, " with", capitalizedName, '(', argumentType, ' ', name, ");");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	public void writeImplementationOfInterfaceTo(AutoIndentingWriter writer, String returnType) throws IOException {
		if (isNotAllowedToBeNull) {
			writer.writeLine("@Override");
			writer.writeLine("public final ", returnType, " without", capitalizedName, "() {");
			writer.writeLine("return safelyUseStatement(statement -> {");
			for (int position : positions) {
				writer.writeLine("statement.setNull(", position, ", Types.", nameOfJDBCConstant, ");");
			}
			writer.writeLine("return this;");
			writer.writeLine("});");
			writer.writeLine('}');
			writer.writeEmptyLine();
		}
		writer.writeLine("@Override");
		writer.writeLine("public final ", returnType, " with", capitalizedName, "(", argumentType, " ", name, ") {");
		writer.writeLine("return safelyUseStatement(statement -> {");
		if (isNotAllowedToBeNull) {
			writeStatementSettersTo(writer);
		} else {
			writer.writeLine("if (", name, " == null) {");
			for (int position : positions) {
				writer.writeLine("statement.setNull(", position, ", Types." + nameOfJDBCConstant, ");");
			}
			writer.writeLine("} else {");
			writeStatementSettersTo(writer);
			writer.writeLine("}");
		}
		writer.writeLine("return this;");
		writer.writeLine("});");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	protected void writeStatementSettersTo(AutoIndentingWriter writer) throws IOException {
		for (int position : positions) {
			writer.writeLine("statement.", getSetter(position, name), ";");
		}
	}

	protected String getSetter(int position, String nameOfVariable) {
		return getNameOfMethodInPreparedStatement() + "(" + position + ", " + nameOfVariable + ")";
	}

	protected abstract boolean isNullable();
	protected abstract String getNameOfMethodInPreparedStatement();
	protected abstract String getNameOfJDBCConstant();
	protected abstract String getArgumentType();

}
