package com.github.ryanholdren.typesafesql.parameters;

public enum ParameterType {

	VARCHAR {
		@Override
		public StringParameter createParameter(String name) {
			return new StringParameter(name);
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
	};

	public abstract Parameter createParameter(String name);

}
