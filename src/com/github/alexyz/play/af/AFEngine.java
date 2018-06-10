package com.github.alexyz.play.af;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

import com.github.alexyz.play.*;

public class AFEngine extends Engine {
	
	private static final Log log = new Log(AFEngine.class);
	/** 1=chans 2=freq 3=format */
	private static final Pattern datapat = Pattern.compile("^Data format: (\\d+ ch), (\\d+) Hz, '(.*?)'");
	/** 1=seconds */
	private static final Pattern durationpat = Pattern.compile("^estimated duration: ([\\d.]+) sec");
	/** 1=bps */
	private static final Pattern ratepat = Pattern.compile("^bit rate: (\\d+) bits per second");
	
	private volatile Process process;
	
	@Override
	public Info info (File f) {
		String name = f.getName();
		log.println("info " + name);
		Info info = new Info();
		try {
			ProcessBuilder pb = new ProcessBuilder("afinfo", f.getAbsolutePath());
			pb.redirectErrorStream(true);
			Process p2 = pb.start();
			List<String> lines = Util.readLines(p2.getInputStream(), StandardCharsets.UTF_8);
			for (String line : lines) {
				line = Util.normalise(line);
				Matcher mat;
				if ((mat = datapat.matcher(line)).find()) {
					info.ch = mat.group(1);
					info.freq = Float.parseFloat(mat.group(2));
					info.type = mat.group(3).trim().toUpperCase();
				} if ((mat = durationpat.matcher(line)).find()) {
					info.duration = Float.parseFloat(mat.group(1));
				} if ((mat = ratepat.matcher(line)).find()) {
					info.rate = Float.parseFloat(mat.group(1))/1000;
				} else {
					//log.println("[" + line + "]");
				}
			}
			info.out = String.join("\n", lines);
		} catch (Exception e) {
			info.out = e.toString();
		}
		log.println("info=" + info);
		return info;
	}
	
	@Override
	public synchronized void play (File f) {
		log.println("play " + f.getName());
		if (process != null) {
			throw new RuntimeException("already playing");
		}
		try {
			// want to say - it's now playing
			AFOptions opt = new AFOptions();
			opt.load(PlayFrame.PREFS);
			String[] a = { "afplay", 
					"-v", Float.toString(opt.volume), 
					"-r", Float.toString(opt.rate), 
					f.getAbsolutePath()
			};
			ProcessBuilder pb = new ProcessBuilder(a);
			process = pb.start();
			firestarted(f);
			Util.EX.execute(() -> waitforprocess(process));
		} catch (Exception e) {
			throw new RuntimeException("could not start", e);
		}
	}
	
	@Override
	public void pause () {
		log.println("pause - ignored");
	}
	
	@Override
	public synchronized void stop () {
		log.println("stop");
		while (process != null) {
			process.destroy();
			try {
				wait();
			} catch (InterruptedException e) {
				log.println("waitforstop: " + e);
			}
		}
	}
	
	private void waitforprocess (Process p) {
		try {
			log.println("waitforprocess " + Util.pid(p));
			int ex = p.waitFor();
			log.println("waitforprocess - exited " + ex);
			synchronized (this) {
				process = null;
				notifyAll();
			}
			fireexit(ex);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String convertExt () {
		return "m4a";
	}
	
	@Override
	public File merge (List<File> srcs) {
		log.println("merge " + srcs.size());
		
		List<File> dests = new ArrayList<>();
		
		try {
			for (File src : srcs) {
				File dest = File.createTempFile("alexyz", null);
				dests.add(dest);
				ProcessBuilder pb = new ProcessBuilder("afconvert", "-f", "AIFF", "-d", "BEI16", src.getAbsolutePath(), dest.getAbsolutePath());
				pb.redirectErrorStream(true);
				Process p2 = pb.start();
				String out = Util.read(p2.getInputStream(), StandardCharsets.UTF_8);
				int ex = p2.waitFor();
				if (ex != 0) {
					throw new Exception("could not convert " + src + ": " + out);
				}
			}
			
			return Util.concat(dests);
			
		} catch (Exception e) {
			throw new RuntimeException("could not merge", e);
			
		} finally {
			for (File dest : dests) {
				dest.delete();
			}
		}
	}
	
	@Override
	public void convert (File src, File dest) {
		log.println("convert " + dest);
		// afconvert -f m4af -d aac -b 128000 brown.aiff x.m4a
		// afconvert -f AIFF -d BEI16 brown.aiff x.aiff
		// afconvert -f WAVE -d LEI16 brown.aiff x.wav
		String out = null;
		try {
			AFOptions opt = new AFOptions();
			opt.load(PlayFrame.PREFS);
			ProcessBuilder pb = new ProcessBuilder("afconvert", "-f", "m4af", "-d", "aac", "-b", "" + opt.convertrate, src.getAbsolutePath(), dest.getAbsolutePath());
			pb.redirectErrorStream(true);
			Process p2 = pb.start();
			out = Util.read(p2.getInputStream(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("could not convert: " + out, e);
		}
	}
	
}
