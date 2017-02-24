package com.github.ryanholdren.typesafesql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalStreamExecutable extends ObjectStreamExecutable<BigDecimal> {

	public BigDecimalStreamExecutable(String sql, Connection connection, ConnectionHandling handling) {
		super(sql, connection, handling);
	}

	@Override
	protected BigDecimal read(ResultSet results) throws SQLException {
		return results.getBigDecimal(1);
	}

}
