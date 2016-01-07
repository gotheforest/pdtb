package edu.upenn.cis.anntool.gui;

import javax.swing.JComponent;

public interface AnnComponent {
	public String getAnnValue();

	public void setAnnValue(String s);

	public void annReset();
	
	public JComponent getJComponent();
	
	public JComponent getContainer();
	
	//public void annDisable();
}
