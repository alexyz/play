package com.github.alexyz.play;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {
	@Override
	public int compare (File f1, File f2) {
		return f1.getAbsolutePath().toLowerCase().compareTo(f1.getAbsolutePath().toLowerCase());
	}
}