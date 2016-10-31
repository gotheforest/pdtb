package edu.upenn.cis.anntool.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.upenn.cis.anntool.util.FileManager;
import edu.upenn.cis.anntool.util.Relation;
import edu.upenn.cis.anntool.util.SpanList;

public class MainFrame extends JFrame implements WindowListener {

	private static final long serialVersionUID = 1L;

	private static final String title = "Annotator";

	private RawTextPanel rawTextPanel;
	private RelListPanel relListPanel;
	private RelPanel relPanel;
	private NavPanel navPanel;

	private FileManager fileManager;

	JDesktopPane desktop = new JDesktopPane();

	public MainFrame(FileManager fileManager) {
		super(title);

		this.fileManager = fileManager;

		// relation Panel
		relPanel = new RelPanel(this, fileManager);
		JInternalFrame relFrame = new IFrame("Relation Editor", relPanel,
				desktop);
		relFrame.setMinimumSize(relFrame.getPreferredSize());
		relFrame.setLocation(60, 60);
		// relPanel.setBorder(BorderFactory.createTitledBorder("relPanel"));

		// raw text Pane
		rawTextPanel = new RawTextPanel(this, fileManager);
		JScrollPane rawTextSP = new JScrollPane(rawTextPanel);
		rawTextSP.setPreferredSize(new Dimension(
				relPanel.getPreferredSize().width,
				relPanel.getPreferredSize().height));
		JInternalFrame rawTextFrame = new IFrame("Raw Text", rawTextSP, desktop);
		rawTextFrame.setLocation(120, 120);

		// relation list
		relListPanel = new RelListPanel(this, fileManager);
		relListPanel.setPreferredSize(new Dimension(relListPanel
				.getPreferredSize().width, relPanel.getPreferredSize().height));
		JInternalFrame relListFrame = new IFrame("Relation List", relListPanel,
				desktop);
		relListFrame.setLocation(0, 0);
		// relListPanel.setBorder(BorderFactory.createTitledBorder("relListPanel"));

		// combo Panel
		navPanel = new NavPanel(this, fileManager);
		// comboPanel.setBorder(BorderFactory.createTitledBorder("comboPanel"));

		JPanel panel = (JPanel) getContentPane();
		panel.add(navPanel, BorderLayout.PAGE_START);

		desktop.setBackground(Color.BLACK);
		JScrollPane desktopSP = new JScrollPane(desktop);
		desktopSP.getVerticalScrollBar().setUnitIncrement(16);
		desktopSP.getHorizontalScrollBar().setUnitIncrement(16);

		panel.add(desktopSP, BorderLayout.CENTER);

		String fontName = fileManager.getLanguage().getFont();
		rawTextPanel.setFontName(fontName);
		relListPanel.setFontName(fontName);
		navPanel.setFontName(fontName);

		addWindowListener(this);
		pack();
		setVisible(true);

		// setSize(new Dimension(relFrame.getWidth() + relListFrame.getWidth()
		// + rawTextFrame.getWidth(), getHeight() + relFrame.getHeight()));
		Dimension outerBounds = IFrame.computeOuterBounds(desktop
				.getAllFrames());
		setSize(new Dimension((int) outerBounds.getWidth()
				+ desktopSP.getVerticalScrollBar().getPreferredSize().width,
				(int) outerBounds.getHeight() + getHeight()));

		desktop.setPreferredSize(outerBounds);
		desktop.revalidate();
	}

	// TODO: each of the components should implement a class that has these
	// actions? Or maybe I can have a single function that handles all the
	// cases?

	public void loadAction() {
		rawTextPanel.loadAction();
		relPanel.disableAll();
		relListPanel.loadAction();
		navPanel.loadAction();
		setTitle(title + ":   " + fileManager.getTitle());
	}

	public void newAction() {
		relPanel.newAction();
		relListPanel.newAction();
		navPanel.newAction();
		rawTextPanel.cancelAction();
	}
	
	public void expandAction() {
		relListPanel.expandAction();
	}

	public void selectionAction(Relation relation, Relation parent) {
		SpanButton[] spanButtons = relPanel.selectionAction(relation, parent);
		navPanel.cancelAction();
		rawTextPanel.selectionAction(relation, spanButtons);	
				
		relListPanel.selectionAction(parent != null, parent == null && !relation.isGhost(), relation.isRejected());
		if (parent != null) {
			relListPanel.acceptAction(parent != null, isValidAccept(relation, parent));
		}
	}

