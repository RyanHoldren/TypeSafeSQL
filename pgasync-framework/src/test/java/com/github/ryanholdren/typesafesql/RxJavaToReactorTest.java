package com.github.ryanholdren.typesafesql;

import org.junit.Test;
import rx.Observable;
import static com.github.ryanholdren.typesafesql.Utilities.toFlux;
import static java.lang.Long.MAX_VALUE;
import static java.time.Duration.ofSeconds;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

public class RxJavaToReactorTest {

	@Test
	public void testToFluxWithOneElement() {
		final Flux<String> flux = toFlux(Observable.create(subscriber -> {
			try {
				Thread.sleep(1000);
				subscriber.onNext("applesauce");
				Thread.sleep(1000);
				subscriber.onCompleted();
			} catch (InterruptedException exception) {
				throw new RuntimeException(exception);
			}
		}));
		assertEquals("applesauce", flux.single().block(ofSeconds(5)));
	}

	@Test
	public void testToFluxWithTwoElements() {
		final Flux<String> flux = toFlux(Observable.create(subscriber -> {
			try {
				Thread.sleep(1000);
				subscriber.onNext("apple");
				Thread.sleep(1000);
				subscriber.onNext("sauce");
				Thread.sleep(1000);
				subscriber.onCompleted();
			} catch (InterruptedException exception) {
				throw new RuntimeException(exception);
			}
		}));
		assertEquals("apple", flux.blockFirst(ofSeconds(5)));
		assertEquals("sauce", flux.blockLast(ofSeconds(5)));
	}

	@Test
	public void testToFluxWithTwoElementsInDifferentThreads() throws Throwable {
		final Flux<String> flux = toFlux(Observable.create(subscriber -> {
			final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			executor.schedule(() -> subscriber.onNext("apple"), 1, SECONDS);
			executor.schedule(() -> subscriber.onNext("sauce"), 2, SECONDS);
			executor.schedule(() -> subscriber.onCompleted(), 3, SECONDS);
			executor.shutdown();
		}));
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> container = new AtomicReference<>();
		final AtomicInteger counter = new AtomicInteger();
		flux.subscribe(new Subscriber<String>() {

			@Override
			public void onSubscribe(Subscription subscription) {
				subscription.request(MAX_VALUE);
			}

			@Override
			public void onNext(String value) {
				switch (counter.incrementAndGet()) {
					case 1:
						assertEquals("apple", value);
						break;
					case 2:
						assertEquals("sauce", value);
						break;
					default:
						throw new IllegalStateException();
				}
			}

			@Override
			public void onError(Throwable throwable) {
				container.accumulateAndGet(throwable, (previous, next) -> {
					previous.addSuppressed(next);
					return previous;
				});
			}

			@Override
			public void onComplete() {
				try {
					assertEquals(3, counter.incrementAndGet());
				} finally {
					latch.countDown();
				}
			}

		});
		if (latch.await(10, SECONDS) == false) {
			fail("onComplete() was never called!");
		}
		final Throwable exception = container.get();
		if (exception != null) {
			throw exception;
		}
	}

}
