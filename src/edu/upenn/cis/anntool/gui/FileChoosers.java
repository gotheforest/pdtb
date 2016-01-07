package edu.upenn.cis.anntool.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.upenn.cis.anntool.settings.Constants;
import edu.upenn.cis.anntool.settings.Language;
import edu.upenn.cis.anntool.util.FileManager;
import edu.upenn.cis.anntool.util.SpringUtilities;

public class FileChoosers extends JFrame {

	//TODO: use a list box for the adjudication files?
	
	private static final long serialVersionUID = 1L;
	private static final String rawRootKey = "rawRoot";
	private static final String outputRootKey = "outputRoot";
	private static final String tipsterRootKey = "tipsterRoot";
	private static final String outputCommentRootKey = "outputCommentRoot";	
	private static final String annRoot1Key = "annRoot1";
	private static final String annRoot2Key = "annRoot2";	
	private static final String commentRoot1Key = "commentRoot1";
	private static final String commentRoot2Key = "commentRoot2";
//	private static final String annRootKey = "annRoot";
	private static final String langKey = "lang";
	private static final String fileLocation = "AnnSettings.txt";

	private JTextField rawRootTF;
	private JTextField outputRootTF;
	private JTextField tipsterRootTF;
	private JTextField outputCommentRootTF;
	private JTextField annRoot1TF;
	private JTextField annRoot2TF;
	private JTextField commentRoot1TF;
	private JTextField commentRoot2TF;
	
//	private List<JTextField> annRootTFs;
	
	private JComboBox langCB;
	private JPanel panel;
	
	private static FileInputStream is = null;
	private static FileOutputStream os = null;
	

	FileChoosers() {

		super("File Dialog");
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				
				if (os != null) {
					try {
						os.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				
				System.exit(0); 
			}
		});
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		panel = (JPanel) getContentPane();		
		panel.setLayout(new SpringLayout());
		
		rawRootTF = createTFGroup(
				"RawRoot",
				"This is the directory such that RawRoot"
						+ File.separatorChar
						+ "00"
						+ File.separatorChar
						+ "wsj_0003\n gives the Raw text file for Section 00 file 03.",
				JFileChooser.DIRECTORIES_ONLY);
		outputRootTF = createTFGroup(
				"GoldRoot",
				"This is the directory such that GoldRoot"
						+ File.separatorChar
						+ "00"
						+ File.separatorChar
						+ "wsj_0003\n gives the Annotation file for Section 00 file 03.",
				JFileChooser.DIRECTORIES_ONLY);
		/*tipsterRootTF = createTFGroup(
				"TipsterRoot (Optional)",
				"This is the directory that contains the tipster files (w9_1..w9_9) and the ptb-tipster map files (ptb_*.tbl files).",
				JFileChooser.DIRECTORIES_ONLY);*/
		outputCommentRootTF = createTFGroup(
				"GoldComment",
				"This is the directory such that GoldComment"
						+ File.separatorChar
						+ "00"
						+ File.separatorChar
						+ "wsj_0003\n gives the Comments file for Section 00 file 03.",
				JFileChooser.DIRECTORIES_ONLY);

		annRoot1TF = createTFGroup(
				"Ann1Root",
				"This is the directory such that Ann1Root"
						+ File.separatorChar
						+ "00"
						+ File.separatorChar
						+ "wsj_0003\n gives the Annotation file for Section 00 file 03.",
				JFileChooser.DIRECTORIES_ONLY);

		commentRoot1TF = createTFGroup(
				"Ann1Comment",
				"This is the directory such that Ann1Comment"
						+ File.separatorChar
						+ "00"
						+ File.separatorChar
						+ "wsj_0003\n gives the Comments file for Section 00 file 03.",
				JFileChooser.DIRECTORIES_ONLY);	
		
		annRoot2TF = createTFGroup(
				"Ann2Root",
				"This is the directory such that Ann2Root"
						+ File.separatorChar
						+ "00"
						+ File.separatorChar
						+ "wsj_0003\n gives the Annotation file for Section 00 file 03.",
				JFileChooser.DIRECTORIES_ONLY);		
		
		commentRoot2TF = createTFGroup(
				"Ann2Comment",
				"This is the directory such that Ann2Comment"
						+ File.separatorChar
						+ "00"
						+ File.separatorChar
						+ "wsj_0003\n gives the Comments file for Section 00 file 03.",
				JFileChooser.DIRECTORIES_ONLY);		
		
		langCB = new JComboBox(Constants.languages.values());
		final JLabel lbl = new JLabel("Language:");
		panel.add(lbl);
		panel.add(langCB);
		panel.add(Box.createHorizontalStrut(0));

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<String[]> keyVals = new ArrayList(
							Arrays.asList(new String[][] {
									{ rawRootKey, rawRootTF.getText() },
									{ outputRootKey, outputRootTF.getText() },
									//{ tipsterRootKey, tipsterRootTF.getText() },
									{ outputCommentRootKey, outputCommentRootTF.getText() },
									{ annRoot1Key, annRoot1TF.getText() },
									{ commentRoot1Key, commentRoot1TF.getText() },
									{ annRoot2Key, annRoot2TF.getText() },
									{ commentRoot2Key, commentRoot2TF.getText() },
									{ langKey, langCB.getSelectedItem().toString() } }));
/*					int adjudicationKeyIndex = 1;
					for (JTextField adjudicationRoot : annRootTFs) {
						if (adjudicationRoot.getText().length() > 0) {
							keyVals.add(new String[] {
									annRootKey + adjudicationKeyIndex,
									adjudicationRoot.getText() });
							adjudicationKeyIndex++;
						}
					}*/
					
					saveFilenames(keyVals);

