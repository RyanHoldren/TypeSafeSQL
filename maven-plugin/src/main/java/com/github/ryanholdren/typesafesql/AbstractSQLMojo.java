package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.AbstractJavaClassWriter.AbstractBuilder;
import static java.io.File.separator;
import java.io.IOException;
import static java.lang.System.lineSeparator;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import static java.util.Collections.newSetFromMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.HOURS;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.LITERAL;
import static java.util.regex.Pattern.compile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractSQLMojo extends AbstractMojo {

	@Parameter(defaultValue = "JDBC")
	protected TargetAPI api;

	@Parameter(defaultValue = "${project}")
	protected MavenProject project;

	private static final int NUMBER_OF_THREADS = 24;

	protected static Path replaceInPath(Path path, Pattern pattern, String replacement) {
		final Matcher matcher = pattern.matcher(path.toString());
		if (matcher.find()) {
			return Paths.get(matcher.replaceFirst(replacement));
		} else {
			return path;
		}
	}

	protected void process(List<String> sqlPathRoots, String javaPathRoot) throws MojoExecutionException, MojoFailureException {
		final Log log = getLog();
		final ExecutorService executor = createExectutorService();
		try {
			final Set<Path> javaPaths = newSetFromMap(new ConcurrentHashMap<>());
			final LongAdder created = new LongAdder();
			final LongAdder updated = new LongAdder();
			final LongAdder skipped = new LongAdder();
			for (String sqlPathRoot : sqlPathRoots) {
				final Path rootPath = Paths.get(sqlPathRoot);
				if (isDirectory(rootPath) == false) {
					log.error("Skipping processing SQL files in '" + sqlPathRoot + "' because it does not exist...");
					return;
				}
				log.info("Processing SQL files in '" + sqlPathRoot + "' into '" + javaPathRoot + "'...");
				walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path sqlPath, BasicFileAttributes attrs) throws IOException {
						if (sqlPath.toString().endsWith(".sql")) {
							final Path javaPath = getJavaPathFrom(sqlPath, sqlPathRoot, javaPathRoot);
							javaPaths.add(javaPath);
							if (exists(javaPath)) {
								final Instant lastModified = getLastModifiedTime(sqlPath).toInstant();
								final Instant lastGenerated = getLastModifiedTime(javaPath).toInstant();
								if (lastGenerated.isAfter(lastModified)) {
									skipped.increment();
									return CONTINUE;
								} else {
									updated.increment();
								}
							} else {
								created.increment();
							}
							executor.submit(() -> {
								try {
									log.info("Processing '" + sqlPath + "' into '" + javaPath + "'...");
									newClassWriterBuilder()
										.setTargetAPI(api)
										.setNamespace(getNamespaceFrom(javaPath, javaPathRoot))
										.setClassName(getClassNameFrom(javaPath))
										.setReader(sqlPath)
										.setWriter(javaPath)
										.writeClass();
								} catch (Exception exception) {
									log.error("Encounted exception while processing '" + sqlPath + "':", exception);
								}
							});
						}
						return CONTINUE;
					}
				});
			}
			executor.shutdown();
			if (executor.awaitTermination(1L, HOURS) == false) {
				log.error("Aborting the processing of SQL files because it did not finish within an hour!");
				return;
			}
			final LongAdder deleted = new LongAdder();
			final Path outputPath = Paths.get(javaPathRoot);
			if (isDirectory(outputPath)) {
				walkFileTree(outputPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path javaPath, BasicFileAttributes attrs) throws IOException {
						if (javaPaths.contains(javaPath) == false) {
							delete(javaPath);
							deleted.increment();
						}
						return CONTINUE;
					}
				});
			}
			log.info(
				"\n" +
				"\tCreated: " + created.longValue() + lineSeparator() +
				"\tUpdated: " + updated.longValue() + lineSeparator() +
				"\tSkipped: " + skipped.longValue() + lineSeparator() +
				"\tDeleted: " + deleted.longValue() + lineSeparator()
			);
		} catch (Exception exception) {
			log.error("Encounted exception while processing SQL files into '" + javaPathRoot + "':", exception);
		} finally {
			executor.shutdownNow();
		}
	}

	protected String getClassNameFrom(Path javaPath) {
		final String path = javaPath.toString();
		return path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
	}

	protected String getNamespaceFrom(Path javaPath, String javaPathRoot) {
		final String path = javaPath.toString();
		return path.substring(javaPathRoot.length(), path.lastIndexOf('/')).replace(separator, ".");
	}

	protected Path getJavaPathFrom(Path sqlPath, String sqlPathRoot, String javaPathRoot) {
		final Pattern rootPattern = compile(sqlPathRoot, LITERAL);
		final Pattern sqlPattern = compile("\\.sql$");
		return replaceInPath(
			replaceInPath(
				sqlPath,
				sqlPattern,
				".java"
			),
			rootPattern,
			javaPathRoot
		);
	}

	protected abstract AbstractBuilder newClassWriterBuilder();

	private ExecutorService createExectutorService() {
		final AtomicInteger numberOfThreads = new AtomicInteger(0);
		return newFixedThreadPool(
			NUMBER_OF_THREADS,
			runnable -> {
				final Thread thread = new Thread(runnable, "SQL Processing Worker #" + numberOfThreads.incrementAndGet());
				thread.setDaemon(true);
				return thread;
			}
		);
	}

}
