package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.github.ryanholdren.typesafesql.TestTwoByteArrayColumns.Result;
import com.google.common.io.BaseEncoding;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class ByteArrayTest extends FunctionalTest {

	private static final byte[] FIRST_EXPECTED_BYTE_ARRAY = BaseEncoding.base16().decode("DEADBEEF");
	private static final byte[] SECOND_EXPECTED_BYTE_ARRAY = BaseEncoding.base16().decode("4321FEED1234");

	@Test
	public void testByteArrayParameter() throws Throwable {
		TestByteArrayParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_BYTE_ARRAY)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testNullByteArrayParameter() throws Throwable {
		TestNullByteArrayParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(null)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testByteArrayColumn() throws Throwable {
		final byte[] actual = TestByteArrayColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertArrayEquals(FIRST_EXPECTED_BYTE_ARRAY, actual);
	}

	@Test
	public void testTwoByteArrayColumns() throws Throwable {
		final Result actual = TestTwoByteArrayColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.getFirstResult();
		assertNotNull(actual);
		assertArrayEquals(FIRST_EXPECTED_BYTE_ARRAY, actual.getFirstOutput());
		assertArrayEquals(SECOND_EXPECTED_BYTE_ARRAY, actual.getSecondOutput());
	}

}
