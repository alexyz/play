package as.play;

import java.beans.PropertyChangeSupport;
import java.io.*;

public abstract class Player {
	
	public static final String DONE = "done";
	
	protected float vol = 1;
	
	private final PropertyChangeSupport props = new PropertyChangeSupport(this);
	
	public Player () {
		//
	}
	
	public void setVol (float vol) {
		this.vol = vol;
	}
	
	public PropertyChangeSupport getProps () {
		return props;
	}
	
//	public File getFile() {
//		return file;
//	}
	
	protected void fire(String s) {
		props.firePropertyChange(s, null, true);
	}
	
	public abstract void play (File f);
	
	public abstract void pause ();
	
	public abstract void stop ();
	
}
