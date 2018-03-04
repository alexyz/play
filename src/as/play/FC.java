package as.play;

import java.io.File;
import java.util.Comparator;

public class FC implements Comparator<File> {
	@Override
	public int compare (File f1, File f2) {
		return f1.getAbsolutePath().toLowerCase().compareTo(f1.getAbsolutePath().toLowerCase());
	}
}