package com.github.ryanholdren.typesafesql;

import com.github.ryanholdren.typesafesql.columns.ParameterImports;
import com.github.ryanholdren.typesafesql.columns.ResultColumn;
import com.github.ryanholdren.typesafesql.columns.ResultColumnImports;
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
import static java.util.regex.Pattern.*;

public class SQL {

	protected static final String IDENTIFIER = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
	protected static final Pattern IMPLEMENTS = compile("^\\{implements:(?<className>" + IDENTIFIER + "(?:\\." + IDENTIFIER + ")*)\\}$");
	protected static final Pattern PARAMETER = compile("\\{(?<direction>in|out):(?<type>[A-Z][A-Z_]+):(?<name>[a-z][a-zA-Z0-9]*)\\}");

	protected final ImmutableList<ResultColumn> columns;
	protected final ImmutableList<Parameter> parameters;
	protected final ImmutableList<String> lines;
	protected final ImmutableList<String> interfaces;

	public SQL(TargetAPI api, BufferedReader reader) throws IOException {
		final LinkedHashMap<String, ResultColumn> columnsBuilder = new LinkedHashMap<>();
		final LinkedHashMap<String, Parameter> parametersBuilder = new LinkedHashMap<>();
		final ImmutableList.Builder<String> linesBuilder = ImmutableList.builder();
		final ImmutableList.Builder<String> interfacesBuilder = ImmutableList.builder();
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
			final Matcher interfaceMatcher = IMPLEMENTS.matcher(line);
			if (interfaceMatcher.find()) {
				final String className = interfaceMatcher.group("className");
				interfacesBuilder.add(className);
				continue;
			}
			final Matcher parameterMatcher = PARAMETER.matcher(line);
			if (parameterMatcher.find()) {
				final StringBuilder lineBuilder = new StringBuilder();
				int end = 0;
				do {
					final int start = parameterMatcher.start();
					final String before = line.substring(end, start);
					lineBuilder.append(before);
					final String direction = parameterMatcher.group("direction");
					final String nameOfType = parameterMatcher.group("type");
					final String name = parameterMatcher.group("name");
					if (direction.equals("in")) {
						final ParameterType type = ParameterType.valueOf(nameOfType);
						final Parameter parameter = parametersBuilder.computeIfAbsent(name, type::createParameter);
						parameter.addPosition(parameterPosition);
						lineBuilder.append(api.getParameterPlaceholder(parameterPosition));
						parameterPosition ++;
						lineBuilder.append("::");
						lineBuilder.append(parameter.getCast());
					} else {
						final ResultColumnType type = ResultColumnType.valueOf(nameOfType);
						final ResultColumn column = type.createResultColumn(columnPosition ++, name);
						if (columnsBuilder.put(name, column) != null) {
							throw new IllegalStateException("Output names must be unique!");
						}
						lineBuilder.append(name);
					}
					end = parameterMatcher.end();
				} while (parameterMatcher.find());
				final String after = line.substring(end, line.length());
				lineBuilder.append(after);
				linesBuilder.add(lineBuilder.toString());
			} else {
				linesBuilder.add(line);
			}
		}
		lines = linesBuilder.build();
		interfaces = interfacesBuilder.build();
		columns = copyOf(columnsBuilder.values());
		parameters = copyOf(parametersBuilder.values());
	}

	private static boolean isEntirelyWhitespace(String string) {
		return WHITESPACE.matchesAllOf(string);
	}

	public ImmutableList<String> getLines() {
		return lines;
	}

	public void forEachRequiredImport(Consumer<String> action, boolean isNotMocking) {
		parameters.stream().forEach(parameter -> {
			parameter.accept(ParameterImports.VISITOR).stream().forEach(action);
		});
		columns.stream().forEach(column -> {
			column.accept(ResultColumnImports.VISITOR).stream().forEach(action);
		});
	}

	public boolean needsParameterInterface() {
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

	public boolean hasInterfaces() {
		return interfaces.size() > 0;
	}

	public ImmutableList<String> getInterfaces() {
		return interfaces;
	}

	@FunctionalInterface
	public interface ParameterConsumer<E extends Exception> {
		void accept(Parameter parameter) throws E;
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
				action.accept(current);
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

	public boolean needsResultInterface() {
		return columns.size() > 1;
	}

	@FunctionalInterface
	public interface ResultColumnConsumer<E extends Exception> {

		default void acceptFirst(ResultColumn column) throws E {
			accept(column);
		}

		void accept(ResultColumn column) throws E;

		default void acceptLast(ResultColumn column) throws E {
			accept(column);
		}

	}

	public <E extends Exception> void forEachColumn(ResultColumnConsumer<E> action) throws E {
		final int indexOfLast = columns.size() - 1;
		for (int index = 0; index <= indexOfLast; index++) {
			final ResultColumn column = columns.get(index);
			if (index == 0) {
				action.acceptFirst(column);
			} else if (index == indexOfLast) {
				action.acceptLast(column);
			} else {
				action.accept(column);
			}
		}
	}

}
