package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoDoubleColumns.Result;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DoubleTest extends FunctionalTest {

	private static final double FIRST_EXPECTED_DOUBLE = 3.14;
	private static final double SECOND_EXPECTED_DOUBLE = -93.1311;
	private static final double EPSILON = 1e-10;

	@Test
	public void testDoubleParameter() {
		TestDoubleParameter
			.prepare()
			.withInput(FIRST_EXPECTED_DOUBLE)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testNullDoubleParameter() {
		TestNullDoubleParameter
			.prepare()
			.withoutInput()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testDoubleColumn() {
		TestDoubleColumn
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(FIRST_EXPECTED_DOUBLE);
	}

	@Test
	public void testTwoDoubleColumns() {
		TestTwoDoubleColumns
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isExpectedResult);
	}

	@Test
	public void testTwoDoubleRows() {
		TestTwoDoubleRows
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValues(FIRST_EXPECTED_DOUBLE, SECOND_EXPECTED_DOUBLE);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_DOUBLE, actual.getFirstOutput(), EPSILON);
		assertEquals(SECOND_EXPECTED_DOUBLE, actual.getSecondOutput(), EPSILON);
		return true;
	}

}
