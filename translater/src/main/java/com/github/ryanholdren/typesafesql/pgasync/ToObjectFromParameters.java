package com.github.ryanholdren.typesafesql.pgasync;

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

public enum ToObjectFromParameters implements ParameterVisitor<String, RuntimeException> {

	TO_OBJECT_FROM_PARAMETERS;

	@Override
	public String visit(BigDecimalParameter column) {
		return "get" + column.getCapitalizedName() + "()";
	}

	@Override
	public String visit(BooleanParameter column) {
		return "get" + column.getCapitalizedName() + "()";
	}

	@Override
	public String visit(ByteArrayParameter column) {
		return "get" + column.getCapitalizedName() + "()";
	}

	@Override
	public String visit(DoubleParameter column) {
		return "get" + column.getCapitalizedName() + "()";
	}

	@Override
	public String visit(InstantParameter column) {
		return "toSql(get" + column.getCapitalizedName() + "())";
	}

	@Override
	public String visit(IntegerParameter column) {
		return "get" + column.getCapitalizedName() + "()";
	}

	@Override
	public String visit(LocalDateParameter column) {
		return "toSql(get" + column.getCapitalizedName() + "())";
	}

	@Override
	public String visit(LongParameter column) {
		return "get" + column.getCapitalizedName() + "()";
	}

	@Override
	public String visit(UUIDParameter column) {
		return "toSql(get" + column.getCapitalizedName() + "())";
	}

	@Override
	public String visit(StringParameter column) {
		return "get" + column.getCapitalizedName() + "()";
	}

}
