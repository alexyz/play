package com.github.alexyz.play.af;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.github.alexyz.play.*;

import static com.github.alexyz.play.SwingUtil.*;

public class AFDialog extends JDialog {
	
	public static void main (String[] args) {
		AFDialog d = new AFDialog();
		border(d.getContentPane());
		d.setModalityType(ModalityType.APPLICATION_MODAL);
		d.setVisible(true);
		System.exit(0);
	}
	
	private final JSpinner volSpinner = new JSpinner(new SpinnerNumberModel(1, 0.1, 2, 0.1));
	private final JSpinner rateSpinner = new JSpinner(new SpinnerNumberModel(1, 0.1, 2, 0.1));
	private final JSpinner convertRateSpinner = new JSpinner(new SpinnerNumberModel(128, 16, 256, 16));
	
	public AFDialog () {
		setTitle(getClass().getSimpleName());
		setContentPane(new JPanel(new GridBagLayout()));

		JPanel butPanel = xpanel(button("OK", e -> ok()), button("Cancel", e -> cancel()));
		
		add(label("Volume (1 = normal)"), gbc().pos(1,1).anchorEast().insets(5));
		add(volSpinner, gbc().pos(2,1).anchorWest().insets(5));
		add(label("Rate (1 = normal)"), gbc().pos(1,2).anchorEast().insets(5));
		add(rateSpinner, gbc().pos(2,2).anchorWest().insets(5));
		add(label("Convert Bitrate"), gbc().pos(1,3).anchorEast().insets(5));
		add(convertRateSpinner, gbc().pos(2,3).anchorWest().insets(5));
		add(butPanel, gbc().pos(1,4).size(2,1).insets(5));
		
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
		AFOptions opts = new AFOptions();
		opts.load(PlayFrame.PREFS);
		rateSpinner.setValue(Double.valueOf(opts.rate));
		volSpinner.setValue(Double.valueOf(opts.volume));
		convertRateSpinner.setValue(Double.valueOf(opts.convertrate));
	}
	
	private void save () {
		AFOptions opts = new AFOptions();
		opts.rate = ((Number)rateSpinner.getValue()).floatValue();
		opts.volume = ((Number)volSpinner.getValue()).floatValue();
		opts.convertrate = ((Number)convertRateSpinner.getValue()).intValue();
		opts.save(PlayFrame.PREFS);
	}
	
}
