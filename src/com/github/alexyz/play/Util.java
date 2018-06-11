package com.github.alexyz.play;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

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
	public static final String[] MODES = {
			MSINGLE, MSEQ, MSHUF, MREP, MREPALL
	};
	public static final String[] ENGINES = {
			EAFPLAY, EFFMPEG
	};
	
	private static final Pattern spacepat = Pattern.compile("\\s+");
	private static final String[] types = new String[] {
			"mp2", "mp3", "mp4", "m4a", "m4v", "aac", "ac3", "wav", "aiff", "alac", "flac", "ogg", "opus", "wma"
	};
	
	public static List<File> dir3 (File f, List<File> l) {
		if (f.isDirectory()) {
			for (File g : f.listFiles()) {
				dir3(g, l);
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
	
	public static List<String> readLines (InputStream is) throws IOException {
		return readLines(is, Charset.defaultCharset());
	}
	
	public static List<String> readLines (InputStream is, Charset cs) throws IOException {
		List<String> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, cs))) {
			String l;
			while ((l = br.readLine()) != null) {
				list.add(l);
			}
		}
		return list;
	}
	
	public static String normalise (String l) {
		return spacepat.matcher(l.trim()).replaceAll(" ");
	}
	
	public static String read (InputStream is) throws IOException {
		return read(is, Charset.defaultCharset());
	}
	
	/** read input stream into string, converting newlines to n */
	public static String read (InputStream is, Charset cs) throws IOException {
		return String.join("\n", readLines(is, cs));
	}
	
//	public static void write0 (OutputStream os, Charset cs, String... args) {
//		try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, cs))) {
//			for (String arg : args) {
//				pw.print(arg + "\0");
//			}
//		}
//	}
	
	private Util () {
		//
	}
	
	public static String formatSize (long l) {
		return Math.round(l / 1_000_000.0) + "M";
	}
	
	public static String formatTime (double s_) {
		int s = (int) s_;
		int sf = s % 60;
		int mf = (s / 60) % 60;
		int hf = s / 3600;
		return (hf > 0 ? ft(hf) + ":" : "") + ft(mf) + ":" + ft(sf);
	}
	
	private static String ft (int v) {
		return String.format("%02d", v);
	}
	
	public static double nanoToS (long ns) {
		return ns / 1_000_000_000.0;
	}
	
	public static int pid (Process p) {
		try {
			Field f = p.getClass().getDeclaredField("pid");
			f.setAccessible(true);
			return ((Integer) f.get(p)).intValue();
		} catch (Exception e) {
			throw new RuntimeException("could not get pid", e);
		}
	}
	
	public static void print (Properties p) {
		for (String k : new TreeSet<>(p.stringPropertyNames())) {
			System.out.println(k + " = " + p.getProperty(k));
		}
	}
	
	/** concat audio files to temp file */
	public static File concat (List<File> srcs) {
		List<AudioInputStream> clips = new ArrayList<>();
		
		try {
			long len = 0;
			AudioFileFormat format = AudioSystem.getAudioFileFormat(srcs.get(0));
			
			for (File src : srcs) {
				AudioInputStream clip = AudioSystem.getAudioInputStream(src);
				len += clip.getFrameLength();
				clips.add(clip);
			}
			
			try (AudioInputStream is = new AudioInputStream(new SequenceInputStream(Collections.enumeration(clips)), clips.get(0).getFormat(), len)) {
				File dest = File.createTempFile("alexyz", null);
				dest.deleteOnExit();
				AudioSystem.write(is, format.getType(), dest);
				return dest;
			}
			
		} catch (Exception e) {
			throw new RuntimeException("could not concat", e);
			
		} finally {
			for (AudioInputStream clip : clips) {
				try {
					clip.close();
				} catch (IOException e) {
					System.out.println("could not close clip: " + e);
				}
			}
		}
	}
}
