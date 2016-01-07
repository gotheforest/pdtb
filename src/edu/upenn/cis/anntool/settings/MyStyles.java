/*
 * MyStyles.java
 *
 * Created on April 28, 2006, 9:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.upenn.cis.anntool.settings;

import javax.swing.text.*;
import java.awt.Color;

/**
 * 
 * @author aleewk
 */
public class MyStyles {

	private StyledDocument doc;
	private Style plainStyle;
	private Style connStyle;
	private Style attrStyle;
	private Style deleteStyle;
	private Style selectStyle;
	private Style selectListStyle;

	private int fontSize;
	private String font;

	/** Creates a new instance of MyStyles */
	public MyStyles(StyledDocument doc, int fontSize) {
		this.doc = doc;
		this.fontSize = fontSize;
		this.font = "SansSerif";

		setStyles();
	}

	private void setStyles() {
		plainStyle = doc.addStyle("plainStyle", null);
		StyleConstants.setFontFamily(plainStyle, font);
		StyleConstants.setFontSize(plainStyle, fontSize);
		StyleConstants.setForeground(plainStyle, Color.black);
		StyleConstants.setUnderline(plainStyle, false);

		connStyle = doc.addStyle("connStyle", null);
		StyleConstants.setFontFamily(connStyle, font);
		StyleConstants.setFontSize(connStyle, fontSize);
		StyleConstants.setForeground(connStyle, Color.red);

		attrStyle = doc.addStyle("attrStyle", null);
		StyleConstants.setBold(attrStyle, true);
		StyleConstants.setFontFamily(attrStyle, font);
		StyleConstants.setFontSize(attrStyle, fontSize);
		StyleConstants.setForeground(attrStyle, Color.black);

		deleteStyle = doc.addStyle("deleteStyle", null);
		StyleConstants.setUnderline(deleteStyle, true);
		StyleConstants.setItalic(deleteStyle, true);
		StyleConstants.setFontFamily(deleteStyle, font);
		StyleConstants.setFontSize(deleteStyle, fontSize);
		StyleConstants.setForeground(deleteStyle, Color.black);

		selectStyle = doc.addStyle("selectStyle", null);
		StyleConstants.setUnderline(selectStyle, true);
		StyleConstants.setFontFamily(selectStyle, font);
		StyleConstants.setFontSize(selectStyle, fontSize);
		StyleConstants.setForeground(selectStyle, Color.black);

		selectListStyle = doc.addStyle("selectListStyle", null);
		StyleConstants.setUnderline(selectListStyle, true);
		StyleConstants.setFontFamily(selectListStyle, font);
		StyleConstants.setFontSize(selectListStyle, fontSize);
		StyleConstants.setForeground(selectListStyle, Color.black);

	}

//	public Style getPlainStyle() {
//		return plainStyle;
//	}
//
//	public Style getConnStyle() {
//		return connStyle;
//	}
//
//	public Style getAttrStyle() {
//		return attrStyle;
//	}
//
//	public Style getDeleteStyle() {
//		return deleteStyle;
//	}
//
//	public Style getSelectStyle() {
//		return selectStyle;
//	}
//
//	public Style getSelectListStyle() {
//		return selectListStyle;
//	}
//
//	public void updateFontSize(int fontSize) {
//		this.fontSize = fontSize;
//		this.setStyles();
//	}
//
//	public void updateFont(String font) {
//		this.font = font;
//		this.setStyles();
//	}
}
