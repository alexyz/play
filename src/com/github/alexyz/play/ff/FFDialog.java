package com.github.alexyz.play.ff;

import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.github.alexyz.play.GbcHelper;
import com.github.alexyz.play.PlayFrame;

import static com.github.alexyz.play.SwingUtil.*;

public class FFDialog extends JDialog {
	
	private static final String[] rates = new String[] {
			"48000", "44100", "32000", "22050", "16000", "11025", "8000"
	};
	private static final String[] chans = new String[] { "2", "1" };
	private static final String[] formats = new String[] { "16", "8" };
	
	public static void main (String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		FFDialog d = new FFDialog();
		border(d.getContentPane());
		d.setModalityType(ModalityType.APPLICATION_MODAL);
		d.setVisible(true);
		System.exit(0);
	}
	
	private final JComboBox<String> inrateCombo = new JComboBox<>(rates);
	private final JComboBox<String> outrateCombo = new JComboBox<>(rates);
	private final JComboBox<String> chanCombo = new JComboBox<>(chans);
	private final JComboBox<String> fmtCombo = new JComboBox<>(formats);
	private final JTextField exeField = new JTextField(30);
	private final JSpinner gainSpinner = new JSpinner(new SpinnerNumberModel(0, -80, 6, 1));
	
	public FFDialog () {
		setTitle(getClass().getSimpleName());
		setContentPane(new JPanel(new GridBagLayout()));
		
		JPanel exepanel = new JPanel(new GridBagLayout());
		exepanel.add(exeField, gbc().pos(1,1).weightX().fillHorizontal());
		exepanel.add(pad(10), gbc().pos(2,1));
		exepanel.add(button("...", e -> file()), gbc().pos(3,1));
		
		JPanel butPanel = xpanel(button("OK", e -> ok()), button("Cancel", e -> cancel()));
		
		add(label("FFmpeg Location"), gbc().pos(1,1).anchorEast().insets(5));
		add(exepanel, gbc().pos(2,1).anchorWest().fillHorizontal().weight(1,0).insets(5));
		
		add(label("Output Rate"), gbc().pos(1,2).anchorEast().insets(5));
		add(outrateCombo, gbc().pos(2,2).anchorWest().insets(5));
		
		add(label("Input Rate"), gbc().pos(1,3).anchorEast().insets(5));
		add(inrateCombo, gbc().pos(2,3).anchorWest().insets(5));
		
		add(label("Channels"), gbc().pos(1,4).anchorEast().insets(5));
		add(chanCombo, gbc().pos(2,4).anchorWest().insets(5));
		
		add(label("Format"), gbc().pos(1,5).anchorEast().insets(5));
		add(fmtCombo, gbc().pos(2,5).anchorWest().insets(5));
		
		add(label("Gain"), gbc().pos(1,6).anchorEast().insets(5));
		add(gainSpinner, gbc().pos(2,6).anchorWest().insets(5));
		
		add(butPanel, gbc().pos(1,7).size(2,1).insets(5));
		
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
		opts.load(PlayFrame.PREFS);
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
		opts.save(PlayFrame.PREFS);
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
	
}
