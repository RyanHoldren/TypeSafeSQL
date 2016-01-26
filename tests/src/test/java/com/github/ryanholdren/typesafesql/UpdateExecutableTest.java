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

public class UpdateExecutableTest extends AbstractExecutableTest {

	@Test
	public void testConnectionClosedWhenExecuteThrowsException() throws SQLException {
		final Connection connection = helpTestWhenExecuteThrowsException(CLOSE_WHEN_DONE);
		verify(connection, times(1)).close();
	}

	@Test
	public void testConnectionLeftOpenWhenExecuteThrowsException() throws SQLException {
		final Connection connection = helpTestWhenExecuteThrowsException(LEAVE_OPEN);
		verify(connection, never()).close();
	}

	private Connection helpTestWhenExecuteThrowsException(ConnectionHandling handling) throws SQLException {
		final Connection connection = mock(Connection.class);
		final PreparedStatement statement = mock(PreparedStatement.class);
		final SQLException anException = new SQLException(EXCEPTION_MESSAGE);
		when(connection.prepareStatement(NOT_REALLY_SQL)).thenReturn(statement);
		when(statement.execute()).thenThrow(anException);
		try {
			final UpdateExecutable executable = new UpdateExecutable(NOT_REALLY_SQL, connection, handling);
			executable.execute();
			failBecauseShouldHaveThrownException();
		} catch (RuntimeSQLException exception) {
			assertEquals(anException, exception.getCause());
		}
		verify(statement, times(1)).close();
		return connection;
	}

}
