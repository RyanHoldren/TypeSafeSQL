package com.github.ryanholdren.typesafesql.parameters;

public class DoubleParameter extends Parameter {

	public DoubleParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "double";
	}

	@Override
	public String getCast() {
		return "FLOAT";
	}

	@Override
	public boolean isNullable() {
		return false;
	}

}
