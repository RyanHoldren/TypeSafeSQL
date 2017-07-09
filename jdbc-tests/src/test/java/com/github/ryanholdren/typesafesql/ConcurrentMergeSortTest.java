package com.github.ryanholdren.typesafesql;

import static com.github.ryanholdren.typesafesql.ConcurrentMergeSort.usingNaturalOrder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static java.util.stream.Stream.of;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

public class ConcurrentMergeSortTest {

	private static final String[] EMPTY = {};
	private static final String[] ALPHABET = {
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
	};

	@Test
	public void testOnlyOneStream() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
		).toArray(length -> new String[length]));
	}

	@Test
	public void testOnlyOneStreamThatThrowsException() {
		assertArrayEquals(EMPTY, usingNaturalOrder().concurrentlyMergeSort(
			throwException()
		).toArray(length -> new String[length]));
	}

	@Test
	public void testTwoStreamsWhereBothThrowExceptions() {
		assertArrayEquals(EMPTY, usingNaturalOrder().concurrentlyMergeSort(
			throwException(),
			throwException()
		).toArray(length -> new String[length]));
	}

	@Test
	public void testTwoStreamsWhereOneThrowsException() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"),
			throwException()
		).toArray(length -> new String[length]));
	}

	@Test
	public void testTwoIdenticalStreams() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"),
			supplyStreamOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
		).toArray(length -> new String[length]));
	}

	@Test
	public void testTwoStreamsWhereOneIsEmpty() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf(),
			supplyStreamOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
		).toArray(length -> new String[length]));
	}

	@Test
	public void testTwoStreamsWhereBothAreEmpty() {
		assertArrayEquals(EMPTY, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf(),
			supplyStreamOf()
		).toArray(length -> new String[length]));
	}

	@Test
	public void testTwoShortStreams() {
		final String[] expected = {"a", "b", "c", "d"};
		assertArrayEquals(expected, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "c"),
			supplyStreamOf("b", "d")
		).toArray(length -> new String[length]));
	}

	@Test
	public void testThreeStreams() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "d", "g", "j"),
			supplyStreamOf("b", "c", "e", "f", "h", "i"),
			supplyStreamOf("k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
		).toArray(length -> new String[length]));
	}

	@Test
	public void testFiveStreams() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "d", "g", "j"),
			supplyStreamOf("b", "c", "e", "f", "h", "i"),
			supplyStreamOf("k", "m", "p", "q", "r", "t"),
			supplyStreamOf("l", "n", "o", "s"),
			supplyStreamOf("u", "v", "w", "x", "y", "z")
		).toArray(length -> new String[length]));
	}

	@Test
	public void testThreeStreamsWhereOneIsDelayed() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "d", "g", "j"),
			supplyStreamOf("b", "c", "e", "f", "h", "i"),
			delay(supplyStreamOf("k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"))
		).toArray(length -> new String[length]));
	}

	@Test
	public void testThreeStreamsWhereTwoAreDelayed() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "d", "g", "j"),
			delay(supplyStreamOf("b", "c", "e", "f", "h", "i")),
			supplyStreamOf("k", "m", "p", "q", "r", "t"),
			delay(supplyStreamOf("l", "n", "o", "s")),
			supplyStreamOf("u", "v", "w", "x", "y", "z")
		).toArray(length -> new String[length]));
	}

	@Test
	public void testThreeStreamsWhereOneIsSlow() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "d", "g", "j"),
			supplyStreamOf("b", "c", "e", "f", "h", "i"),
			supplyDelayedStreamOf("k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
		).toArray(length -> new String[length]));
	}

	@Test
	public void testThreeStreamsWhereTwoAreSlow() {
		assertArrayEquals(ALPHABET, usingNaturalOrder().concurrentlyMergeSort(
			supplyStreamOf("a", "d", "g", "j"),
			supplyDelayedStreamOf("b", "c", "e", "f", "h", "i"),
			supplyStreamOf("k", "m", "p", "q", "r", "t"),
			supplyDelayedStreamOf("l", "n", "o", "s"),
			supplyStreamOf("u", "v", "w", "x", "y", "z")
		).toArray(length -> new String[length]));
	}

	private Supplier<Stream<String>> supplyDelayedStreamOf(String ... letters) {
		return () -> of(letters).map(letter -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException exception) {
				throw new RuntimeException(exception);
			}
			return letter;
		});
	}

	private Supplier<Stream<String>> supplyStreamOf(String ... letters) {
		return () -> of(letters);
	}

	private Supplier<Stream<String>> throwException() {
		return () -> {
			throw new RuntimeException();
		};
	}

	private Supplier<Stream<String>> delay(Supplier<Stream<String>> letters) {
		return () -> {
			try {
				Thread.sleep(250);
			} catch (InterruptedException exception) {
				throw new RuntimeException(exception);
			}
			return letters.get();
		};
	}

}
