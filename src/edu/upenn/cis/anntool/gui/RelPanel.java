package edu.upenn.cis.anntool.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import edu.upenn.cis.anntool.settings.ColorConstants;
import edu.upenn.cis.anntool.settings.Constants;
import edu.upenn.cis.anntool.settings.Constants.LABELS;
import edu.upenn.cis.anntool.settings.Constants.RELTYPELABELS;
import edu.upenn.cis.anntool.util.FileManager;
import edu.upenn.cis.anntool.util.OptFeat;
import edu.upenn.cis.anntool.util.Relation;
import edu.upenn.cis.anntool.util.SpanList;

public class RelPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	private AnnComponent[] relComponents = new AnnComponent[LABELS.values().length];
	private Relation relation;
	private SpanButton[] spanButtons;
	private CommentTextPane commentPane;
	private FileManager fileManager;
	private int gridy = 0;
	// number of rows:
	private static final int maxGridy = 11;
	private boolean isParent;

	
	private ActionListener popupListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JCheckBox current = (JCheckBox) e.getSource();
			
			String originalDiffs = relComponents[LABELS.adjudicationDisagreement.ordinal()].getAnnValue();			
			Set<String> originalDiffsSet = new HashSet<String>();
			
			if (!originalDiffs.equals("")) {
				String[] originalDiffsArr = originalDiffs.split(",");
				for (int i=0; i<originalDiffsArr.length; i++) {
					originalDiffsSet.add(originalDiffsArr[i].trim());
				}
			}	
			
			if (current.isSelected()) {				
				originalDiffsSet.add(current.getText());				
			} else {
				if (originalDiffsSet.contains(current.getText())) {
					originalDiffsSet.remove(current.getText());
				}
			}			
			
			String[] newArray = originalDiffsSet.toArray(new String[originalDiffsSet.size()]);
			String newStr = "";
			for (int i=0; i<newArray.length; i++) {
				if (i<newArray.length-1) {
					newStr += newArray[i] + ", ";
				} else {
					newStr += newArray[i];
				}				
			}
			
			relComponents[LABELS.adjudicationDisagreement.ordinal()].setAnnValue(newStr);
			mainFrame.inputAction((JComponent) e.getSource(), spanButtons);
			enableRelationType();
		}				
	};
	
	private ActionListener checkboxListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			CheckboxComponent j = (CheckboxComponent) e.getSource();
			String value = relComponents[LABELS.consensus.ordinal()].getAnnValue();
			relComponents[LABELS.consensus.ordinal()].setAnnValue(value);
			mainFrame.inputAction((JComponent) e.getSource(), spanButtons);
			enableRelationType();
		}
	};
	
	private ActionListener buttonListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpanButton b = (SpanButton) e.getSource();
			/**
			 * For the Implicit/EntRel/NoRel case where we do not have a
			 * spanlist for the conn, we do not want to do anything when the
			 * button is clicked
			 */
			// TODO: This is probably the place to calculate the sentence
			// position if the spanbutton is arg2
			String relType = relComponents[LABELS.rel.ordinal()].getAnnValue();
			SpanButton connSpanListButton = (SpanButton) relComponents[LABELS.connSpanList
					.ordinal()];
			if (relType.equals(RELTYPELABELS.Explicit.toString())
					|| relType.equals(RELTYPELABELS.AltLex.toString())
					|| b != connSpanListButton) {
				mainFrame.spanAction(b);
				mainFrame.inputAction((JComponent) e.getSource(), spanButtons);
			} else {
				b.setSelected(!b.isSelected());
			}
		}
	};
	
	private ItemListener adjuComboBoxListener = new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {

				AdjuPopupPane popup = (AdjuPopupPane) relComponents[LABELS.adjudicationDisagreement.ordinal()];
				if (isParent) {
					String itemValue = (String) ((JComboBox) e.getSource()).getSelectedItem();
					if (itemValue.equals("Selected Annotator") || itemValue.equals("Select and Edit")) {														
						String diff = relation.getAdjuDiff();		
						popup.setAnnValue(diff);												
						popup.setClickable(true);													
						//relComponents[LABELS.adjudicationDisagreement.ordinal()}.
					} else {
						popup.annReset();
						popup.setClickable(false);
					}
				} else {
					popup.annReset();
					popup.setClickable(false);
				}

				mainFrame.inputAction((JComponent) e.getSource(), spanButtons);
				enableRelationType();
			}
		}
	};	

	private ItemListener comboBoxListener = new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				mainFrame.inputAction((JComponent) e.getSource(), spanButtons);
				enableRelationType();
			}
		}
	};

	private ItemListener featComboBoxListener = new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				mainFrame.inputAction((JComponent) e.getSource(), spanButtons);
			}
		}
	};

	private KeyListener textFieldListener = new KeyListener() {
		public void keyPressed(KeyEvent e) {
			// System.out.println("pressed " + e.getKeyChar());
		}

		public void keyReleased(KeyEvent e) {
			// System.out.println("released " + e.getKeyChar());
			mainFrame.inputAction((JComponent) e.getSource(), spanButtons);
			enableRelationType();
		}

		public void keyTyped(KeyEvent e) {
			// System.out.println("typed " + e.getKeyChar());
		}
	};

	private KeyListener commentListener = new KeyListener() {
		public void keyPressed(KeyEvent e) {
			// System.out.println("pressed " + e.getKeyChar());
		}

		public void keyReleased(KeyEvent e) {
			// System.out.println("released " + e.getKeyChar());
			mainFrame.inputAction((JComponent) e.getSource(), spanButtons);
		}

		public void keyTyped(KeyEvent e) {
			// System.out.println("typed " + e.getKeyChar());
		}
	};

	public RelPanel(final MainFrame mainFrame, final FileManager fileManager) {
		super(new GridBagLayout());
		this.mainFrame = mainFrame;
		this.fileManager = fileManager;

		String[] adjuReasons = new String[10];
		adjuReasons[0] = "";
		adjuReasons[1] = "Full Agreement";
		adjuReasons[2] = "Technical Agreement";
		adjuReasons[3] = "Direct Entry";
		adjuReasons[4] = "Selected Annotator";
		adjuReasons[5] = "Select and Edit";
		adjuReasons[6] = "Single Annotator";
		adjuReasons[7] = "Rejected";		
		adjuReasons[8] = "Reannotate";
		adjuReasons[9] = "PDTB2 Correction";
		createAdjuComboBox(new LABELS[] { LABELS.adjudicationReason }, adjuReasons);
		
		createCheckboxComponent(new LABELS[] { LABELS.consensus });
		
		String[] adjuDiffs = new String[9];
		adjuDiffs[0] = "Relation Type";
		adjuDiffs[1] = "Connective";
		adjuDiffs[2] = "Arg1";
		adjuDiffs[3] = "Arg2";
		adjuDiffs[4] = "Sense 1A";
		adjuDiffs[5] = "Sense 1B";
		adjuDiffs[6] = "Sense 2A";
		adjuDiffs[7] = "Sense 2B";
		adjuDiffs[8] = "Attribution";
		
		AdjuPopupPane diffPane = new AdjuPopupPane(adjuDiffs, popupListener, LABELS.adjudicationDisagreement.toString() + ": ");
		
		//adjuSP.setPreferredSize(new Dimension(adjuSP.getPreferredSize().width, 50));	
		relComponents[LABELS.adjudicationDisagreement.ordinal()] = diffPane;
		
		PropBankTextPane propBankRolePane = new PropBankTextPane(LABELS.propBankRole.toString() + ": ");
		propBankRolePane.setEnabled(false);
		propBankRolePane.setEditable(false);
		relComponents[LABELS.propBankRole.ordinal()] = propBankRolePane;
		
		PropBankTextPane propBankVerbPane = new PropBankTextPane(LABELS.propBankVerb.toString() + ": ");
		propBankVerbPane.setEnabled(false);	
		propBankVerbPane.setEditable(false);
		relComponents[LABELS.propBankVerb.ordinal()] = propBankVerbPane;
				
		PropBankTextPane identifierPane = new PropBankTextPane(LABELS.identifier.toString() + ": ");
		relComponents[LABELS.identifier.ordinal()] = identifierPane;
		
		PropBankTextPane identifierTypePane = new PropBankTextPane(LABELS.identifierType.toString() + ": ");
		relComponents[LABELS.identifierType.ordinal()] = identifierTypePane;
		
		createRelTypeComboBox(new LABELS[] { LABELS.rel },
				new OptFeat[] { Constants.relTypes });
		createFeatComboBoxes(new LABELS[] { LABELS.connAttrSrc,
				LABELS.connAttrType, LABELS.connAttrPol, LABELS.connAttrDet,
				LABELS.arg1AttrSrc, LABELS.arg1AttrType, LABELS.arg1AttrPol,
				LABELS.arg1AttrDet, LABELS.arg2AttrSrc, LABELS.arg2AttrType,
				LABELS.arg2AttrPol, LABELS.arg2AttrDet },
				new OptFeat[] { Constants.options.getConnSourceFeature(),
						Constants.options.getConnTypeFeature(),
						Constants.options.getConnPolarityFeature(),
						Constants.options.getConnDeterminancyFeature(),
						Constants.options.getArgSourceFeature(),
						Constants.options.getArgTypeFeature(),
						Constants.options.getArgPolarityFeature(),
						Constants.options.getArgDeterminancyFeature(),
						Constants.options.getArgSourceFeature(),
						Constants.options.getArgTypeFeature(),
						Constants.options.getArgPolarityFeature(),
						Constants.options.getArgDeterminancyFeature() });
		createEdComboBoxes(new LABELS[] { LABELS.conn1, LABELS.conn2 },
				new String[][] { Constants.options.getConns(),
						Constants.options.getConns() });
		createButtons(new LABELS[] { LABELS.connSpanList,
				LABELS.connAttrSpanList, LABELS.sup1SpanList,
				LABELS.arg1SpanList, LABELS.arg1AttrSpanList,
				LABELS.arg2SpanList, LABELS.arg2AttrSpanList,
				LABELS.sup2SpanList }, new Color[] {
				ColorConstants.ExplicitConnColor,
				ColorConstants.ExplicitConnAttribColor,
				ColorConstants.Sup1Color, ColorConstants.Arg1Color,
				ColorConstants.Arg1AtrribColor, ColorConstants.Arg2Color,
				ColorConstants.Arg2AtrribColor, ColorConstants.Sup2Color });
		createFormattedComboBoxes(new LABELS[] { LABELS.sClass1A,
				LABELS.sClass1B, LABELS.sClass2A, LABELS.sClass2B },
				Constants.options.getSemanticClassesTree(),
				Constants.options.getSemanticClassesTreeLong());

		spanButtons = new SpanButton[] {
				(SpanButton) relComponents[LABELS.connSpanList.ordinal()],
				(SpanButton) relComponents[LABELS.connAttrSpanList.ordinal()],
				(SpanButton) relComponents[LABELS.sup1SpanList.ordinal()],
				(SpanButton) relComponents[LABELS.arg1SpanList.ordinal()],
				(SpanButton) relComponents[LABELS.arg1AttrSpanList.ordinal()],
				(SpanButton) relComponents[LABELS.arg2SpanList.ordinal()],
				(SpanButton) relComponents[LABELS.arg2AttrSpanList.ordinal()],
				(SpanButton) relComponents[LABELS.sup2SpanList.ordinal()] };

		commentPane = new CommentTextPane(commentListener);
		JScrollPane commentSP = new JScrollPane(commentPane);
		commentSP.setPreferredSize(new Dimension(
				commentSP.getPreferredSize().width, 50));

		addRow(new LABELS[] { LABELS.adjudicationReason, LABELS.consensus});
		addRow(new LABELS[] { LABELS.adjudicationDisagreement });
		addRow(new LABELS[] { LABELS.propBankRole, LABELS.propBankVerb });
		//addRow(new LABELS[] { LABELS.identifier, LABELS.identifierType });
		addRow(new LABELS[] { LABELS.rel });
		addRow(new LABELS[] { LABELS.conn1, LABELS.conn2 });
		addRow(new LABELS[] { LABELS.sClass1A, LABELS.sClass2A });
		addRow(new LABELS[] { LABELS.sClass1B, LABELS.sClass2B });
		addRow(new LABELS[] { LABELS.connSpanList, LABELS.connAttrSpanList });
		addRow(new LABELS[] { LABELS.connAttrSrc, LABELS.connAttrType,
				LABELS.connAttrPol, LABELS.connAttrDet });
		addRow(new LABELS[] { LABELS.arg1SpanList, LABELS.arg1AttrSpanList });
		addRow(new LABELS[] { LABELS.arg1AttrSrc, LABELS.arg1AttrType,
				LABELS.arg1AttrPol, LABELS.arg1AttrDet });
		addRow(new LABELS[] { LABELS.arg2SpanList, LABELS.arg2AttrSpanList });
		addRow(new LABELS[] { LABELS.arg2AttrSrc, LABELS.arg2AttrType,
				LABELS.arg2AttrPol, LABELS.arg2AttrDet });
		addRow(new LABELS[] { LABELS.sup1SpanList, LABELS.sup2SpanList });		

		addRow(commentSP, 1);

		disableAll();
	}

	private void addRow(JComponent component, int weighty) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = gridy;
		c.weightx = 1;
		c.weighty = weighty;
		c.fill = GridBagConstraints.BOTH;
		if (gridy == 0) {
			c.insets = new Insets(Constants.spacing, Constants.spacing,
					Constants.spacing / 2, Constants.spacing);
		} else if (gridy == maxGridy) {
			c.insets = new Insets(Constants.spacing / 2, Constants.spacing,
					Constants.spacing, Constants.spacing);
		} else {
			c.insets = new Insets(Constants.spacing / 2, Constants.spacing,
					Constants.spacing / 2, Constants.spacing);
		}
		add(component, c);
		gridy++;
	}

	private void addRow(LABELS[] labels) {
		JPanel p = new JPanel();
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		// p.setBorder(BorderFactory.createTitledBorder("panel"));//
		for (int i = 0; i < labels.length; i++) {
			AnnComponent ac = relComponents[labels[i].ordinal()];
			if (ac instanceof LabelPair) {
				LabelPair lp = (LabelPair) ac;
				p.add(lp.getLabel());
				p.add(Box.createHorizontalStrut(2));
			} else if (ac instanceof CheckboxComponent) {
				CheckboxComponent cc = (CheckboxComponent) ac;
				p.add(cc.getJLabel());
				p.add(Box.createHorizontalStrut(2));
			}
			p.add(ac.getContainer());
			if (i < labels.length - 1) {
				p.add(Box.createHorizontalStrut(Constants.spacing));
			}
		}
		addRow(p, 0);
	}

	private void createButtons(LABELS[] labels, Color[] colors) {
		for (int i = 0; i < labels.length; i++) {
			relComponents[labels[i].ordinal()] = new SpanButton(buttonListener,
					labels[i].toString(), "Arg2 Sentence #:", colors[i]);
		}
	}
	
	private void createAdjuComboBox(LABELS[] labels, String[] options) {
		for (int i = 0; i < labels.length; i++) {
			relComponents[labels[i].ordinal()] = new FeatLabelComboBox(
					adjuComboBoxListener, labels[i].toString(),
					options, 0);
		}
	}
	
	private void createCheckboxComponent(LABELS[] labels) {
		for (int i = 0; i < labels.length; i++) {
			relComponents[labels[i].ordinal()] = new CheckboxComponent(labels[i].toString(), checkboxListener);
		}		
	}

	private void createRelTypeComboBox(LABELS[] labels, OptFeat[] options) {
		for (int i = 0; i < labels.length; i++) {
			relComponents[labels[i].ordinal()] = new FeatLabelComboBox(
					comboBoxListener, labels[i].toString(),
					options[i].getFeatValsArr(), options[i].getDef());
		}
	}

	private void createFeatComboBoxes(LABELS[] labels, OptFeat[] options) {
		for (int i = 0; i < labels.length; i++) {
			relComponents[labels[i].ordinal()] = new FeatLabelComboBox(
					featComboBoxListener, labels[i].toString(),
					options[i].getFeatValsArr(), options[i].getDef());
		}
	}

	private void createFormattedComboBoxes(LABELS[] labels, String[] options,
			String[] optionsLong) {
		for (int i = 0; i < labels.length; i++) {
			relComponents[labels[i].ordinal()] = SClassLabelComboBox.create(
					comboBoxListener, labels[i].toString(), options,
					optionsLong, 0);
		}
	}

	private void createEdComboBoxes(LABELS[] labels, String[][] options) {
		for (int i = 0; i < labels.length; i++) {
			relComponents[labels[i].ordinal()] = new ConnLabelComboBox(
					comboBoxListener, textFieldListener, labels[i].toString(),
					options[i]);
		}
	}

	private void enableAdjuBox(boolean isParent) {
		JComboBox adjuBox = (JComboBox) relComponents[LABELS.adjudicationReason.ordinal()].getJComponent();
		AdjuPopupPane popup = (AdjuPopupPane) relComponents[LABELS.adjudicationDisagreement.ordinal()];
		CheckboxComponent consensusBox = (CheckboxComponent) relComponents[LABELS.consensus.ordinal()].getJComponent();
		
		if (isParent) {
			adjuBox.setEnabled(true);	
			consensusBox.setEnabled(true);
			String itemValue = (String) adjuBox.getSelectedItem();
			popup.setEnabled(true);
			if (itemValue.equals("Selected Annotator") || itemValue.equals("Select and Edit")) {
				String diff = relation.getAdjuDiff();		
				popup.setAnnValue(diff);						
				popup.getJComponent().setEnabled(true);
				popup.setClickable(true);			
			} else {	
				popup.setAnnValue("");;
				popup.setClickable(false);
			}
		} else {
			adjuBox.setEnabled(false);
			consensusBox.setEnabled(false);
			popup.setEnabled(false);			
		}
	}

	
	private void enableRelationType() {
		disableAll();		

		LABELS[] l = Constants.getLabels(relComponents[LABELS.rel.ordinal()].getAnnValue());
		for (int i = 0; i < l.length; i++) {
			relComponents[l[i].ordinal()].getJComponent().setEnabled(true);
			if (relComponents[l[i].ordinal()] instanceof LabelPair) {
				LabelPair lp = (LabelPair) relComponents[l[i].ordinal()];
				lp.getLabel().setEnabled(true);
			}
		}
		
		if (relComponents[LABELS.rel.ordinal()].getAnnValue().equals(
				RELTYPELABELS.Implicit.toString())
				&& relComponents[LABELS.conn1.ordinal()].getAnnValue().equals(
						"")) {
			relComponents[LABELS.sClass1B.ordinal()].getJComponent()
					.setEnabled(false);
			relComponents[LABELS.conn2.ordinal()].getJComponent().setEnabled(
					false);
			relComponents[LABELS.sClass2A.ordinal()].getJComponent()
					.setEnabled(false);
			relComponents[LABELS.sClass2B.ordinal()].getJComponent()
					.setEnabled(false);
		}
		if (relComponents[LABELS.sClass1A.ordinal()].getAnnValue().equals("")) {
			relComponents[LABELS.sClass1B.ordinal()].getJComponent()
					.setEnabled(false);
		}
		if (relComponents[LABELS.rel.ordinal()].getAnnValue().equals(
				RELTYPELABELS.Implicit.toString())
				&& relComponents[LABELS.conn2.ordinal()].getAnnValue().equals(
						"")) {
			relComponents[LABELS.sClass2A.ordinal()].getJComponent()
					.setEnabled(false);
			relComponents[LABELS.sClass2B.ordinal()].getJComponent()
					.setEnabled(false);
		}
		if (relComponents[LABELS.sClass2A.ordinal()].getAnnValue().equals("")) {
			relComponents[LABELS.sClass2B.ordinal()].getJComponent()
					.setEnabled(false);
		}
		/**
		 * Remove connective span/site if it violates the relation type
		 */
		String relType = relComponents[LABELS.rel.ordinal()].getAnnValue();
		String connSpanList = relComponents[LABELS.connSpanList.ordinal()]
				.getAnnValue();
		if (SpanList.isSpanList(connSpanList)
				&& (relType.equals(RELTYPELABELS.Implicit.toString())
						|| relType.equals(RELTYPELABELS.EntRel.toString()) || relType
							.equals(RELTYPELABELS.NoRel.toString()))) {
			relComponents[LABELS.connSpanList.ordinal()].setAnnValue("");
		} else if (!SpanList.isSpanList(connSpanList)
				&& (relType.equals(RELTYPELABELS.Explicit.toString()) || relType
						.equals(RELTYPELABELS.AltLex.toString()))) {
			relComponents[LABELS.connSpanList.ordinal()].setAnnValue("");
		}
		commentPane.setEnabled(true);
	}

	public void disableAll() {
		for (int i = 0; i < relComponents.length; i++) {
			relComponents[i].getJComponent().setEnabled(false);
			if (relComponents[i] instanceof LabelPair) {
				LabelPair lp = (LabelPair) relComponents[i];
				lp.getLabel().setEnabled(false);
			}
		}
		commentPane.setEnabled(false);
	}

	public void newAction() {
		relation = new Relation(fileManager);
		for (int i = 0; i < relComponents.length; i++) {
			relComponents[i].annReset();
		}
		commentPane.setText(null);
		enableRelationType();
	}

	public SpanButton[] selectionAction(Relation relation, Relation parent) {
		if (parent == null) {
			this.relation = relation;
			this.isParent = true;
			relation.setPBComponents();
		} else {
			this.relation = parent;
			this.isParent = false;
		}
		
		relation.setComponents(relComponents, commentPane);
		enableRelationType();
		
		if (parent == null) {
			enableAdjuBox(true);						
		} else {
			enableAdjuBox(false);			
		}

		return spanButtons;
	}

	public Relation saveAction() {
		relation.setNewVals(relComponents, commentPane);
		/**
		 * In case we are adding a new relation that matches a ghost relation
		 * that already exists:
		 */
		List<Relation> model = fileManager.getRelationList();
		if (model.contains(relation)) {
			Relation original = model.get(model.indexOf(relation));
			if (original.isGhost()) {
				relation = original;
				relation.setNewVals(relComponents, commentPane);
			}
		}
		disableAll();
		return relation;
	}
	
	public Relation acceptAction() {
		relation.setAcceptVals(relComponents, commentPane);
		return relation;
	}
	
	public Relation rejectAction() {
		relation.setNoVals(relComponents, commentPane);
		List<Relation> model = fileManager.getRelationList();
		if (model.contains(relation)) {
			Relation original = model.get(model.indexOf(relation));
			if (original.isGhost()) {
				relation = original;
				relation.setNewVals(relComponents, commentPane);
			}
		}
		disableAll();
		return relation;		
	}

	public boolean inputAction() {
		return relation.isEquivalent(relComponents, commentPane);
	}
}
