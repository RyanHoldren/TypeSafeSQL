package com.blackbytes.typesafesql;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "compile-sql", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class SQLPreprocessingMojo extends AbstractMojo {

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
		final String test = target + "/generated-sources/test-sql/";
		for (String root : project.getTestCompileSourceRoots()) {
			process(root, test);
		}
		project.addTestCompileSourceRoot(test);
	}

	private static Path replaceInPath(Path path, Pattern pattern, String replacement) {
		final Matcher matcher = pattern.matcher(path.toString());
		if (matcher.find()) {
			return Paths.get(matcher.replaceFirst(replacement));
		} else {
			return path;
		}
	}

	public void process(String root, String output) throws MojoExecutionException, MojoFailureException {
		System.out.println("Preprocessing SQL files in '" + root + "' into '" + output + "'...");
		final Pattern rootPattern = Pattern.compile(root, Pattern.LITERAL);
		final Pattern sqlPattern = Pattern.compile("\\.sql$");
		final Path rootPath = Paths.get(root);
		try {
			Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path sqlPath, BasicFileAttributes attrs) throws IOException {
					if (sqlPath.toString().endsWith(".sql")) {
						final Path javaPath = replaceInPath(
							replaceInPath(
								sqlPath,
								sqlPattern,
								".java"
							),
							rootPattern,
							output
						);
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
							throw new RuntimeException(exception);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

}
