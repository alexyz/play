package com.github.alexyz.play.cfg;

import java.io.*;
import java.util.*;

import javax.xml.bind.*;

import com.github.alexyz.play.*;

public class Config {
	
	private static final Log log = new Log(Config.class);
	
	public static void main (String[] args) throws Exception {
		
		XRoot root = new XRoot();
		root.files = new ArrayList<>();
		XFile f = new XFile();
		f.name = "cool.mp3";
		f.count = 2;
		f.len = 3.14f;
		root.files.add(f);

		JAXBContext c = JAXBContext.newInstance(XRoot.class, XFile.class);
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
		this.context = JAXBContext.newInstance(XRoot.class, XFile.class);
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
	
	public void save () {
		log.println("save " + configFile.getAbsolutePath());
		try {
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			try (FileOutputStream os = new FileOutputStream(configFile)) {
				Collections.sort(root.files);
				m.marshal(root, os);
			}
		} catch (IOException | JAXBException e) {
			log.println("could not save config", e);
		}
	}
	
	/** remove file from config */
	public void delete (File f1) {
		int i = fileIndex(f1);
		if (i >= 0) {
			log.println("delete " + f1);
			root.files.remove(i);
		}
	}
	
	/** rename file in config */
	public void rename (File f1, File f2) {
		int i = fileIndex(f1);
		if (i >= 0) {
			log.println("rename " + f1 + " to " + f2);
			XFile info = root.files.remove(i);
			info.name = f2.getName().toLowerCase();
			insertFile(info);
		}
	}
	
	private int fileIndex (File f1) {
		XFile info = new XFile(f1.getName().toLowerCase());
		return Collections.binarySearch(root.files, info);
	}
	
	private void insertFile (XFile info) {
		int j = Collections.binarySearch(root.files, info);
		if (j < 0) {
			root.files.add(-(j + 1), info);
		}
	}
	
	/** get file info, create if required, return null otherwise */
	public XFile getFile (File f, boolean add) {
		XFile info = new XFile();
		info.name = f.getName().toLowerCase();
		int i = Collections.binarySearch(root.files, info);
		if (i >= 0) {
			return root.files.get(i);
		} else if (add) {
			root.files.add(-(i + 1), info);
			return info;
		} else {
			return null;
		}
	}
	
}
