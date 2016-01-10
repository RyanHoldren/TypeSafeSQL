package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteArrayStreamExecutable extends ObjectStreamExecutable<byte[]> {

	public ByteArrayStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected final byte[] read(ResultSet results) throws SQLException {
		return results.getBytes(1);
	}

}