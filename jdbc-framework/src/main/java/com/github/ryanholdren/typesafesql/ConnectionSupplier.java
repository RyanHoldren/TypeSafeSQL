package com.github.ryanholdren.typesafesql;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionSupplier {
	Connection openConnection() throws SQLException;
}
