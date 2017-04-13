package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.github.ryanholdren.typesafesql.TestTwoOptionalDoubleColumns.Result;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class OptionalDoubleTest extends FunctionalTest {

	private static final double FIRST_EXPECTED_DOUBLE = 3.14;
	private static final double SECOND_EXPECTED_DOUBLE = -93.1311;
	private static final double EPSILON = 1e-10;

	@Test
	public void testOptionalDoubleColumn() throws Throwable {
		final OptionalDouble actual = TestOptionalDoubleColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertEquals(FIRST_EXPECTED_DOUBLE, actual.getAsDouble(), EPSILON);
	}

	@Test
	public void testNullOptionalDoubleResult() throws Throwable {
		final OptionalDouble actual = TestNullOptionalDoubleResult
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertFalse(actual.isPresent());
	}

	@Test
	public void testTwoOptionalDoubleColumns() throws Throwable {
		final Result actual = TestTwoOptionalDoubleColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertNotNull(actual);
		assertEquals(FIRST_EXPECTED_DOUBLE, actual.getFirstOutput().getAsDouble(), EPSILON);
		assertEquals(SECOND_EXPECTED_DOUBLE, actual.getSecondOutput().getAsDouble(), EPSILON);
	}

	@Test
	public void testTwoOptionalDoubleRows() throws Throwable {
		TestTwoOptionalDoubleRows
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute(assertStreamEquals(
				OptionalDouble.of(FIRST_EXPECTED_DOUBLE),
				OptionalDouble.of(SECOND_EXPECTED_DOUBLE)
			));
	}

	private Consumer<Stream<OptionalDouble>> assertStreamEquals(OptionalDouble ... values) {
		return actual -> assertArrayEquals(values, actual.toArray());
	}

}
