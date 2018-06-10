

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.github.alexyz.play.PlayFrame;

class UEH implements Thread.UncaughtExceptionHandler {
	@Override
	public void uncaughtException (Thread t, Throwable e) {
		System.out.println(t);
		e.printStackTrace(System.out);
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PlayFrame.frame, e.toString()));
	}
}