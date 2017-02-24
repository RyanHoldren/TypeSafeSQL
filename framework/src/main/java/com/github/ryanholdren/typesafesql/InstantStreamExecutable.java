package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class InstantStreamExecutable extends ObjectStreamExecutable<Instant> {

	public InstantStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected Instant read(ResultSet results) throws SQLException {
		final Timestamp result = results.getTimestamp(1);
		if (result == null) {
			return null;
		}
		return result.toInstant();
	}

}
