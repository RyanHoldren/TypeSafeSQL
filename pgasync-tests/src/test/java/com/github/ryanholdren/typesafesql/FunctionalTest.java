package com.github.ryanholdren.typesafesql;

import com.github.pgasync.ConnectionPool;
import com.github.pgasync.ConnectionPoolBuilder;
import static com.opentable.db.postgres.junit.EmbeddedPostgresRules.preparedDatabase;
import com.opentable.db.postgres.junit.PreparedDbRule;
import java.sql.Connection;
import java.time.Duration;
import static java.time.Duration.ofSeconds;
import javax.sql.DataSource;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import reactor.test.StepVerifier;

public class FunctionalTest {

	protected static final Duration TIMEOUT = ofSeconds(1);

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

	private ConnectionPool db;
	protected Database database;

	@Before
	public void createConnectionPool() {
		db = toDb(rule.getTestDatabase());
		database = () -> db;
	}

	@Test
	public void testAssert() {
		StepVerifier
			.create(database.testAssertShouldFail())
			.expectErrorMatches(error -> {
				assertTrue(error.getMessage().contains("Assertion failed!"));
				return true;
			})
			.verify(TIMEOUT);
	}

	@Test
	public void testUpdate() {
		StepVerifier
			.create(database.testUpdate())
			.expectComplete()
			.verify(TIMEOUT);
	}

	@After
	public void closeConnectionPool() throws Exception {
		db.close();
	}

}
