package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.github.ryanholdren.typesafesql.ConnectionHandling.LEAVE_OPEN;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExecutableTest extends AbstractExecutableTest {

	@Test
	public void testConnectionClosedWhenPrepareStatementThrowsException() throws SQLException {
		final Connection connection = helpTestWhenPrepareStatementThrowsException(CLOSE_WHEN_DONE);
		verify(connection, times(1)).close();
	}

	@Test
	public void testConnectionLeftOpenWhenPrepareStatementThrowsException() throws SQLException {
		final Connection connection = helpTestWhenPrepareStatementThrowsException(LEAVE_OPEN);
		verify(connection, never()).close();
	}

	private Connection helpTestWhenPrepareStatementThrowsException(ConnectionHandling handling) throws SQLException {
		final Connection connection = mock(Connection.class);
		final SQLException anException = new SQLException(EXCEPTION_MESSAGE);
		when(connection.prepareStatement(NOT_REALLY_SQL)).thenThrow(anException);
		try {
			new Executable(NOT_REALLY_SQL, connection, handling) {};
			failBecauseShouldHaveThrownException();
		} catch (RuntimeSQLException exception) {
			assertEquals(anException, exception.getCause());
		}
		return connection;
	}

	@Test
	public void testConnectionClosedWhenPreparedStatementConsumerThrowsException() throws SQLException {
		final Connection connection = helpTestWhenPreparedStatementConsumerThrowsException(CLOSE_WHEN_DONE);
		verify(connection, times(1)).close();
	}

	@Test
	public void testConnectionLeftOpenWhenPreparedStatementConsumerThrowsException() throws SQLException {
		final Connection connection = helpTestWhenPreparedStatementConsumerThrowsException(LEAVE_OPEN);
		verify(connection, never()).close();
	}

	private Connection helpTestWhenPreparedStatementConsumerThrowsException(ConnectionHandling handling) throws SQLException {
		final Connection connection = mock(Connection.class);
		final PreparedStatement statement = mock(PreparedStatement.class);
		final SQLException anException = new SQLException(EXCEPTION_MESSAGE);
		when(connection.prepareStatement(NOT_REALLY_SQL)).thenReturn(statement);
		try {
			final Executable executable = new Executable(NOT_REALLY_SQL, connection, handling) {};
			executable.safelyUseStatement(actualStatement -> {
				assertEquals(statement, actualStatement);
				throw anException;
			});
			failBecauseShouldHaveThrownException();
		} catch (RuntimeSQLException exception) {
			assertEquals(anException, exception.getCause());
		}
		verify(statement, times(1)).close();
		return connection;
	}


}
