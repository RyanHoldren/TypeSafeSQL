package com.github.ryanholdren.typesafesql.columns;

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
import static com.google.common.collect.ImmutableSortedSet.of;
import java.util.SortedSet;

public enum ParameterImports implements ParameterVisitor<SortedSet<String>, RuntimeException> {

	VISITOR;

	@Override
	public SortedSet<String> visit(BigDecimalParameter column) {
		return of("java.math.BigDecimal");
	}

	@Override
	public SortedSet<String> visit(BooleanParameter column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(ByteArrayParameter column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(DoubleParameter column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(InstantParameter column) {
		return of("java.sql.Timestamp", "java.time.Instant");
	}

	@Override
	public SortedSet<String> visit(IntegerParameter column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(LocalDateParameter column) {
		return of("java.sql.Date", "java.time.LocalDate");
	}

	@Override
	public SortedSet<String> visit(LongParameter column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(StringParameter column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(UUIDParameter column) {
		return of("java.util.UUID");
	}

}
