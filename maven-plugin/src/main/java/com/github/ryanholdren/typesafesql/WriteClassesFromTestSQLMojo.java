package com.github.ryanholdren.typesafesql;

import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_TEST_SOURCES;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "write-classes-from-test-sql", defaultPhase = GENERATE_TEST_SOURCES, threadSafe = true)
public class WriteClassesFromTestSQLMojo extends AbstractSQLMojo {
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final String target = project.getBuild().getDirectory();
		final List<String> input = project.getTestCompileSourceRoots();
		final String output = target + "/generated-sources/test-sql/";
		process(input, output);
		project.addTestCompileSourceRoot(output);
	}
}
