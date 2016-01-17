package com.github.ryanholdren.typesafesql.parameters;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Parameters implements Iterable<Parameter> {

	private final LinkedHashMap<String, Parameter> parameters = new LinkedHashMap<>();

	private int position = 1;

	public void add(String name, String nameOfType) {
		final ParameterType type = ParameterType.valueOf(nameOfType);
		final Parameter parameter = parameters.compute(name, (existingName, existingParameter) -> {
			if (existingParameter == null) {
				return type.createParameter(name);
			} else {
				return existingParameter;
			}
		});
		parameter.addPosition(position);
		position ++;
	}

	public void writeInterfacesTo(AutoIndentingWriter writer, String lastReturnType) throws IOException {
		forEach((parameter, returnType) -> {
				parameter.writeInterfaceTo(writer, returnType);
		}, lastReturnType);
	}

	public void writeImplemenationOfInterfacesTo(AutoIndentingWriter writer, String lastReturnType) throws IOException {
		forEach((parameter, returnType) -> {
				parameter.writeImplementationOfInterfaceTo(writer, returnType);
		}, lastReturnType);
	}

	@Override
	public Iterator<Parameter> iterator() {
		return parameters.values().iterator();
	}

	private interface ParameterConsumer {
		void accept(Parameter parameter, String returnType) throws IOException;
	}

	private void forEach(ParameterConsumer action, String lastReturnType) throws IOException {
		final Iterator<Parameter> iterator = iterator();
		if (iterator.hasNext()) {
			Parameter current = iterator.next();
			do {
				final String returnType;
				final Parameter next;
				if (iterator.hasNext()) {
					next = iterator.next();
					returnType = next.getNameOfInterface();
				} else {
					next = null;
					returnType = lastReturnType;
				}
				action.accept(current, returnType);
				current = next;
			} while (current != null);
		}
	}

}
