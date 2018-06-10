package com.github.alexyz.play;

import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.Collections;
import java.util.List;

public abstract class Engine {
	
	public static final String start = "start", exit = "exit";
	
	private final PropertyChangeSupport props = new PropertyChangeSupport(this);
	
	public Engine () {
		//
	}
	
	public PropertyChangeSupport getProps () {
		return props;
	}
	
	protected void firestarted(File f) {
		props.firePropertyChange(start, null, f);
	}
	
	protected void fireexit(int v) {
		props.firePropertyChange(exit, null, Integer.valueOf(v));
	}
	
	public abstract Info info (File f);
	
	/** start - audio will be started after this */
	public abstract void play (File f);
	
	public void pause () {
		throw new RuntimeException("pause not implemented");
	}
	
	/** stop - audio will be completely stopped after this */
	public abstract void stop ();

	public String convertExt () {
		throw new RuntimeException("convert not implemented");
	}
	
	public File merge (List<File> srcs) {
		throw new RuntimeException("merge not implemented");
	}
	
	public void convert (File src, File dest) {
		throw new RuntimeException("convert not implemented");
	}
}
