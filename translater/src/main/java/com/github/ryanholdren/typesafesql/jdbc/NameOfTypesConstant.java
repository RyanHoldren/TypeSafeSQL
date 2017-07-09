package com.github.ryanholdren.typesafesql.jdbc;

import com.github.ryanholdren.typesafesql.parameters.BigDecimalParameter;
import com.github.ryanholdren.typesafesql.parameters.BooleanParameter;
import com.github.ryanholdren.typesafesql.parameters.ByteArrayParameter;
import com.github.ryanholdren.typesafesql.parameters.DoubleParameter;
import com.github.ryanholdren.typesafesql.parameters.InstantParameter;
import com.github.ryanholdren.typesafesql.parameters.IntegerParameter;
import com.github.ryanholdren.typesafesql.parameters.LocalDateParameter;
import com.github.ryanholdren.typesafesql.parameters.LongParameter;
import com.github.ryanholdren.typesafesql.parameters.ParameterVisitor;
import com.github.ryanholdren.typesafesql.parameters.StringParameter;
import com.github.ryanholdren.typesafesql.parameters.UUIDParameter;

public enum NameOfTypesConstant implements ParameterVisitor<String, RuntimeException> {

	VISITOR;

	@Override
	public String visit(BigDecimalParameter column) throws RuntimeException {
		return "DECIMAL";
	}

	@Override
	public String visit(BooleanParameter column) throws RuntimeException {
		return "BOOLEAN";
	}

	@Override
	public String visit(ByteArrayParameter column) throws RuntimeException {
		return "VARBINARY";
	}

	@Override
	public String visit(DoubleParameter column) throws RuntimeException {
		return "DOUBLE";
	}

	@Override
	public String visit(InstantParameter column) throws RuntimeException {
		return "TIMESTAMP";
	}

	@Override
	public String visit(IntegerParameter column) throws RuntimeException {
		return "INTEGER";
	}

	@Override
	public String visit(LocalDateParameter column) throws RuntimeException {
		return "DATE";
	}

	@Override
	public String visit(LongParameter column) throws RuntimeException {
		return "BIGINT";
	}

	@Override
	public String visit(StringParameter column) throws RuntimeException {
		return "VARCHAR";
	}

	@Override
	public String visit(UUIDParameter column) throws RuntimeException {
		return "OTHER";
	}

}
