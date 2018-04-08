package as.play.ff;

import java.io.*;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.*;
import javax.swing.SwingUtilities;

import as.play.*;

public class FFEngine extends Engine {
	
	private static final Log log = new Log(FFEngine.class);
	/** 1=hours 2=mins 3=secs 4=start */
	// Duration: 00:05:56.34, bitrate: 192 kb/s
	// Duration: 00:06:53.66, start: 0.047889, bitrate: 304 kb/s
	// Duration: 00:00:30.00, start: 0.000000, bitrate: 1021 kb/s
	// Duration: 00:00:30.01, start: 0.000000, bitrate: 88 kb/s
	private static final Pattern durationpat = Pattern.compile("^Duration: (\\d+):(\\d+):([\\d.]+)");
	/** 1=format 2=freq 3=chans 4=kbps */
	// Stream #0:0: Audio: mp2, 44100 Hz, stereo, s16p, 384 kb/s
	// Stream #0:0(eng): Audio: aac (LC) (mp4a / 0x6134706D), 44100 Hz, stereo, fltp, 294 kb/s (default)
	// Stream #0:0: Audio: flac, 44100 Hz, stereo, s16
	// Stream #0:0: Audio: opus, 48000 Hz, stereo, fltp
	private static final Pattern streampat = Pattern.compile("^Stream #.*?: Audio: (.*?), (\\d+) Hz, (\\w+), \\w+, (\\d+) kb/s");
	private volatile Process process;
//	private volatile boolean read;
	
	@Override
	public Info info (File f) {
		log.println("info " + f);
		FFOptions opts = new FFOptions();
		opts.load(PlayFrame.PREFS);
		File exe = new File(new File(opts.exe).getParentFile(), "ffprobe");
		Info info = new Info();
		try {
			ProcessBuilder pb = new ProcessBuilder(exe.getAbsolutePath(), f.getAbsolutePath());
			pb.redirectErrorStream(true);
			Process p2 = pb.start();
			List<String> lines = Util.readLines(p2.getInputStream());
			for (String line : lines) {
				line = Util.normalise(line);
				log.println("[" + line + "]");
				Matcher mat;
				if ((mat = durationpat.matcher(line)).find()) {
					float h = Float.parseFloat(mat.group(1));
					float m = Float.parseFloat(mat.group(2));
					float s = Float.parseFloat(mat.group(3));
					info.duration = (((h*60)+m)*60)+s;
				} else if ((mat = streampat.matcher(line)).find()) {
					String t = mat.group(1).toUpperCase();
					int i = t.indexOf(" ");
					info.type = i > 0 ? t.substring(0, i) : t;
					info.freq = Integer.parseInt(mat.group(2));
					info.ch = mat.group(3);
					info.rate = Float.parseFloat(mat.group(4));
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
			FFOptions opts = new FFOptions();
			opts.load(PlayFrame.PREFS);
			String codec = opts.fmt.equals("16") ? "pcm_s16be" : "pcm_s8";
			String format = opts.fmt.equals("16") ? "s16be" : "s8";
			String[] a = { 
					opts.exe, 
					"-nostats",
					"-i", f.getAbsolutePath(), 
					"-acodec", codec, 
					"-f", format, 
					"-ar", opts.outrate, 
					"-ac", opts.chan,
					"-",
			};
			ProcessBuilder pb = new ProcessBuilder(a);
			process = pb.start();
			firestarted(f);
//			read = true;
			Util.EX.execute(() -> readAsync(process, opts));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void readAsync (Process p, FFOptions opts) {
		try {
			log.println("read " + opts);
			int sr = Integer.parseInt(opts.inrate);
			int ss = Integer.parseInt(opts.fmt);
			int ch = Integer.parseInt(opts.chan);
			int bufs = sr*(ss/8)*ch;
			log.println("sr=" + sr + " ss=" + ss + " ch=" + ch + " bufsize=" + bufs);
			
			Future<String> errf = Util.EX.submit(() -> Util.read(p.getErrorStream()));
			
			AudioFormat af = new AudioFormat(sr, ss, ch, true, true);
			DataLine.Info dli = new DataLine.Info(SourceDataLine.class, af);
			long t = 0;
			try (SourceDataLine sdl = (SourceDataLine) AudioSystem.getLine(dli)) {
				sdl.open(af, bufs);
				FloatControl gainctl = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
				gainctl.setValue(opts.gain);
				sdl.start();
				byte[] a = new byte[bufs];
				try (InputStream is = p.getInputStream()) {
					int i;
					while ((i = is.read(a)) >= 0) {
						sdl.write(a, 0, i);
						t += i;
					}
				}
				sdl.stop();
			} catch (Exception e) {
				log.println("could not read async: " + e);
			}
			
			int ex = p.waitFor();
			String err = errf.get();
			log.println("ffmpeg exited " + ex + " after " + t + " bytes");
			fireexit(ex);
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
			
		} finally {
			synchronized (this) {
				process = null;
				notifyAll();
			}
		}
	}
	
	@Override
	public void pause () {
		log.println("ffplayer can't pause");
		// TODO stop reading from stdin...
	}
	
	@Override
	public synchronized void stop () {
		log.println("stop");
		while (process != null) {
//			read = false;
			process.destroy();
			try {
				wait();
			} catch (InterruptedException e) {
				log.println("stop: " + e);
			}
		}
	}
	
}
