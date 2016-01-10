package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.BaseStream;

public abstract class StreamExecutable<T, S extends BaseStream<T, S>> extends Executable {

	public StreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	public final S execute() {
		return safelyUseStatement(statement -> {
			boolean isResultSet = statement.execute();
			do {
				if (isResultSet) {
					final ResultSet results = statement.getResultSet();
					final Runnable cleanup = () -> {
						try {
							try {
								results.close();
							} finally {
								close();
							}
						} catch (SQLException exception) {
							throw new RuntimeSQLException(exception);
						}
					};
					final S stream = helpExecute(results);
					return stream.onClose(cleanup);
				}
				isResultSet = statement.getMoreResults();
			} while (isResultSet || statement.getUpdateCount() != -1);
			throw new IllegalStateException("Could not find result set!");
		});
	}

	protected abstract S helpExecute(ResultSet results);

}
