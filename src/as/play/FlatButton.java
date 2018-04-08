package as.play;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public class FlatButton extends JComponent implements MouseListener {
	private static final class FlatButtonUI extends ComponentUI {
		@Override
		public Dimension getPreferredSize (JComponent c) {
			int s = (int)(c.getFont().getSize()*1.5);
			return new Dimension(s,s);
		}
	}
	private String text;
	private ActionListener listener;
	public FlatButton (String text) {
		this.text = text;
		addMouseListener(this);
		setUI(new FlatButtonUI());
		updateUI();
	}
	public void setListener (ActionListener listener) {
		this.listener = listener;
	}
	public ActionListener getListener () {
		return listener;
	}
	@Override
	protected void paintComponent (Graphics g) {
//		g.setColor(getBackground());
//		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(getForeground());
		g.setFont(getFont());
		FontMetrics m = g.getFontMetrics();
		int sw = m.stringWidth(text);
		int sh = m.getHeight();
		Dimension c = getSize();
		int x = (c.width-sw)/2;
		int y = (c.height-sh)/2;
		g.drawString(text, x, y + m.getAscent());
	}
	@Override
	public void mouseClicked (MouseEvent e) {
		if (listener != null) {
			listener.actionPerformed(new ActionEvent(this, e.getID(), "click"));
		}
	}
	@Override
	public void mousePressed (MouseEvent e) {
		setBorder(BorderFactory.createLoweredBevelBorder());
		repaint();
	}
	@Override
	public void mouseReleased (MouseEvent e) {
		setBorder(BorderFactory.createEtchedBorder());
		repaint();
	}
	@Override
	public void mouseEntered (MouseEvent e) {
		setBorder(BorderFactory.createEtchedBorder());
		//setBackground(Color.white);
		repaint();
	}
	@Override
	public void mouseExited (MouseEvent e) {
		setBorder(null);
//		Color c = UIManager.getDefaults().getColor("Button.background");
//		setBackground(c);
		repaint();
	}
}