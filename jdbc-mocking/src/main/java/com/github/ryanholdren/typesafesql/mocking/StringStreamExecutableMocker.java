package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.StringStreamExecutable;

public class StringStreamExecutableMocker extends ObjectStreamExecutableMocker<String, StringStreamExecutable> {
	public StringStreamExecutableMocker() {
		super(StringStreamExecutable.class);
	}
}
