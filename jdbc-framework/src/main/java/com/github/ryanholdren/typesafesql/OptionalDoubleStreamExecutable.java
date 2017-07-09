package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.OptionalDouble;

public class OptionalDoubleStreamExecutable extends ObjectStreamExecutable<OptionalDouble> {

	public OptionalDoubleStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected OptionalDouble read(ResultSet results) throws SQLException {
		final double value = results.getDouble(1);
		if (results.wasNull()) {
			return OptionalDouble.empty();
		} else {
			return OptionalDouble.of(value);
		}
	}

}
