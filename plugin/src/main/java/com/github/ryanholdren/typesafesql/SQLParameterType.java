package com.github.ryanholdren.typesafesql;

public enum SQLParameterType {

	VARCHAR {

		@Override
		protected boolean canBeNull() {
			return true;
		}

		@Override
		public String getNameOfJavaType() {
			return "String";
		}

		@Override
		public String getNameOfMethodInPreparedStatement() {
			return "setString";
		}

		@Override
		public String getNameOfMethodInResultSet() {
			return "getString";
		}

		@Override
		public String getNameOfSingleParameterExecutor() {
			return "StringStreamExecutable";
		}

	},

	DOUBLE {

		@Override
		protected boolean canBeNull() {
			return false;
		}

		@Override
		public String getNameOfJavaType() {
			return "double";
		}

		@Override
		public String getNameOfMethodInPreparedStatement() {
			return "setDouble";
		}

		@Override
		public String getNameOfMethodInResultSet() {
			return "getDouble";
		}

		@Override
		public String getNameOfSingleParameterExecutor() {
			return "DoubleStreamExecutable";
		}

	},

	INTEGER {

		@Override
		protected boolean canBeNull() {
			return false;
		}

		@Override
		public String getNameOfJavaType() {
			return "int";
		}

		@Override
		public String getNameOfMethodInPreparedStatement() {
			return "setInt";
		}

		@Override
		public String getNameOfMethodInResultSet() {
			return "getInt";
		}

		@Override
		public String getNameOfSingleParameterExecutor() {
			return "IntStreamExecutable";
		}

	},

	VARBINARY {

		@Override
		protected boolean canBeNull() {
			return true;
		}

		@Override
		public String getNameOfJavaType() {
			return "byte[]";
		}

		@Override
		public String getNameOfMethodInPreparedStatement() {
			return "setBytes";
		}

		@Override
		public String getNameOfMethodInResultSet() {
			return "getBytes";
		}

		@Override
		public String getNameOfSingleParameterExecutor() {
			return "ByteArrayStreamExecutable";
		}

	},

	BIGINT {

		@Override
		protected boolean canBeNull() {
			return false;
		}

		@Override
		public String getNameOfJavaType() {
			return "long";
		}

		@Override
		public String getNameOfMethodInPreparedStatement() {
			return "setLong";
		}

		@Override
		public String getNameOfMethodInResultSet() {
			return "getLong";
		}

		@Override
		public String getNameOfSingleParameterExecutor() {
			return "LongStreamExecutable";
		}

	},

	TIMESTAMP {

		@Override
		protected boolean canBeNull() {
			return true;
		}

		@Override
		public String getNameOfJavaType() {
			return "Instant";
		}

		@Override
		public String getNameOfMethodInPreparedStatement() {
			return "setTimestamp";
		}

		@Override
		public String getSetter(int position, String nameOfVariable) {
			return getNameOfMethodInPreparedStatement() + "(" + position + ", Timestamp.from(" + nameOfVariable + "))";
		}

		@Override
		public String getNameOfMethodInResultSet() {
			return "getTimestamp";
		}

		@Override
		public String getGetter(int position) {
			return super.getGetter(position) + ".toInstant()";
		}

		@Override
		public String getNameOfSingleParameterExecutor() {
			return "InstantStreamExecutable";
		}

	};

	protected abstract boolean canBeNull();
	protected abstract String getNameOfMethodInPreparedStatement();
	protected abstract String getNameOfMethodInResultSet();

	public abstract String getNameOfJavaType();
	public abstract String getNameOfSingleParameterExecutor();

	public String getSetter(int position, String nameOfVariable) {
		return getNameOfMethodInPreparedStatement() + "(" + position + ", " + nameOfVariable + ")";
	}

	public String getGetter(int position) {
		return getNameOfMethodInResultSet() + "(" + position + ")";
	}

}
