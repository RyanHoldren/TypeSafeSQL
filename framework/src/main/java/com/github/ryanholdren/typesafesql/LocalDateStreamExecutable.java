package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class LocalDateStreamExecutable extends ObjectStreamExecutable<LocalDate> {

	public LocalDateStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected LocalDate read(ResultSet results) throws SQLException {
		final Date date = results.getDate(1);
		if (date == null) {
			return null;
		}
		return date.toLocalDate();
	}

}
