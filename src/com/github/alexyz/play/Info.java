package com.github.alexyz.play;

public class Info {
	public String out;
	public String type;
	public float duration;
	public float rate;
	public String ch;
	public float freq;
	@Override
	public String toString () {
		return "Info[type=" + type + " duration=" + duration + " kbps=" + rate + " ch=" + ch + " rate=" + freq + "]";
	}
}
