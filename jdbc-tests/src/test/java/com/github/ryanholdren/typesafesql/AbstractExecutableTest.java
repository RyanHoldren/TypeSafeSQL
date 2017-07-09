package com.github.ryanholdren.typesafesql;

import static org.junit.Assert.fail;

public class AbstractExecutableTest {

	protected static final String NOT_REALLY_SQL = "♪ Is it too late now to say sorry? ♪";
	protected static final String EXCEPTION_MESSAGE = "Umm... That's not SQL. That's a Bieber song.";

	protected static final void failBecauseShouldHaveThrownException() {
		fail("Should have thrown an exception!");
	}

}
