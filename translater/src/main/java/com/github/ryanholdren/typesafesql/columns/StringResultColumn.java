package com.github.ryanholdren.typesafesql.columns;

class StringResultColumn extends ResultColumn {

	public StringResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getNameOfJavaType() {
		return "String";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "StringStreamExecutable";
	}

}
