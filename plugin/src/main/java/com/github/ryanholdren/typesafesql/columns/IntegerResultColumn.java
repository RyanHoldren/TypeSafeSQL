package com.github.ryanholdren.typesafesql.columns;

class IntegerResultColumn extends ResultColumn {

	public IntegerResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getNameOfJavaType() {
		return "int";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "IntStreamExecutable";
	}

}
