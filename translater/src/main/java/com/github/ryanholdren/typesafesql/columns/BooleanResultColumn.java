package com.github.ryanholdren.typesafesql.columns;

public class BooleanResultColumn extends ResultColumn {

	public BooleanResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "boolean";
	}

	@Override
	public String getBoxedReturnType() {
		return "Boolean";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}