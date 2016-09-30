package com.github.ryanholdren.typesafesql;

import java.lang.reflect.Method;
import static java.util.Arrays.sort;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ColumnPositionComparatorTest {

	private final ColumnPositionComparator comparator = ColumnPositionComparator.getInstance();

	@Test
	public void testCompareOnInterface() {
		helpTest(Interface.class);
	}

	@Test
	public void testCompareOnImplementation() {
		helpTest(Implementation.class);
	}

	@Test
	public void testCompareOnSuperclass() {
		helpTest(Superclass.class);
	}

	private void helpTest(Class<? extends Interface> clazz) {
		final Method[] methods = clazz.getMethods();
		sort(methods, comparator);
		assertEquals("y", methods[0].getName());
		assertEquals("z", methods[1].getName());
		assertEquals("x", methods[2].getName());
		assertEquals("w", methods[3].getName());
	}

	private interface Interface {
		void w();
		@ColumnPosition(3)
		void x();
		@ColumnPosition(1)
		void y();
		@ColumnPosition(2)
		void z();
	}

	private class Implementation implements Interface {
		@Override
		public void w() {}
		@Override
		public void x() {}
		@Override
		public void y() {}
		@Override
		public void z() {}
	}

	private class Superclass extends Implementation {

	}

}