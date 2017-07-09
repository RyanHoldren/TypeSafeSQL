package com.github.ryanholdren.typesafesql.columns;

public abstract class ResultColumn {

	protected final int position;
	protected final String name;

	public ResultColumn(int position, String name) {
		this.position = position;
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public int getIndex() {
		return position - 1;
	}

	public String getName() {
		return name;
	}

	public abstract String getReturnType();

	public String getBoxedReturnType() {
		return getReturnType();
	}

	public abstract <T,E extends Exception> T accept(ResultColumnVisitor<T,E> visitor) throws E;

}
