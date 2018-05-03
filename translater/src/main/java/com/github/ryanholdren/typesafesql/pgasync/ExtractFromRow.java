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

class ExtractFromRow implements ResultColumnVisitor<Void, IOException> {

	private final AutoIndentingWriter writer;

	public ExtractFromRow(AutoIndentingWriter writer) {
		this.writer = writer;
	}

	@Override
	public Void visit(BigDecimalResultColumn column) throws IOException {
		writer.writeLine("return row.getBigDecimal(" + column.getIndex() + ");");
		return null;
	}

	@Override
	public Void visit(BooleanResultColumn column) throws IOException {
		writer.writeLine("return row.getBoolean(" + column.getIndex() + ");");
		return null;
	}

	@Override
	public Void visit(ByteArrayResultColumn column) throws IOException {
		writer.writeLine("return row.getBytes(" + column.getIndex() + ");");
		return null;
	}

	@Override
	public Void visit(DoubleResultColumn column) throws IOException {
		writer.writeLine("return row.getDouble(" + column.getIndex() + ");");
		return null;
	}

	@Override
	public Void visit(InstantResultColumn column) throws IOException {
		writer.writeLine("final Timestamp ", column.getName(), " = row.getTimestamp(" + column.getIndex() + ");");
		writer.writeLine("return ", column.getName(), " == null ? null : ", column.getName(), ".toInstant();");
		return null;
	}

	@Override
	public Void visit(IntegerResultColumn column) throws IOException {
		writer.writeLine("return row.getInt(" + column.getIndex() + ");");
		return null;
	}

	@Override
	public Void visit(LocalDateResultColumn column) throws IOException {
		writer.writeLine("final Date ", column.getName(), " = row.getDate(" + column.getIndex() + ");");
		writer.writeLine("return ", column.getName(), " == null ? null : ", column.getName(), ".toLocalDate();");
		return null;
	}

	@Override
	public Void visit(LongResultColumn column) throws IOException {
		writer.writeLine("return row.getLong(" + column.getIndex() + ").longValue();");
		return null;
	}

	@Override
	public Void visit(OptionalDoubleResultColumn column) throws IOException {
		writer.writeLine("final Double ", column.getName(), " = row.getDouble(" + column.getIndex() + ");");
		writer.writeLine("return ", column.getName(), " == null ? OptionalDouble.empty() : OptionalDouble.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(OptionalIntegerResultColumn column) throws IOException {
		writer.writeLine("final Integer ", column.getName(), " = row.getInt(" + column.getIndex() + ");");
		writer.writeLine("return ", column.getName(), " == null ? OptionalInt.empty() : OptionalInt.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(OptionalLongResultColumn column) throws IOException {
		writer.writeLine("final Long ", column.getName(), " = row.getLong(" + column.getIndex() + ");");
		writer.writeLine("return ", column.getName(), " == null ? OptionalLong.empty() : OptionalLong.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(StringResultColumn column) throws IOException {
		writer.writeLine("return row.getString(" + column.getIndex() + ");");
		return null;
	}

	@Override
	public Void visit(UUIDResultColumn column) throws IOException {
		writer.writeLine("final String ", column.getName(), " = row.getString(" + column.getIndex() + ");");
		writer.writeLine("return ", column.getName(), " == null ? null : UUID.fromString(", column.getName(), ");");
		return null;
	}

}