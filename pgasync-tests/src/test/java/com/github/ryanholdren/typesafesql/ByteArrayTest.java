package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoByteArrayColumns.Result;
import com.google.common.io.BaseEncoding;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import reactor.test.StepVerifier;

public class ByteArrayTest extends FunctionalTest {

	private static final byte[] FIRST_EXPECTED_BYTE_ARRAY = BaseEncoding.base16().decode("DEADBEEF");
	private static final byte[] SECOND_EXPECTED_BYTE_ARRAY = BaseEncoding.base16().decode("4321FEED1234");

	@Test
	public void testByteArrayParameter() {
		StepVerifier
			.create(
				TestByteArrayParameter
					.prepare()
					.withInput(FIRST_EXPECTED_BYTE_ARRAY)
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullByteArrayParameter() {
		StepVerifier
			.create(
				TestNullByteArrayParameter
					.prepare()
					.withInput(null)
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testByteArrayColumn() {
		StepVerifier
			.create(
				TestByteArrayColumn
					.prepare()
					.executeIn(database)
			)
			.expectNextMatches(this::isFirstExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testTwoByteArrayColumns() {
		StepVerifier
			.create(
				TestTwoByteArrayColumns
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
	public void testTwoByteArrayRows() {
		StepVerifier
			.create(
				TestTwoByteArrayRows
					.prepare()
					.executeIn(database)
			)
			.expectNextMatches(this::isFirstExpectedResult)
			.expectNextMatches(this::isSecondExpectedResult)
			.expectComplete()
			.verify(TIMEOUT);
	}

	private boolean isFirstExpectedResult(byte[] actual) {
		assertArrayEquals(FIRST_EXPECTED_BYTE_ARRAY, actual);
		return true;
	}

	private boolean isSecondExpectedResult(byte[] actual) {
		assertArrayEquals(SECOND_EXPECTED_BYTE_ARRAY, actual);
		return true;
	}

}
