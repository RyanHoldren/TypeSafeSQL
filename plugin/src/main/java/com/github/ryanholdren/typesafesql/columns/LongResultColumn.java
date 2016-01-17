package com.github.ryanholdren.typesafesql.columns;

class LongResultColumn extends ResultColumn {

	public LongResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getNameOfJavaType() {
		return "long";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "LongStreamExecutable";
	}

}