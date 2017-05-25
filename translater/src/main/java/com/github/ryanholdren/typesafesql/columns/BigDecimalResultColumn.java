package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.function.Consumer;

class BigDecimalResultColumn extends ResultColumn {

	public BigDecimalResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public void writeSetFieldFromResultSetTo(AutoIndentingWriter writer) throws IOException {
		writer.writeLine("this.", name, " = results.getBigDecimal(", indexInResultSet, ");");
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		action.accept("java.math.BigDecimal");
	}

	@Override
	public String getNameOfJavaType() {
		return "BigDecimal";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "BigDecimalStreamExecutable";
	}

}
