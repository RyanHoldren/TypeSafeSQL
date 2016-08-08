package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.ByteArrayStreamExecutable;

public class ByteArrayStreamExecutableMocker extends ObjectStreamExecutableMocker<byte[], ByteArrayStreamExecutable> {
	public ByteArrayStreamExecutableMocker() {
		super(ByteArrayStreamExecutable.class);
	}
}
