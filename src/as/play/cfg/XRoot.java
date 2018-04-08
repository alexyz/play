package as.play.cfg;

import java.util.*;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "root")
public class XRoot {
	@XmlElementWrapper(name = "files")
	@XmlElement(name = "file")
	public List<XInfo> files = new ArrayList<>();
}
