package com.github.ryanholdren.typesafesql.parameters;

public interface ParameterVisitor<T,E extends Exception> {
	T visit(BigDecimalParameter column) throws E;
	T visit(BooleanParameter column) throws E;
	T visit(ByteArrayParameter column) throws E;
	T visit(DoubleParameter column) throws E;
	T visit(InstantParameter column) throws E;
	T visit(IntegerParameter column) throws E;
	T visit(LocalDateParameter column) throws E;
	T visit(LongParameter column) throws E;
	T visit(UUIDParameter column) throws E;
	T visit(StringParameter column) throws E;
}
