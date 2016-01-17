package com.github.ryanholdren.typesafesql.parameters;

class IntegerParameter extends Parameter {

	public IntegerParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setInt";
	}

	@Override
	protected boolean isNullable() {
		return false;
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "INTEGER";
	}

	@Override
	protected String getArgumentType() {
		return "int";
	}

}
