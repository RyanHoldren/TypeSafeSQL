package com.github.ryanholdren.typesafesql;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;
import static rx.RxReactiveStreams.toPublisher;
import rx.Single;

public final class RxJavaToReactor {

	public static <T> Mono<T> toMono(Single<T> observable) {
		return Mono.from(toPublisher(observable));
	}

	public static <T> Flux<T> toFlux(Observable<T> observable) {
		return Flux.from(toPublisher(observable));
	}

}
