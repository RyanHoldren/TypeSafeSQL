package com.github.ryanholdren.typesafesql;

import java.lang.reflect.Method;
import static java.util.Arrays.sort;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ColumnPositionComparatorTest {

	private final ColumnPositionComparator comparator = ColumnPositionComparator.getInstance();

	@Test
	public void testCompare() {
		final Method[] methods = Dummy.class.getMethods();
		sort(methods, comparator);
		assertEquals("y", methods[0].getName());
		assertEquals("z", methods[1].getName());
		assertEquals("x", methods[2].getName());
		assertEquals("w", methods[3].getName());
	}

	private interface Dummy {
		void w();
		@ColumnPosition(3)
		void x();
		@ColumnPosition(1)
		void y();
		@ColumnPosition(2)
		void z();
	}

}