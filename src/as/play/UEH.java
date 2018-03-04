package as.play;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

class UEH implements Thread.UncaughtExceptionHandler {
	@Override
	public void uncaughtException (Thread t, Throwable e) {
		System.out.println(t);
		e.printStackTrace(System.out);
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PlayFrame.frame, e.toString()));
	}
}