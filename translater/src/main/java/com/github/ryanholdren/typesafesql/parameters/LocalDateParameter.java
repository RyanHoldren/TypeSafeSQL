package com.github.ryanholdren.typesafesql.parameters;

import java.util.function.Consumer;

class LocalDateParameter extends Parameter {

	public LocalDateParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setDate";
	}

	@Override
	protected boolean isNullable() {
		return true;
	}

	@Override
	public String getSetter(int position, String nameOfVariable) {
		return getNameOfMethodInPreparedStatement() + "(" + position + ", Date.valueOf(" + nameOfVariable + "))";
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "DATE";
	}

	@Override
	public String getArgumentType() {
		return "LocalDate";
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		super.forEachRequiredImport(action, isNotMocking);
		if (isNotMocking) {
			action.accept("java.sql.Date");
		}
		action.accept("java.time.LocalDate");
	}

}
