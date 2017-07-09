package com.github.ryanholdren.typesafesql.jdbc;

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

class ExtractFromResultSet implements ResultColumnVisitor<Void, IOException> {

	private final AutoIndentingWriter writer;

	public ExtractFromResultSet(AutoIndentingWriter writer) {
		this.writer = writer;
	}

	@Override
	public Void visit(BigDecimalResultColumn column) throws IOException {
		writer.writeLine("this.", column.getName(), " = results.getBigDecimal(", column.getPosition(), ");");
		return null;
	}

	@Override
	public Void visit(BooleanResultColumn column) throws IOException {
		writer.writeLine("this.", column.getName(), " = results.getBoolean(", column.getPosition(), ");");
		return null;
	}

	@Override
	public Void visit(ByteArrayResultColumn column) throws IOException {
		writer.writeLine("this.", column.getName(), " = results.getBytes(", column.getPosition(), ");");
		return null;
	}

	@Override
	public Void visit(DoubleResultColumn column) throws IOException {
		writer.writeLine("this.", column.getName(), " = results.getDouble(", column.getPosition(), ");");
		return null;
	}

	@Override
	public Void visit(InstantResultColumn column) throws IOException {
		writer.writeLine("final Timestamp ", column.getName(), " = results.getTimestamp(", column.getPosition(), ");");
		writer.writeLine("this.", column.getName(), " = ", "results.wasNull() ? null : ", column.getName(), ".toInstant();");
		return null;
	}

	@Override
	public Void visit(IntegerResultColumn column) throws IOException {
		writer.writeLine("this.", column.getName(), " = results.getInt(", column.getPosition(), ");");
		return null;
	}

	@Override
	public Void visit(LocalDateResultColumn column) throws IOException {
		writer.writeLine("final Date ", column.getName(), " = results.getDate(", column.getPosition(), ");");
		writer.writeLine("this.", column.getName(), " = ", "results.wasNull() ? null : ", column.getName(), ".toLocalDate();");
		return null;
	}

	@Override
	public Void visit(LongResultColumn column) throws IOException {
		writer.writeLine("this.", column.getName(), " = results.getLong(", column.getPosition(), ");");
		return null;
	}

	@Override
	public Void visit(OptionalDoubleResultColumn column) throws IOException {
		writer.writeLine("final double ", column.getName(), " = results.getDouble(", column.getPosition(), ");");
		writer.writeLine("this.", column.getName(), " = ", "results.wasNull() ? OptionalDouble.empty() : OptionalDouble.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(OptionalIntegerResultColumn column) throws IOException {
		writer.writeLine("final int ", column.getName(), " = results.getInt(", column.getPosition(), ");");
		writer.writeLine("this.", column.getName(), " = ", "results.wasNull() ? OptionalInt.empty() : OptionalInt.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(OptionalLongResultColumn column) throws IOException {
		writer.writeLine("final long ", column.getName(), " = results.getLong(", column.getPosition(), ");");
		writer.writeLine("this.", column.getName(), " = ", "results.wasNull() ? OptionalLong.empty() : OptionalLong.of(", column.getName(), ");");
		return null;
	}

	@Override
	public Void visit(StringResultColumn column) throws IOException {
		writer.writeLine("this.", column.getName(), " = results.getString(", column.getPosition(), ");");
		return null;
	}

	@Override
	public Void visit(UUIDResultColumn column) throws IOException {
		writer.writeLine("this.", column.getName(), " = (UUID) results.getObject(", column.getPosition(), ");");
		return null;
	}

}
