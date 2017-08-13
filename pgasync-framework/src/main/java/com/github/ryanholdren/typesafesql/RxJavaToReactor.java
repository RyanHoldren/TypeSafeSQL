package com.github.ryanholdren.typesafesql;

import reactor.core.publisher.Flux;
import rx.Observable;
import static rx.RxReactiveStreams.toPublisher;

public final class RxJavaToReactor {
	public static <T> Flux<T> toFlux(Observable<T> observable) {
		return Flux.from(toPublisher(observable));
	}
}
