package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.JavaClassWriter.Builder;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "write-classes-from-sql", defaultPhase = GENERATE_SOURCES, threadSafe = true)
public class WriteClassesFromSQLMojo extends AbstractSQLMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final String target = project.getBuild().getDirectory();
		final List<String> input = project.getCompileSourceRoots();
		final String output = target + "/generated-sources/sql/";
		process(input, output);
		project.addCompileSourceRoot(output);
	}

	@Override
	protected Builder newClassWriterBuilder() {
		return JavaClassWriter.newBuilder();
	}

}
