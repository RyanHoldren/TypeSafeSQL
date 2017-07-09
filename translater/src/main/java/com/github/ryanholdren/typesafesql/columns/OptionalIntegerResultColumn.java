package com.github.ryanholdren.typesafesql.columns;

public class OptionalIntegerResultColumn extends ResultColumn {

	public OptionalIntegerResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "OptionalInt";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}