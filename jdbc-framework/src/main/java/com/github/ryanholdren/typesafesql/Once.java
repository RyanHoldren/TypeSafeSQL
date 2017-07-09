package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.Once.State.ALREADY_RAN;
import static com.github.ryanholdren.typesafesql.Once.State.HAS_NOT_RUN_YET;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import static java.util.concurrent.atomic.AtomicReferenceFieldUpdater.newUpdater;

public class Once implements Runnable {

	public static Once once(Runnable runnable) {
		return new Once(runnable);
	}

	private static final AtomicReferenceFieldUpdater<Once, State> STATE_UPDATER = newUpdater(Once.class, State.class, "state");

	protected enum State {
		HAS_NOT_RUN_YET,
		ALREADY_RAN,
	}

	private final Runnable runnable;
	private volatile State state = HAS_NOT_RUN_YET;

	public Once(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		if (STATE_UPDATER.compareAndSet(this, HAS_NOT_RUN_YET, ALREADY_RAN)) {
			runnable.run();
		}
	}

}
