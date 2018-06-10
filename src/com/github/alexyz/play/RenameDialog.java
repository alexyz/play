package com.github.alexyz.play;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.*;

import static com.github.alexyz.play.SwingUtil.*;

public class RenameDialog extends JDialog implements DocumentListener {
	
	private static final Log log = new Log(RenameDialog.class);
	
	public static void main (String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		RenameDialog d = new RenameDialog();
		d.setModalityType(ModalityType.APPLICATION_MODAL);
		d.setFiles(Arrays.asList(new File("/Users/alex/Dropbox/HOME/Music/Incoming/65 days - Wild Light").listFiles()));
		d.setVisible(true);
		System.exit(0);
	}
	
	private final JTextField regexField = new JTextField();
	private final JTextField replaceField = new JTextField();
	private final JTextArea area = new JTextArea();
	private final JScrollPane areaScroller = new JScrollPane(area);
	private List<File> files;
	private Map<File, File> renameMap;
	private Runnable onOk;
	
	public RenameDialog () {
		setTitle(getClass().getSimpleName());
		regexField.setText("(.*)");
		replaceField.setText("$1");
		regexField.getDocument().addDocumentListener(this);
		replaceField.getDocument().addDocumentListener(this);
		area.setEditable(false);
		
		JPanel reppanel = new JPanel(new GridBagLayout());
		reppanel.add(label("Regex"));
		reppanel.add(regexField, gbc().weightX().fillHorizontal());
		reppanel.add(label("Replacement"));
		reppanel.add(replaceField, gbc().weightX().fillHorizontal());
		
		JPanel butpanel = xpanel(button("OK", e -> ok()), button("Cancel", e -> cancel()));
		
		JPanel p = new JPanel(new GridBagLayout());
		p.add(reppanel, gbc().pos(1,1).fillBoth().insets(5));
		p.add(areaScroller, gbc().pos(1,2).weightBoth().fillBoth().insets(5));
		p.add(butpanel, gbc().pos(1,3).fillBoth().insets(5));
		
		setContentPane(p);
		setPreferredSize(new Dimension(480, 320));
		pack();
	}
	
	private void cancel () {
		setVisible(false);
	}
	
	public void setOnOk (Runnable onOk) {
		this.onOk = onOk;
	}
	
	public Runnable getOnOk () {
		return onOk;
	}
	
	public Map<File, File> getMap () {
		return renameMap;
	}
	
	private void ok () {
		if (renameMap == null || renameMap.size() == 0) {
			JOptionPane.showMessageDialog(this, "Nothing to rename", "Mass Rename", JOptionPane.WARNING_MESSAGE);
		} else if (JOptionPane.showConfirmDialog(this, "Really rename " + renameMap.size() + " files?", "Mass Rename", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			onOk.run();
			setVisible(false);
		}
	}
	
	public void setFiles(List<File> files) {
		this.files = files;
		update();
	}
	
	@Override
	public void changedUpdate (DocumentEvent e) {
		update();
	}
	
	@Override
	public void insertUpdate (DocumentEvent e) {
		update();
	}
	
	@Override
	public void removeUpdate (DocumentEvent e) {
		update();
	}
	
	private void update() {
		this.renameMap = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			Map<File,File> map = new TreeMap<>();
			Set<String> rmap = new TreeSet<>();
			Pattern pat = Pattern.compile(regexField.getText());
			String rep = replaceField.getText();
			boolean ok = true;
			
			for (File f : files) {
				String name = f.getName();
				int i = name.lastIndexOf(".");
				String forename = i >= 0 ? name.substring(0, i) : name;
				String ext = i >= 0 ? name.substring(i) : "";
				String newname = pat.matcher(forename).replaceFirst(rep).trim() + ext.trim();
				sb.append(name).append(" -> ").append(newname);
				if (newname.length() > 0 && !name.equals(newname)) {
					File g = new File(f.getParentFile(), newname);
					if (!rmap.add(newname.toLowerCase())) {
						sb.append(" [duplicate name]");
						ok = false;
					} else if (g.exists()) {
						sb.append(" [file exists]");
						ok = false;
					} else {
						map.put(f, g);
					}
				}
				sb.append("\n");
			}
			
			sb.insert(0, "differences: " + map.size() + "/" + files.size() + "\n\n");
			if (ok && map.size() > 0) {
				this.renameMap = map;
			}
			
		} catch (Exception e) {
			// invalid regex
			log.println("exception in update: " + e);
			sb.append(e.toString());
		}
		
		area.setText(sb.toString());
		area.setCaretPosition(0);
	}
	
}
