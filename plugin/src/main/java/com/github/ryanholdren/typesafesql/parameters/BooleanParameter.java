package com.github.ryanholdren.typesafesql.parameters;

class BooleanParameter extends Parameter {

	public BooleanParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setBoolean";
	}

	@Override
	protected boolean isNullable() {
		return false;
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "BOOLEAN";
	}

	@Override
	protected String getArgumentType() {
		return "boolean";
	}

}
