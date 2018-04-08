package as.play;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.Timer;

import static as.play.SwingUtil.*;

public class ButtonPanel extends JPanel {
	
	private static final Log log = new Log(ButtonPanel.class);
	
	private final JLabel statusLabel = new JLabel(" ");
	private final JTabbedPane tabs = new JTabbedPane();
//	private final JToolBar bar = new JToolBar();
	private final Timer timer = new Timer(100, e -> updateStatus());
	
	private Engine engine;
	private String mode;
	private File currentFile;
	private long startTimeNano;
	private volatile Info currentInfo;
	
	public ButtonPanel () {
//		super(new BorderLayout());
		super(new GridBagLayout());
		
		JPanel bar = new JPanel(new GridBagLayout());
		bar.add(webdingsButton(WD_LEFT2, e -> prev()));
		bar.add(webdingsButton(WD_RIGHT2, e -> playnext()));
		bar.add(webdingsButton(WD_RIGHT, e -> playcurrent()));
		bar.add(webdingsButton(WD_PAUSE, e -> pause()));
		bar.add(webdingsButton(WD_STOP, e -> stop()));
		
//		add(bar, BorderLayout.NORTH);
//		add(tabs, BorderLayout.CENTER);
//		add(statusLabel, BorderLayout.SOUTH);
		add(bar, gbc().pos(1,1).fillBoth().insets(5));
		add(tabs, gbc().pos(1,2).weightBoth().fillBoth().insets(0,5,0,5));
		add(statusLabel, gbc().pos(1,3).fillBoth().insets(5));
	}
	
	@Override
	public void validate () {
		//setMinimumSize(new Dimension(getFont().getSize()*2));
	}
	
	public void setMode (String mode) {
		log.println("set mode " + mode);
		this.mode = mode;
	}
	
	// pc from player - on AWT thread
	private void onchange (PropertyChangeEvent e) {
		String n = e.getPropertyName();
		Object v = e.getNewValue();
		log.println("property change: " + n + " -> " + v);
		switch (n) {
			case Engine.exit: onexit(((Integer)v).intValue()); return;
			case Engine.start: onstart((File)v); return;
		}
	}
	
	private void prev () {
		log.println("prev");
	}
	
	private void playcurrent () {
		log.println("play current");
	}
	
	public void playspecific (File f) {
		log.println("playspecific: " + (f != null ? f.getName() : null));
		if (f != null) {
			engine.stop();
			engine.play(f);
		}
	}
	
	private void updateStatus() {
		statusLabel.setText(statusString());
	}
	
	private String statusString() {
		if (currentFile != null) {
			double s = Util.nanoToS(System.nanoTime() - startTimeNano);
			Info i = currentInfo;
			StringBuffer sb = new StringBuffer("playing ");
			sb.append(Util.formatTime(s)).append(" * ");
			if (i != null) {
				sb.append(Util.formatTime(i.duration));
				sb.append(" * ").append(i.type);
				sb.append(" * ").append((int) i.rate).append(" kbps");
				sb.append(" * ").append((int) i.freq).append(" Hz");
				sb.append(" * ").append(i.ch);
			}
			//sb.append(currentFile.getName());
			return sb.toString();
		} else {
			return "stopped";
		}
	}
	
	private void onstart(File f) {
		log.println("on start");
		startTimeNano = System.nanoTime();
		currentFile = f;
		Util.EX.submit(() -> { currentInfo = engine.info(f); });
		PlayFrame.config.get(f, true).increment();
		PlayFrame.saveConfig();
//		Util.increment(f);
		updateStatus();
		timer.start();
		repaint();
	}
	
	private void onexit (int v) {
		log.println("on exit " + v);
		startTimeNano = 0;
		currentFile = null;
		currentInfo = null;
		updateStatus();
		timer.stop();
		repaint();
		
		if (v == 0) {
			playnext();
		}
	}
	
	private void playnext () {
		log.println("play next");
		FilePanel fp = (FilePanel) tabs.getSelectedComponent();
		File f = fp.next(mode);
		if (f != null) {
			playspecific(f);
		}
	}

	public Engine getEngine () {
		return engine;
	}
	
	public void setEngine (Engine p) {
		log.println("set engine " + p);
		if (this.engine != null) {
			this.engine.stop();
			for (PropertyChangeListener l : this.engine.getProps().getPropertyChangeListeners()) {
				this.engine.getProps().removePropertyChangeListener(l);
			}
		}
		this.engine = p;
		this.engine.getProps().addPropertyChangeListener(e -> SwingUtilities.invokeLater(() -> onchange(e)));
	}
	
	public void pause () {
		if (engine != null) {
			engine.pause();
			//statusLabel.setText("paused");
			updateStatus();
		}
	}
	
	public void stop () {
		if (engine != null) {
			engine.stop();
			currentFile = null;
			timer.stop();
			repaint();
			updateStatus();
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
				p.put("tabdir" + n, fp.getDir().getAbsolutePath());
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
		Util.accept(getSelectedFilePanel(), fp -> fp.changedir());
	}
	
	public File currentFile () {
		return currentFile;
	}
	
	public void rename () {
		log.println("rename");
		Util.accept(getSelectedFilePanel(), fp -> fp.rename());
	}
	
	public void opendir () {
		log.println("opendir");
		Util.accept(getSelectedFile(), f -> {
			try {
				Desktop.getDesktop().open(f.getParentFile());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.toString(), "Open Dir", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	private FilePanel getSelectedFilePanel () {
		int i = tabs.getSelectedIndex();
		return i >= 0 ? (FilePanel) tabs.getComponentAt(i) : null;
	}
	
	private File getSelectedFile () {
		FilePanel fp = getSelectedFilePanel();
		return fp != null ? fp.getSelectedFile() : null;
	}
	
	public void info () {
		log.println("info");
		Util.accept(getSelectedFile(), f -> {
			Info info = engine.info(f);
			TextDialog dialog = new TextDialog(this, "Info: " + f.getName(), info.out);
			dialog.setVisible(true);
		});
	}
	
	public void delete () {
		log.println("delete");
		Util.accept(getSelectedFilePanel(), fp -> fp.delete());
	}
	
	public void massrename () {
		Util.accept(getSelectedFilePanel(), fp -> fp.massrename());
	}

	public void convert () {
		Util.accept(getSelectedFilePanel(), fp -> fp.convert());
	}

}
