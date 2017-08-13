package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoInstantColumns.Result;
import java.time.Instant;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import reactor.test.StepVerifier;

public class InstantTest extends FunctionalTest {

	private static final Instant FIRST_EXPECTED_INSTANT = Instant.parse("2004-10-19T10:23:54Z");
	private static final Instant SECOND_EXPECTED_INSTANT = Instant.parse("2009-05-04T18:12:13Z");

	@Test
	@Ignore("https://github.com/alaisi/postgres-async-driver/issues/45")
	public void testInstantParameter() {
		StepVerifier
			.create(
				TestInstantParameter
					.prepare()
					.withInput(FIRST_EXPECTED_INSTANT)
					.executeIn(db)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullInstantParameter() {
		StepVerifier
			.create(
				TestNullInstantParameter
					.prepare()
					.withInput(null)
					.executeIn(db)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	@Ignore("https://github.com/alaisi/postgres-async-driver/issues/45")
	public void testInstantColumn() {
		StepVerifier
			.create(
				TestInstantColumn
					.prepare()
					.executeIn(db)
			)
			.expectNext(FIRST_EXPECTED_INSTANT)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	@Ignore("https://github.com/alaisi/postgres-async-driver/issues/45")
	public void testTwoInstantColumns() {
		StepVerifier
			.create(
				TestTwoInstantColumns
					.prepare()
					.executeIn(db)
			)
			.expectNextMatches(this::isExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isExpectedResult(Result actual) {
		assertEquals(FIRST_EXPECTED_INSTANT, actual.getFirstOutput());
		assertEquals(SECOND_EXPECTED_INSTANT, actual.getSecondOutput());
		return true;
	}

	@Test
	@Ignore("https://github.com/alaisi/postgres-async-driver/issues/45")
	public void testTwoInstantRows() {
		StepVerifier
			.create(
				TestTwoInstantRows
					.prepare()
					.executeIn(db)
			)
			.expectNext(FIRST_EXPECTED_INSTANT)
			.expectNext(SECOND_EXPECTED_INSTANT)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
