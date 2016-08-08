package com.github.ryanholdren.typesafesql.parameters;

import java.util.function.Consumer;

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
	public String getArgumentType() {
		return "Instant";
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		super.forEachRequiredImport(action, isNotMocking);
		if (isNotMocking) {
			action.accept("java.sql.Timestamp");
		}
		action.accept("java.time.Instant");
	}

}
