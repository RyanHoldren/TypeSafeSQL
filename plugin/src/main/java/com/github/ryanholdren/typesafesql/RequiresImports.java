package com.github.ryanholdren.typesafesql;

import java.util.function.Consumer;

public interface RequiresImports {
	void forEachRequiredImport(Consumer<String> action, boolean isNotMocking);
}
