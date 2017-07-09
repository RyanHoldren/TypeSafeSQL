package com.github.ryanholdren.typesafesql.columns;

public class ByteArrayResultColumn extends ResultColumn {

	public ByteArrayResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "byte[]";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}
