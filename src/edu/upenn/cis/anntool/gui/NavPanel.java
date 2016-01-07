package edu.upenn.cis.anntool.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.upenn.cis.anntool.settings.ColorConstants;
import edu.upenn.cis.anntool.settings.Constants;
import edu.upenn.cis.anntool.util.FileManager;
import edu.upenn.cis.anntool.util.FileManager.Section;

public class NavPanel extends JPanel implements PanelInterface {
	private MainFrame mainFrame;
	private FileManager fileManager;
	private JComboBox secCombo;
	private JComboBox fileCombo;
	private JButton loadBtn;
	private JButton prevBtn;
	private JButton nextBtn;
	private JComboBox fontSize;
	private JTextField searchTextField;
	private JButton addAllButton;
	private JButton clearButton;

	NavPanel(final MainFrame mainFrame, final FileManager fileManager) {
		this.mainFrame = mainFrame;
		this.fileManager = fileManager;

		// file combo
		fileCombo = new JComboBox();

		// section combo
		secCombo = new JComboBox(fileManager.getRawSectionsList());
		secCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileCombo.removeAllItems();
				Section s = fileManager.getSection(secCombo.getSelectedIndex());
				for (Iterator<String> i = s.iterator(); i.hasNext();) {
					fileCombo.addItem(i.next());
				}
			}
		});
		secCombo.setSelectedIndex(0);

		// load button
		loadBtn = new JButton("Load");
		loadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setCombosAndLoad();
			}
		});

		// prev button
		prevBtn = new JButton("<<");
		prevBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileManager.getFilIndex() > 0) {
					secCombo.setSelectedIndex(fileManager.getSecIndex());
					fileCombo.setSelectedIndex(fileManager.getFilIndex() - 1);
					setCombosAndLoad();
				} else if (fileManager.getSecIndex() > 0) {
					secCombo.setSelectedIndex(fileManager.getSecIndex() - 1);
					fileCombo.setSelectedIndex(fileCombo.getItemCount() - 1);
					setCombosAndLoad();
				}
			}
		});
		prevBtn.setEnabled(false);

		// next button
		nextBtn = new JButton(">>");
		nextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileManager.getFilIndex() < fileCombo.getItemCount() - 1) {
					secCombo.setSelectedIndex(fileManager.getSecIndex());
					fileCombo.setSelectedIndex(fileManager.getFilIndex() + 1);
					setCombosAndLoad();
				} else if (fileManager.getSecIndex() < secCombo.getItemCount() - 1) {
					secCombo.setSelectedIndex(fileManager.getSecIndex() + 1);
					// fileCombo automatically sets itself to index 0
					setCombosAndLoad();
				}
			}
		});
		nextBtn.setEnabled(false);

		// font size combo
		JLabel fontSizeLbl = new JLabel("  Font Size: ");
		fontSize = new JComboBox();
		for (int i = 8; i < 40; i += 2) {
			Integer size = new Integer(i);
			fontSize.addItem(size);
		}
		fontSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float size = ((Integer) fontSize.getSelectedItem())
						.floatValue();
				mainFrame.fontSizeAction(size);
			}
		});
		fontSize.setSelectedItem(new Integer(Constants.fontSize));

		// search textField
		searchTextField = new JTextField();
		searchTextField.setToolTipText("Search");
		searchTextField.setEnabled(false);
		// searchTextField.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// mainFrame.searchAction(searchTextField.getText());
		// }
		// });

		searchTextField.addKeyListener(new KeyListener() {
			// @Override
			public void keyPressed(KeyEvent e) {
				// System.out.println(e.getKeyChar() + " pressed");
			}

			// @Override
			public void keyReleased(KeyEvent e) {
				mainFrame.searchAction(searchTextField.getText());
				// System.out.println(e.getKeyChar() + " released");
			}

			// @Override
			public void keyTyped(KeyEvent e) {
				// System.out.println(e.getKeyChar() + " typed");
			}
		});

		// clear Button
		clearButton = new JButton("Clear Search");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchTextField.setText("");
				mainFrame.searchAction(searchTextField.getText());
			}
		});

		// addall button
		addAllButton = new JButton("Add All");
		addAllButton.setEnabled(false);
		// If alpha (opaqueness) is included here, it screws up the paint on
		// windows.
		addAllButton.setBackground(new Color(ColorConstants.searchColor
				.getRGB()));
		addAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.addAllAction();
			}
		});

		// combo Panel
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(secCombo);
		add(fileCombo);
		add(loadBtn);
		add(prevBtn);
		add(nextBtn);
		add(fontSizeLbl);
		add(fontSize);
		add(searchTextField);
		add(clearButton);
		add(addAllButton);

	}

	private void setPrevBtn() {
		prevBtn.setEnabled(fileManager.getFilIndex() > 0
				|| fileManager.getSecIndex() > 0);
	}

	private void setNextBtn() {
		nextBtn
				.setEnabled(fileManager.getFilIndex() < fileCombo
						.getItemCount() - 1
						|| fileManager.getSecIndex() < secCombo.getItemCount() - 1);
	}

	private void setCombosAndLoad() {
		fileManager.setSecFile(secCombo.getSelectedIndex(), fileCombo
				.getSelectedIndex());
		mainFrame.loadAction();
		setPrevBtn();
		setNextBtn();
	}

	public void loadAction() {
		searchTextField.setEnabled(true);
		addAllButton.setEnabled(true);
	}

	public void cancelAction() {
		loadBtn.setEnabled(true);
		setPrevBtn();
		setNextBtn();
		searchTextField.setEnabled(true);
		addAllButton.setEnabled(true);
	}

	public void newAction() {
		loadBtn.setEnabled(false);
		prevBtn.setEnabled(false);
		nextBtn.setEnabled(false);
		searchTextField.setEnabled(false);
		addAllButton.setEnabled(false);
	}

	public void inputAction(boolean isSame) {
		if (isSame) {
			loadBtn.setEnabled(true);
			setPrevBtn();
			setNextBtn();
			searchTextField.setEnabled(true);
			addAllButton.setEnabled(true);
		} else {
			loadBtn.setEnabled(false);
			prevBtn.setEnabled(false);
			nextBtn.setEnabled(false);
			searchTextField.setEnabled(false);
			addAllButton.setEnabled(false);
		}
	}

	public void setFontName(String fontName) {
		Font f = searchTextField.getFont();
		searchTextField.setFont(new Font(fontName, f.getStyle(), f.getSize()));
	}

}
