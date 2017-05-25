package com.github.ryanholdren.typesafesql.columns;

class DoubleResultColumn extends ResultColumn {

	public DoubleResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public String getNameOfJavaType() {
		return "double";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "DoubleStreamExecutable";
	}

	@Override
	public String getTypeOfResultMockerWhenThisIsTheOnlyColumn() {
		return "DoubleStreamExecutableMocker";
	}

}
