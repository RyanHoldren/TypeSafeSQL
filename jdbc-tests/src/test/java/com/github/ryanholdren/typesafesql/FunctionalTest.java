package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.opentable.db.postgres.junit.EmbeddedPostgresRules.preparedDatabase;
import com.opentable.db.postgres.junit.PreparedDbRule;
import java.sql.Connection;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Rule;
import org.junit.Test;

public class FunctionalTest {

	@Rule
	public final PreparedDbRule rule = preparedDatabase(source -> {
		CreateAssertFunction
			.using(source.getConnection(), CLOSE_WHEN_DONE)
			.getNumberOfRowsAffected();
	});

	protected Connection openConnection() throws Throwable {
		return rule.getTestDatabase().getConnection();
	}

	@Test
	public void testAssert() throws Throwable {
		try {
			TestAssertShouldFail
				.using(openConnection(), CLOSE_WHEN_DONE)
				.getNumberOfRowsAffected();
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
			.getNumberOfRowsAffected();
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
