package com.github.ryanholdren.typesafesql;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "process-test-sql", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, threadSafe = true)
public class ProcessTestSQLMojo extends AbstractSQLMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final String target = project.getBuild().getDirectory();
		final String test = target + "/generated-sources/test-sql/";
		for (String root : project.getTestCompileSourceRoots()) {
			process(root, test);
		}
		project.addTestCompileSourceRoot(test);
	}

}
