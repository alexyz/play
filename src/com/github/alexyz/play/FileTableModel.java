package com.github.alexyz.play;

import java.io.File;
import java.util.*;

import javax.swing.table.AbstractTableModel;

import com.github.alexyz.play.cfg.XFile;

public class FileTableModel extends AbstractTableModel {
	
	private final List<File> files = new ArrayList<>();
	private File baseDir;
	
	public File getBaseDir () {
		return baseDir;
	}
	
	public void setBaseDir (File baseDir) {
		this.baseDir = baseDir;
	}
	
	@Override
	public int getRowCount () {
		return files.size();
	}
	
	@Override
	public int getColumnCount () {
		return 3;
	}
	
	@Override
	public String getColumnName (int c) {
		switch (c) {
			case 0: return "Name";
			case 1: return "Count";
			case 2: return "Size";
			default: throw new RuntimeException();
		}
	}
	
	public File getFile (int r) {
		return files.get(r);
	}
	
	public List<File> getFiles (int r1, int r2) {
		return new ArrayList<>(files.subList(r1, r2 + 1));
	}
	
	@Override
	public Object getValueAt (int r, int c) {
		File f = getFile(r);
		switch (c) {
			case 0:
				return f.getName();
			case 1: {
				XFile xf = PlayFrame.config.getFile(f, false);
				return xf != null ? xf.count : null;
			}
			case 2:
				return Util.formatSize(f.length());
			default:
				throw new RuntimeException();
		}
	}

	public void setFiles (List<File> files) {
		this.files.clear();
		this.files.addAll(files);
		Collections.sort(this.files, new FileComparator());
		fireTableDataChanged();
	}

	public void rename (File f1, File f2) {
		int i = files.indexOf(f1);
		if (i >= 0) {
			files.set(i, f2);
			fireTableDataChanged();
		}
	}
	
	/** remove files no longer existing */
	public void update () {
		Iterator<File> i = files.iterator();
		boolean changed = false;
		while (i.hasNext()) {
			if (!i.next().exists()) {
				i.remove();
				changed = true;
			}
		}
		if (changed) {
			fireTableDataChanged();
		}
	}
	
}