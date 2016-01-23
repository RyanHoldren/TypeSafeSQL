package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.github.ryanholdren.typesafesql.FunctionalTest.openConnection;
import com.github.ryanholdren.typesafesql.TestTwoDoubleColumns.Result;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class DoubleTest extends FunctionalTest {

	private static final double FIRST_EXPECTED_DOUBLE = 3.14;
	private static final double SECOND_EXPECTED_DOUBLE = -93.1311;
	private static final double EPSILON = 1e-10;

	@Test
	public void testDoubleParameter() throws Throwable {
		TestDoubleParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_DOUBLE)
			.execute();
	}

	@Test
	public void testNullDoubleParameter() throws Throwable {
		TestNullDoubleParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withoutInput()
			.execute();
	}

	@Test
	public void testDoubleColumn() throws Throwable {
		final double actual = TestDoubleColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		assertEquals(FIRST_EXPECTED_DOUBLE, actual, EPSILON);
	}

	@Test
	public void testTwoDoubleColumns() throws Throwable {
		final Result actual = TestTwoDoubleColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		assertNotNull(actual);
		assertEquals(FIRST_EXPECTED_DOUBLE, actual.getFirstOutput(), EPSILON);
		assertEquals(SECOND_EXPECTED_DOUBLE, actual.getSecondOutput(), EPSILON);
	}

	@Test
	public void testTwoDoubleRows() throws Throwable {
		TestTwoDoubleRows
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute(assertEquals(FIRST_EXPECTED_DOUBLE, SECOND_EXPECTED_DOUBLE));
	}

	private Consumer<DoubleStream> assertEquals(double ... values) {
		return actual -> assertArrayEquals(values, actual.toArray(), EPSILON);
	}

}
