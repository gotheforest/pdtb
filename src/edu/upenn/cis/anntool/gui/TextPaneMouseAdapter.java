/*
 * textPaneMouseAdapter.java
 *
 * Created on April 18, 2006, 3:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.upenn.cis.anntool.gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Highlighter.HighlightPainter;

import edu.upenn.cis.anntool.util.FileManager;
import edu.upenn.cis.anntool.util.Span;
import edu.upenn.cis.anntool.util.SpanList;

public class TextPaneMouseAdapter extends MouseAdapter implements
		MouseMotionListener, CaretListener, KeyListener {

	private RawTextPanel panel;
	private JTextPane pane;
	private FileManager fileManager;

	private boolean ctrlKeyPressed;

	private Span selection;
	private SpanList selections;
	private List<HighlightPainter> selectionPainter;
	private List<HighlightPainter> selectionPainters;
	private SpanList selectionUnderline;

	// TODO: what about double clicks for selections?

	/** Creates a new instance of textPaneMouseAdapter */
	public TextPaneMouseAdapter(RawTextPanel panel, JTextPane pane,
			final FileManager fileManager,
			List<HighlightPainter> selectionPainter,
			List<HighlightPainter> selectionPainters,
			SpanList selectionUnderline, SpanList selections) {
		this.panel = panel;
		this.pane = pane;
		this.fileManager = fileManager;
		this.selectionPainter = selectionPainter;
		this.selectionPainters = selectionPainters;
		this.selectionUnderline = selectionUnderline;
		this.selections = selections;

		pane.addMouseListener(this);
		pane.addMouseMotionListener(this);
		pane.addCaretListener(this);
		pane.addKeyListener(this);

		loadAction();
	}

	public void loadAction() {
		selection = null;
		pane.requestFocus();
	}

	private void clearLists(MouseEvent e) {
		boolean mskPressed = (e.getModifiers() & Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()) == Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask();
		// System.out.println(e.getModifiers());

		if (!(ctrlKeyPressed || mskPressed)) {
			panel.removeHighLights(selectionPainters);
			selections.clear();
			selection = null;
		}
		panel.removeHighLights(selectionPainter);
		panel.removeUnderlines(selectionUnderline);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		clearLists(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (selection != null) {
			panel.addHighLights(selectionPainters, selection, pane
					.getSelectionColor());
			// pane.addHighLights(selectionPainters, spanToHighlight,
			// ColorConstants.SelectionColor);
			selections.add(selection);
		}
	}

	// @Override
	public void mouseDragged(MouseEvent e) {
		clearLists(e);
		if (fileManager.getIndexSpanMap() != null) {
			selection = fileManager.getIndexSpanMap().getTokenizedSpan(
					new Span(pane.getSelectionStart(), pane.getSelectionEnd()));
			if (selection != null) {
				// //Debugging:
				// System.err.println(selection);
				panel.addHighLights(selectionPainter, selection, pane
						.getSelectionColor());
				// // Debugging...so that you can see the different colors
				// pane.addHighLights(selectionPainter, spanToHighlight,
				// ColorConstants.SelectionColor);
				panel.addUnderlines(selectionUnderline, selection);
			}
		}
	}

	// @Override
	public void caretUpdate(CaretEvent e) {
		// int end = e.getDot();
		// int start = e.getMark();
	}

	// @Override
	public void keyPressed(KeyEvent e) {
		e.getComponent().requestFocus();
		int ctrlKey = e.getKeyCode();
		if (ctrlKey == 32) {
			ctrlKeyPressed = true;
		} else {
			ctrlKeyPressed = false;
		}
	}

	// @Override
	public void keyReleased(KeyEvent e) {
		e.getComponent().requestFocus();
		int ctrlKey = e.getKeyCode();
		if (ctrlKey == 32) {
			ctrlKeyPressed = false;
		}
	}

	// @Override
	public void keyTyped(KeyEvent e) {
	}

	public void mouseMoved(MouseEvent arg0) {
		// For compatibility with Java 1.5
	}

}
