package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.LongStreamExecutable;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static java.util.stream.LongStream.of;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class LongStreamExecutableMocker implements ExecutableMocker {

	protected final LongStreamExecutable executable = mock(LongStreamExecutable.class);

	@Override
	public void thenThrow(Throwable throwable) {
		when(executable.getFirstResult()).thenThrow(throwable);
		doThrow(throwable).when(executable).forEachResult(anyObject());
		when(executable.execute()).thenThrow(throwable);
	}

	public void thenReturn(long ... results) {
		if (results.length == 0) {
			when(executable.getFirstResult()).thenThrow(new NoSuchElementException());
		} else {
			when(executable.getFirstResult()).thenReturn(results[0]);
		}
		doAnswer(invocation -> {
			final LongConsumer consumer = invocation.getArgumentAt(0, LongConsumer.class);
			for (long result : results) {
				consumer.accept(result);
			}
			return null;
		}).when(executable).forEachResult(anyObject());
		when(executable.execute()).thenReturn(of(results));
	}

	@Override
	public LongStreamExecutable getMock() {
		return executable;
	}

}
