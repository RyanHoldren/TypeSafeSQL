package com.github.ryanholdren.typesafesql.columns;

public class IntegerResultColumn extends ResultColumn {

	public IntegerResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "int";
	}

	@Override
	public String getBoxedReturnType() {
		return "Integer";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}
