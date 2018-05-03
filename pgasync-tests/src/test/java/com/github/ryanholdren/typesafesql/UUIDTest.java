package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoUUIDColumns.Result;
import java.util.UUID;
import static java.util.UUID.fromString;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import reactor.test.StepVerifier;

public class UUIDTest extends FunctionalTest {

	private static final UUID FIRST_EXPECTED_UUID = fromString("0b5a63d6-f5ab-45a4-a6f1-1c37913ceb39");
	private static final UUID SECOND_EXPECTED_UUID = fromString("91bb1220-8136-4fea-89a9-c973f0ba2ed6");

	@Test
	public void testUUIDParameter() {
		StepVerifier
			.create(
				TestUUIDParameter
					.prepare()
					.withInput(FIRST_EXPECTED_UUID)
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullUUIDParameter() {
		StepVerifier
			.create(
				TestNullUUIDParameter
					.prepare()
					.withInput(null)
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testUUIDColumn() {
		StepVerifier
			.create(
				TestUUIDColumn
					.prepare()
					.executeIn(database)
			)
			.expectNext(FIRST_EXPECTED_UUID)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testTwoUUIDColumns() {
		StepVerifier
			.create(
				TestTwoUUIDColumns
					.prepare()
					.executeIn(database)
			)
			.expectNextMatches(this::isExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_UUID, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_UUID, actual.getSecondOutput());
		return true;
	}

	@Test
	public void testTwoUUIDRows() {
		StepVerifier
			.create(
				TestTwoUUIDRows
					.prepare()
					.executeIn(database)
			)
			.expectNext(FIRST_EXPECTED_UUID)
			.expectNext(SECOND_EXPECTED_UUID)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
