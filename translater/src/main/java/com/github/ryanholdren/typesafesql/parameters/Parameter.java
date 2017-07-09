package com.github.ryanholdren.typesafesql.parameters;

import java.util.HashSet;

public abstract class Parameter {

	public static String capitalize(String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	private final String name;
	private final String capitalizedName;
	private final HashSet<Integer> positions = new HashSet<>();

	public Parameter(String argumentName) {
		this.name = argumentName;
		this.capitalizedName = capitalize(argumentName);
	}

	public void addPosition(int position) {
		positions.add(position);
	}

	public String getName() {
		return name;
	}

	public HashSet<Integer> getPositions() {
		return positions;
	}

	public String getCapitalizedName() {
		return capitalizedName;
	}

	public String getNameOfInterface() {
		return "Needs" + capitalizedName;
	}

	public abstract <T,E extends Exception> T accept(ParameterVisitor<T,E> visitor) throws E;
	public abstract String getCast();
	public abstract String getArgumentType();
	public abstract boolean isNullable();

}
