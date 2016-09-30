package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConnectionHandling.LEAVE_OPEN;
import com.github.ryanholdren.typesafesql.TestComplex.Result;
import java.sql.Connection;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import org.mockito.exceptions.base.MockitoException;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TestComplex.class})
public class MockerTest {

	private static final byte[] EXPECTED_FIRST_INPUT = {1, 2, 3, 4};
	private static final byte[] EXPECTED_SECOND_INPUT = {1, 2, 3, 4};

	private Connection expectedConnection;
	private Result[] expectedResults;
	private Result firstExpectedResult;

	@Before
	public void setUp() {
		mockStatic(TestComplex.class);
		expectedConnection = mock(Connection.class);
		firstExpectedResult = mock(Result.class);
		expectedResults = new Result[] {
			firstExpectedResult,
			mock(Result.class)
		};
	}

	@Test
	public void testExecuteWhenReturningResults() {
		mockToReturnResults();
		final Stream<Result> actualResults = TestComplex
			.using(expectedConnection, LEAVE_OPEN)
			.withFirstInput(EXPECTED_FIRST_INPUT)
			.withSecondInput(EXPECTED_SECOND_INPUT)
			.execute();
		assertEquals(
			stream(expectedResults).collect(toList()),
			actualResults.collect(toList())
		);
	}

	@Test(expected = MockitoException.class)
	public void testExecuteWhenThrowingException() {
		mockToThrowException();
		TestComplex
			.using(expectedConnection, LEAVE_OPEN)
			.withFirstInput(EXPECTED_FIRST_INPUT)
			.withSecondInput(EXPECTED_SECOND_INPUT)
			.execute();
	}

	@Test
	public void testForEachResultWhenReturningResults() {
		mockToReturnResults();
		final ArrayList<Result> results = new ArrayList<>();
		TestComplex
			.using(expectedConnection, LEAVE_OPEN)
			.withFirstInput(EXPECTED_FIRST_INPUT)
			.withSecondInput(EXPECTED_SECOND_INPUT)
			.forEachResult(results::add);
		assertEquals(
			stream(expectedResults).collect(toList()),
			results
		);
	}

	@Test(expected = MockitoException.class)
	public void testForEachResultWhenThrowingException() {
		mockToThrowException();
		TestComplex
			.using(expectedConnection, LEAVE_OPEN)
			.withFirstInput(EXPECTED_FIRST_INPUT)
			.withSecondInput(EXPECTED_SECOND_INPUT)
			.forEachResult(item -> {});
	}

	@Test
	public void testGetFirstResultWhenReturningResults() {
		mockToReturnResults();
		final Result actual = TestComplex
			.using(expectedConnection, LEAVE_OPEN)
			.withFirstInput(EXPECTED_FIRST_INPUT)
			.withSecondInput(EXPECTED_SECOND_INPUT)
			.getFirstResult();
		assertEquals(
			firstExpectedResult,
			actual
		);
	}

	@Test(expected = MockitoException.class)
	public void testGetFirstResultWhenThrowingException() {
		mockToThrowException();
		TestComplex
			.using(expectedConnection, LEAVE_OPEN)
			.withFirstInput(EXPECTED_FIRST_INPUT)
			.withSecondInput(EXPECTED_SECOND_INPUT)
			.getFirstResult();
	}

	private void mockToReturnResults() {
		TestComplexMocker
			.whenConnectionEquals(expectedConnection, LEAVE_OPEN)
			.whenFirstInputEquals(EXPECTED_FIRST_INPUT)
			.whenSecondInputEquals(EXPECTED_SECOND_INPUT)
			.thenReturn(expectedResults);
	}

	private void mockToThrowException() {
		TestComplexMocker
			.whenConnectionEquals(expectedConnection, LEAVE_OPEN)
			.whenFirstInputEquals(EXPECTED_FIRST_INPUT)
			.whenSecondInputEquals(EXPECTED_SECOND_INPUT)
			.thenThrow(new IllegalAccessException());
	}

}
