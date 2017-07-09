package com.github.ryanholdren.typesafesql.parameters;

public class IntegerParameter extends Parameter {

	public IntegerParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "int";
	}

	@Override
	public String getCast() {
		return "INTEGER";
	}

	@Override
	public boolean isNullable() {
		return false;
	}

}
