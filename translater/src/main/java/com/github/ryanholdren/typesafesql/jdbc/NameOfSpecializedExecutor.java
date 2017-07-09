package com.github.ryanholdren.typesafesql.jdbc;

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

public enum NameOfSpecializedExecutor implements ResultColumnVisitor<String, RuntimeException> {

	VISITOR;

	@Override
	public String visit(BigDecimalResultColumn column) {
		return "BigDecimalStreamExecutable";
	}

	@Override
	public String visit(BooleanResultColumn column) {
		return "BooleanStreamExecutable";
	}

	@Override
	public String visit(ByteArrayResultColumn column) {
		return "ByteArrayStreamExecutable";
	}

	@Override
	public String visit(DoubleResultColumn column) {
		return "DoubleStreamExecutable";
	}

	@Override
	public String visit(InstantResultColumn column) {
		return "InstantStreamExecutable";
	}

	@Override
	public String visit(IntegerResultColumn column) {
		return "IntStreamExecutable";
	}

	@Override
	public String visit(LocalDateResultColumn column) {
		return "LocalDateStreamExecutable";
	}

	@Override
	public String visit(LongResultColumn column) {
		return "LongStreamExecutable";
	}

	@Override
	public String visit(OptionalDoubleResultColumn column) {
		return "OptionalDoubleStreamExecutable";
	}

	@Override
	public String visit(OptionalIntegerResultColumn column) {
		return "OptionalIntStreamExecutable";
	}

	@Override
	public String visit(OptionalLongResultColumn column) {
		return "OptionalLongStreamExecutable";
	}

	@Override
	public String visit(StringResultColumn column) {
		return "StringStreamExecutable";
	}

	@Override
	public String visit(UUIDResultColumn column) {
		return "UUIDStreamExecutable";
	}

}
