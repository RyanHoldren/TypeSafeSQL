package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Executable {

	protected final PreparedStatement statement;

	private final Connection connection;
	private final ConnectionHandling handling;

	public Executable(String sql, Connection connection, ConnectionHandling handling) {
		try {
			try {
				this.connection = connection;
				this.handling = handling;
				this.statement = connection.prepareStatement(sql);
			} catch (SQLException exception) {
				throw new RuntimeSQLException(exception);
			}
		} catch (Throwable exception) {
			try {
				closeConnectionIfApplicable();
			} catch (Throwable suppressed) {
				exception.addSuppressed(suppressed);
			}
			throw exception;
		}
	}

	@FunctionalInterface
	protected interface PreparedStatementConsumer<X> {
		X accept(PreparedStatement statement) throws SQLException;
	}

	protected final <X> X safelyUseStatement(PreparedStatementConsumer<X> action) {
		try {
			try {
				return action.accept(statement);
			} catch (SQLException exception) {
				throw new RuntimeSQLException(exception);
			}
		} catch (Throwable exception) {
			try {
				close();
			} catch (Throwable suppressed) {
				exception.addSuppressed(suppressed);
			}
			throw exception;
		}
	}

	private void closeConnectionIfApplicable() throws SQLException {
		if (handling == CLOSE_WHEN_DONE) {
			connection.close();
		}
	}

	protected final void close() throws SQLException {
		try {
			statement.close();
		} finally {
			closeConnectionIfApplicable();
		}
	}

}
