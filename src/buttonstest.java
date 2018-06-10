

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import com.github.alexyz.play.SwingUtil;

public class buttonstest extends JPanel {
	
	public static void main (String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new buttonstest());
		f.setPreferredSize(new Dimension(640, 240));
		f.pack();
		f.setVisible(true);
	}
	
	public buttonstest () {
		setLayout(new GridBagLayout());
		add(SwingUtil.webdingsButton(SwingUtil.WD_LEFT3, e -> {}));
		add(SwingUtil.webdingsButton(SwingUtil.WD_RIGHT3, e -> {}));
		add(SwingUtil.webdingsButton(SwingUtil.WD_RIGHT, e -> {}));
		add(SwingUtil.webdingsButton(SwingUtil.WD_PAUSE, e -> {}));
		add(SwingUtil.webdingsButton(SwingUtil.WD_STOP, e -> {}));
	}
	
}