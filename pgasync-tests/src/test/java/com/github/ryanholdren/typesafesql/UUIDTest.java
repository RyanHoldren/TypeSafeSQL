package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoUUIDColumns.Result;
import java.util.UUID;
import static java.util.UUID.fromString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UUIDTest extends FunctionalTest {

	private static final UUID FIRST_EXPECTED_UUID = fromString("0b5a63d6-f5ab-45a4-a6f1-1c37913ceb39");
	private static final UUID SECOND_EXPECTED_UUID = fromString("91bb1220-8136-4fea-89a9-c973f0ba2ed6");

	@Test
	public void testUUIDParameter() {
		TestUUIDParameter
			.prepare()
			.withInput(FIRST_EXPECTED_UUID)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testNullUUIDParameter() {
		TestNullUUIDParameter
			.prepare()
			.withInput(null)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testUUIDColumn() {
		TestUUIDColumn
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(FIRST_EXPECTED_UUID);
	}

	@Test
	public void testTwoUUIDColumns() {
		TestTwoUUIDColumns
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isExpectedResult);
	}

	@Test
	public void testTwoUUIDRows() {
		TestTwoUUIDRows
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValues(FIRST_EXPECTED_UUID, SECOND_EXPECTED_UUID);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_UUID, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_UUID, actual.getSecondOutput());
		return true;
	}

}
