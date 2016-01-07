package edu.upenn.cis.anntool.util;

public class TipsterFileDocNos {

	private String tipsterFile;
	private String docNo1;
	private String docNo2;

	public TipsterFileDocNos(String tipsterFile, String docNo1, String docNo2) {
		this.tipsterFile = tipsterFile;
		this.docNo1 = docNo1;
		this.docNo2 = docNo2;
	}

	public String getFile() {
		return tipsterFile;
	}

	public String getDocNo1() {
		return docNo1;
	}

	public String getDocNo2() {
		return docNo2;
	}

	public String toString() {
		return "File=" + tipsterFile + ",DOCNO1=" + docNo1 + ",DOCNO2="
				+ docNo2;
	}
}
