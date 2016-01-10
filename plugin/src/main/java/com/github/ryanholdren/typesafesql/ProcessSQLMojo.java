package com.github.ryanholdren.typesafesql;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "process-sql", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class ProcessSQLMojo extends AbstractSQLMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final String target = project.getBuild().getDirectory();
		final String main = target + "/generated-sources/sql/";
		for (String root : project.getCompileSourceRoots()) {
			process(root, main);
		}
		project.addCompileSourceRoot(main);
	}

}
