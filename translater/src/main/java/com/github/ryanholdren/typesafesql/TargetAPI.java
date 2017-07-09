package com.github.ryanholdren.typesafesql;

public enum TargetAPI {
	PGASYNC {

		@Override
		String getFrameworkArtifactId() {
			return "pgasync-framework";
		}

		@Override
		String getParameterPlaceholder(int parameterPosition) {
			return "$" + parameterPosition;
		}

	},
	JDBC {

		@Override
		String getFrameworkArtifactId() {
			return "jdbc-framework";
		}

		@Override
		String getParameterPlaceholder(int parameterPosition) {
			return "?";
		}

	};
	abstract String getFrameworkArtifactId();
	abstract String getParameterPlaceholder(int parameterPosition);
}
