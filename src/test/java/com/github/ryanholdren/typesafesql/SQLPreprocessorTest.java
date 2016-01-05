package com.github.ryanholdren.typesafesql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;

public class SQLPreprocessorTest {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	@Test
	public void test() throws IOException {
		final InputStream input = getClass().getResourceAsStream("SimpleExample.sql");
		final StringWriter writer = new StringWriter();
		SQLPreprocessor
			.newBuilder()
			.setNamespace("com.example.com")
			.setClassName("SimpleExample")
			.setInput(input)
			.setOutput(writer)
			.preprocess();
		final String actual = writer.toString();
		final String expected = getResourceAsString("SimpleExample.txt");
		Assert.assertEquals(expected, actual);
	}

	private String getResourceAsString(String name) throws IOException {
		final char[] buffer = new char[1024];
		final StringBuilder result = new StringBuilder();
		try (
			final InputStream input = getClass().getResourceAsStream(name);) {
			try (final InputStreamReader reader = new InputStreamReader(input, UTF8)) {
				while (true) {
					int charactersRead = reader.read(buffer, 0, buffer.length);
					if (charactersRead < 0) {
						break;
					}
					result.append(buffer, 0, charactersRead);
				}
			}
		}
		return result.toString();
	}

}
