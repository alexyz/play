package as.play;

import java.io.File;

import javax.swing.filechooser.FileFilter;

final class FF extends FileFilter {
	private final String ext;
	
	FF (String ext) {
		this.ext = ext;
	}
	
	@Override
	public String getDescription () {
		return ext;
	}
	
	@Override
	public boolean accept (File f) {
		return f.getName().toLowerCase().endsWith("." + ext);
	}
}