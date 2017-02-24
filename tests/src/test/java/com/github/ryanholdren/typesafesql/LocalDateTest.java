package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.github.ryanholdren.typesafesql.FunctionalTest.openConnection;
import com.github.ryanholdren.typesafesql.TestTwoLocalDateColumns.Result;
import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class LocalDateTest extends FunctionalTest {

	private static final LocalDate FIRST_EXPECTED_DATE = LocalDate.of(2004, 10, 19);
	private static final LocalDate SECOND_EXPECTED_DATE = LocalDate.of(2009, 5, 4);

	@Test
	public void testLocalDateParameter() throws Throwable {
		TestLocalDateParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_DATE)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testNullLocalDateParameter() throws Throwable {
		TestNullLocalDateParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(null)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testLocalDateColumn() throws Throwable {
		final LocalDate actual = TestLocalDateColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertEquals(FIRST_EXPECTED_DATE, actual);
	}

	@Test
	public void testTwoLocalDateColumns() throws Throwable {
		final Result actual = TestTwoLocalDateColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertNotNull(actual);
		assertEquals(FIRST_EXPECTED_DATE, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_DATE, actual.getSecondOutput());
	}

	@Test
	public void testTwoLocalDateRows() throws Throwable {
		TestTwoLocalDateRows
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute(assertEquals(FIRST_EXPECTED_DATE, SECOND_EXPECTED_DATE));
	}

	private Consumer<Stream<LocalDate>> assertEquals(LocalDate... values) {
		return actual -> assertArrayEquals(values, actual.toArray());
	}

}
