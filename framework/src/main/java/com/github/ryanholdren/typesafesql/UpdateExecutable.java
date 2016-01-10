package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateExecutable extends Executable {

	public UpdateExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	public final int execute() {
		final int rowsAffected = safelyUseStatement(statement -> {
			int totalUpdateCount = 0;
			boolean isResultSet = statement.execute();
			while (true) {
				if (isResultSet) {
					continue;
				}
				int updateCount = statement.getUpdateCount();
				if (updateCount < 0) {
					return totalUpdateCount;
				}
				totalUpdateCount += updateCount;
				isResultSet = statement.getMoreResults();
			}
		});
		try {
			close();
		} catch (SQLException exception) {
			throw new RuntimeSQLException(exception);
		}
		return rowsAffected;
	}

}
