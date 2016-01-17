package com.github.ryanholdren.typesafesql.columns;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class ResultColumns {

	private final LinkedHashMap<String, ResultColumn> columns = new LinkedHashMap<>();

	private int position = 1;

	public void add(String name, String nameOfType) {
		if (columns.containsKey(name)) {
			throw new IllegalStateException("Output names must be unique!");
		}
		final ResultColumnType type = ResultColumnType.valueOf(nameOfType);
		final ResultColumn column = type.createResultColumn(position, name);
		columns.put(name, column);
		position ++;
	}

	public String getNameOfExecutorClass() {
		final Iterator<ResultColumn> iterator = columns.values().iterator();
		if (iterator.hasNext()) {
			final ResultColumn column = iterator.next();
			if (iterator.hasNext()) {
				return "ResultStreamExecutable";
			} else {
				return column.getNameOfResultWhenThisIsTheOnlyColumn();
			}
		} else {
			return "UpdateExecutable";
		}
	}

	public void writeResultClassToIfNecessary(AutoIndentingWriter writer) throws IOException {
		if (columns.size() < 2) {
			return;
		}
		writer.writeLine("public static final class Result {");
		writer.writeEmptyLine();
		for (ResultColumn column : columns.values()) {
			column.writeFieldTo(writer);
		}
		writer.writeEmptyLine();
		writer.writeLine("private Result(ResultSet results) throws SQLException {");
		for (ResultColumn column : columns.values()) {
			column.writeSetFieldTo(writer);
		}
		writer.writeLine('}');
		writer.writeEmptyLine();
		for (ResultColumn column : columns.values()) {
			column.writeGetterTo(writer);
			writer.writeEmptyLine();
		}
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine("public static class ResultStreamExecutable extends ObjectStreamExecutable<Result> {");
		writer.writeEmptyLine();
		writer.writeLine("private ResultStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {");
		writer.writeLine("super(sql, connection, handling);");
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine("@Override");
		writer.writeLine("protected final Result read(ResultSet results) throws SQLException {");
		writer.writeLine("return new Result(results);");
		writer.writeLine('}');
		writer.writeEmptyLine();
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

}
