package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.github.ryanholdren.typesafesql.FunctionalTest.openConnection;
import com.github.ryanholdren.typesafesql.TestTwoInstantColumns.Result;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class InstantTest extends FunctionalTest {

	private static final Instant FIRST_EXPECTED_INSTANT = Instant.parse("2004-10-19T10:23:54Z");
	private static final Instant SECOND_EXPECTED_INSTANT = Instant.parse("2009-05-04T18:12:13Z");

	@Test
	public void testInstantParameter() throws Throwable {
		TestInstantParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_INSTANT)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testNullInstantParameter() throws Throwable {
		TestNullInstantParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(null)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testInstantColumn() throws Throwable {
		final Instant actual = TestInstantColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertEquals(FIRST_EXPECTED_INSTANT, actual);
	}

	@Test
	public void testTwoInstantColumns() throws Throwable {
		final Result actual = TestTwoInstantColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertNotNull(actual);
		assertEquals(FIRST_EXPECTED_INSTANT, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_INSTANT, actual.getSecondOutput());
	}

	@Test
	public void testTwoInstantRows() throws Throwable {
		TestTwoInstantRows
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute(assertEquals(FIRST_EXPECTED_INSTANT, SECOND_EXPECTED_INSTANT));
	}

	private Consumer<Stream<Instant>> assertEquals(Instant ... values) {
		return actual -> assertArrayEquals(values, actual.toArray());
	}

}
