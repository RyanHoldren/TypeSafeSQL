package com.github.ryanholdren.typesafesql.parameters;

class DoubleParameter extends Parameter {

	public DoubleParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setDouble";
	}

	@Override
	protected boolean isNullable() {
		return false;
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "DOUBLE";
	}

	@Override
	protected String getArgumentType() {
		return "double";
	}

}
