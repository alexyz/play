package com.github.alexyz.play;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import com.github.alexyz.play.af.*;
import com.github.alexyz.play.cfg.Config;
import com.github.alexyz.play.ff.*;

import static com.github.alexyz.play.SwingUtil.*;

public class PlayFrame extends JFrame {
	
	public static final Preferences PREFS = Preferences.userNodeForPackage(PlayFrame.class);
	public static PlayFrame frame;
	public static Config config;

	private static final Log log = new Log(PlayFrame.class);
	private static final double nanosins = 1_000_000_000.0;
	
	public static void main (String[] args) {
		try {
			long t1 = System.nanoTime();
			//print(System.getProperties());
			log.println("default charset=" + Charset.defaultCharset().displayName());
			File f = new File(System.getProperty("user.home"), ".alexyz.play.xml");
			config = new Config(f);
			config.load();
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			ToolTipManager.sharedInstance().setInitialDelay(2000);
			ToolTipManager.sharedInstance().setDismissDelay(10000);
			ToolTipManager.sharedInstance().setReshowDelay(2000);
			frame = new PlayFrame();
			frame.setVisible(true);
			long t2 = System.nanoTime();
			log.println("start time: " + (t2-t1)/nanosins + " s");
		} catch (Exception e) {
			e.printStackTrace(System.out);
			JOptionPane.showMessageDialog(null, e.toString(), "Main", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	
	public static void savePrefs () {
		try {
			log.println("save prefs");
			frame.savePrefs(PREFS);
			PREFS.flush();
		} catch (Exception e) {
			log.println("could not save prefs", e);
		}
	}
	
	private final ButtonPanel buttonPanel = new ButtonPanel();
	private JMenu modemenu, enginemenu;
	
	public PlayFrame () {
		setTitle(getClass().getSimpleName());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(buttonPanel);
		setPreferredSize(new Dimension(640, 480));
		addWindowListener(new WA());
		setJMenuBar(menuBar());
		pack();
		loadPrefs(PREFS);
	}
	
	private JMenuBar menuBar () {
		modemenu = radioMenu("Mode", m -> radioItem(m, e -> mode(m)), Util.MODES);
		enginemenu = radioMenu("Engine", x -> radioItem(x, e -> engine(x)), Util.ENGINES);
		
		JMenuBar bar = new JMenuBar();
		bar.add(menu("Tab",
				menuItem("New Tab", e -> buttonPanel.addtab()),
				menuItem("Rename Tab", e -> buttonPanel.renametab()),
				menuItem("Close Tab", e -> buttonPanel.closetab()),
				menuItem("Change Tab Directory", e -> buttonPanel.changedir())
				));
		bar.add(menu("File",
				menuItem("File Info", e -> buttonPanel.info()),
				menuItem("Open Parent Dir", e -> buttonPanel.opendir()),
				menuItem("Rename File", e -> buttonPanel.rename()),
				menuItem("Rename Files", e -> buttonPanel.massrename()),
				menuItem("Convert Files", e -> buttonPanel.convert()),
				menuItem("Delete File", e -> buttonPanel.delete())
				));
		bar.add(modemenu);
		bar.add(enginemenu);
		bar.add(menu("Options", 
				menuItem("AFPlay...", e -> afopt()),
				menuItem("FFmpeg...", e -> ffmpegopt())
				));
				
		return bar;
	}
	
	private void engine (String e) {
		log.println("engine " + e);
		switch (e) {
			case Util.EFFMPEG: buttonPanel.setEngine(new FFEngine()); break;
			case Util.EAFPLAY: buttonPanel.setEngine(new AFEngine()); break;
			default:
		}
	}

	private void mode (String m) {
		log.println("mode " + m);
		buttonPanel.setMode(m);
	}

	private void ffmpegopt () {
		FFDialog d = new FFDialog();
		d.setLocationRelativeTo(this);
		d.setVisible(true);
	}
	
	private void afopt () {
		AFDialog d = new AFDialog();
		d.setLocationRelativeTo(this);
		d.setVisible(true);
	}
	
	public ButtonPanel getButtonPanel () {
		return buttonPanel;
	}
	
	private void loadPrefs (Preferences p) {
		
		{
			Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
			int x = Math.min(p.getInt("xwin", 50), s.width - 640);
			int y = Math.min(p.getInt("ywin", 50), s.height - 480);
			setLocation(new Point(x, y));
		}
		
		radioSet(modemenu, p.get("pfmode", Util.MSEQ));
		radioSet(enginemenu, p.get("pfengine", Util.EAFPLAY));
		
		buttonPanel.loadPrefs(p);
	}
	
	private void savePrefs (Preferences p) {
		log.println("save prefs");
		Point p1 = getLocation();
		p.putInt("xwin", p1.x);
		p.putInt("ywin", p1.y);
		
		p.put("pfmode", radioGet(modemenu));
		p.put("pfengine", radioGet(enginemenu));
		
		buttonPanel.saveprefs(p);
	}
	
	private class WA extends WindowAdapter {
		@Override
		public void windowClosing (WindowEvent ev) {
			buttonPanel.stop();
			savePrefs();
			config.save();
		}
	}
	
	public static void showErrorDialog (String title, Exception e) {
		JOptionPane.showMessageDialog(frame, e.toString(), title, JOptionPane.ERROR_MESSAGE);
	}
}
