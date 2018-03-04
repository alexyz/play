package as.play;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.function.Function;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.multi.MultiLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import static as.play.SwingUtil.*;

// TODO afinfo dialog, afplay rate/volume
// lose the toolbar/status bar?
// add fxplayer for the cross platform
// reverse sequential mode
// locate current file, history dialog, open in system, copy path
//set columns
//delete file
//set colour of file
//filter files
public class PlayFrame extends JFrame {
	
	public static PlayFrame frame;
	
	private static final Log log = new Log(PlayFrame.class);
	private static final double nsins = 1_000_000_000.0;
	
	public static void main (String[] args) {
		try {
			long t1 = System.nanoTime();
			Thread.setDefaultUncaughtExceptionHandler(new UEH());
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			frame = new PlayFrame();
			long t2 = System.nanoTime();
			log.println("construct time: " + (t2-t1)/nsins + " s");
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private final ButtonPanel buttonPanel = new ButtonPanel();
	private final Preferences p = Preferences.userNodeForPackage(getClass());
	
	public PlayFrame () {
		setTitle(getClass().getName());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(pad(buttonPanel));
		setPreferredSize(new Dimension(640, 480));
		addWindowListener(new WA());
		setJMenuBar(menuBar());
		pack();
		loadPrefs(p);
	}
	
	private JMenuBar menuBar () {
		JMenuBar bar = new JMenuBar();
		bar.add(menu("Tab",
				menuItem("Add", e -> buttonPanel.addtab()),
				menuItem("Rename", e -> buttonPanel.renametab()),
				menuItem("Close", e -> buttonPanel.closetab()),
				menuItem("Change Directory", e -> buttonPanel.changedir())
				));
		Function<String,JRadioButtonMenuItem> f = m -> radioItem(m, e -> buttonPanel.setMode(m));
		bar.add(radioMenu("Mode", Util.MODES.stream().map(f).toArray(i -> new JRadioButtonMenuItem[i])));
		bar.add(radioMenu("Engine", 
				radioItem(Util.EAFPLAY, e -> afplay()), 
				radioItem(Util.EFFMPEG, e -> ffmpeg())));
		bar.add(menu("Options", menuItem("FFmpeg...", e -> ffmpegopt())));
		return bar;
	}
	
	private void ffmpegopt () {
		FFDialog d = new FFDialog();
		d.setLocationRelativeTo(this);
		d.setVisible(true);
	}
	
	public ButtonPanel getButtonPanel () {
		return buttonPanel;
	}
	
	private void ffmpeg () {
		buttonPanel.setPlayer(new FFPlayer());
	}
	
	private void afplay () {
		buttonPanel.setPlayer(new AFPlayer());
	}
	
	private void loadPrefs (Preferences p) {
		
		{
			Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
			int x = Math.min(p.getInt("xwin", 50), s.width - 640);
			int y = Math.min(p.getInt("ywin", 50), s.height - 480);
			setLocation(new Point(x, y));
		}
		JMenuBar bar = getJMenuBar();
		for (int n = 0; n < bar.getMenuCount(); n++) {
			JMenu menu = bar.getMenu(n);
			switch (menu.getText()) {
				case "Mode": radioSet(menu, p.get("pfmode", Util.MSINGLE)); break;
				case "Engine": radioSet(menu, p.get("pfengine", Util.EAFPLAY)); break;
				default:
			}
		}
		buttonPanel.loadPrefs(p);
	}
	
	private void savePrefs (Preferences p) {
		Point p1 = getLocation();
		p.putInt("xwin", p1.x);
		p.putInt("ywin", p1.y);
		JMenuBar bar = getJMenuBar();
		for (int n = 0; n < bar.getMenuCount(); n++) {
			JMenu menu = bar.getMenu(n);
			switch (menu.getText()) {
				case "Mode": p.put("pfmode", radioGet(menu)); break;
				case "Engine": p.put("pfengine", radioGet(menu)); break;
				default:
			}
		}
		buttonPanel.saveprefs(p);
		try {
			p.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace(System.out);
		}
	}
	
	private class WA extends WindowAdapter {
		@Override
		public void windowClosing (WindowEvent ev) {
			try {
				buttonPanel.stop();
				savePrefs(p);
				p.flush();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(PlayFrame.this, e.toString());
			}
		}
	}
	
}
