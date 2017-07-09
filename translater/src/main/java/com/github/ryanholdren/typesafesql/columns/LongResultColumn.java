package com.github.ryanholdren.typesafesql.columns;

public class LongResultColumn extends ResultColumn {

	public LongResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "long";
	}

	@Override
	public String getBoxedReturnType() {
		return "Long";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}