					/*
					List<String> adjudicationRootsList = new ArrayList<String>();
					for (JTextField adjudicationRoot : annRootTFs) {
						if (adjudicationRoot.getText().length() > 0) {
							adjudicationRootsList.add(adjudicationRoot
									.getText());
						}
					}*/

					List<String> adjudicationRootsList = new ArrayList<String>();
					adjudicationRootsList.add(annRoot1TF.getText());
					adjudicationRootsList.add(annRoot2TF.getText());
					
					List<String> commentRootsList = new ArrayList<String>();
					commentRootsList.add(commentRoot1TF.getText());
					commentRootsList.add(commentRoot2TF.getText());
					
					new MainFrame(new FileManager(rawRootTF.getText(),
							outputRootTF.getText(), "", //tipsterRootTF.getText(),
							outputCommentRootTF.getText(), adjudicationRootsList, commentRootsList,							
							(Language) langCB.getSelectedItem()));
					dispose();
					
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(panel,
							"Invalid Raw Text Root.");
					exception.printStackTrace();
					dispose();
				}
			}
		});
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JPanel btnPanel = new JPanel();
		btnPanel.add(okBtn);
		btnPanel.add(cancelBtn);
		panel.add(Box.createHorizontalStrut(0));
		panel.add(btnPanel);
		panel.add(Box.createHorizontalStrut(0));


//		annRootTFs = new ArrayList<JTextField>();
//		addAdjudicationTextField();


		// set up real properties
		try {
			Properties fileLocations = new Properties();
			is = null;
			is = new FileInputStream(fileLocation);
			fileLocations.load(is);

			rawRootTF.setText(fileLocations.getProperty(rawRootKey));
			outputRootTF.setText(fileLocations.getProperty(outputRootKey));
			//tipsterRootTF.setText(fileLocations.getProperty(tipsterRootKey));
			outputCommentRootTF.setText(fileLocations.getProperty(outputCommentRootKey));
			annRoot1TF.setText(fileLocations.getProperty(annRoot1Key));
			commentRoot1TF.setText(fileLocations.getProperty(commentRoot1Key));
			annRoot2TF.setText(fileLocations.getProperty(annRoot2Key));
			commentRoot2TF.setText(fileLocations.getProperty(commentRoot2Key));
			
			String lang = fileLocations.getProperty(langKey);
			if (lang != null) {
				try {
					langCB.setSelectedItem(Constants.languages.valueOf(lang));
				} catch (IllegalArgumentException e) {
					langCB.setSelectedIndex(0);
				}
			}

			if (langCB.getItemCount() > 0 && langCB.getSelectedIndex() < 0) {
				langCB.setSelectedIndex(0);
			}
			
			is.close();

		} catch (IOException e) {
			e.printStackTrace();
			// do nothing because text fields will just be null if the
			// properties file doesn't exist yet
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		SpringUtilities.makeCompactGrid(panel, // parent
				panel.getComponentCount() / 3, 3, // rows, cols
				3, 3, // initX, initY (left and top padding)
				0, 0); // xPad, yPad (right and bottom padding)
		
		toFront();
		pack();
		setVisible(true);
	}

	/*
	private void addAdjudicationTextField() {
		annRootTFs
				.add(createTFGroup(
						"AnnRoot",
						"This is the directory such that AnnRoot"
								+ File.separatorChar
								+ "00"
								+ File.separatorChar
								+ "wsj_0003\n gives the Annotation file for Section 00 file 03.",
						JFileChooser.DIRECTORIES_ONLY,
						panel.getComponentCount() - 6));
		annRootTFs.get(annRootTFs.size() - 1).getDocument()
				.addDocumentListener(new DocumentListener() {
					private void update(DocumentEvent e) {
						boolean noBlanks = true;
						for (ListIterator<JTextField> i = annRootTFs
								.listIterator(); i.hasNext() && noBlanks;) {
							JTextField adjudicationRootTF = i.next();
							noBlanks &= adjudicationRootTF.getText().length() > 0;
						}
						if (noBlanks) {
							addAdjudicationTextField();
							panel.setLayout(new SpringLayout());
							SpringUtilities.makeCompactGrid(panel, // parent
									panel.getComponentCount() / 3, 3, // rows,
																		// cols
									3, 3, // initX, initY (left and top padding)
									0, 0); // xPad, yPad (right and bottom
											// padding)
							pack();
						}
					}

					public void removeUpdate(DocumentEvent e) {
						update(e);
					}

					public void insertUpdate(DocumentEvent e) {
						update(e);
					}

					public void changedUpdate(DocumentEvent e) {
					}
				});
	}*/

	public static void saveFilenames(List<String[]> keyValuePairs) {		
		try {
			Properties fileLocations = new Properties();
			for (String[] keyValuePair : keyValuePairs) {
				fileLocations.setProperty(keyValuePair[0], keyValuePair[1]);
			}
			os = new FileOutputStream(fileLocation);
			fileLocations
					.store(os,
							"This file contains user settings for your Annotator. It is safe to delete, but you will lose your settings.");
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {			
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private JTextField createTFGroup(final String labelText,
			final String description, final int type, final int index) {
		final JLabel lbl = new JLabel(labelText + ":");
		final JTextField tf = new JTextField(20);
		final JButton btn = new JButton("Browse...");
		lbl.setToolTipText(description);
		tf.setToolTipText(description);
		btn.setToolTipText(description);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(tf.getText());
				fc.setFileSelectionMode(type);
				int retVal = fc.showDialog((JFrame) getParent(), labelText);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					tf.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		panel.add(lbl, index);
		panel.add(tf, index + 1);
		panel.add(btn, index + 2);
		return tf;
	}

	private JTextField createTFGroup(final String labelText,
			final String description, final int type) {
		return createTFGroup(labelText, description, type,
				panel.getComponentCount());
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		new FileChoosers();
	}

}
