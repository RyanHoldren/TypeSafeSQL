package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanStreamExecutable extends ObjectStreamExecutable<Boolean> {

	public BooleanStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected final Boolean read(ResultSet results) throws SQLException {
		final boolean result = results.getBoolean(1);
		if (results.wasNull()) {
			return null;
		}
		return result;
	}

}