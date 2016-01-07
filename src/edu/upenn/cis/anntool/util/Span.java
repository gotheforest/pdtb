/*
 * Span.java
 *
 * Created on December 3, 2004, 5:35 PM
 */

package edu.upenn.cis.anntool.util;

import java.util.regex.*;

/**
 * Vanilla spans.
 * 
 * @author nikhild
 */
public class Span {

	int start;
	int end;

	private static final Pattern SpanPattern = Pattern.compile("\\.\\.");

	/** Creates a new instance of Span */
	public Span(String s) {
		String[] comps = SpanPattern.split(s);
		if (comps.length != 2) {
			throw (new IllegalArgumentException("Invalid string for span " + s));
		}

		start = Integer.parseInt(comps[0]);
		end = Integer.parseInt(comps[1]);

	}

	public Span(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String toString() {
		return "" + this.start + ".." + this.end;
	}

	public boolean equals(Object o) {
		Span s = (Span) o;

		return (this.start == s.getStart() && this.end == s.getEnd());
	}

}
