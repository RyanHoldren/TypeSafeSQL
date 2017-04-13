package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.ObjectStreamExecutable;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import static java.util.stream.Stream.of;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class ObjectStreamExecutableMocker<T, E extends ObjectStreamExecutable<T>> implements ExecutableMocker {

	private final E executable;

	protected ObjectStreamExecutableMocker(Class<E> clazz) {
		executable = mock(clazz);
	}

	@Override
	public void thenThrow(Throwable throwable) {
		when(executable.getFirstResult()).thenThrow(throwable);
		doThrow(throwable).when(executable).forEachResult(anyObject());
		when(executable.execute()).thenThrow(throwable);
	}

	public void thenReturn(T ... results) {
		if (results.length == 0) {
			when(executable.getFirstResult()).thenThrow(new NoSuchElementException());
		} else {
			when(executable.getFirstResult()).thenReturn(results[0]);
		}
		doAnswer(invocation -> {
			final Consumer<T> consumer = invocation.getArgument(0);
			for (T result : results) {
				consumer.accept(result);
			}
			return null;
		}).when(executable).forEachResult(anyObject());
		when(executable.execute()).thenReturn(of(results));
	}

	@Override
	public E getMock() {
		return executable;
	}

}
