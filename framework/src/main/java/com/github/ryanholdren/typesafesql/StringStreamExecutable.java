package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringStreamExecutable extends ObjectStreamExecutable<String> {

	public StringStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected String read(ResultSet results) throws SQLException {
		return results.getString(1);
	}

}