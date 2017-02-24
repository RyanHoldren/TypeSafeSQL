package com.github.ryanholdren.typesafesql.parameters;

import java.util.function.Consumer;

class UUIDParameter extends Parameter {

	public UUIDParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	protected String getNameOfMethodInPreparedStatement() {
		return "setObject";
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
	public String getArgumentType() {
		return "UUID";
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		super.forEachRequiredImport(action, isNotMocking);
		action.accept("java.util.UUID");
	}

}
