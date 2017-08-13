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
					.executeIn(db)
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
					.executeIn(db)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
