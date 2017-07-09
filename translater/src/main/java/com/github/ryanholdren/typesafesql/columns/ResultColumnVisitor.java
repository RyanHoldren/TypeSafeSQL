package com.github.ryanholdren.typesafesql.columns;

public interface ResultColumnVisitor<T, E extends Exception> {
	T visit(BigDecimalResultColumn column) throws E;
	T visit(BooleanResultColumn column) throws E;
	T visit(ByteArrayResultColumn column) throws E;
	T visit(DoubleResultColumn column) throws E;
	T visit(InstantResultColumn column) throws E;
	T visit(IntegerResultColumn column) throws E;
	T visit(LocalDateResultColumn column) throws E;
	T visit(LongResultColumn column) throws E;
	T visit(OptionalDoubleResultColumn column) throws E;
	T visit(OptionalIntegerResultColumn column) throws E;
	T visit(OptionalLongResultColumn column) throws E;
	T visit(UUIDResultColumn column) throws E;
	T visit(StringResultColumn column) throws E;
}
