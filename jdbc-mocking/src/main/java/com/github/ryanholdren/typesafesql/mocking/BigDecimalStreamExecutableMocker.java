package com.github.ryanholdren.typesafesql.mocking;

import com.github.ryanholdren.typesafesql.BigDecimalStreamExecutable;
import java.math.BigDecimal;

public class BigDecimalStreamExecutableMocker extends ObjectStreamExecutableMocker<BigDecimal, BigDecimalStreamExecutable> {
	public BigDecimalStreamExecutableMocker() {
		super(BigDecimalStreamExecutable.class);
	}
}
