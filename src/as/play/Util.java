package as.play;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

public final class Util {
	
	public static final Random RAND = new Random();
	public static final ExecutorService EX = Executors.newCachedThreadPool();
	public static final String MSINGLE = "Single";
	public static final String MSEQ = "Sequential";
	public static final String MSHUF = "Shuffle";
	public static final String MREP = "Repeat";
	public static final String MREPALL = "RepeatAll";
	public static final String EFFMPEG = "FFmpeg";
	public static final String EAFPLAY = "AFPlay";
	public static final List<String> MODES = Arrays.asList(MSINGLE,MSEQ,MSHUF,MREP,MREPALL);
	
	private static final String[] types = new String[] { "mp3", "mp4", "m4a", "aac", "wav", "aiff", "alac", "ogg" };
	
	public static List<File> dir3(File f, List<File> l) {
		if (f.isDirectory()) {
			for (File g : f.listFiles()) {
				dir3(g,l);
			}
		} else if (f.isFile()) {
			String n = f.getName().toLowerCase();
			for (String e : types) {
				if (n.endsWith(e)) {
					l.add(f);
				}
			}
		}
		return l;
	}

	public static String readLines (InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String l;
			while ((l = br.readLine()) != null) {
				sb.append(l).append("\n");
			}
		}
		return sb.toString();
	}

	public static Preferences prefs = Preferences.userNodeForPackage(Util.class);

	public static Integer count (File f) {
		int v = prefs.getInt(Integer.toHexString(f.getName().hashCode()), 0);
		return v > 0 ? Integer.valueOf(v) : null;
	}
	
	public static void increment (File f) {
		String h = Integer.toHexString(f.getName().hashCode());
		prefs.putInt(h, prefs.getInt(h, 0) + 1);
	}
	
	
	private Util () {
		//
	}
	
}
