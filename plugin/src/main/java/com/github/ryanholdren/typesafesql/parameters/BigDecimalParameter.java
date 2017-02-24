package com.github.ryanholdren.typesafesql.parameters;

import java.util.function.Consumer;

class BigDecimalParameter extends Parameter {

	public BigDecimalParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setBigDecimal";
	}

	@Override
	protected boolean isNullable() {
		return true;
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "DECIMAL";
	}

	@Override
	public String getArgumentType() {
		return "BigDecimal";
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		super.forEachRequiredImport(action, isNotMocking);
		action.accept("java.math.BigDecimal");
	}

}
