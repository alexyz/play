package com.github.alexyz.play;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GbcHelper extends GridBagConstraints {
	
	public GbcHelper ipad (int x, int y) {
		ipadx = x;
		ipady = y;
		return this;
	}
	
	public GbcHelper fill (int f) {
		fill = f;
		return this;
	}
	
	public GbcHelper fillBoth () {
		return fill(BOTH);
	}
	
	public GbcHelper fillHorizontal () {
		return fill(HORIZONTAL);
	}
	
	public GbcHelper fillVertical () {
		return fill(VERTICAL);
	}
	
	public GbcHelper anchor (int a) {
		anchor = a;
		return this;
	}
	
	public GbcHelper anchorEast() {
		return anchor(EAST);
	}
	
	public GbcHelper anchorWest() {
		return anchor(WEST);
	}
	
	public GbcHelper pos (int x, int y) {
		gridx = x;
		gridy = y;
		return this;
	}
	
	public GbcHelper size(int x, int y) {
		gridwidth = x;
		gridheight = y;
		return this;
	}
	
	public GbcHelper weight(double x, double y) {
		weightx = x;
		weighty = y;
		return this;
	}
	
	public GbcHelper weightBoth () {
		return weight(1,1);
	}
	
	public GbcHelper weightX () {
		return weight(1,0);
	}
	
	public GbcHelper weightY () {
		return weight(0,1);
	}
	
	public GbcHelper insets (int z) {
		insets = new Insets(z,z,z,z);
		return this;
	}
	
	public GbcHelper insets (int t, int l, int b, int r) {
		insets = new Insets(t,l,b,r);
		return this;
	}
	
	@Override
	public GbcHelper clone () {
		return (GbcHelper) super.clone();
	}
	
}
