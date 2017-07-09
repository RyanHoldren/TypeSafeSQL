package com.github.ryanholdren.typesafesql;

import static java.util.concurrent.TimeUnit.SECONDS;
import org.junit.Test;

public class NoResultsTest extends FunctionalTest {

	@Test
	public void testNoResults() {
		TestNoResults
			.prepare()
			.executeIn(db)
			.test()
			.awaitDone(10L, SECONDS)
			.assertNoErrors()
			.assertNoValues()
			.assertNoTimeout();
	}

}
