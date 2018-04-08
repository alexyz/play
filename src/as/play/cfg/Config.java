package as.play.cfg;

import java.io.*;
import java.util.*;

import javax.xml.bind.*;

import as.play.*;

public class Config {
	
	private static final Log log = new Log(Config.class);
	
	public static void main (String[] args) throws Exception {
		
		XRoot root = new XRoot();
		root.files = new ArrayList<>();
		XInfo f = new XInfo();
		f.name = "cool.mp3";
		f.count = 2;
		f.len = 3.14f;
		root.files.add(f);
		
		JAXBContext c = JAXBContext.newInstance(XRoot.class, XInfo.class);
		Marshaller m = c.createMarshaller();
		try (StringWriter w = new StringWriter()) {
			m.marshal(root, w);
			System.out.println(w);
		}
	}
	
	private JAXBContext context;
	private XRoot root;
	private File configFile;
	
	public Config (File f) throws Exception {
		this.configFile = f;
		this.context = JAXBContext.newInstance(XRoot.class, XInfo.class);
	}
	
	public void load () throws Exception {
		log.println("load " + configFile.getAbsolutePath());
		Unmarshaller um = context.createUnmarshaller();
		if (configFile.exists() && configFile.length() > 0) {
			try (FileInputStream is = new FileInputStream(configFile)) {
				root = (XRoot) um.unmarshal(configFile);
				Collections.sort(root.files);
			}
		} else {
			root = new XRoot();
		}
	}
	
	public void save () throws Exception {
		log.println("save " + configFile.getAbsolutePath());
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		try (FileOutputStream os = new FileOutputStream(configFile)) {
			Collections.sort(root.files);
			m.marshal(root, os);
		}
	}
	
	public void rename (File f1, File f2) {
		log.println("rename " + f1 + " to " + f2);
		XInfo info = new XInfo(f1.getName().toLowerCase());
		int i = Collections.binarySearch(root.files, info);
		if (i >= 0) {
			info = root.files.remove(i);
			info.name = f2.getName().toLowerCase();
			int j = Collections.binarySearch(root.files, info);
			if (j < 0) {
				root.files.add(-(j + 1), info);
			}
		}
	}
	
	public XInfo get (File f) {
		return get(f, false);
	}
	
	public XInfo get (File f, boolean add) {
		XInfo info = new XInfo();
		info.name = f.getName().toLowerCase();
		int i = Collections.binarySearch(root.files, info);
		if (i >= 0) {
			return root.files.get(i);
		} else {
			if (add) {
				root.files.add(-(i + 1), info);
			}
			return info;
		}
	}
	
}
