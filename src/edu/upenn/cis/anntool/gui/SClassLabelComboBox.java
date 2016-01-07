package edu.upenn.cis.anntool.gui;

import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class SClassLabelComboBox extends JComboBox implements LabelPair,
		AnnComponent {

	private JLabel label;
	private int defaultIdx;
	private static final String space = "        ";
	private static final String period = ".";
	private String[] sclasses;

	public SClassLabelComboBox(ItemListener inputListener, String label,
			String[] items, String[] sclasses, int defaultIdx) {
		super(items);
		this.sclasses = sclasses;
		this.label = new JLabel(label);
		this.defaultIdx = defaultIdx;
		setMaximumSize(new Dimension(getMaximumSize().width,
				getPreferredSize().height));
		setMaximumRowCount(20);
		addItemListener(inputListener);
	}

	public static SClassLabelComboBox create(ItemListener inputListener,
			String label, String[] items, String[] sclasses, int defaultIdx) {
		for (int i = 0; i < items.length; i++) {
			items[i] = items[i].replaceAll(Pattern.quote(period), space);
		}
		return new SClassLabelComboBox(inputListener, label, items, sclasses,
				defaultIdx);
	}

	public JLabel getLabel() {
		return label;
	}

	public String getAnnValue() {
		if (getSelectedIndex() < 0) {
			return "";
		}
		return sclasses[getSelectedIndex()];
	}

	public void setAnnValue(String s) {
		for (int i = 0; i < sclasses.length; i++) {
			if (sclasses[i].equals(s)) {
				setSelectedIndex(i);
				return;
			}
		}
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
