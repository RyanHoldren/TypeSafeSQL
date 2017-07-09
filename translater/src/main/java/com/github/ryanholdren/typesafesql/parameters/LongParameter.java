package com.github.ryanholdren.typesafesql.parameters;

public class LongParameter extends Parameter {

	public LongParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "long";
	}

	@Override
	public String getCast() {
		return "BIGINT";
	}

	@Override
	public boolean isNullable() {
		return false;
	}

}
