package edu.upenn.cis.anntool.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;

import edu.upenn.cis.anntool.settings.ColorConstants;
import edu.upenn.cis.anntool.util.FileManager;
import edu.upenn.cis.anntool.util.Relation;
import edu.upenn.cis.anntool.util.Span;
import edu.upenn.cis.anntool.util.SpanList;
import edu.upenn.cis.anntool.util.SpanString;

public class RawTextPanel extends JPanel implements PanelInterface {

	private static final long serialVersionUID = 1L;	
	private MainFrame mainFrame;
	private FileManager fileManager;

	private JTextPane rawTextPane = new JTextPane();

	// private JLabel[] tipserLabels = new
	// JLabel[10];//TipsterParser.NumberOfAttributes];
	private JTextArea tipsterTextArea1 = new JTextArea();
	private JTextArea tipsterTextArea2 = new JTextArea();

	// This is for the search box highlights
	private List<HighlightPainter> searchPainters = new ArrayList<HighlightPainter>();
	// This is for the colorful span button highlights
	private List<HighlightPainter> spanPainters = new ArrayList<HighlightPainter>();
	// This is for the current selection's highlight
	private List<HighlightPainter> selectionPainter = new ArrayList<HighlightPainter>();
	// This is for the rest of the selections' highlights
	private List<HighlightPainter> selectionPainters = new ArrayList<HighlightPainter>();
	// This is for the current selection's underline
	private SpanList selectionUnderline = new SpanList();
	// This is for the current selection's spans
	private SpanList selections = new SpanList();
	// This is for the trace underlines after changing a span button selection
	private SpanList traceUnderlines = new SpanList();
	private String currentSearchString;
	private SpanList currentSearchSpans = new SpanList();
	private TextPaneMouseAdapter tpma;
	// This is for the article breaks
	private List<HighlightPainter> breakPainter = new ArrayList<HighlightPainter>();
	private SpanList breakSpans = new SpanList();

	private static int previousCaretPos = 0;
	
	public RawTextPanel(final MainFrame mainFrame, final FileManager fileManager) {
		super(new GridBagLayout());
		this.mainFrame = mainFrame;
		this.fileManager = fileManager;

		rawTextPane.setEditable(false);
		addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent arg0) {
			}

			public void componentResized(ComponentEvent arg0) {
				setHeight();
			}

			public void componentMoved(ComponentEvent arg0) {
			}

