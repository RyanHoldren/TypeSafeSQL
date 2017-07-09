package com.github.ryanholdren.typesafesql.parameters;

public class ByteArrayParameter extends Parameter {

	public ByteArrayParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "byte[]";
	}

	@Override
	public String getCast() {
		return "bytea";
	}

	@Override
	public boolean isNullable() {
		return true;
	}

}
