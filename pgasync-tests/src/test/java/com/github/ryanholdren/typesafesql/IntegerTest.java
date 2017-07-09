package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoIntegerColumns.Result;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class IntegerTest extends FunctionalTest {

	private static final int FIRST_EXPECTED_INTEGER = 42;
	private static final int SECOND_EXPECTED_INTEGER = -51123;

	@Test
	public void testIntegerParameter() {
		TestIntegerParameter
			.prepare()
			.withInput(FIRST_EXPECTED_INTEGER)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testNullIntegerParameter() {
		TestNullIntegerParameter
			.prepare()
			.withoutInput()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testIntegerColumn() {
		TestIntegerColumn
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(FIRST_EXPECTED_INTEGER);
	}

	@Test
	public void testTwoIntegerColumns() {
		TestTwoIntegerColumns
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isExpectedResult);
	}

	@Test
	public void testTwoIntRows() {
		TestTwoIntegerRows
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValues(FIRST_EXPECTED_INTEGER, SECOND_EXPECTED_INTEGER);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_INTEGER, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_INTEGER, actual.getSecondOutput());
		return true;
	}

}
