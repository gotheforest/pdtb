package edu.upenn.cis.anntool.util;

import java.util.LinkedList;

public class OptFeat {

	private LinkedList featVals;
	private int def;

	public OptFeat(String def) {
		this.def = Integer.parseInt(def);
		featVals = new LinkedList();
	}

	public OptFeat(String[] vals, int def) {
		this.def = def;
		featVals = new LinkedList();
		for (int i = 0; i < vals.length; i++) {
			featVals.add(vals[i]);
		}
	}

	public int getDef() {
		return def;
	}

	public String[] getFeatValsArr() {
		return (String[]) featVals.toArray(new String[0]);
	}

	public LinkedList getFeatVals() {
		return featVals;
	}

	public void addFeatVal(String featVal) {
		featVals.add(featVal);
	}

}