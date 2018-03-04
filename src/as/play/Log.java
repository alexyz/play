package as.play;

public class Log {
	private final Class<?> cl;
	public Log (Class<?> cl) {
		this.cl = cl;
	}
	public void println(String msg) {
		String c = cl.getSimpleName();
		String t = Thread.currentThread().getName();
		System.out.println(String.format("[%s] (%s) %s", c, t, msg)); 
	}
}
