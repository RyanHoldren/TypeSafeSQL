package com.github.ryanholdren.typesafesql.columns;

public enum ResultColumnType {

	VARCHAR {
		@Override
		public StringResultColumn createResultColumn(int indexInResultSet, String name) {
			return new StringResultColumn(indexInResultSet, name);
		}
	},

	DOUBLE {
		@Override
		public DoubleResultColumn createResultColumn(int indexInResultSet, String name) {
			return new DoubleResultColumn(indexInResultSet, name);
		}
	},

	INTEGER {
		@Override
		public IntegerResultColumn createResultColumn(int indexInResultSet, String name) {
			return new IntegerResultColumn(indexInResultSet, name);
		}
	},

	NULLABLE_DOUBLE {
		@Override
		public OptionalDoubleResultColumn createResultColumn(int indexInResultSet, String name) {
			return new OptionalDoubleResultColumn(indexInResultSet, name);
		}
	},

	NULLABLE_INTEGER {
		@Override
		public OptionalIntegerResultColumn createResultColumn(int indexInResultSet, String name) {
			return new OptionalIntegerResultColumn(indexInResultSet, name);
		}
	},

	VARBINARY {
		@Override
		public ByteArrayResultColumn createResultColumn(int indexInResultSet, String name) {
			return new ByteArrayResultColumn(indexInResultSet, name);
		}
	},

	NULLABLE_BIGINT {
		@Override
		public OptionalLongResultColumn createResultColumn(int indexInResultSet, String name) {
			return new OptionalLongResultColumn(indexInResultSet, name);
		}
	},

	BIGINT {
		@Override
		public LongResultColumn createResultColumn(int indexInResultSet, String name) {
			return new LongResultColumn(indexInResultSet, name);
		}
	},

	TIMESTAMP {
		@Override
		public InstantResultColumn createResultColumn(int indexInResultSet, String name) {
			return new InstantResultColumn(indexInResultSet, name);
		}
	},

	BOOLEAN {
		@Override
		public BooleanResultColumn createResultColumn(int indexInResultSet, String name) {
			return new BooleanResultColumn(indexInResultSet, name);
		}
	};

	public abstract ResultColumn createResultColumn(int indexInResultSet, String name);

}
