package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.opentable.db.postgres.embedded.EmbeddedPostgreSQLRule;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

public class FunctionalTests {

	@ClassRule
	public static final EmbeddedPostgreSQLRule postgres = new EmbeddedPostgreSQLRule();

	private Connection openConnection() throws Throwable {
		return postgres.getEmbeddedPostgreSQL().getPostgresDatabase().getConnection();
	}

	@Test
	public void testByteArrayVariable() throws Throwable {
		final byte[] expected = new byte[] { 1, 2, 3, 4 };
		final AtomicInteger count = new AtomicInteger();
		TestByteArrayVariable
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(expected)
			.execute()
			.forEach(actual -> {
				Assert.assertArrayEquals(expected, actual);
				count.incrementAndGet();
			});
		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testDoubleVariable() throws Throwable {
		final AtomicInteger count = new AtomicInteger();
		final double expected = Math.PI;
		TestDoubleVariable
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(expected)
			.execute()
			.forEach(actual -> {
				Assert.assertEquals(expected, actual, 1e-10);
				count.incrementAndGet();
			});
		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testInstantVariable() throws Throwable {
		final Instant expected = Instant.now();
		final AtomicInteger count = new AtomicInteger();
		TestInstantVariable
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(expected)
			.execute()
			.forEach(actual -> {
				Assert.assertEquals(expected, actual);
				count.incrementAndGet();
			});
		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testStringVariable() throws Throwable {
		final AtomicInteger count = new AtomicInteger();
		final String expected = "Bees?";
		TestStringVariable
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(expected)
			.execute()
			.forEach(actual -> {
				Assert.assertEquals(expected, actual);
				count.incrementAndGet();
			});
		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testTwoStringVariableOnOneLine() throws Throwable {
		final AtomicInteger count = new AtomicInteger();
		final String firstExpected = "Bees?";
		final String secondExpected = "Here be dragons!";
		TestTwoStringVariablesOnOneLine
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withFirstInput(firstExpected)
			.withSecondInput(secondExpected)
			.execute()
			.forEach(result -> {
				Assert.assertEquals(firstExpected, result.getFirstOutput());
				Assert.assertEquals(secondExpected, result.getSecondOutput());
				count.incrementAndGet();
			});
		Assert.assertEquals(1, count.get());
	}

	@Test
	public void testTwoStringVariableOnSeperateLines() throws Throwable {
		final Queue<String> expected = new LinkedList<>();
		final String firstExpected = "Bees?";
		final String secondExpected = "Here be dragons!";
		expected.add(firstExpected);
		expected.add(secondExpected);
		final AtomicInteger count = new AtomicInteger();
		TestTwoStringVariablesOnSeperateLines
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withFirstInput(firstExpected)
			.withSecondInput(secondExpected)
			.execute()
			.forEach(actual -> {
				Assert.assertEquals(expected.poll(), actual);
			});
		Assert.assertTrue(expected.isEmpty());
	}

	@Test
	public void testUpdate() throws Throwable {
		final int rowsAffected = TestUpdate
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		Assert.assertEquals(0, rowsAffected);
	}

}