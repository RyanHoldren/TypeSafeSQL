package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoOptionalDoubleColumns.Result;
import java.util.OptionalDouble;
import static java.util.OptionalDouble.empty;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class OptionalDoubleTest extends FunctionalTest {

	private static final double FIRST_EXPECTED_DOUBLE = 3.14;
	private static final double SECOND_EXPECTED_DOUBLE = -93.1311;
	private static final double EPSILON = 1e-10;

	@Test
	public void testOptionalDoubleColumn() {
		TestOptionalDoubleColumn
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isFirstExpectedResult);
	}

	@Test
	public void testNullOptionalDoubleResult() {
		TestNullOptionalDoubleResult
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(empty());
	}

	@Test
	public void testTwoOptionalDoubleColumns() {
		TestTwoOptionalDoubleColumns
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isExpectedResult);
	}

	@Test
	public void testTwoOptionalDoubleRows() {
		TestTwoOptionalDoubleRows
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValueCount(2)
			.assertValueAt(0, this::isFirstExpectedResult)
			.assertValueAt(1, this::isSecondExpectedResult);
	}

	private boolean isExpectedResult(Result actual) {
		isFirstExpectedResult(actual.getFirstOutput());
		isSecondExpectedResult(actual.getSecondOutput());
		return true;
	}

	private boolean isFirstExpectedResult(OptionalDouble actual) {
		assertEquals(FIRST_EXPECTED_DOUBLE, actual.getAsDouble(), EPSILON);
		return true;
	}

	private boolean isSecondExpectedResult(OptionalDouble actual) {
		assertEquals(SECOND_EXPECTED_DOUBLE, actual.getAsDouble(), EPSILON);
		return true;
	}

}
