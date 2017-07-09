package com.github.ryanholdren.typesafesql.columns;

public class StringResultColumn extends ResultColumn {

	public StringResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "String";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}
