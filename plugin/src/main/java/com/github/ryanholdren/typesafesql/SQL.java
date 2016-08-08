package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.columns.ResultColumn;
import com.github.ryanholdren.typesafesql.columns.ResultColumnType;
import com.github.ryanholdren.typesafesql.parameters.Parameter;
import com.github.ryanholdren.typesafesql.parameters.ParameterType;
import static com.google.common.base.CharMatcher.WHITESPACE;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.copyOf;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQL implements RequiresImports {

	protected static final Pattern PARAMETER = Pattern.compile("\\{(?<direction>in|out):(?<type>[A-Z][A-Z_]+):(?<name>[a-z][a-zA-Z0-9]*)\\}");

	protected final ImmutableList<ResultColumn> columns;
	protected final ImmutableList<Parameter> parameters;
	protected final ImmutableList<String> lines;

	public SQL(BufferedReader reader) throws IOException {
		final LinkedHashMap<String, ResultColumn> columnsBuilder = new LinkedHashMap<>();
		final LinkedHashMap<String, Parameter> parametersBuilder = new LinkedHashMap<>();
		final ImmutableList.Builder<String> linesBuilder = ImmutableList.builder();
		int parameterPosition = 1;
		int columnPosition = 1;
		while (true) {
			final String line = reader.readLine();
			if (line == null) {
				break;
			}
			if (isEntirelyWhitespace(line)) {
				continue;
			}
			final Matcher matcher = PARAMETER.matcher(line);
			if (matcher.find()) {
				final StringBuilder lineBuilder = new StringBuilder();
				int end = 0;
				do {
					final int start = matcher.start();
					final String before = line.substring(end, start);
					lineBuilder.append(before);
					final String direction = matcher.group("direction");
					final String nameOfType = matcher.group("type");
					final String name = matcher.group("name");
					if (direction.equals("in")) {
						final ParameterType type = ParameterType.valueOf(nameOfType);
						final Parameter parameter = parametersBuilder.computeIfAbsent(name, type::createParameter);
						parameter.addPosition(parameterPosition ++);
						lineBuilder.append('?');
					} else {
						final ResultColumnType type = ResultColumnType.valueOf(nameOfType);
						final ResultColumn column = type.createResultColumn(columnPosition ++, name);
						if (columnsBuilder.put(name, column) != null) {
							throw new IllegalStateException("Output names must be unique!");
						}
						lineBuilder.append(name);
					}
					end = matcher.end();
				} while (matcher.find());
				final String after = line.substring(end, line.length());
				lineBuilder.append(after);
				linesBuilder.add(lineBuilder.toString());
			} else {
				linesBuilder.add(line);
			}
		}
		lines = linesBuilder.build();
		columns = copyOf(columnsBuilder.values());
		parameters = copyOf(parametersBuilder.values());
	}

	private static boolean isEntirelyWhitespace(String string) {
		return WHITESPACE.matchesAllOf(string);
	}

	public ImmutableList<String> getLines() {
		return lines;
	}

	@Override
	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		parameters.stream().forEach(parameter -> {
			parameter.forEachRequiredImport(action, isNotMocking);
		});
		if (isNotMocking && getNumberOfColumns() > 1) {
			columns.stream().forEach(column -> {
				column.forEachRequiredImport(action, isNotMocking);
			});
		}
	}

	public boolean hasParameters() {
		return parameters.isEmpty() == false;
	}

	public int getNumberOfParameters() {
		return parameters.size();
	}

	public Optional<Parameter> firstParameter() {
		final Iterator<Parameter> iterator = parameters.iterator();
		if (iterator.hasNext()) {
			return of(iterator.next());
		} else {
			return empty();
		}
	}

	public ImmutableList<ResultColumn> getColumns() {
		return columns;
	}

	public ImmutableList<Parameter> getParameters() {
		return parameters;
	}

	@FunctionalInterface
	public interface ParameterConsumer<E extends Exception> {
		void accept(Parameter parameter, Parameter next) throws E;
	}

	public <E extends Exception> void forEachParameter(ParameterConsumer<E> action) throws E {
		final Iterator<Parameter> iterator = parameters.iterator();
		if (iterator.hasNext()) {
			Parameter current = iterator.next();
			do {
				final Parameter next;
				if (iterator.hasNext()) {
					next = iterator.next();
				} else {
					next = null;
				}
				action.accept(current, next);
				current = next;
			} while (current != null);
		}
	}

	public boolean hasColumns() {
		return columns.isEmpty() == false;
	}

	public int getNumberOfColumns() {
		return columns.size();
	}

	public boolean needsResultClass() {
		return columns.size() > 1;
	}

	@FunctionalInterface
	public interface ResultColumnConsumer<E extends Exception> {
		void accept(ResultColumn column) throws E;
	}

	public <E extends Exception> void forEachColumn(ResultColumnConsumer<E> action) throws E {
		for (ResultColumn column : columns) {
			action.accept(column);
		}
	}

	public String getClassNameOfExecutable() {
		final Iterator<ResultColumn> iterator = columns.iterator();
		if (iterator.hasNext()) {
			final ResultColumn column = iterator.next();
			if (iterator.hasNext()) {
				return "ResultStreamExecutable";
			} else {
				return column.getNameOfResultWhenThisIsTheOnlyColumn();
			}
		} else {
			return "UpdateExecutable";
		}
	}

	public String getClassNameOfExecutableParentClass() {
		final Iterator<ResultColumn> iterator = columns.iterator();
		if (iterator.hasNext()) {
			final ResultColumn column = iterator.next();
			if (iterator.hasNext()) {
				return "ObjectStreamExecutable";
			} else {
				return column.getNameOfResultWhenThisIsTheOnlyColumn();
			}
		} else {
			return "UpdateExecutable";
		}
	}

	public String getTypeOfExecutableMocker() {
		final Iterator<ResultColumn> iterator = columns.iterator();
		if (iterator.hasNext()) {
			final ResultColumn column = iterator.next();
			if (iterator.hasNext()) {
				return "ObjectStreamExecutableMocker<Result, ResultStreamExecutable>";
			} else {
				return column.getNameOfResultWhenThisIsTheOnlyColumn() + "Mocker";
			}
		} else {
			return "UpdateExecutableMocker";
		}
	}

	public String getClassOfExecutableMocker() {
		return getClassNameOfExecutableParentClass() + "Mocker";
	}

}
