package com.github.ryanholdren.typesafesql;

import org.junit.Test;
import reactor.test.StepVerifier;

public class BooleanTest extends FunctionalTest {

	@Test
	public void testTrueBooleanParameter() {
		StepVerifier
			.create(
				database
					.testTrueBooleanParameter()
					.withInput(true)
					.prepare()
					.execute()
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testFalseBooleanParameter() {
		StepVerifier
			.create(
				database
					.testFalseBooleanParameter()
					.withInput(false)
					.prepare()
					.execute()
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
