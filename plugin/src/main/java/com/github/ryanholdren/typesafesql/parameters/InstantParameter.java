package com.github.ryanholdren.typesafesql.parameters;

class InstantParameter extends Parameter {

	public InstantParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setTimestamp";
	}

	@Override
	protected boolean isNullable() {
		return true;
	}

	@Override
	public String getSetter(int position, String nameOfVariable) {
		return getNameOfMethodInPreparedStatement() + "(" + position + ", Timestamp.from(" + nameOfVariable + "))";
	}

	@Override
	protected String getNameOfJDBCConstant() {
		return "TIMESTAMP";
	}

	@Override
	protected String getArgumentType() {
		return "Instant";
	}

}
