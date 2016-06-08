package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class InstantStreamExecutable extends ObjectStreamExecutable<Instant> {

	public InstantStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected Instant read(ResultSet results) throws SQLException {
		return results.getTimestamp(1).toInstant();
	}

}
