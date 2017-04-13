package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.CLOSE_WHEN_DONE;
import org.junit.Test;

public class BooleanTest extends FunctionalTest {

	@Test
	public void testTrueBooleanParameter() throws Throwable {
		TestTrueBooleanParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(true)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testFalseBooleanParameter() throws Throwable {
		TestFalseBooleanParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withInput(false)
			.getNumberOfRowsAffected();
	}

	@Test
	public void testNullBooleanParameter() throws Throwable {
		TestNullBooleanParameter
			.using(openConnection(), CLOSE_WHEN_DONE)
			.withoutInput()
			.getNumberOfRowsAffected();
	}

}
