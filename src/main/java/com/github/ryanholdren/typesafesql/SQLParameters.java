package com.github.ryanholdren.typesafesql;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class SQLParameters implements Iterable<SQLParameter> {

	private final LinkedHashMap<String, SQLParameter> parameters = new LinkedHashMap<>();

	private int position = 1;

	public SQLParameter add(String name, SQLParameterType type) {
		try {
			return parameters.compute(name, (existingName, existingParameter) -> {
				if (existingParameter == null) {
					return new SQLParameter(name, type, position);
				} else {
					existingParameter.addPosition(position);
					return existingParameter;
				}
			});
		} finally {
			position ++;
		}
	}

	@Override
	public Iterator<SQLParameter> iterator() {
		return parameters.values().iterator();
	}

}
