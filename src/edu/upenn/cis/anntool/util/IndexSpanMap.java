package edu.upenn.cis.anntool.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;

import edu.upenn.cis.anntool.settings.Language;

public class IndexSpanMap {

	private static final boolean hindiDetailedSpan = true;

	private Span[] mapping;
	private Language lang;
	private Span[] spanList;

	private static final CompareStart compareStart = new CompareStart();
	private static final CompareEnd compareEnd = new CompareEnd();

	private static final boolean trimSpaces = true;
	private static final Pattern spaces = Pattern.compile("\\s");

	public IndexSpanMap(Span[] spanList, Language lang, String rawText) {
		this.spanList = spanList;
		this.lang = lang;

		int last;
		if (spanList.length > 0) {
			last = spanList[spanList.length - 1].getEnd();
		} else {
			last = -1;
		}
		mapping = new Span[last + 1];

		Span dummySpan = new Span(-1, -1);

		for (int i = 0; i < last; i++) {
			mapping[i] = dummySpan; // initialize everything to dummy spans
			// first
		}

		for (int i = 0; i < spanList.length; i++) {
			int s = spanList[i].getStart();
			int e = spanList[i].getEnd();
			if (trimSpaces) {
				Span detokenizedSpan = tokenToChar(spanList[i]);
				String subString = rawText.substring(
						detokenizedSpan.getStart(), detokenizedSpan.getEnd());
				if (spaces.matcher(subString).matches()) {
					if (s == 0) {
						s++;
					}
					for (int j = s; j <= e; j++) {
						mapping[j] = mapping[s - 1];
					}
				} else {
					for (int j = s; j <= e; j++) {
						mapping[j] = spanList[i];
					}
				}
			} else {
				for (int j = s; j <= e; j++) {
					mapping[j] = spanList[i];
				}
			}
		}

		for (int i = 0; i < mapping.length; i++) {
			if (mapping[i].getStart() == -1) {
				mapping[i] = new Span(i - 1, i);
			}
		}

	}

	/*
	 * ex, converts 5-8 to 2-13
	 */
	public Span getTokenizedSpan(Span span) {
		if (span.getStart() < mapping.length && span.getEnd() <= mapping.length) {
			return new Span(mapping[span.getStart()].getStart(), mapping[span
					.getEnd()].getEnd());
		}
		return null;
	}

	/*
	 * ex, converts 2-13 to 1-3
	 */
	public Span charToToken(Span span) {
		if (lang.toString().equals("Hindi")) {
			if (hindiDetailedSpan) {
				return span;
			} else {
				return new Span(Arrays.binarySearch(spanList, span,
						compareStart), Arrays.binarySearch(spanList, span,
						compareEnd) + 1);
			}
			// case Tamil:
			// return new Span(Arrays.binarySearch(spanList, span,
			// compareStart), Arrays.binarySearch(spanList, span,
			// compareEnd) + 1);
		} else {
			return span;
		}
	}

	public SpanList charToToken(SpanList spanlist) {
		SpanList ret = new SpanList();
		for (Iterator<Span> i = spanlist.iterator(); i.hasNext();) {
			Span span = i.next();
			ret.add(charToToken(span));
		}
		return ret;
	}

	/*
	 * ex, converts 1-3 to 2-13
	 */
	public Span tokenToChar(Span span) {
		if (lang.toString().equals("Hindi")) {
			if (hindiDetailedSpan) {
				return span;
			} else {
				return new Span(spanList[span.getStart()].getStart(),
						spanList[span.getEnd() - 1].getEnd());
			}
			// case Tamil:
			// return new Span(spanList[span.getStart()].getStart(),
			// spanList[span.getEnd() - 1].getEnd());
		} else {
			return span;
		}
	}

	public SpanList tokenToChar(SpanList spanlist) {
		SpanList ret = new SpanList();
		for (Iterator<Span> i = spanlist.iterator(); i.hasNext();) {
			Span span = i.next();
			ret.add(tokenToChar(span));
		}
		return ret;
	}

}

/*
 * o1 is the object in the array; o2 is the object that is input to the search
 */
class CompareStart implements Comparator {
	public int compare(Object o1, Object o2) {
		Span s1 = (Span) o1;
		Span s2 = (Span) o2;
		if (s1.getEnd() <= s2.getStart()) {
			return -1;
		} else if (s1.getStart() > s2.getStart()) {
			return 1;
		} else {
			return 0;
		}
	}
}

/*
 * o1 is the object in the array; o2 is the object that is input to the search
 */
class CompareEnd implements Comparator {
	public int compare(Object o1, Object o2) {
		Span s1 = (Span) o1;
		Span s2 = (Span) o2;

		if (s1.getEnd() < s2.getEnd()) {
			return -1;
		} else if (s1.getStart() >= s2.getEnd()) {
			return 1;
		} else {
			return 0;
		}
	}
}
