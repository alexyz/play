package as.play;

import java.io.*;
import java.util.concurrent.Future;

import javax.sound.sampled.*;
import javax.swing.SwingUtilities;

public class FFPlayer extends Player {
	
	private static final Log log = new Log(FFPlayer.class);
	
	private final FFOptions opts = new FFOptions();
	private volatile Process process;
	private volatile boolean running;
	
	@Override
	public void play (File f) {
		log.println("play " + f.getName());
		try {
			stop();
			synchronized (this) {
				opts.load();
				String codec = opts.fmt.equals("16") ? "pcm_s16be" : "pcm_s8";
				String format = opts.fmt.equals("16") ? "s16be" : "s8";
				String[] a = { opts.exe, 
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
				running = true;
				Util.EX.execute(() -> readAsync(process));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void readAsync (Process p) {
		try {
			log.println("read " + opts);
			int sr = Integer.parseInt(opts.inrate);
			int ss = Integer.parseInt(opts.fmt);
			int ch = Integer.parseInt(opts.chan);
			int bufs = sr*(ss/8)*ch;
			log.println("sr=" + sr + " ss=" + ss + " ch=" + ch + " bufsize=" + bufs);
			
			Future<String> errf = Util.EX.submit(() -> Util.readLines(p.getErrorStream()));
			
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
					while (running && (i = is.read(a)) >= 0) {
						sdl.write(a, 0, i);
						t += i;
					}
				}
				sdl.stop();
				
			}
			
			
			int ex = p.waitFor();
			String err = errf.get();
			log.println("ffmpeg exited " + ex + " after " + t + " bytes");
			if (ex == 0) {
				SwingUtilities.invokeLater( () -> fire(DONE));
			} else {
				log.println("err: " + err);
			}
			
			synchronized (this) {
				process = null;
				log.println("read async - notify");
				notifyAll();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void pause () {
		log.println("ffplayer can't pause");
	}
	
	@Override
	public void stop () {
		log.println("stop");
		synchronized (this) {
			if (process != null && process.isAlive()) {
				running = false;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					log.println("stop - could not sleep: " + e);
				}
				process.destroy();
				while (process != null) {
					try {
						wait();
					} catch (InterruptedException e) {
						log.println("stop - could not wait: " + e);
					}
				}
			}
		}
	}
	
}
