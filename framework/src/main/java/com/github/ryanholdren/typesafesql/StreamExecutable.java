package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.BaseStream;

public abstract class StreamExecutable<T, S extends BaseStream<T, S>> extends Executable {

	public StreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	protected ResultSet getResultSetFrom(PreparedStatement statement) throws SQLException {
		boolean isResultSet = statement.execute();
			do {
				if (isResultSet) {
					return statement.getResultSet();
				}
				isResultSet = statement.getMoreResults();
			} while (isResultSet || statement.getUpdateCount() != -1);
			throw new IllegalStateException("Could not find result set!");
	}

	private S executeStream() {
		return safelyUseStatement(statement -> {
			final ResultSet results = getResultSetFrom(statement);
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
		});
	}

	public final T execute(Function<S, T> action) {
		try (S stream = executeStream()) {
			return action.apply(stream);
		}
	}

	public final void execute(Consumer<S> action) {
		try (S stream = executeStream()) {
			action.accept(stream);
		}
	}

	protected abstract S helpExecute(ResultSet results);

}
