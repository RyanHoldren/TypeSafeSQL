package com.github.ryanholdren.typesafesql;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import reactor.test.StepVerifier;

public class ComplexTest extends FunctionalTest {

	private static final int FIRST_EXPECTED_INTEGER = 42;
	private static final String FIRST_EXPECTED_STRING = "apple";
	private static final int SECOND_EXPECTED_INTEGER = 8;
	private static final String SECOND_EXPECTED_STRING = "sauce";

	@Test
	public void testTwoComplexRows() {
		StepVerifier
			.create(
				database
					.testTwoComplexRows()
					.withFirstInput(FIRST_EXPECTED_INTEGER)
					.withSecondInput(FIRST_EXPECTED_STRING)
					.withThirdInput(SECOND_EXPECTED_INTEGER)
					.withFourthInput(SECOND_EXPECTED_STRING)
					.prepare()
					.execute()
			)
			.expectNextMatches(actual -> {
				assertEquals(FIRST_EXPECTED_INTEGER, actual.getFirstOutput());
				assertEquals(FIRST_EXPECTED_STRING, actual.getSecondOutput());
				return true;
			})
			.expectNextMatches(actual -> {
				assertEquals(SECOND_EXPECTED_INTEGER, actual.getFirstOutput());
				assertEquals(SECOND_EXPECTED_STRING, actual.getSecondOutput());
				return true;
			})
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testZeroComplexRows() throws InterruptedException {
		StepVerifier
			.create(database.testZeroComplexRows())
			.expectComplete()
			.verify(TIMEOUT);
	}

}
