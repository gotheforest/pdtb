package edu.upenn.cis.anntool.gui;

import java.awt.Color;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextPane;

public class PropBankTextPane extends JTextPane implements AnnComponent, LabelPair {

	private JLabel label;
	
	public PropBankTextPane(String labelString) {
		this.label = new JLabel(labelString);	
		this.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
	}

	public String getAnnValue() {
		if (getText() == null) {
			return "";
		}
		return getText();
	}

	public void setAnnValue(String s) {
		setText(s);
	}

	public void annReset() {
		setText(null);
	}

	public JTextPane getContainer(){
		return this;
	}
	
	public JTextPane getJComponent() {
		return this;
	}

	public JLabel getLabel() {
		return label;
	}

}
