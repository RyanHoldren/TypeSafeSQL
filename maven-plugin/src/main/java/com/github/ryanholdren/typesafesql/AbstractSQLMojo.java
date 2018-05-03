package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.AbstractJavaClassWriter.UTF8;
import static com.github.ryanholdren.typesafesql.AutoIndentingWriter.NEW_LINE;
import static com.github.ryanholdren.typesafesql.JavaClassWriter.newBuilder;
import static java.io.File.separator;
import java.io.IOException;
import java.io.Writer;
import static java.lang.System.lineSeparator;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import static java.util.Collections.newSetFromMap;
import java.util.Iterator;
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
			final List<String> interfaces = new ArrayList<>();
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
							final String namespace = getNamespaceFrom(javaPath, javaPathRoot);
							final String className = getClassNameFrom(javaPath);
							interfaces.add(namespace + '.' + className);
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
									newBuilder()
										.setTargetAPI(api)
										.setNamespace(namespace)
										.setClassName(className)
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
			final String namespace = getNamespaceFrom(interfaces);
			final Path pathToDatabaseFile = Paths.get(javaPathRoot, namespace.replace(".", separator), "Database.java");
			javaPaths.add(pathToDatabaseFile);
			try {
				log.info("Updating Database interface '" + pathToDatabaseFile + "'...");
				createDirectories(pathToDatabaseFile.getParent());
				try (final Writer writer = newBufferedWriter(pathToDatabaseFile, UTF8, CREATE, TRUNCATE_EXISTING)) {
					writer.write("package ");
					writer.write(namespace);
					writer.write(';');
					writer.write(NEW_LINE);
					writer.write(NEW_LINE);
					writer.write("public interface Database extends");
					final Iterator<String> iterator = interfaces.iterator();
					while (iterator.hasNext()) {
						writer.write(NEW_LINE);
						writer.write('\t');
						writer.write(iterator.next());
						if (iterator.hasNext()) {
							writer.write(',');
						}
					}
					writer.write(" {}");
					writer.write(NEW_LINE);
				}
			} catch (Exception exception) {
				log.error("An error occurred while writing Database interface: " + exception);
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

	protected String getNamespaceFrom(List<String> fullyQualifiedClassNames) {
		final String prefix = longestCommonPrefix(fullyQualifiedClassNames);
		return prefix.substring(0, prefix.lastIndexOf('.'));
	}

	public String longestCommonPrefix(List<String> strings) {
		final int numberOfStrings = strings.size();
		if (strings == null || numberOfStrings == 0) {
			return "";
		}

		if (numberOfStrings == 1) {
			for (String string : strings) {
				return string;
			}
		}

		int minimumLength = numberOfStrings + 1;

		for (String str : strings) {
			if (minimumLength > str.length()) {
				minimumLength = str.length();
			}
		}

		for (int x = 0; x < minimumLength; x++) {
			for (int y = 0; y < numberOfStrings - 1; y++) {
				String s1 = strings.get(y);
				String s2 = strings.get(y + 1);
				if (s1.charAt(x) != s2.charAt(x)) {
					return s1.substring(0, x);
				}
			}
		}

		return strings.get(0).substring(0, minimumLength);
	}

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
