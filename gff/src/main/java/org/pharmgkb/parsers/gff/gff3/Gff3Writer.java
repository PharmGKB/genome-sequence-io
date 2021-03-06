package org.pharmgkb.parsers.gff.gff3;

import org.pharmgkb.parsers.LineWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Writes GFF3.
 * See {@link Gff3Parser} for more information.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class Gff3Writer implements LineWriter<Gff3Feature> {

	private static final long sf_logEvery = 10000;

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0l);

	@Nonnull
	@Override
	public String apply(@Nonnull Gff3Feature f) {

		if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
			sf_logger.debug("Reading line #{}", m_lineNumber);
		}

		return tabify(Gff3Escapers.COORDINATE_SYSTEM_IDS.escape(f.getCoordinateSystemName()),
		              f.getSource().map(Gff3Escapers.FIELDS::escape).orElse(null),
		              Gff3Escapers.FIELDS.escape(f.getType()),
		              f.getStart() + 1, f.getEnd() + 1,
		              f.getScore().orElse(null),
		              f.getStrand().getSymbol(),
		              f.getPhase().orElse(null),
		              mapToString(f.getAttributes()));
	}

	@Nonnull
	private static String mapToString(@Nonnull Map<String, List<String>> attributes) {
		if (attributes.isEmpty()) {
			return ".";
		}
		return attributes.entrySet().stream()
				.map(entry -> Gff3Escapers.FIELDS.escape(entry.getKey())
						     + "=" + entry.getValue().stream()
						     .map(Gff3Escapers.FIELDS::escape)
						     .collect(Collectors.joining(","))
				).collect(Collectors.joining(";"));
	}

	@Nonnull
	private static String tabify(@Nonnull Object... objects) {
		if (objects.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(objects[0]==null? "." : objects[0].toString());
		for (int i = 1; i < objects.length; i++) {
			sb.append("\t").append(objects[i]==null? "." : objects[i]);
		}
		return sb.toString();
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}
}
