package com.github.ryanholdren.typesafesql;

import static java.lang.Integer.MAX_VALUE;
import java.lang.reflect.Method;
import java.util.Comparator;

public class ColumnPositionComparator implements Comparator<Method> {

	private static final ColumnPositionComparator INSTANCE = new ColumnPositionComparator();

	public static ColumnPositionComparator getInstance() {
		return INSTANCE;
	}

	private ColumnPositionComparator() {}

	@Override
	public int compare(Method a, Method b) {
		return getColumnPositionOf(a) - getColumnPositionOf(b);
	}

	private int getColumnPositionOf(Method method) {
		return new Finder(method).find();
	}

	private class Finder {

		private final Method method;
		private final Class<?> clazz;

		private Finder(Method method) {
			this.method = method;
			this.clazz = method.getDeclaringClass();
		}

		private int find() {
			ColumnPosition position = fromMethod();
			if (position == null) {
				position = fromInterfaces();
			}
			if (position == null) {
				position = fromSuperclasses();
			}
			if (position == null) {
				return MAX_VALUE;
			}
			return position.value();
		}

		private ColumnPosition fromMethod() {
			final ColumnPosition position = method.getAnnotation(ColumnPosition.class);
			if (position == null) {
				return null;
			}
			return position;
		}

		private ColumnPosition fromSuperclasses() {
			for (Class<?> interfaze : clazz.getInterfaces()) {
				try {
					final Method equivalentMethod = interfaze.getDeclaredMethod(method.getName(), method.getParameterTypes());
					final ColumnPosition position = equivalentMethod.getAnnotation(ColumnPosition.class);
					if (position != null) {
						return position;
					}
				} catch (NoSuchMethodException | SecurityException exception) {}
			}
			return null;
		}

		private ColumnPosition fromInterfaces() {
			Class<?> subclass = method.getDeclaringClass();
			while (true) {
				subclass = subclass.getSuperclass();
				if (subclass == null || Object.class == subclass) {
					return null;
				}
				try {
					final Method equivalentMethod = subclass.getDeclaredMethod(method.getName(), method.getParameterTypes());
					final ColumnPosition position = equivalentMethod.getAnnotation(ColumnPosition.class);
					if (position != null) {
						return position;
					}
				} catch (NoSuchMethodException | SecurityException exception) {}
			}
		}
	}

}
