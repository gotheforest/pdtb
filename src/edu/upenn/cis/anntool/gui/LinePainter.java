package edu.upenn.cis.anntool.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.HighlightPainter;

public class LinePainter implements HighlightPainter
{
	Color color;

	public LinePainter(Color color) {
		this.color = color;
	}

	public void paint(Graphics g, int offs0, int offs1, Shape bounds,
			JTextComponent c) {
		try {
			g.setColor(color);			
			TextUI mapper = c.getUI();
			Rectangle p0 = mapper.modelToView(c, offs0);
			g.fillRect(0, p0.y, c.getWidth(), p0.height);
		} catch (BadLocationException ble) {
			System.out.println(ble);
		}
	}
}
