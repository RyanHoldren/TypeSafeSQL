package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoLocalDateColumns.Result;
import java.time.LocalDate;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import reactor.test.StepVerifier;

public class LocalDateTest extends FunctionalTest {

	private static final LocalDate FIRST_EXPECTED_DATE = LocalDate.of(2004, 10, 19);
	private static final LocalDate SECOND_EXPECTED_DATE = LocalDate.of(2009, 5, 4);

	@Test
	public void testLocalDateParameter() {
		StepVerifier
			.create(
				TestLocalDateParameter
					.prepare()
					.withInput(FIRST_EXPECTED_DATE)
					.executeIn(db)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullLocalDateParameter() {
		StepVerifier
			.create(
				TestNullLocalDateParameter
					.prepare()
					.withInput(null)
					.executeIn(db)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testLocalDateColumn() {
		StepVerifier
			.create(
				TestLocalDateColumn
					.prepare()
					.executeIn(db)
			)
			.expectNext(FIRST_EXPECTED_DATE)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testTwoLocalDateColumns() {
		StepVerifier
			.create(
				TestTwoLocalDateColumns
					.prepare()
					.executeIn(db)
			)
			.expectNextMatches(this::isExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_DATE, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_DATE, actual.getSecondOutput());
		return true;
	}

	@Test
	public void testTwoLocalDateRows() {
		StepVerifier
			.create(
				TestTwoLocalDateRows
					.prepare()
					.executeIn(db)
			)
			.expectNext(FIRST_EXPECTED_DATE)
			.expectNext(SECOND_EXPECTED_DATE)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
