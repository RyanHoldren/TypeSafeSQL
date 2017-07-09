package com.github.ryanholdren.typesafesql;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class TypeSafeSQLPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		final TaskContainer tasks = project.getTasks();
		tasks.create("createJavaFilesFromSQL", TypeSafeSQLMainTask.class);
		tasks.create("createTestJavaFilesFromSQL", TypeSafeSQLTestTask.class);
	}

}