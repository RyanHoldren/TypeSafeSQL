package com.github.ryanholdren.typesafesql.parameters;

public class UUIDParameter extends Parameter {

	public UUIDParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "UUID";
	}

	@Override
	public String getCast() {
		return "UUID";
	}

	@Override
	public boolean isNullable() {
		return true;
	}

}
