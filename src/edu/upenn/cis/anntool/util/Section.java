package edu.upenn.cis.anntool.util;

import java.util.Vector;

public class Section extends Vector<String> implements Comparable<Section> {

	private String sec;

	public Section(String sec) {
		this.sec = sec;
	}

	public String getSec() {
		return sec;
	}

	public String toString() {
		return sec;
	}

	// @Override
	public int compareTo(Section o) {
		return sec.compareTo(o.getSec());
	}

}
