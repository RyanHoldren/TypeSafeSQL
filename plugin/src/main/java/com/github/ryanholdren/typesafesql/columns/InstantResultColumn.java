package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;

class InstantResultColumn extends ResultColumn {

	public InstantResultColumn(int indexInResultSet, String name) {
		super(indexInResultSet, name);
	}

	@Override
	public void writeSetFieldTo(AutoIndentingWriter writer) throws IOException {
		writer.writeLine("final Timestamp ", name, " = results.getTimestamp(", indexInResultSet, ");");
		writer.writeLine("this.", name, " = ", "results.wasNull() ? null : ", name, ".toInstant();");
	}

	@Override
	public String getNameOfJavaType() {
		return "Instant";
	}

	@Override
	public String getNameOfResultWhenThisIsTheOnlyColumn() {
		return "InstantStreamExecutable";
	}

}
