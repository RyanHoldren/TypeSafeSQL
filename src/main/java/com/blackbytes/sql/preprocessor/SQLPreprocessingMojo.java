package com.blackbytes.sql.preprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "compile-sql", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class SQLPreprocessingMojo extends AbstractMojo {

	private static final Pattern SQL = Pattern.compile("\\.sql$", Pattern.CASE_INSENSITIVE);

	@Parameter(property="project.compileSourceRoots", required=true, readonly=true)
	private List<String> compileSourceRoots;

	@Parameter(property="project.testCompileSourceRoots", required=true, readonly=true)
	private List<String> testCompileSourceRoots;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		for (String root : compileSourceRoots) {
			process(root);
		}
		for (String root : testCompileSourceRoots) {
			process(root);
		}
	}

	public void process(String root) throws MojoExecutionException, MojoFailureException {
		System.out.println("Preprocessing SQL files in '" + root + "'...");
		final Path rootPath = Paths.get(root);
		try {
			Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path sqlPath, BasicFileAttributes attrs) throws IOException {
					final Matcher matcher = SQL.matcher(sqlPath.toString());
					final Path javaPath = Paths.get(matcher.replaceFirst(".java"));
					if (javaPath.equals(sqlPath)) {
						return FileVisitResult.CONTINUE;
					}
					final String path = sqlPath.toString();
					final String className = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
					final String namespace = path.substring(rootPath.toString().length() + 1, path.lastIndexOf('/')).replace(File.separator.charAt(0), '.');
					try {
						SQLPreprocessor
							.newBuilder()
							.setNamespace(namespace)
							.setClassName(className)
							.setInput(sqlPath)
							.setOutput(javaPath)
							.preprocess();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
