package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import com.opentable.db.postgres.embedded.EmbeddedPostgreSQL;
import java.io.IOException;
import java.sql.Connection;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class FunctionalTest {

	private static final EmbeddedPostgreSQL postgres;

	static {
		try {
			final Runtime runtime = Runtime.getRuntime();
			postgres = EmbeddedPostgreSQL.start();
			runtime.addShutdownHook(new Thread(() -> {
				try {
					postgres.close();
				} catch (IOException exception) {
					throw new RuntimeException(exception);
				}
			}));
			CreateAssertFunction
				.using(openConnection(), CLOSE_WHEN_DONE)
				.execute();
		} catch (Throwable exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	protected static Connection openConnection() throws Throwable {
		return postgres.getPostgresDatabase().getConnection();
	}

	@Test
	public void testAssert() throws Throwable {
		try {
			TestAssertShouldFail
				.using(openConnection(), CLOSE_WHEN_DONE)
				.execute();
			fail("Should have thrown an exception!");
		} catch (RuntimeSQLException exception) {
			final String message = exception.getMessage();
			assertTrue(message.contains("Assertion failed!"));
		}
	}

	@Test
	public void testUpdate() throws Throwable {
		final int rowsAffected = TestUpdate
			.using(openConnection(), CLOSE_WHEN_DONE)
			.execute();
		assertEquals(0, rowsAffected);
	}

	protected static <T> Stream<T> toStream(T ... values) {
		final Stream.Builder<T> builder = Stream.builder();
		for (T value : values) {
			builder.add(value);
		}
		return builder.build();
	}

}
