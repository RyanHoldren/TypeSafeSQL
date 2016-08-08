package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.IntStreamExecutable;
import java.util.NoSuchElementException;
import java.util.function.IntConsumer;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static java.util.stream.IntStream.of;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class IntStreamExecutableMocker implements ExecutableMocker {

	protected final IntStreamExecutable executable = mock(IntStreamExecutable.class);

	@Override
	public void thenThrow(Throwable throwable) {
		when(executable.getFirstResult()).thenThrow(throwable);
		doThrow(throwable).when(executable).forEachResult(anyObject());
		when(executable.execute()).thenThrow(throwable);
	}

	public void thenReturn(int ... results) {
		if (results.length == 0) {
			when(executable.getFirstResult()).thenThrow(new NoSuchElementException());
		} else {
			when(executable.getFirstResult()).thenReturn(results[0]);
		}
		doAnswer(invocation -> {
			final IntConsumer consumer = invocation.getArgumentAt(0, IntConsumer.class);
			for (int result : results) {
				consumer.accept(result);
			}
			return null;
		}).when(executable).forEachResult(anyObject());
		when(executable.execute()).thenReturn(of(results));
	}

	@Override
	public IntStreamExecutable getMock() {
		return executable;
	}

}
