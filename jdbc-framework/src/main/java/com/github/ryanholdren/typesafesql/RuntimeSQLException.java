package com.github.ryanholdren.typesafesql;

import java.sql.SQLException;

public class RuntimeSQLException extends RuntimeException {

	public RuntimeSQLException(SQLException cause) {
		super(cause);
	}

}
