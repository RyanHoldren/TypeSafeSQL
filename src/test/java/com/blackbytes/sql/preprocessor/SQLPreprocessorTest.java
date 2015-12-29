package com.blackbytes.sql.preprocessor;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;

public class SQLPreprocessorTest {

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
		try (
			final InputStream input = getClass().getResourceAsStream(name);
		) {
			final byte[] bytes = ByteStreams.toByteArray(input);
			final String string = new String(bytes, Charset.defaultCharset());
			return string;
		}
	}

}