package org.pharmgkb.parsers;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A parser that transforms or more lines into one or more data structures.
 * To be used with {@link Stream#flatMap(Function)}.
 * @author Douglas Myers-Turnbull
 */
public interface MultilineParser<R> extends Function<String, Stream<R>> {

	@Nonnull
	default List<R> collectAll(@Nonnull File file) throws IOException, BadDataFormatException {
		return collectAll(file.toPath());
	}

	@Nonnull
	default List<R> collectAll(@Nonnull Path file) throws IOException, BadDataFormatException {
		return collectAll(Files.lines(file));
	}

	@Nonnull
	default List<R> collectAll(@Nonnull Stream<String> stream) throws IOException, BadDataFormatException {
		return parseAll(stream).collect(Collectors.toList());
	}

	@Nonnull
	default Stream<R> parseAll(@Nonnull File file) throws IOException, BadDataFormatException {
		return parseAll(file.toPath());
	}

	@Nonnull
	default Stream<R> parseAll(@Nonnull Path file) throws IOException, BadDataFormatException {
		return parseAll(Files.lines(file));
	}

	/**
	 * For example:
	 * <code>
	 *     return stream.filter(s -> s.isEmpty() || s.startsWith("#")).map(this);
	 * </code>
	 * @throws IOException
	 * @throws BadDataFormatException
	 */
	@Nonnull
	Stream<R> parseAll(@Nonnull Stream<String> stream) throws IOException, BadDataFormatException;

	@Nonnull
	@Override
	Stream<R> apply(String s);

	/**
	 * @return The total number of lines this parser processed since its creation
	 */
	@Nonnegative
	long nLinesProcessed();

}
