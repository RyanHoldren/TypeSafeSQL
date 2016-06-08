package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class ObjectStreamExecutable<T> extends StreamExecutable<T, Stream<T>> {

	public ObjectStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	public T getFirstResult() {
		try {
			try {
				final ResultSet results = getResultSetFrom(statement);
				if (results.next()) {
					return read(results);
				} else {
					throw new NoSuchElementException();
				}
			} finally {
				close();
			}
		} catch (SQLException exception) {
			throw new RuntimeSQLException(exception);
		}
	}

	public void forEachResult(Consumer<T> action) {
		try (
			final Stream<T> stream = execute()
		) {
			stream.forEach(action);
		}
	}

	@Override
	protected Stream<T> helpExecute(ResultSet results) {
		return StreamSupport.stream(
			new Spliterators.AbstractSpliterator<T>(
				Long.MAX_VALUE,
				Spliterator.ORDERED
			) {
				@Override
				public boolean tryAdvance(Consumer<? super T> action) {
					try {
						if (results.next()) {
							final T value = read(results);
							action.accept(value);
							return true;
						} else {
							return false;
						}
					} catch (SQLException exception) {
						throw new RuntimeSQLException(exception);
					}
				}
			},
			false
		);
	}

	protected abstract T read(ResultSet results) throws SQLException;

}