package com.github.ryanholdren.typesafesql.columns;

public class LocalDateResultColumn extends ResultColumn {

	public LocalDateResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "LocalDate";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}
