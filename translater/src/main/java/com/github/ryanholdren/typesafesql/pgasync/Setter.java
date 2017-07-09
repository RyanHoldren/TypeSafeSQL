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

public enum Setter implements ParameterVisitor<String, RuntimeException> {

	INSTANCE;

	@Override
	public String visit(BigDecimalParameter column) {
		return column.getName();
	}

	@Override
	public String visit(BooleanParameter column) {
		return column.getName();
	}

	@Override
	public String visit(ByteArrayParameter column) {
		return column.getName();
	}

	@Override
	public String visit(DoubleParameter column) {
		return column.getName();
	}

	@Override
	public String visit(InstantParameter column) {
		return column.getName() + " == null ? null : Timestamp.from(" + column.getName() + ')';
	}

	@Override
	public String visit(IntegerParameter column) {
		return column.getName();
	}

	@Override
	public String visit(LocalDateParameter column) {
		return column.getName() + " == null ? null : Date.valueOf(" + column.getName() + ')';
	}

	@Override
	public String visit(LongParameter column) {
		return column.getName();
	}

	@Override
	public String visit(UUIDParameter column) {
		return column.getName() + " == null ? null : " + column.getName() + ".toString()";
	}

	@Override
	public String visit(StringParameter column) {
		return column.getName();
	}

}
