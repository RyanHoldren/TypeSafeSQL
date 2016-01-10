package com.github.ryanholdren.typesafesql;

public enum SQLParameterType {

	VARCHAR {

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
		public String getGetter(int position, String nameOfVariable) {
			return super.getGetter(position, nameOfVariable) + ".toInstant()";
		}

		@Override
		public String getNameOfSingleParameterExecutor() {
			return "InstantStreamExecutable";
		}

	};

	public abstract String getNameOfJavaType();
	public abstract String getNameOfMethodInPreparedStatement();
	public abstract String getNameOfMethodInResultSet();
	public abstract String getNameOfSingleParameterExecutor();

	public String getSetter(int position, String nameOfVariable) {
		return getNameOfMethodInPreparedStatement() + "(" + position + ", " + nameOfVariable + ")";
	}

	public String getGetter(int position, String nameOfVariable) {
		return getNameOfMethodInPreparedStatement() + "(" + position + ", " + nameOfVariable + ")";
	}

}
