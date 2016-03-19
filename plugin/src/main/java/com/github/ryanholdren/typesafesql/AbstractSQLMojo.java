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
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

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
		final Path rootPath = Paths.get(root);
		final Log log = getLog();
		if (Files.isDirectory(rootPath) == false) {
			log.error("Skipping processing SQL files in '" + root + "' because it does not exist...");
			return;
		}
		log.info("Processing SQL files in '" + root + "' into '" + output + "'...");
		final Pattern rootPattern = Pattern.compile(root, Pattern.LITERAL);
		final Pattern sqlPattern = Pattern.compile("\\.sql$");
		final LongAdder created = new LongAdder();
		final LongAdder updated = new LongAdder();
		final LongAdder skipped = new LongAdder();
		final Set<Path> javaPaths = Collections.newSetFromMap(new ConcurrentHashMap<>());
		final AtomicInteger numberOfThreads = new AtomicInteger(0);
		final ExecutorService executor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(),
			runnable -> {
				final Thread thread = new Thread(runnable, "SQL Processing Worker #" + numberOfThreads.incrementAndGet());
				thread.setDaemon(true);
				return thread;
			}
		);
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
						javaPaths.add(javaPath);
						if (Files.exists(javaPath)) {
							final Instant lastModified = Files.getLastModifiedTime(sqlPath).toInstant();
							final Instant lastGenerated = Files.getLastModifiedTime(javaPath).toInstant();
							if (lastGenerated.isAfter(lastModified)) {
								skipped.increment();
								return FileVisitResult.CONTINUE;
							} else {
								updated.increment();
							}
						} else {
							created.increment();
						}
						executor.submit(() -> {
							try {
								final String path = sqlPath.toString();
								final String className = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
								final String namespace = path.substring(rootPath.toString().length() + 1, path.lastIndexOf('/')).replace(File.separator.charAt(0), '.');
								SQLProcessor
									.newBuilder()
									.setNamespace(namespace)
									.setClassName(className)
									.setReader(sqlPath)
									.setWriter(javaPath)
									.preprocess();
							} catch (Exception exception) {
								log.error("Encounted exception while processing '" + sqlPath + "':", exception);
							}
						});
					}
					return FileVisitResult.CONTINUE;
				}
			});
			executor.shutdown();
			if (executor.awaitTermination(1L, TimeUnit.HOURS) == false) {
				log.error("Aborting the processing of SQL files because it did not finish within an hour!");
				return;
			}
			final LongAdder deleted = new LongAdder();
			final Path outputPath = Paths.get(output);
			Files.walkFileTree(outputPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path javaPath, BasicFileAttributes attrs) throws IOException {
					if (javaPaths.contains(javaPath) == false) {
						Files.delete(javaPath);
						deleted.increment();
					}
					return FileVisitResult.CONTINUE;
				}
			});
			log.info(
				"\tCreated: " + created.longValue() + System.lineSeparator() +
				"\tUpdated: " + updated.longValue() + System.lineSeparator() +
				"\tSkipped: " + skipped.longValue() + System.lineSeparator() +
				"\tDeleted: " + deleted.longValue() + System.lineSeparator()
			);
		} catch (Exception exception) {
			log.error("Encounted exception while processing SQL files in '" + root + "':", exception);
		} finally {
			executor.shutdownNow();
		}
	}

}
