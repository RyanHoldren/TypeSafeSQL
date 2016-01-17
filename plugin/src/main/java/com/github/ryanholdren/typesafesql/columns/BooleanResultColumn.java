package com.github.ryanholdren.typesafesql.columns;

class BooleanResultColumn extends ResultColumn {

	public BooleanResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getNameOfJavaType() {
		return "boolean";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		throw new UnsupportedOperationException("If there is only one column in the result, that column cannot be a boolean!");
	}

}