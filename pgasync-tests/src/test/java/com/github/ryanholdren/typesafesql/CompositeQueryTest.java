package com.github.ryanholdren.typesafesql;

import org.junit.Test;
import reactor.test.StepVerifier;

public class CompositeQueryTest extends FunctionalTest {

	@Test
	public void testCreateThenSelect() {
		StepVerifier
			.create(
				TestCreateThenSelect
					.prepare()
					.executeIn(database)
			)
			.expectNext(42)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testMultipleInserts() {
		StepVerifier
			.create(
				TestMultipleInserts
					.prepare()
					.executeIn(database)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
