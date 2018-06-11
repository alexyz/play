package com.github.alexyz.play.af;

import java.util.prefs.Preferences;

public class AFOptions {
	private static float round (float f) {
		return Math.round(f*100)/100;
	}
	public float volume, rate;
	public int convertrate;
	public void load (Preferences p) {
		rate = round(p.getFloat("afrate", 1));
		volume = round(p.getFloat("afvol", 1));
		convertrate = p.getInt("afcrate", 16);
	}
	public void save (Preferences p) {
		p.putFloat("afrate", round(rate));
		p.putFloat("afvol", round(volume));
		p.putInt("afcrate", convertrate);
	}
	@Override
	public String toString () {
		return "AFOptions[rate=" + rate + " volume=" + volume + "]";
	}
	
}