package as.play.cfg;

import java.io.File;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "file")
public class XInfo implements Comparable<XInfo> {
	
	@XmlAttribute(name = "name")
	public String name;
	@XmlAttribute(name = "count")
	public Integer count;
	@XmlAttribute(name = "len")
	public Float len;
	
	public XInfo () {
		//
	}
	
	public XInfo (String name) {
		this.name = name;
	}
	
	public void increment() {
		count = Integer.valueOf((count != null ? count.intValue() : 0) + 1);
	}
	
	@Override
	public int compareTo (XInfo o) {
		return String.valueOf(name).compareToIgnoreCase(String.valueOf(o.name));
	}

	@Override
	public String toString () {
		return "FI [name=" + name + " count=" + count + " len=" + len + "]";
	}
	
}
