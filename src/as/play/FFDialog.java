package as.play;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import static as.play.SwingUtil.*;

public class FFDialog extends JDialog {
	
	private static final String[] rates = new String[] { "44100", "22050", "11025", "48000", "32000", "16000", "8000" };
	private static final String[] chans = new String[] { "2", "1" };
	private static final String[] formats = new String[] { "16", "8" };
	
	private final JComboBox<String> inrateCombo = new JComboBox<>(rates);
	private final JComboBox<String> outrateCombo = new JComboBox<>(rates);
	private final JComboBox<String> chanCombo = new JComboBox<>(chans);
	private final JComboBox<String> fmtCombo = new JComboBox<>(formats);
	private final JTextField exeField = new JTextField(20);
	private final JSpinner gainSpinner = new JSpinner(new SpinnerNumberModel(0, -80, 6, 1));
	
	public FFDialog () {
		setTitle(getClass().getName());
		int y = 0;
		setContentPane(new JPanel(new GridBagLayout()));
		addgc("FFmpeg Location", flowPanel(exeField, button("...", e -> file())), y++);
		addgc("Output Rate", outrateCombo, y++);
		addgc("Input Rate", inrateCombo, y++);
		addgc("Channels", chanCombo, y++);
		addgc("Format", fmtCombo, y++);
		addgc("Gain", gainSpinner, y++);
		add(flowPanel(button("OK", e -> ok()), button("Cancel", e -> cancel())), gcwh(0, y++, 2, 1));
		load();
		pack();
	}
	
	private void cancel () {
		setVisible(false);
	}

	private void ok () {
		save();
		setVisible(false);
	}

	private void load () {
		FFOptions opts = new FFOptions();
		opts.load();
		exeField.setText(opts.exe);
		inrateCombo.setSelectedItem(opts.inrate);
		outrateCombo.setSelectedItem(opts.outrate);
		chanCombo.setSelectedItem(opts.chan);
		fmtCombo.setSelectedItem(opts.fmt);
		gainSpinner.setValue(new Float(opts.gain));
	}
	
	private void save () {
		FFOptions opts = new FFOptions();
		opts.exe = exeField.getText();
		opts.inrate = (String) inrateCombo.getSelectedItem();
		opts.outrate = (String) outrateCombo.getSelectedItem();
		opts.chan = (String) chanCombo.getSelectedItem();
		opts.fmt = (String) fmtCombo.getSelectedItem();
		opts.gain = ((Number) gainSpinner.getValue()).floatValue();
		opts.save();
	}
	
	private void file () {
		JFileChooser fc = new JFileChooser();
		File f = new File(exeField.getText());
		if (f.isFile()) {
			fc.setSelectedFile(f);
		}
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			exeField.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}
	
	public void addgc (String label, Component c, int y) {
		add(new JLabel(label), gca(0, y, GridBagConstraints.EAST));
		add(c, gca(1, y, GridBagConstraints.WEST));
	}
	
}
