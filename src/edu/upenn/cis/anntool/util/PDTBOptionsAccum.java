package edu.upenn.cis.anntool.util;

import java.util.LinkedList;


/**
 * Used by PDTBOptions and lexyacc/Options to accumulate and get the options
 * from the Options.properties file
 * 
 * @author geraud
 */
public class PDTBOptionsAccum {

	private LinkedList classList;
	private LinkedList connList;
	private OptFeat connSource;
	private OptFeat connType;
	private OptFeat connPolarity;
	private OptFeat connDet;
	private OptFeat argSource;
	private OptFeat argType;
	private OptFeat argPolarity;
	private OptFeat argDet;

	public PDTBOptionsAccum() {
		classList = new LinkedList();
		connList = new LinkedList();
	}

	public LinkedList getClasses() {
		return classList;
	}

	public void addClass(String cl) {
		classList.add(cl);
	}

	public LinkedList getConns() {
		return connList;
	}

	public void addConn(String co) {
		connList.add(co);
	}

	public OptFeat getConnSource() {
		return connSource;
	}

	public void setConnSource(String def) {
		connSource = new OptFeat(def);
	}

	public OptFeat getConnType() {
		return connType;
	}

	public void setConnType(String def) {
		connType = new OptFeat(def);
	}

	public OptFeat getConnPolarity() {
		return connPolarity;
	}

	public void setConnPolarity(String def) {
		connPolarity = new OptFeat(def);
	}

	public OptFeat getConnDet() {
		return connDet;
	}

	public void setConnDet(String def) {
		connDet = new OptFeat(def);
	}

	public OptFeat getArgSource() {
		return argSource;
	}

	public void setArgSource(String def) {
		argSource = new OptFeat(def);
	}

	public OptFeat getArgType() {
		return argType;
	}

	public void setArgType(String def) {
		argType = new OptFeat(def);
	}

	public OptFeat getArgPolarity() {
		return argPolarity;
	}

	public void setArgPolarity(String def) {
		argPolarity = new OptFeat(def);
	}

	public OptFeat getArgDet() {
		return argDet;
	}

	public void setArgDet(String def) {
		argDet = new OptFeat(def);
	}

}