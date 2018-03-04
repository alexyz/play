package as.play;

import java.io.*;
import java.lang.reflect.Field;

public class AFPlayer extends Player {
	
	private static final Log log = new Log(AFPlayer.class);
	
	private volatile Process process;
	private volatile boolean paused;
	
	@Override
	public void play (File f) {
		Process p = process;
		log.println("play created=" + (p!=null) + " alive=" + (p!=null&&p.isAlive()));
		if (p != null && p.isAlive() && paused) {
			cont();
		} else {
			stop();
			start(f);
		}
	}
	
	@Override
	public void pause () {
		log.println("pause");
		if (signal("STOP")) {
			paused = true;
		}
	}
	
	public void cont () {
		log.println("continue");
		if (signal("CONT")) {
			paused = false;
		}
	}
	
	@Override
	public void stop () {
		log.println("stop");
		synchronized (this) {
			stopSync();
		}
	}

	private void stopSync () {
		if (process != null && process.isAlive()) {
			log.println("stop - destroy");
			process.destroy();
			while (process != null) {
				try {
					log.println("stop - wait for notify");
					wait(1000);
				} catch (InterruptedException e) {
					log.println("stop - could not wait for stop: " + e);
				}
			}
			log.println("stop - stopped");
		}
	}
	
	private void start (File f) {
		try {
			log.println("start " + f.getName());
			// want to say - it's now playing
			synchronized (this) {
				startSync(f);
			}
		} catch (Exception e) {
			throw new RuntimeException("could not start", e);
		}
	}

	private void startSync (File f) throws IOException {
		if (process != null) {
			log.println("start - already started");
			return;
		}
		String[] a = { "afplay", "-v", Float.toString(vol), f.getAbsolutePath() };
		ProcessBuilder pb = new ProcessBuilder(a);
		Process p = pb.start();
		process = p;
		paused = false;
		Util.EX.execute(() -> waitfor(p));
	}
	
	private void waitfor (Process p) {
		try {
			log.println("waitfor");
			int ex = p.waitFor();
			log.println("waitfor - exited " + ex);
			if (ex == 0) {
				fire(DONE);
			}
			// want to say - it's stopped
			synchronized (this) {
				process = null;
				paused = false;
				log.println("waitfor - notify all");
				notifyAll();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean signal (String code) {
		log.println("signal " + code);
		try {
			Process p = process;
			if (p != null && p.isAlive()) {
				int pid = pid(p);
				ProcessBuilder pb = new ProcessBuilder("kill", "-" + code, "" + pid);
				pb.redirectErrorStream(true);
				Process p2 = pb.start();
				String err = Util.readLines(p2.getInputStream());
				int ex = p2.waitFor();
				log.println("signal - kill exited " + ex + " - " + err);
				return ex == 0;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new RuntimeException("could not kill", e);
		}
	}
	
	private static int pid (Process p) {
		try {
			Field f = p.getClass().getDeclaredField("pid");
			f.setAccessible(true);
			return ((Integer)f.get(p)).intValue();
		} catch (Exception e) {
			throw new RuntimeException("could not get pid", e);
		}
	}
	
}
