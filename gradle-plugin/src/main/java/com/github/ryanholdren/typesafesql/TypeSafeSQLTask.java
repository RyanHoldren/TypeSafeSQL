package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.TargetAPI.JDBC;
import com.google.common.base.Splitter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RelativePath;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

public class TypeSafeSQLTask extends DefaultTask {

	private String sourceDirectory;
	private String destinationDirectory;
	private TargetAPI defaultApi = JDBC;

	public TypeSafeSQLTask(String sourceDirectory, String destinationDirectory) {
		this.sourceDirectory = sourceDirectory;
		this.destinationDirectory = destinationDirectory;
	}

	public TypeSafeSQLTask setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
		return this;
	}

	public TypeSafeSQLTask setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
		return this;
	}

	public TypeSafeSQLTask setTargetAPI(TargetAPI api) {
		this.defaultApi = api;
		return this;
	}

	@TaskAction
	public void createJavaFilesFromSQL() {
		final Project project = getProject();
		final Gradle gradle = project.getGradle();
		gradle.addListener(this);
		final Logger logger = getLogger();
		logger.info(
			"Creating Java files from SQL files in '%s' and writing them to '%s'...",
			sourceDirectory,
			destinationDirectory
		);
		final File output = project.file(destinationDirectory);
		final FileTree files = project.files(sourceDirectory).getAsFileTree();
		if (files.isEmpty()) {
			logger.info("There are no files to be processed!");
		}
		files.visit(details -> {
			if (details.isDirectory()) {
				return;
			}
			TargetAPI api = defaultApi;
			final String fileName = details.getName();
			final List<String> parts = Splitter.on('.').splitToList(fileName);
			final int indexOfLastPart = parts.size() - 1;
			if (indexOfLastPart == 0) {
				return;
			}
			final String extension = parts.get(indexOfLastPart);
			if ("sql".equalsIgnoreCase(extension) == false) {
				return;
			}
			if (parts.size() > 2) {
				final int indexOfSecondLastPart = indexOfLastPart - 1;
				api = TargetAPI.valueOf(parts.get(indexOfSecondLastPart).toUpperCase());
			}
			final String className = parts.get(0);
			final RelativePath relative = details.getRelativePath();
			logger.info("Creating Java file from '%s'...", relative);
			final Path sqlFile = details.getFile().toPath();
			final Path javaFile = relative.replaceLastName(className + ".java").getFile(output).toPath();
			final String path = details.getPath();
			final String namespace = path.substring(0, path.lastIndexOf('/')).replace('/', '.');
			try {
				JavaClassWriter
					.newBuilder()
					.setTargetAPI(api)
					.setNamespace(namespace)
					.setClassName(className)
					.setReader(sqlFile)
					.setWriter(javaFile)
					.writeClass();
			} catch (IOException exception) {
				throw new RuntimeException(exception);
			}
		});
	}

}
