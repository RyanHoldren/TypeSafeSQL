package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.UUIDStreamExecutable;
import java.util.UUID;

public class UUIDStreamExecutableMocker extends ObjectStreamExecutableMocker<UUID, UUIDStreamExecutable> {
	public UUIDStreamExecutableMocker() {
		super(UUIDStreamExecutable.class);
	}
}
