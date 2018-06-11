package com.github.alexyz.play;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.github.alexyz.play.af.AFEngine;

import static com.github.alexyz.play.SwingUtil.*;

public class ConvertDialog extends JDialog {
	
	public static void main (String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		ConvertDialog d = new ConvertDialog();
		d.setEngine(new AFEngine());
		File dir = new File("/Users/alex/Dropbox/Music/Incoming/65 days - Wild Light");
		d.setFiles(dir, Arrays.asList(dir.listFiles()));
		border(d.getContentPane());
		d.setModalityType(ModalityType.APPLICATION_MODAL);
		d.setVisible(true);
		System.exit(0);
	}
	
	private final JTextArea area = new JTextArea();
	private final JCheckBox mergeBox = new JCheckBox("Merge");
	private final JScrollPane areaScroller = new JScrollPane(area);
	private Engine engine;
	private File dir;
	
	public ConvertDialog () {
		setTitle(getClass().getSimpleName());
		
		areaScroller.setBorder(new TitledBorder("Input Files"));
		
		JPanel butpanel = xpanel(button("Convert", e -> convert()), button("Close", e -> close()));
		
		int y = 0;
		JPanel toppanel = new JPanel(new GridBagLayout());
		toppanel.add(areaScroller, gbc().pos(1,y++).size(2,1).weightBoth().fillBoth().insets(5));
		toppanel.add(mergeBox, gbc().pos(2, y++).anchorWest().insets(5));
		toppanel.add(butpanel, gbc().pos(1, y++).size(2, 1).insets(5));
		
		setPreferredSize(new Dimension(480,320));
		setContentPane(toppanel);
		pack();
	}
	
	public void setEngine (Engine p) {
		this.engine = p;
	}
	
	private void close () {
		setVisible(false);
	}
	
	private List<File> files () {
		List<File> files = new ArrayList<>();
		for (String l : area.getText().split("\n")) {
			if ((l = l.trim()).length() > 0) {
				files.add(new File(dir, l));
			}
		}
		return files;
	}
	
	private void convert () {
		try {
			List<File> files = files();
			
			if (files.size() == 0) {
				JOptionPane.showMessageDialog(this, "No input files", "Convert", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			for (File f : files) {
				if (!f.isFile()) {
					JOptionPane.showMessageDialog(this, "Could not find input file " + f.getName(), "Convert", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			if (mergeBox.isSelected() && files.size() > 0) {
				convertmerge(files);
			} else {
				convertmany(files);
			}
			
		} catch (Exception e) {
			PlayFrame.showErrorDialog("Convert", e);
		}
	}

	private void convertmany (List<File> files) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File destdir = chooser.getSelectedFile();
			for (File srcfile : files) {
				File destfile = new File(destdir, srcfile.getName() + ".converted." + engine.convertExt());
				engine.convert(srcfile, destfile);
			}
		}
	}

	private void convertmerge (List<File> files) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FF(engine.convertExt()));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File destfile = chooser.getSelectedFile();
			File srctempfile = engine.merge(files);
			engine.convert(srctempfile, destfile);
			srctempfile.delete();
		}
	}
	
	public void setFiles (File dir, List<File> files) {
		this.dir = dir;
		StringBuilder sb = new StringBuilder();
		String dirpath = dir.getAbsolutePath();
		for (File f : files) {
			sb.append(f.getAbsolutePath().substring(dirpath.length() + 1)).append("\n");
		}
		area.setText(sb.toString());
		area.setCaretPosition(0);
	}
	
}
