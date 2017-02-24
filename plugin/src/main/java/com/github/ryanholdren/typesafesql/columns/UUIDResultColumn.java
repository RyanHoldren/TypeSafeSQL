package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.function.Consumer;

class UUIDResultColumn extends ResultColumn {

	public UUIDResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public void writeSetFieldTo(AutoIndentingWriter writer) throws IOException {
		writer.writeLine("this.", name, " = (UUID) results.getObject(", indexInResultSet, ");");
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		action.accept("java.util.UUID");
	}

	@Override
	public String getNameOfJavaType() {
		return "UUID";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "UUIDStreamExecutable";
	}

}
