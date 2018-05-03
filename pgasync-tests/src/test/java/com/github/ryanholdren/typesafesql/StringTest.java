package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoStringColumns.Result;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import reactor.test.StepVerifier;

public class StringTest extends FunctionalTest {

	private static final String FIRST_EXPECTED_STRING = "Bees?";
	private static final String SECOND_EXPECTED_STRING = "Here be dragons!";

	@Test
	public void testStringParameter() {
		StepVerifier
			.create(
				TestStringParameter
					.prepare()
					.withInput(FIRST_EXPECTED_STRING)
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullStringParameter() {
		StepVerifier
			.create(
				TestNullStringParameter
					.prepare()
					.withInput(null)
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testStringResult() {
		StepVerifier
			.create(
				TestStringColumn
					.prepare()
					.executeIn(database)
			)
			.expectNext(FIRST_EXPECTED_STRING)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testTwoStringColumns() {
		StepVerifier
			.create(
				TestTwoStringColumns
					.prepare()
					.executeIn(database)
			)
			.expectNextMatches(this::isExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_STRING, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_STRING, actual.getSecondOutput());
		return true;
	}

	@Test
	public void testTwoStringRows() {
		StepVerifier
			.create(
				TestTwoStringRows
					.prepare()
					.executeIn(database)
			)
			.expectNext(FIRST_EXPECTED_STRING)
			.expectNext(SECOND_EXPECTED_STRING)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
