package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.UpdateExecutable;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class UpdateExecutableMocker implements ExecutableMocker {

	protected final UpdateExecutable executable = mock(UpdateExecutable.class);

	@Override
	public void thenThrow(Throwable throwable) {
		when(executable.getNumberOfRowsAffected()).thenThrow(throwable);
	}

	public void thenAffect(int numberOfRows) {
		when(executable.getNumberOfRowsAffected()).thenReturn(numberOfRows);
	}

	@Override
	public UpdateExecutable getMock() {
		return executable;
	}

}
