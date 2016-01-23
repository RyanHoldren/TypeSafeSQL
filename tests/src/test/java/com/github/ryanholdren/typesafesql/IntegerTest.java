package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.github.ryanholdren.typesafesql.FunctionalTest.openConnection;
import com.github.ryanholdren.typesafesql.TestTwoIntegerColumns.Result;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class IntegerTest extends FunctionalTest {

	private static final int FIRST_EXPECTED_INTEGER = 42;
	private static final int SECOND_EXPECTED_INTEGER = -51123;

	@Test
	public void testIntegerParameter() throws Throwable {
		TestIntegerParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_INTEGER)
			.execute();
	}

	@Test
	public void testNullIntegerParameter() throws Throwable {
		TestNullIntegerParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withoutInput()
			.execute();
	}

	@Test
	public void testIntegerColumn() throws Throwable {
		final int actual = TestIntegerColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		assertEquals(FIRST_EXPECTED_INTEGER, actual);
	}

	@Test
	public void testTwoIntegerColumns() throws Throwable {
		final Result actual = TestTwoIntegerColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		assertNotNull(actual);
		assertEquals(FIRST_EXPECTED_INTEGER, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_INTEGER, actual.getSecondOutput());
	}

	@Test
	public void testTwoIntRows() throws Throwable {
		TestTwoIntegerRows
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute(assertEquals(FIRST_EXPECTED_INTEGER, SECOND_EXPECTED_INTEGER));
	}

	private Consumer<IntStream> assertEquals(int ... values) {
		return actual -> assertArrayEquals(values, actual.toArray());
	}

}
