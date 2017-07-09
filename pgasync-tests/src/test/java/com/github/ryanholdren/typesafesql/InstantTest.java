package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoInstantColumns.Result;
import java.time.Instant;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class InstantTest extends FunctionalTest {

	private static final Instant FIRST_EXPECTED_INSTANT = Instant.parse("2004-10-19T10:23:54Z");
	private static final Instant SECOND_EXPECTED_INSTANT = Instant.parse("2009-05-04T18:12:13Z");

	@Test
	public void testInstantParameter() {
		TestInstantParameter
			.prepare()
			.withInput(FIRST_EXPECTED_INSTANT)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testNullInstantParameter() {
		TestNullInstantParameter
			.prepare()
			.withInput(null)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testInstantColumn() {
		TestInstantColumn
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(FIRST_EXPECTED_INSTANT);
	}

	@Test
	public void testTwoInstantColumns() {
		TestTwoInstantColumns
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isExpectedResult);
	}

	@Test
	public void testTwoInstantRows() {
		TestTwoInstantRows
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValues(FIRST_EXPECTED_INSTANT, SECOND_EXPECTED_INSTANT);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_INSTANT, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_INSTANT, actual.getSecondOutput());
		return true;
	}

}
