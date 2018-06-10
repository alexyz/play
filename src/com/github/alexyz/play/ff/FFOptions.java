package com.github.alexyz.play.ff;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class FFOptions {
	public String outrate, chan, fmt, inrate, exe;
	public float gain;
	public void load (Preferences p) {
		inrate = p.get("ffinrate", "44100");
		outrate = p.get("ffoutrate", "44100");
		chan = p.get("ffchan", "2");
		fmt = p.get("ffformat", "16");
		exe = p.get("ffexe", "/usr/local/bin/ffmpeg");
		gain = p.getFloat("ffgain", 0);
	}
	public void save (Preferences p) {
		p.put("ffinrate", inrate);
		p.put("ffoutrate", outrate);
		p.put("ffchan", chan);
		p.put("ffformat", fmt);
		p.put("ffexe", exe);
		p.putFloat("ffgain", gain);
	}
	@Override
	public String toString () {
		return "FFOptions[outrate=" + outrate + " chan=" + chan + " fmt=" + fmt + " inrate=" + inrate + " exe=" + exe + " gain=" + gain + "]";
	}
	
}