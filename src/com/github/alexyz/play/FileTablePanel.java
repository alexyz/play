package com.github.alexyz.play;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

import javax.swing.*;

public class FileTablePanel extends JPanel {
	
	private static final Log log = new Log(FileTablePanel.class);
	
	private final FileTable fileTable = new FileTable();
	
	private ButtonPanel parent;
	
	public FileTablePanel (ButtonPanel parent) {
		super(new BorderLayout());
		this.parent = parent;
		
		fileTable.getColumnModel().getColumn(1).setMaxWidth(30);
		fileTable.getColumnModel().getColumn(2).setMaxWidth(40);
		fileTable.addMouseListener(new FPMA());
		
		JScrollPane fileScroller = new JScrollPane(fileTable);
		
		add(fileScroller, BorderLayout.CENTER);
		
	}
	
	public File next (String mode) {
		log.println("next " + mode);
		int i = fileTable.getSelectionModel().getMinSelectionIndex();
		log.println("next current=" + i);
		int max = fileTable.getRowCount();
		// XXX only if selection not changed
		switch (mode) {
			case Util.MSEQ: i = i + 1; break;
			case Util.MREP: break;
			case Util.MREPALL: i = (i + 1) % max; break;
			case Util.MSINGLE: i = -1; break;
			case Util.MSHUF: i = Util.RAND.nextInt(max); break;
			default: throw new RuntimeException("invalid mode " + mode);
		}
		if (i >= 0 && i < max) {
			log.println("next i=" + i);
			fileTable.getSelectionModel().setSelectionInterval(i, i);
			return fileTable.getFileTableModel().getFile(i);
		} else {
			return null;
		}
	}

	public File changedir () {
		JFileChooser fc = new JFileChooser();
		File f = fileTable.getFileTableModel().getBaseDir();
		if (f != null && f.isDirectory()) {
			fc.setCurrentDirectory(f);
		}
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File dir = fc.getSelectedFile();
			setDir(dir);
			PlayFrame.savePrefs();
			return dir;
		} else {
			return null;
		}
	}
	
	public File getDir () {
		return fileTable.getFileTableModel().getBaseDir();
	}
	
	public void setDir (File dir) {
		fileTable.getFileTableModel().setBaseDir(dir);
		fileTable.getFileTableModel().setFiles(Collections.emptyList());
//		dirLabel.setText("");
		if (dir != null && dir.isDirectory()) {
//			dirLabel.setText(dir.getAbsolutePath());
			Util.EX.execute(() -> {
				List<File> l = Util.dir3(dir, new ArrayList<>());
				SwingUtilities.invokeLater(() -> fileTable.getFileTableModel().setFiles(l));
			});
		}
	}

	public File getSelectedFile () {
		List<File> list = getSelectedFiles();
		return list.size() == 1 ? list.get(0) : null;
	}
	
	public List<File> getSelectedFiles () {
		int i = fileTable.getSelectionModel().getMinSelectionIndex();
		int j = fileTable.getSelectionModel().getMaxSelectionIndex();
		List<File> list = fileTable.getFileTableModel().getFiles(i, j);
		return list;
	}
	
	private class FPMA extends MouseAdapter {
		@Override
		public void mousePressed (MouseEvent e) {
			if (!e.isPopupTrigger() && e.getClickCount() >= 2) {
				log.println("file table mouse pressed click count " + e.getClickCount());
				//parent.stop();
				File f = fileTable.getFileTableModel().getFile(fileTable.getSelectedRow());
				parent.playspecific(f);
				//parent.playnext();
			}
		}
	}

	public void rename () {
		File f1 = getSelectedFile();
		if (f1 != null) {
			log.println("rename " + f1);
			String name = (String) JOptionPane.showInputDialog(this, "Rename " + f1.getName(), "Rename", JOptionPane.QUESTION_MESSAGE, null, null, f1.getName());
			if (name != null && name.length() > 0 && !name.equals(f1.getName())) {
				File f2 = new File(f1.getParentFile(), name);
				if (!rename(f1, f2)) {
					JOptionPane.showMessageDialog(this, "Could not rename", "Rename", JOptionPane.ERROR_MESSAGE);
				}
				PlayFrame.config.save();
			}
		}
	}

	public void massrename () {
		List<File> list = getSelectedFiles();
		if (list.size() > 0) {
			RenameDialog d = new RenameDialog();
			d.setFiles(list);
			d.setOnOk(() -> {
				for (Map.Entry<File,File> e : d.getMap().entrySet()) {
					File f1 = e.getKey();
					File f2 = e.getValue();
					log.println("rename " + f1 + " to " + f2);
					if (!rename(f1, f2)) {
						JOptionPane.showMessageDialog(this, "Could not rename " + f1 + " to " + f2, "Rename", JOptionPane.ERROR_MESSAGE);
					}
				}
				PlayFrame.config.save();
			});
			d.setLocationRelativeTo(this);
			d.setVisible(true);
		}
	}

	private boolean rename (File f1, File f2) {
		if (f1.renameTo(f2)) {
			// update model
			fileTable.getFileTableModel().rename(f1, f2);
			PlayFrame.config.rename(f1, f2);
			return true;
		} else {
			return false;
		}
	}

	/** prompt to delete selected files */
	public void delete () {
		List<File> list = getSelectedFiles();
		log.println("delete " + list.size());
		String msg = list.size() == 1 ? list.get(0).getName() : list.size() + " files";
		if (list.size() > 0 && JOptionPane.showConfirmDialog(this, "Really delete " + msg + "?", "Delete", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			for (File f : list) {
				// if f playing, stop
				if (f.equals(parent.currentFile())) {
					parent.stop();
				}
				if (f.delete()) {
					PlayFrame.config.delete(f);
					// update model
					fileTable.getFileTableModel().update();
				} else {
					log.println("could not delete " + f);
				}
			}
			PlayFrame.config.save();
		}
	}

	public void convert () {
		List<File> list = getSelectedFiles();
		if (list.size() > 0) {
			ConvertDialog d = new ConvertDialog();
			d.setFiles(getDir(), list);
			d.setEngine(parent.getEngine());
			d.setLocationRelativeTo(this);
			d.setVisible(true);
		}
	}

}
