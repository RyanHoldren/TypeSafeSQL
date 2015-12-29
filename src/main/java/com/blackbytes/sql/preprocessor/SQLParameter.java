package com.blackbytes.sql.preprocessor;

import java.util.HashSet;

public class SQLParameter {

	private final String type;
	private final String nameInUpperCamelCase;
	private final String nameInLowerCamelCase;
	private final HashSet<Integer> positions = new HashSet<>();

	public SQLParameter(String nameInLowerCamelCase, String type, int position) {
		this.nameInLowerCamelCase = nameInLowerCamelCase;
		this.nameInUpperCamelCase = capitalize(nameInLowerCamelCase);
		this.type = type;
		this.positions.add(position);
	}

	public void addPosition(int position) {
		positions.add(position);
	}

	public String getType() {
		return type;
	}

	public HashSet<Integer> getPositions() {
		return positions;
	}

	public String getNameInUpperCamelCase() {
		return nameInUpperCamelCase;
	}

	public String getNameInLowerCamelCase() {
		return nameInLowerCamelCase;
	}

	public String getNameOfJavaType() {
		switch (type) {
			case "VARCHAR":
				return "String";
			case "DOUBLE":
				return "double";
			case "INTEGER":
				return "int";
			case "VARBINARY":
				return "byte[]";
			default:
				throw new UnsupportedOperationException();
		}
	}

	private static String capitalize(String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

}
