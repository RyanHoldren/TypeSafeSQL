package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.parameters.Parameter;
import static com.github.ryanholdren.typesafesql.parameters.Parameter.capitalize;
import java.io.IOException;
import java.util.function.Consumer;

public class JavaMockWriter extends AbstractJavaClassWriter {

	public static class Builder extends AbstractBuilder {
		@Override
		protected AbstractJavaClassWriter createClassWriter() throws IOException {
			return new JavaMockWriter(this);
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	private JavaMockWriter(Builder builder) throws IOException {
		super(builder);
	}

	@Override
	public void writeClass() throws IOException {
		writeNamespace();
		writeImports();
		writeStartOfClass();
		writeParameterMockerInterfaces();
		writeStartMockPreparedClass();
		writeParameterMockerImplementations();
		writeEndMockPreparedClass();
		writeMockUsingMethods();
		writeEndOfClass();
	}

	@Override
	protected void forEachImport(Consumer<String> action) {
		super.forEachImport(action);
		sql.forEachRequiredImport(action, false);
		action.accept("com.github.ryanholdren.typesafesql.mocking." + sql.getClassOfExecutableMocker());
		final String qualifiedNameOfClassBeingMocked = namespace + '.' + getNameOfClassBeingMocked();
		action.accept("static " + qualifiedNameOfClassBeingMocked + ".using");
		for (Parameter parameter : sql.getParameters()) {
			action.accept(qualifiedNameOfClassBeingMocked + ".Needs" + parameter.getCapitalizedName());
		}
		if (sql.needsResultClass()) {
			action.accept(qualifiedNameOfClassBeingMocked + ".Result");
			action.accept(qualifiedNameOfClassBeingMocked + ".ResultStreamExecutable");
		}
		if (sql.hasParameters()) {
			action.accept("static org.mockito.Mockito.mock");
		}
		action.accept("static org.mockito.Mockito.when");
	}

	private String getNameOfClassBeingMocked() {
		return className.substring(0, className.length() - "Mocker".length());
	}

	private void writeParameterMockerInterfaces() throws IOException {
		sql.forEachParameter((parameter, nextParameter) -> {
			final String name = parameter.getName();
			final String argumentType = parameter.getArgumentType();
			final String capitalizedName = capitalize(name);
			writer.writeLine("public interface ", capitalizedName, "Mocker {");
			if (nextParameter == null) {
				writer.write(sql.getTypeOfExecutableMocker());
			} else {
				writer.write(nextParameter.getCapitalizedName());
				writer.write("Mocker");
			}
			writer.writeLine(" when", capitalizedName, "Equals(", argumentType, ' ', name, ");");
			writer.writeLine('}');
			writer.writeEmptyLine();
		});
	}

	private void writeStartMockPreparedClass() throws IOException {
		writer.write("private static final class Mocker extends ");
		writer.write(sql.getTypeOfExecutableMocker());
		if (sql.hasParameters()) {
			writer.write(" implements ");
			sql.forEachParameter((parameter, nextParameter) -> {
				writer.write(parameter.getCapitalizedName());
				writer.write("Mocker");
				if (nextParameter != null) {
					writer.write(", ");
				}
			});
		}
		writer.writeLine(" {");
		writer.writeEmptyLine();
		if (sql.needsResultClass()) {
			writer.writeLine("public Mocker() {");
			writer.writeLine("super(ResultStreamExecutable.class);");
			writer.writeLine("}");
			writer.writeEmptyLine();
		}
	}

	private void writeParameterMockerImplementations() throws IOException {
		sql.forEachParameter((parameter, nextParameter) -> {
			final String name = parameter.getName();
			final String argumentType = parameter.getArgumentType();
			final String capitalizedName = capitalize(name);
			final String returnType = nextParameter == null ? sql.getTypeOfExecutableMocker() : capitalize(nextParameter.getName()) + "Mocker";
			final String nextMocker = nextParameter == null ? "getMock()" : "needs" + capitalize(nextParameter.getName());
			writer.writeLine("private final Needs", capitalizedName, " needs", capitalizedName, " = mock(Needs", capitalizedName, ".class);");
			writer.writeEmptyLine();
			writer.writeLine("@Override");
			writer.writeLine("public ", returnType, " when", capitalizedName, "Equals(", argumentType, ' ', name, ") {");
			writer.writeLine("when(needs", capitalizedName, ".with", capitalizedName, '(', name, ")).thenReturn(", nextMocker, ");");
			writer.writeLine("return this;");
			writer.writeLine('}');
			writer.writeEmptyLine();
		});
	}

	private void writeEndMockPreparedClass() throws IOException {
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

	private void writeMockUsingMethods() throws IOException {
		final String nameOfFirstInterface = sql.firstParameter().map(parameter -> {
			return parameter.getCapitalizedName() + "Mocker";
		}).orElse(sql.getTypeOfExecutableMocker());
		writer.writeLine("public static final ", nameOfFirstInterface, " whenConnectionEquals(Connection connection, ConnectionHandling handling) {");
		writer.writeLine("final Mocker mocker = new Mocker();");
		final String firstMock = sql.firstParameter().map(parameter -> {
			return "needs" + parameter.getCapitalizedName();
		}).orElse("getMock()");
		writer.writeLine("when(using(connection, handling)).thenReturn(mocker.", firstMock, ");");
		writer.writeLine("return mocker;");
		writer.writeLine('}');
		writer.writeEmptyLine();
	}

}