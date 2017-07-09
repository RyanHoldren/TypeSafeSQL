package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoStringColumns.Result;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StringTest extends FunctionalTest {

	private static final String FIRST_EXPECTED_STRING = "Bees?";
	private static final String SECOND_EXPECTED_STRING = "Here be dragons!";

	@Test
	public void testStringParameter() {
		TestStringParameter
			.prepare()
			.withInput(FIRST_EXPECTED_STRING)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testNullStringParameter() {
		TestNullStringParameter
			.prepare()
			.withInput(null)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testStringResult() {
		TestStringColumn
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(FIRST_EXPECTED_STRING);
	}

	@Test
	public void testTwoStringColumns() {
		TestTwoStringColumns
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isExpectedResult);
	}

	@Test
	public void testTwoStringRows() {
		TestTwoStringRows
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValues(FIRST_EXPECTED_STRING, SECOND_EXPECTED_STRING);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_STRING, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_STRING, actual.getSecondOutput());
		return true;
	}

}
