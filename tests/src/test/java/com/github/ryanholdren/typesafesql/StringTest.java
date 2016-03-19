package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.github.ryanholdren.typesafesql.FunctionalTest.openConnection;
import com.github.ryanholdren.typesafesql.TestTwoStringColumns.Result;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class StringTest extends FunctionalTest {

	private static final String FIRST_EXPECTED_STRING = "Bees?";
	private static final String SECOND_EXPECTED_STRING = "Here be dragons!";

	@Test
	public void testStringParameter() throws Throwable {
		TestStringParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_STRING)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testNullStringParameter() throws Throwable {
		TestNullStringParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(null)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testStringResult() throws Throwable {
		final String actual = TestStringColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertEquals(FIRST_EXPECTED_STRING, actual);
	}

	@Test
	public void testTwoStringColumns() throws Throwable {
		final Result actual = TestTwoStringColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertNotNull(actual);
		assertEquals(FIRST_EXPECTED_STRING, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_STRING, actual.getSecondOutput());
	}

	@Test
	public void testTwoStringRows() throws Throwable {
		TestTwoStringRows
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute(assertEquals(FIRST_EXPECTED_STRING, SECOND_EXPECTED_STRING));
	}

	private Consumer<Stream<String>> assertEquals(String ... values) {
		return actual -> assertArrayEquals(values, actual.toArray());
	}

}
