package com.github.ryanholdren.typesafesql.columns;

class ByteArrayResultColumn extends ResultColumn {

	public ByteArrayResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	protected String getNameOfGetterInResultSet() {
		return "getBytes";
	}

	@Override
	public String getNameOfJavaType() {
		return "byte[]";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "ByteArrayStreamExecutable";
	}

}
