package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.AbstractExecutableTest.EXCEPTION_MESSAGE;
import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import static com.github.ryanholdren.typesafesql.ConnectionHandling.LEAVE_OPEN;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IntStreamExecutableTest extends AbstractExecutableTest {

	@Test
	public void testConnectionClosedWhenStreamConsumerThrowsException() throws SQLException {
		final Connection connection = helpTestWhenStreamConsumerThrowsException(CLOSE_WHEN_DONE);
		verify(connection, times(1)).close();
	}

	@Test
	public void testConnectionLeftOpenWhenStreamConsumerThrowsException() throws SQLException {
		final Connection connection = helpTestWhenStreamConsumerThrowsException(LEAVE_OPEN);
		verify(connection, never()).close();
	}

	@Test
	public void testConnectionClosedWhenStreamConsumerSuccess() throws SQLException {
		final Connection connection = helpTestWhenStreamConsumerSuccess(CLOSE_WHEN_DONE);
		verify(connection, times(1)).close();
	}

	@Test
	public void testConnectionLeftOpenWhenStreamConsumerSuccess() throws SQLException {
		final Connection connection = helpTestWhenStreamConsumerSuccess(LEAVE_OPEN);
		verify(connection, never()).close();
	}

	private Connection helpTestWhenStreamConsumerSuccess(ConnectionHandling handling) throws SQLException {
		return useMockExecutable(handling, executable -> {
			executable.execute(new Consumer<IntStream>() {
				@Override
				public void accept(IntStream stream) {
					return;
				}
			});
		});
	}

	private Connection helpTestWhenStreamConsumerThrowsException(ConnectionHandling handling) throws SQLException {
		return useMockExecutable(handling, executable -> {
			final RuntimeException anException = new RuntimeException(EXCEPTION_MESSAGE);
			try {
				executable.execute(new Consumer<IntStream>() {
					@Override
					public void accept(IntStream stream) {
						throw anException;
					}
				});
				failBecauseShouldHaveThrownException();
			} catch (RuntimeException exception) {
				assertEquals(anException, exception);
			}
		});
	}

	private static Connection useMockExecutable(ConnectionHandling handling, Consumer<IntStreamExecutable> action) throws SQLException {
		final Connection connection = mock(Connection.class);
		final PreparedStatement statement = mock(PreparedStatement.class);
		final ResultSet results = mock(ResultSet.class);
		when(statement.execute()).thenReturn(true);
		when(statement.getResultSet()).thenReturn(results);
		when(connection.prepareStatement(NOT_REALLY_SQL)).thenReturn(statement);
		final IntStreamExecutable executable = new IntStreamExecutable(NOT_REALLY_SQL, connection, handling);
		action.accept(executable);
		verify(results, times(1)).close();
		verify(statement, times(1)).close();
		return connection;
	}

}
