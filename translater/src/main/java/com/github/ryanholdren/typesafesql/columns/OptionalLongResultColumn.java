package com.github.ryanholdren.typesafesql.columns;

public class OptionalLongResultColumn extends ResultColumn {

	public OptionalLongResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "OptionalLong";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}