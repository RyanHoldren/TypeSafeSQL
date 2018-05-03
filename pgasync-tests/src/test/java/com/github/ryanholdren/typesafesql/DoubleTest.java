package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoDoubleColumns.Result;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import reactor.test.StepVerifier;

public class DoubleTest extends FunctionalTest {

	private static final double FIRST_EXPECTED_DOUBLE = 3.14;
	private static final double SECOND_EXPECTED_DOUBLE = -93.1311;
	private static final double EPSILON = 1e-10;

	@Test
	public void testDoubleParameter() {
		StepVerifier
			.create(
				TestDoubleParameter
					.prepare()
					.withInput(FIRST_EXPECTED_DOUBLE)
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullDoubleParameter() {
		StepVerifier
			.create(
				TestNullDoubleParameter
					.prepare()
					.withoutInput()
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testDoubleColumn() {
		StepVerifier
			.create(
				TestDoubleColumn
					.prepare()
					.executeIn(database)
			)
			.expectNext(FIRST_EXPECTED_DOUBLE)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testTwoDoubleColumns() {
		StepVerifier
			.create(
				TestTwoDoubleColumns
					.prepare()
					.executeIn(database)
			)
			.expectNextMatches(this::isExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_DOUBLE, actual.getFirstOutput(), EPSILON);
		assertEquals(SECOND_EXPECTED_DOUBLE, actual.getSecondOutput(), EPSILON);
		return true;
	}

	@Test
	public void testTwoDoubleRows() {
		StepVerifier
			.create(
				TestTwoDoubleRows
					.prepare()
					.executeIn(database)
			)
			.expectNext(FIRST_EXPECTED_DOUBLE)
			.expectNext(SECOND_EXPECTED_DOUBLE)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
