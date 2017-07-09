package com.github.ryanholdren.typesafesql.columns;

public class UUIDResultColumn extends ResultColumn {

	public UUIDResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "UUID";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}
