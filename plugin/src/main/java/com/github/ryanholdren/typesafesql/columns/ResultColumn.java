package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;

public abstract class ResultColumn {

	public static String capitalize(String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	protected final int indexInResultSet;
	protected final String name;

	public ResultColumn(int indexInResultSet, String name) {
		this.indexInResultSet = indexInResultSet;
		this.name = name;
	}

	protected abstract String getNameOfJavaType();
	public abstract String getNameOfResultWhenThisIsTheOnlyColumn();

	public void writeFieldTo(AutoIndentingWriter writer) throws IOException {
		final String nameOfJavaType = getNameOfJavaType();
		writer.writeLine("private final ", nameOfJavaType, ' ', name, ';');
	}

	public void writeSetFieldTo(AutoIndentingWriter writer) throws IOException {
		final String nameInResultSetGetter = getNameOfGetterInResultSet();
		writer.writeLine("this.", name, " = ", "results.", nameInResultSetGetter, '(', indexInResultSet, ");");
	}

	protected String getNameOfGetterInResultSet() {
		final String nameOfJavaType = getNameOfJavaType();
		return "get" + capitalize(nameOfJavaType);
	}

	public void writeGetterTo(AutoIndentingWriter writer) throws IOException {
		final String nameOfJavaType = getNameOfJavaType();
		final String capitalizedName = capitalize(name);
		writer.writeLine("public ", nameOfJavaType, " get", capitalizedName, "() {");
		writer.writeLine("return ", name, ';');
		writer.writeLine('}');
	}

}
