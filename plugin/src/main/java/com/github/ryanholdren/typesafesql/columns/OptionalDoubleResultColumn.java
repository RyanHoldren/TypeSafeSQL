package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.function.Consumer;

class OptionalDoubleResultColumn extends ResultColumn {

	public OptionalDoubleResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public void writeSetFieldFromResultSetTo(AutoIndentingWriter writer) throws IOException {
		writer.writeLine("final double ", name, " = results.getDouble(", indexInResultSet, ");");
		writer.writeLine("this.", name, " = ", "results.wasNull() ? OptionalDouble.empty() : OptionalDouble.of(", name, ");");
	}

	@Override
	public String getNameOfJavaType() {
		return "OptionalDouble";
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		action.accept("java.util.OptionalDouble");
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "OptionalDoubleStreamExecutable";
	}

}