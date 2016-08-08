package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.InstantStreamExecutable;
import java.time.Instant;

public class InstantStreamExecutableMocker extends ObjectStreamExecutableMocker<Instant, InstantStreamExecutable> {
	public InstantStreamExecutableMocker() {
		super(InstantStreamExecutable.class);
	}
}
