package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.JavaMockWriter.Builder;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.LITERAL;
import static java.util.regex.Pattern.compile;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_TEST_SOURCES;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "write-mocking-classes-from-sql", defaultPhase = GENERATE_TEST_SOURCES, threadSafe = true)
public class WriteMockingClassesFromSQLMojo extends AbstractSQLMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final String target = project.getBuild().getDirectory();
		final List<String> input = project.getCompileSourceRoots();
		final String output = target + "/generated-sources/test-sql-mocks/";
		process(input, output);
		project.addTestCompileSourceRoot(output);
	}

	@Override
	protected Path getJavaPathFrom(Path sqlPath, String sqlFileRoot, String javaFileRoot) {
		final Pattern pattern = compile(".java", LITERAL);
		return replaceInPath(
			super.getJavaPathFrom(sqlPath, sqlFileRoot, javaFileRoot),
			pattern,
			"Mocker.java"
		);
	}

	@Override
	protected Builder newClassWriterBuilder() {
		return JavaMockWriter.newBuilder();
	}

}
