package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.function.Consumer;

class OptionalIntegerResultColumn extends ResultColumn {

	public OptionalIntegerResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public void writeSetFieldFromResultSetTo(AutoIndentingWriter writer) throws IOException {
		writer.writeLine("final int ", name, " = results.getInt(", indexInResultSet, ");");
		writer.writeLine("this.", name, " = ", "results.wasNull() ? OptionalInt.empty() : OptionalInt.of(", name, ");");
	}

	@Override
	public String getNameOfJavaType() {
		return "OptionalInt";
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		action.accept("java.util.OptionalInt");
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "OptionalIntStreamExecutable";
	}

}