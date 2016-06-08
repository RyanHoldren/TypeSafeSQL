package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateExecutable extends Executable {

	public UpdateExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	public int getNumberOfRowsAffected() {
		final int rowsAffected = safelyUseStatement(statement -> {
			int totalUpdateCount = 0;
			boolean isResultSet = statement.execute();
			while (true) {
				if (isResultSet == false) {
					int updateCount = statement.getUpdateCount();
					if (updateCount < 0) {
						break;
					}
					totalUpdateCount += updateCount;
				}
				isResultSet = statement.getMoreResults();
			}
			return totalUpdateCount;
		});
		try {
				close();
		} catch (SQLException exception) {
			throw new RuntimeSQLException(exception);
		}
		return rowsAffected;
	}

}
