package com.github.ryanholdren.typesafesql.pgasync;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import com.github.ryanholdren.typesafesql.columns.BigDecimalResultColumn;
import com.github.ryanholdren.typesafesql.columns.BooleanResultColumn;
import com.github.ryanholdren.typesafesql.columns.ByteArrayResultColumn;
import com.github.ryanholdren.typesafesql.columns.DoubleResultColumn;
import com.github.ryanholdren.typesafesql.columns.InstantResultColumn;
import com.github.ryanholdren.typesafesql.columns.IntegerResultColumn;
import com.github.ryanholdren.typesafesql.columns.LocalDateResultColumn;
import com.github.ryanholdren.typesafesql.columns.LongResultColumn;
import com.github.ryanholdren.typesafesql.columns.OptionalDoubleResultColumn;
import com.github.ryanholdren.typesafesql.columns.OptionalIntegerResultColumn;
import com.github.ryanholdren.typesafesql.columns.OptionalLongResultColumn;
import com.github.ryanholdren.typesafesql.columns.ResultColumnVisitor;
import com.github.ryanholdren.typesafesql.columns.StringResultColumn;
import com.github.ryanholdren.typesafesql.columns.UUIDResultColumn;
import java.io.IOException;

class ReturnFromRow implements ResultColumnVisitor<Void, IOException> {

	private final AutoIndentingWriter writer;

	public ReturnFromRow(AutoIndentingWriter writer) {
		this.writer = writer;
	}

	@Override
	public Void visit(BigDecimalResultColumn column) throws IOException {
		writer.writeLine("return row.getBigDecimal(0);");
		return null;
	}

	@Override
	public Void visit(BooleanResultColumn column) throws IOException {
		writer.writeLine("return row.getBoolean(0);");
		return null;
	}

	@Override
	public Void visit(ByteArrayResultColumn column) throws IOException {
		writer.writeLine("return row.getBytes(0);");
		return null;
	}

	@Override
	public Void visit(DoubleResultColumn column) throws IOException {
		writer.writeLine("return row.getDouble(0);");
		return null;
	}

	@Override
	public Void visit(InstantResultColumn column) throws IOException {
		writer.writeLine("final Timestamp ", column.getName(), " = row.getTimestamp(0);");
		writer.writeLine("if (", column.getName(), " == null) {");
		writer.writeLine("return null;");
		writer.writeLine("}");
		writer.writeLine("return ", column.getName(), ".toInstant();");
		return null;
	}

	@Override
	public Void visit(IntegerResultColumn column) throws IOException {
		writer.writeLine("return row.getInt(0);");
		return null;
	}

	@Override
	public Void visit(LocalDateResultColumn column) throws IOException {
		writer.writeLine("final Date ", column.getName(), " = row.getDate(0);");
		writer.writeLine("if (", column.getName(), " == null) {");
		writer.writeLine("return null;");
		writer.writeLine("}");
		writer.writeLine("return ", column.getName(), ".toLocalDate();");
		return null;
	}

	@Override
	public Void visit(LongResultColumn column) throws IOException {
		writer.writeLine("return row.getLong(0).longValue();");
		return null;
	}

	@Override
	public Void visit(OptionalDoubleResultColumn column) throws IOException {
		writer.writeLine("final Double ", column.getName(), " = row.getDouble(0);");
		writer.writeLine("if (", column.getName(), " == null) {");
		writer.writeLine("return OptionalDouble.empty();");
		writer.writeLine("}");
		writer.writeLine("return OptionalDouble.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(OptionalIntegerResultColumn column) throws IOException {
		writer.writeLine("final Integer ", column.getName(), " = row.getInt(0);");
		writer.writeLine("if (", column.getName(), " == null) {");
		writer.writeLine("return OptionalInt.empty();");
		writer.writeLine("}");
		writer.writeLine("return  OptionalInt.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(OptionalLongResultColumn column) throws IOException {
		writer.writeLine("final Long ", column.getName(), " = row.getLong(0);");
		writer.writeLine("if (", column.getName(), " == null) {");
		writer.writeLine("return OptionalLong.empty();");
		writer.writeLine("}");
		writer.writeLine("return OptionalLong.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(StringResultColumn column) throws IOException {
		writer.writeLine("return row.getString(0);");
		return null;
	}

	@Override
	public Void visit(UUIDResultColumn column) throws IOException {
		writer.writeLine("final String ", column.getName(), " = row.getString(" + column.getIndex() + ");");
		writer.writeLine("if (", column.getName(), " == null) {");
		writer.writeLine("return null;");
		writer.writeLine("}");
		writer.writeLine("return UUID.fromString(", column.getName(), ");");
		return null;
	}

}