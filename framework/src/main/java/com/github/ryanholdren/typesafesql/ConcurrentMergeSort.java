package com.github.ryanholdren.typesafesql;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Thread.MAX_PRIORITY;
import java.util.Comparator;
import static java.util.Comparator.naturalOrder;
import java.util.Map.Entry;
import java.util.Spliterator;
import static java.util.Spliterator.CONCURRENT;
import static java.util.Spliterator.NONNULL;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newCachedThreadPool;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static java.util.stream.StreamSupport.stream;

public class ConcurrentMergeSort<R> extends Semaphore implements Spliterator<R> {

	private static final ExecutorService DEFAULT_THREAD_POOL = newCachedThreadPool(runnable -> {
		final Thread thread = new Thread(runnable);
		thread.setName("Concurrent Merge Sort Worker");
		thread.setPriority(MAX_PRIORITY);
		return thread;
	});

	private static final Consumer<? super Throwable> IGNORE = exception -> {};

	public static class Builder<R> {

		private final Comparator<? super R> comparator;

		private ExecutorService executor = DEFAULT_THREAD_POOL;
		private Consumer<? super Throwable> exceptionHandler = IGNORE;
		private Stream<? extends Supplier<? extends Stream<? extends R>>> suppliers = empty();

		private Builder(Comparator<? super R> comparator) {
			this.comparator = comparator;
		}

		public Builder<R> withExecutor(ExecutorService executor) {
			this.executor = executor;
			return this;
		}

		public Builder<R> withExceptionHandler(Consumer<? super Throwable> exceptionHandler) {
			this.exceptionHandler = exceptionHandler;
			return this;
		}

		public Stream<? extends R> concurrentlyMergeSort(Supplier<? extends Stream<? extends R>> ... suppliers) {
			return concurrentlyMergeSort(of(suppliers));
		}

		public Stream<? extends R> concurrentlyMergeSort(Stream<? extends Supplier<? extends Stream<? extends R>>> suppliers) {
			return new ConcurrentMergeSort(comparator, exceptionHandler).initialize(executor, suppliers).toStream();
		}

	}

	public static <R extends Comparable> Builder<R> usingNaturalOrder() {
		return new Builder(naturalOrder());
	}

	public static <R> Builder<R> using(Comparator<? super R> comparator) {
		return new Builder(comparator);
	}

	private static final int CONCURRENCY = 3;

	private final ConcurrentSkipListMap<R, StreamTask<R>> results;
	private final Consumer<? super Throwable> exceptionHandler;

	private ConcurrentMergeSort(Comparator<? super R> comparator, Consumer<? super Throwable> exceptionHandler) {
		super(CONCURRENCY);
		this.exceptionHandler = exceptionHandler;
		this.results = new ConcurrentSkipListMap<>(comparator);
	}

	private ConcurrentMergeSort<R> initialize(
		ExecutorService executor,
		Stream<? extends Supplier<? extends Stream<? extends R>>> suppliers
	) {
		final Runnable[] runnables = suppliers.map(supplier -> (Runnable) () -> {
			try {
				final Stream<? extends R> stream = supplier.get();
				try {
					final Spliterator<? extends R> spliterator = stream.spliterator();
					new StreamTask<R>(executor) {

						@Override
						protected void runOnce() {
							try {
								if (spliterator.tryAdvance(this) == false) {
									release();
									close();
								}
							} catch (Throwable throwable) {
								try {
									exceptionHandler.accept(throwable);
								} finally {
									release();
									close();
								}
							}
						}

						@Override
						public void accept(R result) {
							final StreamTask<R> replaced = results.put(result, this);
							if (replaced == null) {
								release();
							} else {
								replaced.enqueue(1);
							}
						}

						private final AtomicBoolean wasClosed = new AtomicBoolean(false);

						@Override
						public void close() {
							if (wasClosed.compareAndSet(false, true)) {
								stream.close();
							}
						}

					}.enqueue(CONCURRENCY);
				} catch (Throwable throwable) {
					stream.close();
					throw throwable;
				}
			} catch (Throwable throwable) {
				try {
					exceptionHandler.accept(throwable);
				} finally {
					release(CONCURRENCY);
				}
			}
		}).toArray(size -> new Runnable[size]);
		reducePermits(runnables.length * CONCURRENCY);
		for (Runnable runnable : runnables) {
			executor.submit(runnable);
		}
		return this;
	}

	@Override
	public boolean tryAdvance(Consumer<? super R> action) {
		try {
			acquire();
			final Entry<R, StreamTask<R>> entry = results.pollFirstEntry();
			if (entry == null) {
				return false;
			}
			final StreamTask<R> task = entry.getValue();
			task.enqueue(1);
			final R result = entry.getKey();
			action.accept(result);
			return true;
		} catch (InterruptedException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public Spliterator<R> trySplit() {
		return null;
	}

	@Override
	public long estimateSize() {
		return MAX_VALUE;
	}

	@Override
	public int characteristics() {
		return CONCURRENT | NONNULL | ORDERED;
	}

	private Stream<R> toStream() {
		return stream(this, true).onClose(this::close);
	}

	private void close() {
		while (true) {
			final Entry<R, StreamTask<R>> entry = results.pollFirstEntry();
			if (entry == null) {
				return;
			}
			entry.getValue().close();
		}
	}

	private static abstract class StreamTask<R> extends RepeatingTask implements Consumer<R> {

		public StreamTask(Executor executor) {
			super(executor);
		}

		public abstract void close();

	}

}
