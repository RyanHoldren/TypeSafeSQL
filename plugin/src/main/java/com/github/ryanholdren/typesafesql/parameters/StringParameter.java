package com.github.ryanholdren.typesafesql.parameters;

class StringParameter extends Parameter {

	public StringParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setString";
	}

	@Override
	protected boolean isNullable() {
		return true;
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "VARCHAR";
	}

	@Override
	protected String getArgumentType() {
		return "String";
	}

}