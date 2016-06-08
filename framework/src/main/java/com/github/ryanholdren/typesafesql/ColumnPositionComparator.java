package com.github.ryanholdren.typesafesql;

import static java.lang.Integer.MAX_VALUE;
import java.lang.reflect.Method;
import java.util.Comparator;

public class ColumnPositionComparator implements Comparator<Method> {

	private static final ColumnPositionComparator instance = new ColumnPositionComparator();

	public static ColumnPositionComparator getInstance() {
		return instance;
	}

	private ColumnPositionComparator() {}

	@Override
	public int compare(Method a, Method b) {
		return getColumnPositionOf(a) - getColumnPositionOf(b);
	}

	private int getColumnPositionOf(Method method) {
		final ColumnPosition position = method.getAnnotation(ColumnPosition.class);
		if (position == null) {
			return MAX_VALUE;
		}
		return position.value();
	}

}
