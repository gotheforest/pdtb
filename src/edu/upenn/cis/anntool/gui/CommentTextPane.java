package edu.upenn.cis.anntool.gui;

import java.awt.event.KeyListener;

import javax.swing.JTextPane;

public class CommentTextPane extends JTextPane implements AnnComponent {

	public CommentTextPane(KeyListener keyListener) {
		//setToolTipText("Comments");
		addKeyListener(keyListener);
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

}
