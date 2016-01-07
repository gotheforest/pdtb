package edu.upenn.cis.anntool.util;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.LinkedList;




/**
 * Spans ordered by getStart() and getEnd().
 * 
 * @author nikhild
 */
public class SpanList extends TreeSet {

	/** Creates a new instance of SpanList */
	public SpanList(String spans) {
		super((new Comparator() {

			public int compare(Object o1, Object o2) {
				Span s1 = (Span) o1;
				Span s2 = (Span) o2;

				int startComp = s1.getStart() - s2.getStart();

				return (startComp == 0) ? s1.getEnd() - s2.getEnd() : startComp;

			}

			public boolean equals(Object o) {
				return false;
			}

		}));

		if (!(spans.equals(""))) {
			String[] comps = spans.split(";");

			int len = comps.length;
			for (int i = 0; i < len; i++) {
				super.add(new Span(comps[i]));
			}
		}
	}

	public SpanList() {
		super((new Comparator() {

			public int compare(Object o1, Object o2) {
				Span s1 = (Span) o1;
				Span s2 = (Span) o2;

				int startComp = s1.getStart() - s2.getStart();

				return (startComp == 0) ? s1.getEnd() - s2.getEnd() : startComp;

			}

			public boolean equals(Object o) {
				return false;
			}

		}));
	}

	public static SpanList linkedListToSpanList(LinkedList ll) {
		SpanList sl = new SpanList();
		for (int i = 0; i < ll.size(); i++) {
			Span tmp = (Span) ll.get(i);
			sl.add(tmp);
		}
		return sl;
	}

	public String toString() {
		String s = "";
		int i = 0;
		for (Iterator iter = iterator(); iter.hasNext();) {
			Span sp = (Span) (iter.next());
			s = s + ((i == 0) ? sp.toString() : ";" + sp.toString());
			i++;
		}

		return s;
	}

	public static boolean isSpanList(String str){
		try {
			new SpanList(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
