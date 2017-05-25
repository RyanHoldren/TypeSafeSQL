package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.function.Consumer;

class OptionalLongResultColumn extends ResultColumn {

	public OptionalLongResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public void writeSetFieldFromResultSetTo(AutoIndentingWriter writer) throws IOException {
		writer.writeLine("final long ", name, " = results.getLong(", indexInResultSet, ");");
		writer.writeLine("this.", name, " = ", "results.wasNull() ? OptionalLong.empty() : OptionalLong.of(", name, ");");
	}

	@Override
	public String getNameOfJavaType() {
		return "OptionalLong";
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		action.accept("java.util.OptionalLong");
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "OptionalLongStreamExecutable";
	}

}