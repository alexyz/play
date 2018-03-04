package as.play;

import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {
	
	private final List<File> files = new ArrayList<>();
//	private final Preferences prefs = Preferences.userNodeForPackage(getClass());
	
	@Override
	public int getRowCount () {
		return files.size();
	}
	
	@Override
	public int getColumnCount () {
		return 2;
	}
	
	@Override
	public String getColumnName (int c) {
		switch (c) {
			case 0: return "Name";
			case 1: return "Count";
			default: throw new RuntimeException();
		}
	}
	
	public File getFile (int r) {
		return files.get(r);
	}
	
//	public void update (File f) {
//		int i = files.indexOf(f);
//		if (i >= 0) {
//			fireTableRowsUpdated(i, i);
//		}
//	}
	
	@Override
	public Object getValueAt (int r, int c) {
		File f = getFile(r);
		switch (c) {
			case 0: return f.getName();
			case 1: {
				return Util.count(f);
			}
			default: throw new RuntimeException();
		}
	}

	public void setFiles (List<File> files) {
		this.files.clear();
		this.files.addAll(files);
		Collections.sort(this.files, new FC());
		fireTableDataChanged();
	}
	
}