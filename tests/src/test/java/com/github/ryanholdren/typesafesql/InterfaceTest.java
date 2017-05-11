package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.TestComplex.Result;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class InterfaceTest {
	@Test
	public void testImplementsInterface() {
		final TwoInputs twoInputs = (TwoInputs) mock(Result.class);
	}
}
