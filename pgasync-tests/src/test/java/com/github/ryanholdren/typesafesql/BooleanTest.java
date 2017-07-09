package com.github.ryanholdren.typesafesql;

import static java.util.concurrent.TimeUnit.SECONDS;
import org.junit.Test;

public class BooleanTest extends FunctionalTest {

	@Test
	public void testTrueBooleanParameter() {
		TestTrueBooleanParameter
			.prepare()
			.withInput(true)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testFalseBooleanParameter() {
		TestFalseBooleanParameter
			.prepare()
			.withInput(false)
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

	@Test
	public void testNullBooleanParameter() {
		TestNullBooleanParameter
			.prepare()
			.withoutInput()
			.executeIn(db)
			.test()
			.awaitDone(1L, SECONDS)
			.assertNoErrors();
	}

}
