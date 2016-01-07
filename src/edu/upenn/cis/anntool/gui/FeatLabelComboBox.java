package edu.upenn.cis.anntool.gui;

import java.awt.Dimension;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class FeatLabelComboBox extends JComboBox implements LabelPair,
		AnnComponent {

	private JLabel label;
	private int defaultIdx;

	public FeatLabelComboBox(ItemListener inputListener, String label,
			Object[] items, int defaultIdx) {
		super(items);
		this.label = new JLabel(label);
		this.defaultIdx = defaultIdx;
		setMaximumSize(new Dimension(getMaximumSize().width,
				getPreferredSize().height));
		addItemListener(inputListener);
	}

	public JLabel getLabel() {
		return label;
	}

	public String getAnnValue() {
		if (getSelectedItem() == null) {
			return "";
		}
		return (String) getSelectedItem();
	}

	public void setAnnValue(String s) {
		setSelectedItem(s);
	}

	public void annReset() {
		setSelectedIndex(defaultIdx);
	}

	// @Override
	public JComboBox getContainer() {
		return this;
	}
	
	// @Override
	public JComboBox getJComponent() {
		return this;
	}
}
