package com.github.ryanholdren.typesafesql.parameters;

public enum ParameterType {

	DECIMAL {
		@Override
		public BigDecimalParameter createParameter(String name) {
			return new BigDecimalParameter(name);
		}
	},
	VARCHAR {
		@Override
		public StringParameter createParameter(String name) {
			return new StringParameter(name);
		}
	},
	DATE {
		@Override
		public LocalDateParameter createParameter(String name) {
			return new LocalDateParameter(name);
		}
	},
	DOUBLE {
		@Override
		public DoubleParameter createParameter(String name) {
			return new DoubleParameter(name);
		}
	},
	INTEGER {
		@Override
		public IntegerParameter createParameter(String name) {
			return new IntegerParameter(name);
		}
	},
	VARBINARY {
		@Override
		public ByteArrayParameter createParameter(String name) {
			return new ByteArrayParameter(name);
		}
	},
	BIGINT {
		@Override
		public LongParameter createParameter(String name) {
			return new LongParameter(name);
		}
	},
	TIMESTAMP {
		@Override
		public InstantParameter createParameter(String name) {
			return new InstantParameter(name);
		}
	},
	BOOLEAN {
		@Override
		public BooleanParameter createParameter(String name) {
			return new BooleanParameter(name);
		}
	},
	UUID {
		@Override
		public UUIDParameter createParameter(String name) {
			return new UUIDParameter(name);
		}
	};

	public abstract Parameter createParameter(String name);

}
