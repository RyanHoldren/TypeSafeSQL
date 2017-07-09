package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.parameters.Parameter;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import static java.nio.file.Files.*;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import java.util.TreeSet;
import java.util.function.Consumer;

public abstract class AbstractJavaClassWriter {

	protected static final Charset UTF8 = Charset.forName("UTF-8");

	public static String capitalize(String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}

	public abstract static class AbstractBuilder {

		private SQL sql;
		private AutoIndentingWriter writer;
		private String className;
		private String namespace;

		protected TargetAPI api;

		public AbstractBuilder setReader(Path file) throws IOException {
			try (BufferedReader reader = newBufferedReader(file, UTF8)) {
				sql = new SQL(api, reader);
			}
			return this;
		}

		public AbstractBuilder setWriter(Path file) throws IOException {
			createDirectories(file.getParent());
			this.writer = new AutoIndentingWriter(newBufferedWriter(file, UTF8, CREATE, TRUNCATE_EXISTING));
			return this;
		}

		public AbstractBuilder setClassName(String className) {
			this.className = className;
			return this;
		}

		public AbstractBuilder setNamespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public AbstractBuilder setTargetAPI(TargetAPI api) {
			this.api = api;
			return this;
		}

		public void writeClass() throws IOException {
			final AbstractJavaClassWriter classWriter = createClassWriter();
			try {
				classWriter.writeClass();
			} finally {
				writer.flush();
				writer.close();
			}
		}

		protected abstract AbstractJavaClassWriter createClassWriter() throws IOException;

	}

	protected final AutoIndentingWriter writer;
	protected final String className;
	protected final String namespace;
	protected final SQL sql;

	public AbstractJavaClassWriter(AbstractBuilder builder) throws IOException {
		this.namespace = builder.namespace;
		this.className = builder.className;
		this.writer = builder.writer;
		this.sql = builder.sql;
	}

	protected abstract void writeClass() throws IOException;

	protected void writeNamespace() throws IOException {
		writer.writeLine("package ", namespace, ";");
		writer.writeEmptyLine();
	}

	protected abstract void forEachImport(Consumer<String> action);

	protected void writeImports() throws IOException {
		final TreeSet<String> imports = new TreeSet<>();
		forEachImport(imports::add);
		for (String classNameOfImport : imports) {
			writer.writeLine("import ", classNameOfImport, ';');
		}
		writer.writeEmptyLine();
	}

	protected void writeStartOfClass() throws IOException {
		writer.writeLine("public final class ", className, " {");
		writer.writeEmptyLine();
	}

	protected String getNameOfFirstInterface() {
		return sql.firstParameter().map(Parameter::getNameOfInterface).orElse(getClassNameOfExecutable());
	}

	protected abstract String getClassNameOfExecutable();

	protected void writeEndOfClass() throws IOException {
		writer.writeLine("}");
	}
}
