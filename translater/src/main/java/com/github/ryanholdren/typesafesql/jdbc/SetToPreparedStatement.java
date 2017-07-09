package com.github.ryanholdren.typesafesql.jdbc;

import com.github.ryanholdren.typesafesql.AutoIndentingWriter;
import com.github.ryanholdren.typesafesql.parameters.BigDecimalParameter;
import com.github.ryanholdren.typesafesql.parameters.BooleanParameter;
import com.github.ryanholdren.typesafesql.parameters.ByteArrayParameter;
import com.github.ryanholdren.typesafesql.parameters.DoubleParameter;
import com.github.ryanholdren.typesafesql.parameters.InstantParameter;
import com.github.ryanholdren.typesafesql.parameters.IntegerParameter;
import com.github.ryanholdren.typesafesql.parameters.LocalDateParameter;
import com.github.ryanholdren.typesafesql.parameters.LongParameter;
import com.github.ryanholdren.typesafesql.parameters.ParameterVisitor;
import com.github.ryanholdren.typesafesql.parameters.StringParameter;
import com.github.ryanholdren.typesafesql.parameters.UUIDParameter;
import java.io.IOException;

public class SetToPreparedStatement implements ParameterVisitor<Void, IOException> {

	private final AutoIndentingWriter writer;
	private final String nameOfStatement;

	public SetToPreparedStatement(AutoIndentingWriter writer, String nameOfStatement) {
		this.writer = writer;
		this.nameOfStatement = nameOfStatement;
	}

	@Override
	public Void visit(BigDecimalParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setBigDecimal(", position, ", ", parameter.getName(), ");");
		}
		return null;
	}

	@Override
	public Void visit(BooleanParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setBoolean(", position, ", ", parameter.getName(), ");");
		}
		return null;
	}

	@Override
	public Void visit(ByteArrayParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setBytes(", position, ", ", parameter.getName(), ");");
		}
		return null;
	}

	@Override
	public Void visit(DoubleParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setDouble(", position, ", ", parameter.getName(), ");");
		}
		return null;
	}

	@Override
	public Void visit(InstantParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setTimestamp(" + position + ", Timestamp.from(" + parameter.getName() + "));");
		}
		return null;
	}

	@Override
	public Void visit(IntegerParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setInt(", position, ", ", parameter.getName(), ");");
		}
		return null;
	}

	@Override
	public Void visit(LocalDateParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setDate(" + position + ", Date.valueOf(" + parameter.getName() + "));");
		}
		return null;
	}

	@Override
	public Void visit(LongParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setLong(", position, ", ", parameter.getName(), ");");
		}
		return null;
	}

	@Override
	public Void visit(StringParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setString(", position, ", ", parameter.getName(), ");");
		}
		return null;
	}

	@Override
	public Void visit(UUIDParameter parameter) throws IOException {
		for (int position : parameter.getPositions()) {
			writer.writeLine(nameOfStatement, ".setObject(", position, ", ", parameter.getName(), ");");
		}
		return null;
	}

}
