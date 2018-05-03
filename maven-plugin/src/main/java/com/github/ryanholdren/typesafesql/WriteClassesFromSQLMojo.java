package com.github.ryanholdren.typesafesql;

import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "write-classes-from-sql", defaultPhase = GENERATE_SOURCES, threadSafe = true)
public class WriteClassesFromSQLMojo extends AbstractSQLMojo {
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final String target = project.getBuild().getDirectory();
		final List<String> input = project.getCompileSourceRoots();
		final String output = target + "/generated-sources/sql/";
		process(input, output);
		project.addCompileSourceRoot(output);
	}
}
