package as.play;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class FilePanel extends JPanel {
	
	private static final Log log = new Log(FilePanel.class);
	
//	private final FileTableModel fileModel = new FileTableModel();
	private final FileTable fileTable = new FileTable();
	private final JLabel dirLabel = new JLabel();
	
	private ButtonPanel parent;
	
	public FilePanel (ButtonPanel parent) {
		super(new BorderLayout());
		this.parent = parent;
		
		fileTable.getColumnModel().getColumn(1).setMaxWidth(30);
		//fileTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileTable.addMouseListener(new FPMA());
		
		JScrollPane fileScroller = new JScrollPane(fileTable);
		
		add(dirLabel, BorderLayout.NORTH);
		add(fileScroller, BorderLayout.CENTER);
		
	}
	
	public String getDir () {
		return dirLabel.getText();
	}
	
	public File next (String mode) {
		log.println("next " + mode);
		int i = fileTable.getSelectionModel().getMinSelectionIndex();
		log.println("next current=" + i);
		int max = fileTable.getRowCount();
		// XXX only if selection not changed
		switch (mode) {
			case Util.MSEQ: i = i + 1; break;
			case Util.MREP: break;
			case Util.MREPALL: i = (i + 1) % max; break;
			case Util.MSINGLE: i = -1; break;
			case Util.MSHUF: i = Util.RAND.nextInt(max); break;
			default: throw new RuntimeException("invalid mode " + mode);
		}
		if (i >= 0 && i < max) {
			log.println("next i=" + i);
			fileTable.getSelectionModel().setSelectionInterval(i, i);
			return fileTable.getFileTableModel().getFile(i);
		} else {
			return null;
		}
	}

//	public void onplay () {
//		log.println("onplay");
//		fileTable.repaint();
//	}
	
	public File changedir () {
		JFileChooser fc = new JFileChooser();
		File f = new File(dirLabel.getText());
		if (f.isDirectory()) {
			fc.setCurrentDirectory(f);
		}
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File dir = fc.getSelectedFile();
			setDir(dir);
			return dir;
		} else {
			return null;
		}
	}
	
	public void setDir (File dir) {
		fileTable.getFileTableModel().setFiles(Collections.emptyList());
		dirLabel.setText("");
		if (dir != null && dir.isDirectory()) {
			dirLabel.setText(dir.getAbsolutePath());
			Util.EX.execute(() -> {
				List<File> l = Util.dir3(dir, new ArrayList<>());
				SwingUtilities.invokeLater(() -> fileTable.getFileTableModel().setFiles(l));
			});
		}
	}

	public File getFile () {
		int i = fileTable.getSelectionModel().getMinSelectionIndex();
		File f = fileTable.getFileTableModel().getFile(i);
		log.println("file panel get " + i + " is " + f);
		return f;
	}
	
	private class FPMA extends MouseAdapter {
		@Override
		public void mousePressed (MouseEvent e) {
			if (!e.isPopupTrigger() && e.getClickCount() >= 2) {
				log.println("file table mouse pressed click count " + e.getClickCount());
				//parent.stop();
				File f = fileTable.getFileTableModel().getFile(fileTable.getSelectedRow());
				parent.playspecific(f);
				//parent.playnext();
			}
		}
	}

}
