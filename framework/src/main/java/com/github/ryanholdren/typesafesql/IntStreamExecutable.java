package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class IntStreamExecutable extends StreamExecutable<Integer, IntStream> {

	public IntStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	public int getFirstResult() {
		try {
			try {
				final ResultSet results = getResultSetFrom(statement);
				if (results.next()) {
					return results.getInt(1);
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

	public void forEachResult(IntConsumer action) {
		try (
			final IntStream stream = execute()
		) {
			stream.forEach(action);
		}
	}

	@Override
	protected IntStream helpExecute(ResultSet results) {
		return StreamSupport.intStream(
			new Spliterators.AbstractIntSpliterator(
				Long.MAX_VALUE,
				Spliterator.ORDERED
			) {
				@Override
				public boolean tryAdvance(IntConsumer action) {
					try {
						if (results.next()) {
							final int value = results.getInt(1);
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

}