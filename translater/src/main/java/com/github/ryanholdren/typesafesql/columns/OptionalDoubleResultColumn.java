package com.github.ryanholdren.typesafesql.columns;

public class OptionalDoubleResultColumn extends ResultColumn {

	public OptionalDoubleResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "OptionalDouble";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}