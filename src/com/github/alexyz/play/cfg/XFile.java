package com.github.alexyz.play.cfg;

import javax.xml.bind.annotation.*;

/** file info - name, count, length */
@XmlRootElement(name = "file")
public class XFile implements Comparable<XFile> {
	
	@XmlAttribute(name = "name")
	public String name;
	@XmlAttribute(name = "count")
	public Integer count;
	@XmlAttribute(name = "len")
	public Float len;
	
	public XFile () {
		//
	}
	
	public XFile (String name) {
		this.name = name;
	}
	
	public void increment() {
		count = Integer.valueOf((count != null ? count.intValue() : 0) + 1);
	}
	
	@Override
	public int compareTo (XFile o) {
		return String.valueOf(name).compareToIgnoreCase(String.valueOf(o.name));
	}

	@Override
	public String toString () {
		return "FI [name=" + name + " count=" + count + " len=" + len + "]";
	}
	
}
