package com.github.ryanholdren.typesafesql;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class AutoIndentingWriter extends Writer {

	public static final String NEW_LINE = System.getProperty("line.separator");
	private static final char LAST_CHARACTER_OF_LINE_BREAK = NEW_LINE.charAt(NEW_LINE.length() - 1);

	private final BufferedWriter writer;
	private boolean isAtStartOfLine = true;
	private int indent;

	public AutoIndentingWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public void write(char character) throws IOException {
		if (character == '}' || character == ')') {
			indent --;
		}
		if (isAtStartOfLine) {
			for (int count = 0; count < indent; count ++) {
				writer.write('\t');
			}
			isAtStartOfLine = false;
		}
		writer.write(character);
		if (character == LAST_CHARACTER_OF_LINE_BREAK) {
			isAtStartOfLine = true;
		}
		if (character == '{' || character == '(') {
			indent ++;
		}
	}

	@Override
	public void write(char[] characters, int offset, int length) throws IOException {
		for (int index = 0; index < length; index ++) {
			final char character = characters[index + offset];
			write(character);
		}
	}

	public final void writeLine(Object ... parts) throws IOException {
		final int indexOfLastPart = parts.length - 1;
		for (int index = 0; index <= indexOfLastPart; index ++) {
			final String part = parts[index].toString();
			write(part);
		}
		write(NEW_LINE);
	}

	public final void writeEmptyLine() throws IOException {
		write(NEW_LINE);
	}

	public final void writeLineBreak() throws IOException {
		write(NEW_LINE);
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

}
