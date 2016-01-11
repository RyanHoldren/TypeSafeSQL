package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.google.common.io.BaseEncoding;
import com.opentable.db.postgres.embedded.EmbeddedPostgreSQLRule;
import java.sql.Connection;
import java.time.Instant;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class FunctionalTests {

	@ClassRule
	public static final EmbeddedPostgreSQLRule postgres = new EmbeddedPostgreSQLRule();

	private Connection openConnection() throws Throwable {
		return postgres.getEmbeddedPostgreSQL().getPostgresDatabase().getConnection();
	}

	private static final byte[] FIRST_EXPECTED_BYTE_ARRAY = BaseEncoding.base16().decode("DEADBEEF");
	private static final byte[] SECOND_EXPECTED_BYTE_ARRAY = BaseEncoding.base16().decode("4321FEED1234");

	@Test
	public void testByteArrayParameter() throws Throwable {
		testByteArrayParameter(FIRST_EXPECTED_BYTE_ARRAY);
	}

	@Test
	public void testNullByteArrayParameter() throws Throwable {
		testByteArrayParameter(null);
	}

	private void testByteArrayParameter(byte[] expected) throws Throwable {
		TestByteArrayParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(expected)
			.execute();
	}

	@Test
	public void testByteArrayResult() throws Throwable {
		final byte[] actual = TestByteArrayResult
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.get();
		Assert.assertArrayEquals(FIRST_EXPECTED_BYTE_ARRAY, actual);
	}

	@Test
	public void testTwoByteArrayResults() throws Throwable {
		final TestTwoByteArrayResults.Result actual = TestTwoByteArrayResults
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.get();
		Assert.assertNotNull(actual);
		Assert.assertArrayEquals(FIRST_EXPECTED_BYTE_ARRAY, actual.getFirstOutput());
		Assert.assertArrayEquals(SECOND_EXPECTED_BYTE_ARRAY, actual.getSecondOutput());
	}

	private static final double FIRST_EXPECTED_DOUBLE = 3.14;
	private static final double SECOND_EXPECTED_DOUBLE = -93.1311;

	@Test
	public void testDoubleParameter() throws Throwable {
		TestDoubleParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_DOUBLE)
			.execute();
	}

	@Test
	public void testNullDoubleParameter() throws Throwable {
		TestDoubleParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withoutInput()
			.execute();
	}

	@Test
	public void testDoubleResult() throws Throwable {
		final double actual = TestDoubleResult
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.getAsDouble();
		Assert.assertEquals(FIRST_EXPECTED_DOUBLE, actual, 1e-10);
	}

	@Test
	public void testTwoDoubleResults() throws Throwable {
		final TestTwoDoubleResults.Result actual = TestTwoDoubleResults
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.get();
		Assert.assertNotNull(actual);
		Assert.assertEquals(FIRST_EXPECTED_DOUBLE, actual.getFirstOutput(), 1e-10);
		Assert.assertEquals(SECOND_EXPECTED_DOUBLE, actual.getSecondOutput(), 1e-10);
	}

	private static final Instant FIRST_EXPECTED_INSTANT = Instant.parse("2004-10-19T10:23:54Z");
	private static final Instant SECOND_EXPECTED_INSTANT = Instant.parse("2009-05-04T18:12:13Z");

	@Test
	public void testInstantParameter() throws Throwable {
		testInstantParameter(FIRST_EXPECTED_INSTANT);
	}

	@Test
	public void testNullInstantParameter() throws Throwable {
		testInstantParameter(null);
	}

	private void testInstantParameter(Instant expected) throws Throwable {
		TestInstantParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(expected)
			.execute();
	}

	@Test
	public void testInstantResult() throws Throwable {
		final Instant actual = TestInstantResult
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.get();
		Assert.assertEquals(FIRST_EXPECTED_INSTANT, actual);
	}

	@Test
	public void testTwoInstantResults() throws Throwable {
		final TestTwoInstantResults.Result actual = TestTwoInstantResults
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.get();
		Assert.assertNotNull(actual);
		Assert.assertEquals(FIRST_EXPECTED_INSTANT, actual.getFirstOutput());
		Assert.assertEquals(SECOND_EXPECTED_INSTANT, actual.getSecondOutput());
	}

	private static final int FIRST_EXPECTED_INTEGER = 42;
	private static final int SECOND_EXPECTED_INTEGER = -51123;

	@Test
	public void testIntegerParameter() throws Throwable {
		TestIntegerParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(FIRST_EXPECTED_INTEGER)
			.execute();
	}

	@Test
	public void testNullIntegerParameter() throws Throwable {
		TestIntegerParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withoutInput()
			.execute();
	}

	@Test
	public void testIntegerResult() throws Throwable {
		final int actual = TestIntegerResult
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.getAsInt();
		Assert.assertEquals(FIRST_EXPECTED_INTEGER, actual);
	}

	@Test
	public void testTwoIntegerResults() throws Throwable {
		final TestTwoIntegerResults.Result actual = TestTwoIntegerResults
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.get();
		Assert.assertNotNull(actual);
		Assert.assertEquals(FIRST_EXPECTED_INTEGER, actual.getFirstOutput());
		Assert.assertEquals(SECOND_EXPECTED_INTEGER, actual.getSecondOutput());
	}

	private static final String FIRST_EXPECTED_STRING = "Bees?";
	private static final String SECOND_EXPECTED_STRING = "Here be dragons!";

	@Test
	public void testStringParameter() throws Throwable {
		testStringParameter(FIRST_EXPECTED_STRING);
	}

	@Test
	public void testNullStringParameter() throws Throwable {
		testStringParameter(null);
	}

	private void testStringParameter(String expected) throws Throwable {
		TestStringParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(expected)
			.execute();
	}

	@Test
	public void testStringResult() throws Throwable {
		final String actual = TestStringResult
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.get();
		Assert.assertEquals(FIRST_EXPECTED_STRING, actual);
	}

	@Test
	public void testTwoStringResults() throws Throwable {
		final TestTwoStringResults.Result actual = TestTwoStringResults
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute()
			.findFirst()
			.get();
		Assert.assertNotNull(actual);
		Assert.assertEquals(FIRST_EXPECTED_STRING, actual.getFirstOutput());
		Assert.assertEquals(SECOND_EXPECTED_STRING, actual.getSecondOutput());
	}

	@Test
	public void testUpdate() throws Throwable {
		final int rowsAffected = TestUpdate
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		Assert.assertEquals(0, rowsAffected);
	}

}