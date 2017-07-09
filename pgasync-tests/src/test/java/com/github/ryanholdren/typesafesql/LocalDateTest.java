package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoLocalDateColumns.Result;
import java.time.LocalDate;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LocalDateTest extends FunctionalTest {

	private static final LocalDate FIRST_EXPECTED_DATE = LocalDate.of(2004, 10, 19);
	private static final LocalDate SECOND_EXPECTED_DATE = LocalDate.of(2009, 5, 4);

	@Test
	public void testLocalDateParameter() {
		TestLocalDateParameter
			.prepare()
			.withInput(FIRST_EXPECTED_DATE)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testNullLocalDateParameter() {
		TestNullLocalDateParameter
			.prepare()
			.withInput(null)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testLocalDateColumn() {
		TestLocalDateColumn
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(FIRST_EXPECTED_DATE);
	}

	@Test
	public void testTwoLocalDateColumns() {
		TestTwoLocalDateColumns
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isExpectedResult);
	}

	@Test
	public void testTwoLocalDateRows() {
		TestTwoLocalDateRows
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValues(FIRST_EXPECTED_DATE, SECOND_EXPECTED_DATE);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_DATE, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_DATE, actual.getSecondOutput());
		return true;
	}

}
