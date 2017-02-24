package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.function.Consumer;

class LocalDateResultColumn extends ResultColumn {

	public LocalDateResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public void writeSetFieldTo(AutoIndentingWriter writer) throws IOException {
		writer.writeLine("final Date ", name, " = results.getDate(", indexInResultSet, ");");
		writer.writeLine("this.", name, " = ", "results.wasNull() ? null : ", name, ".toLocalDate();");
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		action.accept("java.sql.Date");
		action.accept("java.time.LocalDate");
	}

	@Override
	public String getNameOfJavaType() {
		return "LocalDate";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "LocalDateStreamExecutable";
	}

}
