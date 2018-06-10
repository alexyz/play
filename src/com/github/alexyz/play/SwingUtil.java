package com.github.alexyz.play;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public final class SwingUtil {
	
	public static final Font WEBDINGS = new Font("Webdings", 0, 24);
	public static final String WD_LEFT = "\uf033";
	public static final String WD_RIGHT = "\uf034";
	public static final String WD_UP = "\uf035";
	public static final String WD_DOWN = "\uf036";
	public static final String WD_LEFT2 = "\uf037";
	public static final String WD_RIGHT2 = "\uf038";
	public static final String WD_LEFT3 = "\uf039";
	public static final String WD_RIGHT3 = "\uf03a";
	public static final String WD_PAUSE = "\uf03b";
	public static final String WD_STOP = "\uf03c";

	public static JRadioButtonMenuItem radioItem (String title, ActionListener l) {
		JRadioButtonMenuItem i = new JRadioButtonMenuItem(title);
		i.addActionListener(l);
		return i;
	}
	
	public static JMenuItem menuItem (String title, ActionListener l) {
		JMenuItem i = new JMenuItem(title);
		i.addActionListener(l);
		return i;
	}
	
	/** create menu and add menu items */
	public static JMenu menu (String title, JMenuItem... a) {
		JMenu m = new JMenu(title);
		for (JMenuItem i : a) {
			m.add(i);
		}
		return m;
	}
	
	/** create menu and add menu items and create single button group */
	public static JMenu radioMenu (String title, JRadioButtonMenuItem... a) {
		JMenu m = new JMenu(title);
		ButtonGroup g = new ButtonGroup();
		for (JRadioButtonMenuItem i : a) {
			g.add(i);
			m.add(i);
		}
		return m;
	}
	
	/** create menu and add menu items and create single button group */
	public static <T> JMenu radioMenu (String title, Function<T,JRadioButtonMenuItem> f, T... a) {
		JMenu m = new JMenu(title);
		ButtonGroup g = new ButtonGroup();
		for (T t : a) {
			JRadioButtonMenuItem i = f.apply(t); 
			g.add(i);
			m.add(i);
		}
		return m;
	}
	
	public static JComponent webdingsButton (String label, ActionListener l) {
//		JButton b = new JButton(label);
		FlatButton b = new FlatButton(label);
		b.setFont(WEBDINGS);
		b.setListener(l);
		return b;
	}
	
	public static JLabel label(String t) {
		return new JLabel(t);
	}

	public static JButton button (String text, ActionListener l) {
		JButton b = new JButton(text);
		b.addActionListener(l);
		return b;
	}
	
	public static JPanel flowPanel(Component... a) {
		FlowLayout fl = new FlowLayout(FlowLayout.CENTER, 5, 5);
		JPanel p = new JPanel(fl);
		for (Component c : a) {
			p.add(c);
		}
		return p;
	}
	
	public static JPanel boxPanel(Component... a) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		for (Component c : a) {
			p.add(c);
		}
		return p;
	}
	
//	public static JPanel xPanel(Component... a) {
//		JPanel p = new JPanel();
//		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
//		for (Component c : a) {
//			p.add(c);
//		}
//		return p;
//	}
	
	public static JPanel xpanel (JComponent... a) {
		JPanel p = new JPanel(new GridBagLayout());
		int x = 0;
		for (int n = 0; n < a.length; n++) {
			if (n > 0) {
				p.add(pad(10), gbc().pos(x++,0));
			}
			p.add(a[n], gbc().pos(x++,0));
		}
		return p;
	}
	
	public static JPanel pad (int z) {
		JPanel c = new JPanel();
		Dimension d = new Dimension(z,z);
		c.setMinimumSize(d);
		c.setPreferredSize(d);
		c.setMaximumSize(d);
		return c;
	}
	
	public static GbcHelper gbc() {
		return new GbcHelper();
	}
	
	public static Container border (Container p) {
		for (Component c : p.getComponents()) {
			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				if (jc.getBorder() == null) {
					jc.setBorder(new LineBorder(Color.black));
				}
			}
			if (c instanceof JPanel) {
				border((Container) c);
			}
		}
		return p;
	}
	
//	public static GridBagConstraints gca (int x, int y, int a) {
//		return new GridBagConstraints(x, y, 1, 1, 1, 1, a, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0);
//	}
//	
//	public static GridBagConstraints gc (int x, int y, int w, int h, double wx, double wy, int a, int f) {
//		return new GridBagConstraints(x, y, w, h, wx, wy, a, f, new Insets(5,5,5,5), 0, 0);
//	}
//	
//	public static GridBagConstraints gcwh (int x, int y, int w, int h) {
//		return new GridBagConstraints(x, y, w, h, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0);
//	}

	public static void radioSet (JMenu m, String name) {
		for (int x = 0; x < m.getMenuComponentCount(); x++) {
			JRadioButtonMenuItem c = (JRadioButtonMenuItem) m.getMenuComponent(x);
			if (c.getText().equals(name)) {
				c.setSelected(true);
				for (ActionListener l : c.getActionListeners()) {
					l.actionPerformed(null);
				}
			}
		}
	}
	
	public static String radioGet (JMenu m) {
		for (int x = 0; x < m.getMenuComponentCount(); x++) {
			JRadioButtonMenuItem c = (JRadioButtonMenuItem) m.getMenuComponent(x);
			if (c.isSelected()) {
				return c.getText();
			}
		}
		return null;
	}
	
//	public static JPanel pad (JComponent comp) {
//		return pad(comp, 5,5,5,5);
//	}
	
//	public static JPanel pad (JComponent comp, int top, int left, int bot, int right) {
//		JPanel p2 = new JPanel(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//		c.anchor = GridBagConstraints.WEST;
//		c.weightx = 1;
//		c.weighty = 1;
//		c.fill = GridBagConstraints.BOTH;
//		c.insets = new Insets(top,left,bot,right);
//		p2.add(comp, c);
//		return p2;
//	}
	
	private SwingUtil () {
		//
	}
	
}
