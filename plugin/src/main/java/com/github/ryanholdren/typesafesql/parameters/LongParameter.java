package com.github.ryanholdren.typesafesql.parameters;

class LongParameter extends Parameter {

	public LongParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setLong";
	}

	@Override
	protected boolean isNullable() {
		return false;
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "BIGINT";
	}

	@Override
	protected String getArgumentType() {
		return "long";
	}

}
