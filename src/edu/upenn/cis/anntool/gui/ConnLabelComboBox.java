package edu.upenn.cis.anntool.gui;

import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextPane;

public class ConnLabelComboBox extends JComboBox implements LabelPair,
		AnnComponent {

	private JLabel label;

	public ConnLabelComboBox(ItemListener inputListener,
			KeyListener inputKeyListener, String label, Object[] items) {
		super(items);
		this.label = new JLabel(label);
		setEditable(true);
		setSelectedItem(null);
		setMaximumSize(new Dimension(getMaximumSize().width,
				getPreferredSize().height));
		addItemListener(inputListener);
		getEditor().getEditorComponent().addKeyListener(inputKeyListener);
	}

	// @Override
	public JLabel getLabel() {
		return label;
	}

	// @Override
	public String getAnnValue() {
		if (getEditor().getItem() == null) {
			return "";
		}
		return (String) getEditor().getItem();
	}

	// @Override
	public void setAnnValue(String s) {
		setSelectedItem(s);
	}

	// @Override
	public void annReset() {
		setSelectedItem(null);
	}

	public JComboBox getContainer(){
		return this;
	}
	
	// @Override
	public JComboBox getJComponent() {
		return this;
	}
}
