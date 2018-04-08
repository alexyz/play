package as.play.af;

import java.util.prefs.Preferences;

public class AFOptions {
	public float volume, rate;
	public int convertrate;
	public void load (Preferences p) {
		rate = p.getFloat("afrate", 1);
		volume = p.getFloat("afvol", 1);
		convertrate = p.getInt("afcrate", 16);
	}
	public void save (Preferences p) {
		p.putFloat("afrate", rate);
		p.putFloat("afvol", volume);
		p.putInt("afcrate", convertrate);
	}
	@Override
	public String toString () {
		return "AFOptions[rate=" + rate + " volume=" + volume + "]";
	}
	
}