package com.github.ryanholdren.typesafesql.parameters;

public class InstantParameter extends Parameter {

	public InstantParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "Instant";
	}

	@Override
	public String getCast() {
		return "TIMESTAMP WITH TIME ZONE";
	}

	@Override
	public boolean isNullable() {
		return true;
	}

}
