package com.github.ryanholdren.typesafesql;

import org.junit.Test;
import reactor.test.StepVerifier;

public class BooleanTest extends FunctionalTest {

	@Test
	public void testTrueBooleanParameter() {
		StepVerifier
			.create(
				TestTrueBooleanParameter
					.prepare()
					.withInput(true)
					.executeIn(db)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testFalseBooleanParameter() {
		StepVerifier
			.create(
				TestFalseBooleanParameter
					.prepare()
					.withInput(false)
					.executeIn(db)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

	@Test
	public void testNullBooleanParameter() {
		StepVerifier
			.create(
				TestNullBooleanParameter
					.prepare()
					.withoutInput()
					.executeIn(db)
			)
			.expectComplete()
			.verify(TIMEOUT);
	}

}
