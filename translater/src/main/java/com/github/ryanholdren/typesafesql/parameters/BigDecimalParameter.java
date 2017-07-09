package com.github.ryanholdren.typesafesql.parameters;

public class BigDecimalParameter extends Parameter {

	public BigDecimalParameter(String argumentName) {
		super(argumentName);
	}

	@Override
	public <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String getArgumentType() {
		return "BigDecimal";
	}

	@Override
	public String getCast() {
		return "DECIMAL";
	}

	@Override
	public boolean isNullable() {
		return true;
	}

}
