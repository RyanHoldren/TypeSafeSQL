package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.DoubleStreamExecutable;
import java.util.NoSuchElementException;
import java.util.function.DoubleConsumer;
import static java.util.stream.DoubleStream.of;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DoubleStreamExecutableMocker implements ExecutableMocker {

	protected final DoubleStreamExecutable executable = mock(DoubleStreamExecutable.class);

	@Override
	public void thenThrow(Throwable throwable) {
		when(executable.getFirstResult()).thenThrow(throwable);
		doThrow(throwable).when(executable).forEachResult(anyObject());
		when(executable.execute()).thenThrow(throwable);
	}

	public void thenReturn(double ... results) {
		if (results.length == 0) {
			when(executable.getFirstResult()).thenThrow(new NoSuchElementException());
		} else {
			when(executable.getFirstResult()).thenReturn(results[0]);
		}
		doAnswer(invocation -> {
			final DoubleConsumer consumer = invocation.getArgument(0);
			for (double result : results) {
				consumer.accept(result);
			}
			return null;
		}).when(executable).forEachResult(anyObject());
		when(executable.execute()).thenReturn(of(results));
	}

	@Override
	public DoubleStreamExecutable getMock() {
		return executable;
	}

}
