package as.play;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.*;

import static as.play.SwingUtil.*;

public class ButtonPanel extends JPanel {
	
	private static final Log log = new Log(ButtonPanel.class);
	
	private final JLabel infoLabel = new JLabel(" ");
	private final JTabbedPane tabs = new JTabbedPane();
	private final JToolBar bar = new JToolBar();
	
	private Player player;

	private String mode;
	private File currentFile;
	
	public ButtonPanel () {
		super(new BorderLayout());

//		setBorder(BorderFactory.createLineBorder(Color.gray));
//		infoLabel.setBorder(BorderFactory.createLineBorder(Color.gray));
//		tabs.setBorder(BorderFactory.createLineBorder(Color.gray));
//		bar.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		
		bar.add(webdingsButton(WD_LEFT2, e -> prev()));
		bar.add(webdingsButton(WD_RIGHT2, e -> playnext()));
		bar.add(webdingsButton(WD_RIGHT, e -> playcurrent()));
		bar.add(webdingsButton(WD_PAUSE, e -> pause()));
		bar.add(webdingsButton(WD_STOP, e -> stop()));
		
		add(bar, BorderLayout.NORTH);
		add(tabs, BorderLayout.CENTER);
		add(infoLabel, BorderLayout.SOUTH);
	}
	
	public void setMode (String mode) {
		log.println("set mode " + mode);
		this.mode = mode;
	}
	
	// pc from player
	private void onchange (PropertyChangeEvent e) {
		String name = e.getPropertyName();
		Object value = e.getNewValue();
		log.println("property change: " + name + " -> " + value);
		switch (name) {
			case Player.DONE: playnext(); break;
			default: break;
		}
	}

	private void prev () {
		log.println("prev");
	}
	
	private void playcurrent () {
		log.println("play current");
	}
	
	public void playspecific (File f) {
		log.println("play: " + (f != null ? f.getName() : null));
		FilePanel fp = (FilePanel) tabs.getSelectedComponent();
		if (f != null) {
			player.play(f);
			Util.increment(f);
			currentFile = f;
			repaint();
			infoLabel.setText("playing " + f.getName());
		}
	}
	
	public void playnext() {
		log.println("playnext");
		FilePanel fp = (FilePanel) tabs.getSelectedComponent();
		File f = fp.next(mode);
		playspecific(f);
	}
	
	public void setPlayer (Player p) {
		log.println("set player " + p);
		if (this.player != null) {
			this.player.stop();
			for (PropertyChangeListener l : this.player.getProps().getPropertyChangeListeners()) {
				this.player.getProps().removePropertyChangeListener(l);
			}
		}
		this.player = p;
		this.player.getProps().addPropertyChangeListener(e -> SwingUtilities.invokeLater(() -> onchange(e)));
	}
	
	public void pause () {
		if (player != null) {
			player.pause();
			infoLabel.setText("paused");
		}
	}
	
	public void stop () {
		if (player != null) {
			player.stop();
			currentFile = null;
			repaint();
			infoLabel.setText("stopped");
		}
	}
	
	public void loadPrefs (Preferences p) {
		int count = p.getInt("tabcount", 1);
		for (int n = 0; n < count; n++) {
			String dir = p.get("tabdir" + n, null);
			String name = p.get("tabname" + n, "" + n);
			if (dir != null && name != null) {
				log.println("create tab " + n + ": " + name + ", " + dir);
				FilePanel fp = new FilePanel(this);
				fp.setDir(new File(dir));
				tabs.addTab(name, fp);
			}
		}
	}
	
	public void saveprefs (Preferences p) {
		log.println("save prefs");
		p.putInt("tabcount", tabs.getTabCount());
		for (int n = 0; n < tabs.getTabCount(); n++) {
			FilePanel fp = (FilePanel) tabs.getComponentAt(n);
			if (fp != null) {
				p.put("tabdir" + n, fp.getDir());
				p.put("tabname" + n, tabs.getTitleAt(n));
			}
		}
	}
	
	public void addtab () {
		log.println("add tab");
		FilePanel fp = new FilePanel(ButtonPanel.this);
		File dir = fp.changedir();
		if (dir != null) {
			tabs.addTab(dir.getName(), fp);
			tabs.setSelectedIndex(tabs.getTabCount()-1);
		}
	}

	public void closetab () {
		log.println("close tab");
		int i = tabs.getSelectedIndex();
		if (i >= 0) {
			String title = tabs.getTitleAt(i);
			if (JOptionPane.showConfirmDialog(this, "Close " + title + "?", "Close Tab", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				tabs.removeTabAt(i);
			}
		}
	}

	public void renametab () {
		log.println("rename tab");
		int i = tabs.getSelectedIndex();
		if (i >= 0) {
			String title = tabs.getTitleAt(i);
			String title2 = (String) JOptionPane.showInputDialog(this, "Title", "Rename Tab", JOptionPane.QUESTION_MESSAGE, null, null, title);
			if (title2 != null && title2.length() > 0) {
				tabs.setTitleAt(i, title2);
			}
		}
	}

	public void changedir () {
		log.println("change dir");
		int i = tabs.getSelectedIndex();
		if (i >= 0) {
			FilePanel fp = (FilePanel) tabs.getComponentAt(i);
			log.println("tab comp=" + fp);
			fp.changedir();
		}
	}

	public File currentFile () {
		return currentFile;
	}

//	private class BPMA extends MouseAdapter {
//		@Override
//		public void mouseClicked (MouseEvent e) {
//			log.println("tabs mouse clicked " + e.getClickCount());
//			if (!e.isPopupTrigger() && e.getClickCount() == 2) {
//				addtab();
//			}
//		}
//		
//		@Override
//		public void mousePressed (MouseEvent e) {
//			if (e.isPopupTrigger()) {
//				popup(e.getPoint());
//			}
//		}
//		
//		@Override
//		public void mouseReleased (MouseEvent e) {
//			if (e.isPopupTrigger()) {
//				popup(e.getPoint());
//			}
//		}
//
//		private void popup (Point p) {
//			JPopupMenu menu = new JPopupMenu("Editor");
//			menu.add(menuItem("Rename", e -> renametab()));
//			menu.add(menuItem("Close", e -> closetab()));
//			menu.show(tabs, p.x, p.y);
//		}
//		
//		
//	}
	
}
