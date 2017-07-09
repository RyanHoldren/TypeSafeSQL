package com.github.ryanholdren.typesafesql.columns;

public class DoubleResultColumn extends ResultColumn {

	public DoubleResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "double";
	}

	@Override
	public String getBoxedReturnType() {
		return "Double";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}
