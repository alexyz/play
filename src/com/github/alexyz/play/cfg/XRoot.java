package com.github.alexyz.play.cfg;

import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "root")
public class XRoot {
	@XmlElementWrapper(name = "files")
	@XmlElement(name = "file")
	public List<XFile> files = new ArrayList<>();
}