	private boolean isValidAccept(Relation relation, Relation parent) {
		if (parent.toString().contains("Single Annotator")) {
			if (relation.isComplete()) {
				return true;
			}
		} else if (parent.toString().contains("Annotators agree")) {
			if (relation.isComplete()) {	
				return true;
			}
		}
		return false;
	}
	
	public void saveAction() {
		// If a duplicate relation is created, it does not save the relation and
		// moves selection to the duplicate
		Relation relation = relPanel.saveAction();
		if (relation.getIdentifierSpan().equals("")) {	
			if (relation.getRelationType().equals("Explicit") || relation.getRelationType().equals("AltLex")) {				
				JOptionPane.showMessageDialog(this, "Set the Connective or AltLex span before saving");
			} else {
				JOptionPane.showMessageDialog(this, "Set the Arg2 span before saving");
			}

			return;
		}		
		navPanel.cancelAction();
		rawTextPanel.cancelAction();
		// This must be done last because it calls edit action:
		relListPanel.saveAction(relation);
	}
	
	public void acceptAction() {
		// If a duplicate relation is created, it does not save the relation and
		// moves selection to the duplicate
		Relation relation = relPanel.acceptAction();
		navPanel.cancelAction();
		rawTextPanel.cancelAction();
		// This must be done last because it calls edit action:		
		relListPanel.saveAction(relation);		
	}
	
	public void rejectAction() {
		Relation relation = relPanel.rejectAction();
		navPanel.cancelAction();
		rawTextPanel.cancelAction();
		// This must be done last because it calls edit action:
		relListPanel.saveAction(relation);		
	}
	
	public void annotatorRejectAction() {
		Relation relation = relPanel.annotatorRejectAction();
		navPanel.cancelAction();
		rawTextPanel.cancelAction();
		// This must be done last because it calls edit action:
		relListPanel.saveAction(relation);			
	}
	
	public void undoRejectAction() {
		Relation relation = relPanel.undoRejectAction();
		navPanel.cancelAction();
		rawTextPanel.cancelAction();
		// This must be done last because it calls edit action:
		relListPanel.saveAction(relation);			
	}

	public void deleteAction(Relation relation) {
		relPanel.disableAll();
		navPanel.cancelAction();
		rawTextPanel.cancelAction();
		// This must be done last because it may call edit action:
		relListPanel.deleteAction(relation);
	}

	public void cancelAction() {
		relPanel.disableAll();
		relListPanel.cancelAction();
		navPanel.cancelAction();
		rawTextPanel.cancelAction();
	}

	public void inputAction(JComponent requestor, SpanButton[] spanButtons) {
		boolean isSame = relPanel.inputAction();
		relListPanel.inputAction(isSame);
		navPanel.inputAction(isSame);
		rawTextPanel.inputAction(spanButtons);
	}
	
	public void spanAction(SpanButton b) {
		rawTextPanel.spanAction(b);
	}

	public void fontSizeAction(float fontSize) {
		rawTextPanel.fontSizeAction(fontSize);
	}

	public void searchAction(String word) {
		rawTextPanel.searchAction(word);
	}

	public void addAllAction() {
		SpanList connSpans = rawTextPanel.addAllAction();
		relPanel.disableAll();
		relListPanel.addAllAction(connSpans);
	}

	// @Override
	public void windowClosing(WindowEvent e) {
		boolean needsToBeSaved = relListPanel.windowClosingAction();
		if (needsToBeSaved) {
			JOptionPane.showMessageDialog(this,
					"Please save, cancel, or delete current relation.");
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		} else {
			//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

	// @Override
	public void windowActivated(WindowEvent e) {
	}

	// @Override
	public void windowClosed(WindowEvent e) {
	}

	// @Override
	public void windowDeactivated(WindowEvent e) {
	}

	// @Override
	public void windowDeiconified(WindowEvent e) {
	}

	// @Override
	public void windowIconified(WindowEvent e) {
	}

	// @Override
	public void windowOpened(WindowEvent e) {
	}

}