			public void componentHidden(ComponentEvent arg0) {
			}
		});
		// setSelectionColor(ColorConstants.SelectionColor);
		// getCaret().setVisible(false);
		tpma = new TextPaneMouseAdapter(this, rawTextPane, fileManager,
				selectionPainter, selectionPainters, selectionUnderline,
				selections);

		tipsterTextArea1.setEditable(false);
		tipsterTextArea2.setEditable(false);

		GridBagConstraints c;
		int y = -1;

		c = new GridBagConstraints();
		y++;
		c.gridx = 0;
		c.gridy = y;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(tipsterTextArea1, c);

		c = new GridBagConstraints();
		y++;
		c.gridx = 0;
		c.gridy = y;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new JSeparator(), c);

		c = new GridBagConstraints();
		y++;
		c.gridx = 0;
		c.gridy = y;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(tipsterTextArea2, c);

		c = new GridBagConstraints();
		y++;
		c.gridx = 0;
		c.gridy = y;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new JSeparator(), c);

		c = new GridBagConstraints();
		y++;
		c.gridx = 0;
		c.gridy = y;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;

		add(rawTextPane, c);
	}

	// public boolean getScrollableTracksViewportWidth ( ) {
	// return false ; // Returning false for non-warpping
	// }

	public void setHeight() {
		try {
			Rectangle r;
			r = rawTextPane.modelToView(rawTextPane.getDocument()
					.getLength());
			rawTextPane.setPreferredSize(new Dimension(0, r.y + r.height));
			r = tipsterTextArea1.modelToView(tipsterTextArea1.getDocument()
					.getLength());
			tipsterTextArea1.setPreferredSize(new Dimension(0, r.y + r.height));
			r = tipsterTextArea2.modelToView(tipsterTextArea2.getDocument()
					.getLength());
			tipsterTextArea2.setPreferredSize(new Dimension(0, r.y + r.height));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void loadAction() {
		rawTextPane.setText(fileManager.getRawText());

		rawTextPane.getStyledDocument().setCharacterAttributes(0,
				rawTextPane.getText().length(), new SimpleAttributeSet(), true);
		rawTextPane.getHighlighter().removeAllHighlights();
		searchPainters.clear();
		spanPainters.clear();
		selectionPainter.clear();
		selectionPainters.clear();
		selectionUnderline.clear();
		selections.clear();
		traceUnderlines.clear();
		rawTextPane.setCaretPosition(0);
		searchAction(currentSearchString);

		// Tipster Data
		breakPainter.clear();
		breakSpans.clear();
		if (fileManager.getTipsterData1() == null) {
			tipsterTextArea1.setText("");
			tipsterTextArea2.setText("");
		} else {
			List<String> breaks;
			int index;

			breaks = fileManager.getTipsterData1().getTextAfterArticleBreaks();
			for (ListIterator<String> i = breaks.listIterator(); i.hasNext();) {
				String brk = i.next();
				index = fileManager.getRawText().indexOf(brk);
				breakSpans.add(new Span(index - 1, index));
			}
			tipsterTextArea1.setText(fileManager.getTipsterData1()
					.getTipsterMap());
			if (fileManager.getTipsterData2() == null) {
				tipsterTextArea2.setText("");
			} else {
				String lastLine = fileManager.getTipsterData1()
						.getLastLineOfText();
				index = fileManager.getRawText().lastIndexOf(lastLine)
						+ lastLine.length();
				breakSpans.add(new Span(index + 1, index + 2));

				breaks = fileManager.getTipsterData2()
						.getTextAfterArticleBreaks();
				for (ListIterator<String> i = breaks.listIterator(); i
						.hasNext();) {
					String brk = i.next();
					index = fileManager.getRawText().indexOf(brk);
					breakSpans.add(new Span(index - 1, index));
				}
				tipsterTextArea2.setText(fileManager.getTipsterData2()
						.getTipsterMap());
			}
			addLineHighLights(breakPainter, breakSpans,
					ColorConstants.BreakColor);

		}

		tpma.loadAction();

		setHeight();

	}

	public void spanAction(SpanButton b) {
		if (b.isSelected()) {
			b.setAnnValue(fileManager.getIndexSpanMap().charToToken(selections)
					.toString());
			removeHighLights(selectionPainter);
			removeHighLights(selectionPainters);
			removeUnderlines(selectionUnderline);
			selections.clear();
		} else if (b.isSpan()) {
			SpanList spanList = new SpanList(b.getAnnValue());
			addUnderlines(traceUnderlines, spanList);
			b.setAnnValue(null);
		}
		// TODO: what does this do:
		// getCaret().setSelectionVisible(false);
	}

	public void selectionAction(Relation relation, SpanButton[] spanButtons) {
		inputAction(spanButtons);
		if (relation.getLocationStart() >= 0) {
			Span dummySpan = new Span(relation.getLocationStart(),
					relation.getLocationStart() + 1);
			
			
			int caretPos = fileManager.getIndexSpanMap().tokenToChar(dummySpan).getStart();
		
			//System.out.println(caretPos + " ::: " + fileManager.getRawText().length());
			//if (caretPos > fileManager.getRawText().length()) {
			//	caretPos = fileManager.getRawText().length();
			//}
			rawTextPane.setCaretPosition(caretPos);
			centerLineInScrollPane(rawTextPane);
			/*
			try {
				Rectangle caretRect = rawTextPane.getUI().modelToView(rawTextPane, rawTextPane.getCaretPosition());
				rawTextPane.scrollRectToVisible(caretRect);
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}*/
		}
	}
	
	public static void centerLineInScrollPane(JTextPane component) {
	    Container container = SwingUtilities.getAncestorOfClass(JViewport.class, component);
	    if (container == null) return;

	    try {
	        Rectangle r = component.modelToView(component.getCaretPosition());
	        JViewport viewport = (JViewport)container;

	        int extentWidth = viewport.getExtentSize().width;
	        int viewWidth = viewport.getViewSize().width;

	        int x = Math.max(0, r.x - (extentWidth / 2));
	        x = Math.min(x, viewWidth - extentWidth);

	        int extentHeight = viewport.getExtentSize().height;
	        int viewHeight = viewport.getViewSize().height;

	        int y = Math.max(0, r.y - (extentHeight / 2));
	        y = Math.min(y, viewHeight - extentHeight);

	        viewport.setViewPosition(new Point(x, y));
	    }
	    catch(BadLocationException ble) {
	    	ble.printStackTrace();
	    }
	}	

	public void inputAction(SpanButton[] spanButtons) {
		removeHighLights(spanPainters);
		for (int i = 0; i < spanButtons.length; i++) {
			if (spanButtons[i].isEnabled() && spanButtons[i].isSelected()
					&& spanButtons[i].isSpan()) {
				Color color = spanButtons[i].getHighLight();
				SpanList spanList = fileManager.getIndexSpanMap().tokenToChar(
						new SpanList(spanButtons[i].getAnnValue()));
				addHighLights(spanPainters, spanList, color);
			}
		}
	}

	public void cancelAction() {
		removeHighLights(spanPainters);
		removeUnderlines(traceUnderlines);
	}

	public void fontSizeAction(float fontSize) {
		rawTextPane.setFont(getFont().deriveFont(fontSize));
	}

	public void addUnderlines(SpanList underlines, Span span) {
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setUnderline(attributes, true);
		rawTextPane.getStyledDocument().setCharacterAttributes(span.getStart(),
				span.getEnd() - span.getStart(), attributes, false);
		underlines.add(span);
	}

	public void addUnderlines(SpanList underlines, SpanList spanList) {
		for (Iterator<Span> j = spanList.iterator(); j.hasNext();) {
			Span span = j.next();
			addUnderlines(underlines, span);
		}
	}

	public void removeUnderlines(SpanList underlines) {
		for (Iterator<Span> j = underlines.iterator(); j.hasNext();) {
			Span span = j.next();
			rawTextPane.getStyledDocument().setCharacterAttributes(
					span.getStart(), span.getEnd() - span.getStart(),
					new SimpleAttributeSet(), true);
		}
		underlines.clear();
	}

	public void addHighLights(List<HighlightPainter> highlightPainters,
			Span span, Color color) {
		try {
			HighlightPainter dhp = new DefaultHighlightPainter(color);
			highlightPainters.add(dhp);
			rawTextPane.getHighlighter().addHighlight(span.getStart(),
					span.getEnd(), dhp);
		} catch (BadLocationException e) {
			System.err.println("Bad Text Span For This Relation");
			e.printStackTrace();
		}
	}

	public void addHighLights(List<HighlightPainter> highlightPainters,
			SpanList spanList, Color color) {
		for (Iterator<Span> j = spanList.iterator(); j.hasNext();) {
			Span span = j.next();
			addHighLights(highlightPainters, span, color);
		}
	}

	public void addLineHighLights(List<HighlightPainter> highlightPainters,
			Span span, Color color) {
		try {
			HighlightPainter dhp = new LinePainter(color);
			highlightPainters.add(dhp);
			rawTextPane.getHighlighter().addHighlight(span.getStart(),
					span.getEnd(), dhp);
		} catch (BadLocationException e) {
			System.err.println("Bad Text Span For This Relation");
			e.printStackTrace();
		}
	}

	public void addLineHighLights(List<HighlightPainter> highlightPainters,
			SpanList spanList, Color color) {
		for (Iterator<Span> j = spanList.iterator(); j.hasNext();) {
			Span span = j.next();
			addLineHighLights(highlightPainters, span, color);
		}
	}

	public void removeHighLights(List<HighlightPainter> highlightPainters) {
		Highlighter.Highlight[] highlights = rawTextPane.getHighlighter()
				.getHighlights();
		for (int i = 0; i < highlights.length; i++) {
			if (highlightPainters.contains(highlights[i].getPainter())) {
				rawTextPane.getHighlighter().removeHighlight(highlights[i]);
			}
		}
		highlightPainters.clear();
	}

	public void searchAction(String word) {
		removeHighLights(searchPainters);
		currentSearchSpans.clear();
		currentSearchString = word;
		if (word != null && !word.equals("")) {
			try {
				SpanString[] searchTokenSpans = fileManager.tokenize(word);
				SpanString[] rawTextTokenSpans = fileManager.getTokens();
				for (int i = 0; i < rawTextTokenSpans.length; i++) {
					for (int j = 0; j < searchTokenSpans.length
							&& i + j < rawTextTokenSpans.length; j++) {
						String rawTok = rawTextTokenSpans[i + j].getToken();
						String searchTok = searchTokenSpans[j].getToken();
						if (!rawTok.equalsIgnoreCase(searchTok)) {
							break;
						} else if (j == searchTokenSpans.length - 1) {
							DefaultHighlightPainter dhp = new DefaultHighlightPainter(
									ColorConstants.searchColor);
							int start = rawTextTokenSpans[i].getStart();
							int end = rawTextTokenSpans[i + j].getEnd();
							rawTextPane.getHighlighter().addHighlight(start,
									end, dhp);
							searchPainters.add(dhp);
							currentSearchSpans.add(new Span(start, end));
						}
					}
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	public SpanList addAllAction() {
		cancelAction();
		return fileManager.getIndexSpanMap().charToToken(currentSearchSpans);
	}

	public void setFontName(String fontName) {
		/*rawTextPane.setContentType("text/html;charset=utf-8");*/
		Font f = getFont();
		setFont(new Font(fontName, f.getStyle(), f.getSize()));
	}

	// //Search that uses non-word chars instead of tokens
	// public void searchAction(String word) {
	// removeHighLights(searchPainters);
	// currentSearchSpans.clear();
	// currentSearchString = word;
	// if (word != null && !word.equals("")) {
	// try {
	// // this pattern only works with unicode:
	// Pattern wordPattern = Pattern.compile("\\W("
	// + Pattern.quote(word) + ")\\W",
	// Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	// Matcher wordMatcher = wordPattern.matcher(getDocument()
	// .getText(0, getDocument().getLength()));
	// while (wordMatcher.find()) {
	// int start = wordMatcher.start(1);
	// int end = wordMatcher.end(1);
	// DefaultHighlightPainter dhp = new DefaultHighlightPainter(
	// ColorConstants.searchColor);
	// getHighlighter().addHighlight(start, end, dhp);
	// searchPainters.add(dhp);
	// currentSearchSpans.add(new Span(start, end));
	// }
	// } catch (BadLocationException e) {
	// e.printStackTrace();
	// }
	// }
	// }

}
