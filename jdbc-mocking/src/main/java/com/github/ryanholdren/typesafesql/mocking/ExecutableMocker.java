package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.Executable;

public interface ExecutableMocker {
	void thenThrow(Throwable throwable);
	Executable getMock();
}
