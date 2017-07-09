package com.github.ryanholdren.typesafesql.columns;

public class BigDecimalResultColumn extends ResultColumn {

	public BigDecimalResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getReturnType() {
		return "BigDecimal";
	}

	@Override
	public <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E {
		return visitor.visit(this);
	}

}
