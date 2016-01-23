package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.github.ryanholdren.typesafesql.FunctionalTest.openConnection;
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
			.execute();
	}

	@Test
	public void testNullByteArrayParameter() throws Throwable {
		TestNullByteArrayParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(null)
			.execute();
	}

	@Test
	public void testByteArrayColumn() throws Throwable {
		final byte[] actual = TestByteArrayColumn
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		assertArrayEquals(FIRST_EXPECTED_BYTE_ARRAY, actual);
	}

	@Test
	public void testTwoByteArrayColumns() throws Throwable {
		final Result actual = TestTwoByteArrayColumns
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		assertNotNull(actual);
		assertArrayEquals(FIRST_EXPECTED_BYTE_ARRAY, actual.getFirstOutput());
		assertArrayEquals(SECOND_EXPECTED_BYTE_ARRAY, actual.getSecondOutput());
	}

}
