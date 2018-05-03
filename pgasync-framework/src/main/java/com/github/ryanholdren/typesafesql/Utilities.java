package com.github.ryanholdren.typesafesql;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import reactor.core.publisher.Flux;
import rx.Observable;
import static rx.RxReactiveStreams.toPublisher;

public final class Utilities {

	public static <T> Flux<T> toFlux(Observable<T> observable) {
		return Flux.from(toPublisher(observable));
	}

	public static Timestamp toSql(Instant instant) {
		if (instant == null) {
			return null;
		}
		return Timestamp.from(instant);
	}

	public static Date toSql(LocalDate date) {
		if (date == null) {
			return null;
		}
		return Date.valueOf(date);
	}

	public static String toSql(UUID uuid) {
		return uuid == null ? null : uuid.toString();
	}

}
