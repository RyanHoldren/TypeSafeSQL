package com.github.ryanholdren.typesafesql;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import static java.util.concurrent.atomic.AtomicIntegerFieldUpdater.newUpdater;

public abstract class RepeatingTask implements Runnable {

	public static final AtomicIntegerFieldUpdater<RepeatingTask> EXECUTIONS_UPDATER = newUpdater(RepeatingTask.class, "executions");

	private final Executor executor;

	private volatile int executions = 0;

	public RepeatingTask(Executor executor) {
		this.executor = executor;
	}

	public void enqueue(int repetitions) {
		if (EXECUTIONS_UPDATER.getAndAdd(this, repetitions) == 0) {
			executor.execute(this);
		}
	}

	@Override
	public void run() {
		do {
			runOnce();
		} while (EXECUTIONS_UPDATER.decrementAndGet(this) > 0);
	}

	protected abstract void runOnce();

}