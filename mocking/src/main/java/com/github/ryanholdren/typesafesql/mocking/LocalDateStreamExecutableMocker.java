package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.LocalDateStreamExecutable;
import java.time.LocalDate;

public class LocalDateStreamExecutableMocker extends ObjectStreamExecutableMocker<LocalDate, LocalDateStreamExecutable> {
	public LocalDateStreamExecutableMocker() {
		super(LocalDateStreamExecutable.class);
	}
}
