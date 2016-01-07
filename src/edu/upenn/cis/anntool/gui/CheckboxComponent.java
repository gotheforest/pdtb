package edu.upenn.cis.anntool.gui;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class CheckboxComponent extends JCheckBox implements AnnComponent {
	private JLabel label;
	
	public CheckboxComponent(String name, ActionListener listener) {
		label = new JLabel(name);
		addActionListener(listener);
	}
	
	public JLabel getJLabel() {
		return label;
	}
	
	public String getAnnValue() {		
		if (isSelected()) {
			return "true";
		}
		return "false";
	}

	public void setAnnValue(String s) {
		if (s.equals("true")) {
			setSelected(true);
		} else {
			setSelected(false);
		}
	}

	public void annReset() {
		setSelected(false);
	}

	public JComponent getJComponent() {
		return this;
	}

	public JComponent getContainer() {
		return this;
	}

}
