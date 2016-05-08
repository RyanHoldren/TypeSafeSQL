package com.github.ryanholdren.typesafesql.columns;

class BooleanResultColumn extends ResultColumn {

	public BooleanResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	protected String getNameOfGetterInResultSet() {
		return "getBoolean";
	}

	@Override
	public String getNameOfJavaType() {
		return "boolean";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "BooleanStreamExecutable";
	}

}