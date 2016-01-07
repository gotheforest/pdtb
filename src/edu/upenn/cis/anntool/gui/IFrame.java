package edu.upenn.cis.anntool.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class IFrame extends JInternalFrame implements MouseListener,
		ComponentListener, MouseMotionListener {

	public IFrame(String title, JComponent component, JDesktopPane desktop) {
		super(title, true, false, true, true);
		add(component);
		pack();
		setVisible(true);
		setFocusable(false);
		addMouseListener(this);
		addComponentListener(this);
		addRecursiveListener(this);
		desktop.add(this);
		//this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public static Dimension computeOuterBounds(JInternalFrame[] iframes) {
		int width = 0;
		int height = 0;
		for (JInternalFrame iframe : iframes) {
			int w = iframe.getWidth() + iframe.getX();
			if (w > width) {
				width = w;
			}
			int h = iframe.getHeight() + iframe.getY();
			if (h > height) {
				height = h;
			}
		}
		return new Dimension(width, height);
	}

	private void addRecursiveListener(JComponent component) {
		component.addMouseMotionListener(this);
		Component[] components = component.getComponents();
		for (Component c : components) {
			if (c instanceof JComponent) {
				addRecursiveListener((JComponent) c);
			}
		}
	}

	private void layer(MouseEvent e) {
		//toFront();
	}

	public void mouseEntered(MouseEvent e) {
		layer(e);
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		toFront();
	}

	private void rebound(ComponentEvent e) {
		toFront();
		JDesktopPane desktop = getDesktopPane();
		desktop.setPreferredSize(computeOuterBounds(desktop.getAllFrames()));
		desktop.revalidate();
	}

	public void componentResized(ComponentEvent e) {
		rebound(e);
	}

	public void componentMoved(ComponentEvent e) {
		rebound(e);
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		layer(e);
	}

}
