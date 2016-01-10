package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

public class DoubleStreamExecutable extends StreamExecutable<Double, DoubleStream> {

	public DoubleStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected final DoubleStream helpExecute(ResultSet results) {
		return StreamSupport.doubleStream(
			new Spliterators.AbstractDoubleSpliterator(
				Long.MAX_VALUE,
				Spliterator.ORDERED
			) {
				@Override
				public boolean tryAdvance(DoubleConsumer action) {
					try {
						if (results.next()) {
							final double value = results.getDouble(1);
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