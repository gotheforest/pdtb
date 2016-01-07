package edu.upenn.cis.anntool.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import edu.upenn.cis.anntool.settings.Constants;
import edu.upenn.cis.anntool.util.Span;
import edu.upenn.cis.anntool.util.SpanList;

public class SpanButton extends JToggleButton implements AnnComponent {

	private String spanText;
	private String siteText;
	// private Color originalBack = getBackground();
	// private Color originalFore = getForeground();
	private Color highlight;
	// private Color color;
	private JPanel panel;

	SpanButton(ActionListener inputActionListener, String spanText,
			String siteText, Color color) {
		super("+" + spanText);
		addActionListener(inputActionListener);
		this.spanText = spanText;
		this.siteText = siteText;
		this.highlight = color;
		// this.color = new Color(color.getRGB());

		panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(color.getRGB()));
		// GridBagConstraints c = new GridBagConstraints();
		// c.fill = GridBagConstraints.VERTICAL;
		// c.fill = GridBagConstraints.BOTH;
		// c.insets = new Insets(5,5,5,5);
		// panel.add(this, c);
		panel.add(this, new GridBagConstraints());

	}

	public Color getHighLight() {
		return highlight;
	}

	public String getAnnValue() {
		if (getToolTipText() == null) {
			return "";
		}
		return getToolTipText();
	}

	/* True if 2..7, etc. False if single number */
	public boolean isSpan() {
		return SpanList.isSpanList(getToolTipText());
	}

	public void setAnnValue(String s) {
		setToolTipText(s);
		if (s == null || s.equals("")) {
			setText("+" + spanText);
			setSelected(false);
			// setBackground(color);
			// setForeground(originalFore);
			// button.setFocusPainted(true);
			// button.setContentAreaFilled(true);
		} else {
			setSelected(true);
			if (isSpan()) {
				setText("-" + spanText);
				// setBackground(originalBack);
				// setForeground(color);
				// setFocusPainted(false);
				// setContentAreaFilled(false);
			} else {
				setText(siteText + " " + s);
			}
		}
	}

	public void annReset() {
		setAnnValue(null);
	}

	public JPanel getContainer() {
		return panel;
	}

	// @Override
	public JToggleButton getJComponent() {
		return this;
	}

}
