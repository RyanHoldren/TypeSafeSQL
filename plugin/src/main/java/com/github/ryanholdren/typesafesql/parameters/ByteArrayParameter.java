package com.github.ryanholdren.typesafesql.parameters;

class ByteArrayParameter extends Parameter {

	public ByteArrayParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setBytes";
	}

	@Override
	protected boolean isNullable() {
		return true;
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "VARBINARY";
	}

	@Override
	public String getArgumentType() {
		return "byte[]";
	}

}
