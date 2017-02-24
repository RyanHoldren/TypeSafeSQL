package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDStreamExecutable extends ObjectStreamExecutable<UUID> {

	public UUIDStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected UUID read(ResultSet results) throws SQLException {
		return (UUID) results.getObject(1);
	}

}
