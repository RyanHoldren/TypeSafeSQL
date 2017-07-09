package com.github.ryanholdren.typesafesql;

import com.github.pgasync.ConnectionPool;
import com.github.pgasync.ConnectionPoolBuilder;
import static com.opentable.db.postgres.junit.EmbeddedPostgresRules.preparedDatabase;
import com.opentable.db.postgres.junit.PreparedDbRule;
import java.sql.Connection;
import static java.util.concurrent.TimeUnit.SECONDS;
import javax.sql.DataSource;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

public class FunctionalTest {

	@Rule
	public final PreparedDbRule rule = preparedDatabase(source -> {
		try (Connection connection = source.getConnection()) {
			connection.createStatement().execute(Setup.SQL);
		}
	});

	private ConnectionPool toDb(DataSource source) {
		if (source instanceof PGSimpleDataSource) {
			return toDb((PGSimpleDataSource) source);
		} else {
			throw new IllegalStateException();
		}
	}

	private ConnectionPool toDb(PGSimpleDataSource source) {
		return new ConnectionPoolBuilder()
			.hostname("localhost")
			.port(source.getPortNumber())
			.database(source.getDatabaseName())
			.username(source.getUser())
			.password(source.getPassword())
			.validationQuery("SET TIMEZONE TO UTC")
			.poolSize(100)
			.build();
	}

	protected ConnectionPool db;

	@Before
	public void createConnectionPool() {
		db = toDb(rule.getTestDatabase());
	}

	@Test
	public void testAssert() {
		TestAssertShouldFail
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertError(error -> {
				assertTrue(error.getMessage().contains("Assertion failed!"));
				return true;
			});
	}

	@Test
	public void testUpdate() {
		TestUpdate
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors()
			.assertValue(0);
	}

	@After
	public void closeConnectionPool() throws Exception {
		// db.close();
	}

}
