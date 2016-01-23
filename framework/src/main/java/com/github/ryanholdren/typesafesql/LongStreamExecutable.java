package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

public class LongStreamExecutable extends StreamExecutable<Long, LongStream> {

	public LongStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected LongStream helpExecute(ResultSet results) {
		return StreamSupport.longStream(
			new Spliterators.AbstractLongSpliterator(
				Long.MAX_VALUE,
				Spliterator.ORDERED
			) {
				@Override
				public boolean tryAdvance(LongConsumer action) {
					try {
						if (results.next()) {
							final long value = results.getLong(1);
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

	public final long execute() {
		try {
			try {
				final ResultSet results = getResultSetFrom(statement);
				if (results.next()) {
					return results.getLong(1);
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

}