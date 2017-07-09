package com.github.ryanholdren.typesafesql.parameters;

public class StringParameter extends Parameter {

	public StringParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "String";
	}

	@Override
	public String getCast() {
		return "VARCHAR";
	}

	@Override
	public boolean isNullable() {
		return true;
	}

}