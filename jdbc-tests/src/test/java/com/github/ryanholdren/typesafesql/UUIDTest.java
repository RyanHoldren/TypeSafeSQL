package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.github.ryanholdren.typesafesql.TestTwoUUIDColumns.Result;
import java.util.UUID;
import static java.util.UUID.fromString;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class UUIDTest extends FunctionalTest {

	private static final UUID FIRST_EXPECTED_UUID = fromString("0b5a63d6-f5ab-45a4-a6f1-1c37913ceb39");
	private static final UUID SECOND_EXPECTED_UUID = fromString("91bb1220-8136-4fea-89a9-c973f0ba2ed6");

	@Test
	public void testUUIDParameter() throws Throwable {
		TestUUIDParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_UUID)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testNullUUIDParameter() throws Throwable {
		TestNullUUIDParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(null)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testUUIDColumn() throws Throwable {
		final UUID actual = TestUUIDColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertEquals(FIRST_EXPECTED_UUID, actual);
	}

	@Test
	public void testTwoUUIDColumns() throws Throwable {
		final Result actual = TestTwoUUIDColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertNotNull(actual);
		assertEquals(FIRST_EXPECTED_UUID, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_UUID, actual.getSecondOutput());
	}

	@Test
	public void testTwoUUIDRows() throws Throwable {
		TestTwoUUIDRows
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute(assertEquals(FIRST_EXPECTED_UUID, SECOND_EXPECTED_UUID));
	}

	private Consumer<Stream<UUID>> assertEquals(UUID... values) {
		return actual -> assertArrayEquals(values, actual.toArray());
	}

}
