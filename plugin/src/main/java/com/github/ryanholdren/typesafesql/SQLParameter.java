package com.github.ryanholdren.typesafesql;

import java.util.HashSet;

public class SQLParameter {

	private static String capitalize(String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	private final SQLParameterType type;
	private final String nameInUpperCamelCase;
	private final String nameInLowerCamelCase;
	private final HashSet<Integer> positions = new HashSet<>();

	public SQLParameter(String nameInLowerCamelCase, SQLParameterType type, int position) {
		this.nameInLowerCamelCase = nameInLowerCamelCase;
		this.nameInUpperCamelCase = capitalize(nameInLowerCamelCase);
		this.type = type;
		this.positions.add(position);
	}

	public void addPosition(int position) {
		positions.add(position);
	}

	public SQLParameterType getType() {
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

	public String getDefinition() {
		return "final " + type.getNameOfJavaType() + ' ' + nameInLowerCamelCase;
	}

	public String getNameOfInterface() {
		return "Needs" + nameInUpperCamelCase;
	}

	public String getNameOfMethod() {
		return "with" + nameInUpperCamelCase;
	}

}
