package com.github.ryanholdren.typesafesql.parameters;

public class BooleanParameter extends Parameter {

	public BooleanParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "boolean";
	}

	@Override
	public String getCast() {
		return "BOOLEAN";
	}

	@Override
	public boolean isNullable() {
		return false;
	}

}
