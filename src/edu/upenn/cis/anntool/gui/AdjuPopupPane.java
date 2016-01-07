package edu.upenn.cis.anntool.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class AdjuPopupPane extends JTextPane implements AnnComponent, LabelPair {

	JPopupMenu menu;
	JCheckBox[] checkBoxes;
	
	private JLabel label;
	
	boolean clickable = false;
	
	public AdjuPopupPane(String[] items, ActionListener popupListener, String labelString) {
		menu = new JPopupMenu();
		menu.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.HORIZONTAL;
		
		this.setComponentPopupMenu(menu);
		setText("");
		setEditable(false);
		
		label = new JLabel(labelString);
		this.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
	
		checkBoxes = new JCheckBox[items.length];		
		int x = 0;
		int y = 0;		
		for (int i=0; i<checkBoxes.length; i++) {
			checkBoxes[i] = new JCheckBox(items[i]);
			checkBoxes[i].addActionListener(popupListener);

			if (i % 2 == 1) {
				x = 1;
			} else {
				x = 0;
			}
			y = i / 2;
			
			c.gridx = x;
			c.gridy = y;
			menu.add(checkBoxes[i], c);
		}
		
	    addMouseListener(new MousePopupListener());		
	    
        final JButton button = new JButton("CLOSE");
        //button.setPreferredSize(new Dimension(30, 25));
        
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                menu.setVisible(false);
            }
        });
        
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = y + 1;
        c.gridheight = 50;
        c.fill = GridBagConstraints.NONE;
        menu.add(button, c);
	}
	
	public String getAnnValue() {
		return getText();	
	}
	
	private void setSelectedBoxes() {
		unsetBoxes();
		String[] values = getText().split(",");
		for (int i=0; i<values.length; i++) {
			String value = values[i].trim();
			for (int j=0; j<checkBoxes.length; j++) {
				if (checkBoxes[j].getText().equals(value)) {
					checkBoxes[j].setSelected(true);
				}
			}
		}	
	}
	
	private void unsetBoxes() {
		for (int j=0; j<checkBoxes.length; j++) {
			checkBoxes[j].setSelected(false);
		}
	}

	public void setAnnValue(String s) {
		setText(s);
		setSelectedBoxes();
	}	

	public void annReset() {
		setText("");
		for (int i=0; i<checkBoxes.length; i++) {
			checkBoxes[i].setSelected(false);
		}
	}

	public JComponent getJComponent() {
		return this;
	}

	public JComponent getContainer() {
		return this;
	}

	public void setClickable(boolean click) {
		clickable = click;
	}
	
	
	
	  // An inner class to check whether mouse events are the popup trigger
	class MousePopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			checkPopup(e);
		}

	    public void mouseClicked(MouseEvent e) {
	    	checkPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	    	checkPopup(e);
	    }

	    private void checkPopup(MouseEvent e) {
	    	if (clickable) {
	    		setSelectedBoxes();
	    		menu.show(e.getComponent(), e.getX(), e.getY());	    			    	
	    	}
	    }	
	}

	public JLabel getLabel() {
		return label;
	}

}


