package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoIntegerColumns.Result;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import reactor.test.StepVerifier;

public class IntegerTest extends FunctionalTest {

	private static final int FIRST_EXPECTED_INTEGER = 42;
	private static final int SECOND_EXPECTED_INTEGER = -51123;

	@Test
	public void testIntegerParameter() {
		StepVerifier
			.create(
				TestIntegerParameter
					.prepare()
					.withInput(FIRST_EXPECTED_INTEGER)
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullIntegerParameter() {
		StepVerifier
			.create(
				TestNullIntegerParameter
					.prepare()
					.withoutInput()
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testIntegerColumn() {
		StepVerifier
			.create(
				TestIntegerColumn
					.prepare()
					.executeIn(database)
			)
			.expectNext(FIRST_EXPECTED_INTEGER)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testTwoIntegerColumns() {
		StepVerifier
			.create(
				TestTwoIntegerColumns
					.prepare()
					.executeIn(database)
			)
			.expectNextMatches(this::isExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_INTEGER, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_INTEGER, actual.getSecondOutput());
		return true;
	}

	@Test
	public void testZeroIntegerRows() {
		StepVerifier
			.create(
				TestZeroIntegerRows
					.prepare()
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testTwoIntegerRows() {
		StepVerifier
			.create(
				TestTwoIntegerRows
					.prepare()
					.executeIn(database)
			)
			.expectNext(FIRST_EXPECTED_INTEGER)
			.expectNext(SECOND_EXPECTED_INTEGER)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
