package com.github.ryanholdren.typesafesql;

import static com.google.common.io.Files.getFileExtension;
import static com.google.common.io.Files.getNameWithoutExtension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RelativePath;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

public class TypeSafeSQLTask extends DefaultTask {

	private String sourceDirectory;
	private String destinationDirectory;

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

	@TaskAction
	public void createJavaFilesFromSQL() {
		final Logger logger = getLogger();
		logger.info(
			"Creating Java files from SQL files in '%s' and writing them to '%s'...",
			sourceDirectory,
			destinationDirectory
		);
		final Project project = getProject();
		final File output = project.file(destinationDirectory);
		final FileTree files = project.files(sourceDirectory).getAsFileTree();
		if (files.isEmpty()) {
			logger.info("There are no files to be processed!");
		}
		files.visit(details -> {
			if (details.isDirectory()) {
				return;
			}
			final String fileName = details.getName();
			if ("sql".equalsIgnoreCase(getFileExtension(fileName)) == false) {
				return;
			}
			final String className = getNameWithoutExtension(fileName);
			final RelativePath relative = details.getRelativePath();
			logger.info("Creating Java file from '%s'...", relative);
			final Path sqlFile = details.getFile().toPath();
			final Path javaFile = relative.replaceLastName(className + ".java").getFile(output).toPath();
			final String path = details.getPath();
			final String namespace = path.substring(0, path.lastIndexOf('/')).replace('/', '.');
			try {
				JavaClassWriter
					.newBuilder()
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
