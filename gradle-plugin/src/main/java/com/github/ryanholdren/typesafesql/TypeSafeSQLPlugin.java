package com.github.ryanholdren.typesafesql;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TypeSafeSQLPlugin implements Plugin<Project> {

	@Override
	public void apply(Project target) {
		target.getTasks().create("createJavaFilesFromSQL", TypeSafeSQLMainTask.class);
		target.getTasks().create("createTestJavaFilesFromSQL", TypeSafeSQLTestTask.class);
	}

}