package com.github.ryanholdren.typesafesql;

public enum SQLParameterType {

	VARCHAR("String", "setString"),
	DOUBLE("double", "setDouble"),
	INTEGER("int", "setInt"),
	VARBINARY("byte[]", "setBytes");

	private final String nameOfJavaClass;
	private final String nameOfMethodInPreparedStatement;

	private SQLParameterType(String nameOfJavaClass, String nameOfMethodInPreparedStatement) {
		this.nameOfJavaClass = nameOfJavaClass;
		this.nameOfMethodInPreparedStatement = nameOfMethodInPreparedStatement;
	}

	public String getNameOfJavaClass() {
		return nameOfJavaClass;
	}

	public String getNameOfMethodInPreparedStatement() {
		return nameOfMethodInPreparedStatement;
	}

}
