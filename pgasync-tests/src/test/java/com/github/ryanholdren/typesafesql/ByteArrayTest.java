package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestTwoByteArrayColumns.Result;
import com.google.common.io.BaseEncoding;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

public class ByteArrayTest extends FunctionalTest {

	private static final byte[] FIRST_EXPECTED_BYTE_ARRAY = BaseEncoding.base16().decode("DEADBEEF");
	private static final byte[] SECOND_EXPECTED_BYTE_ARRAY = BaseEncoding.base16().decode("4321FEED1234");

	@Test
	public void testByteArrayParameter() {
		TestByteArrayParameter
			.prepare()
			.withInput(FIRST_EXPECTED_BYTE_ARRAY)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testNullByteArrayParameter() {
		TestNullByteArrayParameter
			.prepare()
			.withInput(null)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testByteArrayColumn() {
		TestByteArrayColumn
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isFirstExpectedResult);
	}

	@Test
	public void testTwoByteArrayColumns() {
		TestTwoByteArrayColumns
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(this::isExpectedResult);
	}

	@Test
	public void testTwoByteArrayRows() {
		TestTwoByteArrayRows
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValueCount(2)
			.assertValueAt(0, this::isFirstExpectedResult)
			.assertValueAt(1, this::isSecondExpectedResult);
	}

	private boolean isExpectedResult(Result actual) {
		isFirstExpectedResult(actual.getFirstOutput());
		isSecondExpectedResult(actual.getSecondOutput());
		return true;
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
