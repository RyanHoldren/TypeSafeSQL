package com.github.ryanholdren.typesafesql.columns;

import static com.google.common.collect.ImmutableSortedSet.of;
import java.util.SortedSet;

public enum ResultColumnImports implements ResultColumnVisitor<SortedSet<String>, RuntimeException> {

	VISITOR;

	@Override
	public SortedSet<String> visit(BigDecimalResultColumn column) {
		return of("java.math.BigDecimal");
	}

	@Override
	public SortedSet<String> visit(BooleanResultColumn column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(ByteArrayResultColumn column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(DoubleResultColumn column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(InstantResultColumn column) {
		return of("java.sql.Timestamp", "java.time.Instant");
	}

	@Override
	public SortedSet<String> visit(IntegerResultColumn column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(LocalDateResultColumn column) {
		return of("java.sql.Date", "java.time.LocalDate");
	}

	@Override
	public SortedSet<String> visit(LongResultColumn column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(OptionalDoubleResultColumn column) {
		return of("java.util.OptionalDouble");
	}

	@Override
	public SortedSet<String> visit(OptionalIntegerResultColumn column) {
		return of("java.util.OptionalInt");
	}

	@Override
	public SortedSet<String> visit(OptionalLongResultColumn column) {
		return of("java.util.OptionalLong");
	}

	@Override
	public SortedSet<String> visit(StringResultColumn column) {
		return of();
	}

	@Override
	public SortedSet<String> visit(UUIDResultColumn column) {
		return of("java.util.UUID", "java.nio.ByteBuffer");
	}

}
