package com.github.ryanholdren.typesafesql;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class AbstractSQLMojo extends AbstractMojo {

	private static Path replaceInPath(Path path, Pattern pattern, String replacement) {
		final Matcher matcher = pattern.matcher(path.toString());
		if (matcher.find()) {
			return Paths.get(matcher.replaceFirst(replacement));
		} else {
			return path;
		}
	}

	protected void process(String root, String output) throws MojoExecutionException, MojoFailureException {
		final Pattern rootPattern = Pattern.compile(root, Pattern.LITERAL);
		final Pattern sqlPattern = Pattern.compile("\\.sql$");
		final Path rootPath = Paths.get(root);
		if (Files.isDirectory(rootPath)) {
			System.out.println("Processing SQL files in '" + root + "' into '" + output + "'...");
			final LongAdder skipped = new LongAdder();
			final LongAdder generated = new LongAdder();
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
							if (Files.exists(javaPath)) {
								final Instant lastModified = Files.getLastModifiedTime(sqlPath).toInstant();
								final Instant lastGenerated = Files.getLastModifiedTime(javaPath).toInstant();
								if (lastGenerated.isAfter(lastModified)) {
									skipped.increment();
									return FileVisitResult.CONTINUE;
								}
							}
							final String path = sqlPath.toString();
							final String className = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
							final String namespace = path.substring(rootPath.toString().length() + 1, path.lastIndexOf('/')).replace(File.separator.charAt(0), '.');
							try {
								SQLProcessor
									.newBuilder()
									.setNamespace(namespace)
									.setClassName(className)
									.setReader(sqlPath)
									.setWriter(javaPath)
									.preprocess();
								generated.increment();
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
			System.out.println(
				String.format(
					"Generated %d SQL files and skipped unmodified %d files.",
					generated.longValue(),
					skipped.longValue()
				)
			);
		} else {
			System.out.println("Skipping processing SQL files in '" + root + "' because it does not exist...");
		}
	}

}
