package com.github.ryanholdren.typesafesql.parameters;

public class LocalDateParameter extends Parameter {

	public LocalDateParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "LocalDate";
	}

	@Override
	public String getCast() {
		return "DATE";
	}

	@Override
	public boolean isNullable() {
		return true;
	}

}
