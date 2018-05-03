package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoOptionalDoubleColumns.Result;
import java.util.OptionalDouble;
import static java.util.OptionalDouble.empty;
import static java.util.OptionalDouble.of;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import reactor.test.StepVerifier;

public class OptionalDoubleTest extends FunctionalTest {

	private static final double FIRST_EXPECTED_DOUBLE = 3.14;
	private static final double SECOND_EXPECTED_DOUBLE = -93.1311;
	private static final double EPSILON = 1e-10;

	@Test
	public void testOptionalDoubleColumn() {
		StepVerifier
			.create(
				TestOptionalDoubleColumn
					.prepare()
					.executeIn(database)
			)
			.expectNext(of(FIRST_EXPECTED_DOUBLE))
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullOptionalDoubleResult() {
		StepVerifier
			.create(
				TestNullOptionalDoubleResult
					.prepare()
					.executeIn(database)
			)
			.expectNext(empty())
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testTwoOptionalDoubleColumns() {
		StepVerifier
			.create(
				TestTwoOptionalDoubleColumns
					.prepare()
					.executeIn(database)
			)
			.expectNextMatches(this::isExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isExpectedResult(Result actual) {
		isFirstExpectedResult(actual.getFirstOutput());
		isSecondExpectedResult(actual.getSecondOutput());
		return true;
	}

	@Test
	public void testTwoOptionalDoubleRows() {
		StepVerifier
			.create(
				TestTwoOptionalDoubleRows
					.prepare()
					.executeIn(database)
			)
			.expectNextMatches(this::isFirstExpectedResult)
			.expectNextMatches(this::isSecondExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isFirstExpectedResult(OptionalDouble actual) {
		assertEquals(FIRST_EXPECTED_DOUBLE, actual.getAsDouble(), EPSILON);
		return true;
	}

	private boolean isSecondExpectedResult(OptionalDouble actual) {
		assertEquals(SECOND_EXPECTED_DOUBLE, actual.getAsDouble(), EPSILON);
		return true;
	}

}
