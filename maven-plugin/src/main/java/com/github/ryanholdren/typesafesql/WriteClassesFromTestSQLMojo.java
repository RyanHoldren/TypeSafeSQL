package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.JavaClassWriter.Builder;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_TEST_SOURCES;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "write-classes-from-test-sql", defaultPhase = GENERATE_TEST_SOURCES, threadSafe = true)
public class WriteClassesFromTestSQLMojo extends AbstractSQLMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final String target = project.getBuild().getDirectory();
		final List<String> input = project.getTestCompileSourceRoots();
		final String output = target + "/generated-sources/test-sql/";
		process(input, output);
		project.addTestCompileSourceRoot(output);
	}

	@Override
	protected Builder newClassWriterBuilder() {
		return JavaClassWriter.newBuilder();
	}

}
