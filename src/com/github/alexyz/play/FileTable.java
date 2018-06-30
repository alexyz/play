package com.github.alexyz.play;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

// display current in bold
// increment file counts
public class FileTable extends JTable {
	
	public static final Color tableBackgroundColour = UIManager.getDefaults().getColor("Table.background");
	public static final Font tableFont = UIManager.getDefaults().getFont("Table.font");
//	public static final Font monoTableFont = new Font("Monospaced", 0, tableFont.getSize());
	public static final Font boldTableFont = tableFont.deriveFont(Font.BOLD);
	private static final Color gray14 = new Color(0xee,0xee,0xee);
	private static final Color gray12 = new Color(0xcc,0xcc,0xcc);
	private static final Color gray10 = new Color(0xaa,0xaa,0xaa);
	
	public FileTable() {
		setModel(new FileTableModel());
	}
	
	public FileTableModel getFileTableModel () {
		return (FileTableModel) super.getModel();
	}
	
	/*
	@Override
	public String getToolTipText(MouseEvent e) {
		int vr = rowAtPoint(e.getPoint());
		int vc = columnAtPoint(e.getPoint());
		if (vr >= 0 && vc >= 0) {
			// calc tooltip on demand
			int mr = convertRowIndexToModel(vr);
			int mc = convertColumnIndexToModel(vc);
			FileTableModel m = getFileTableModel();
			File f = m.getFile(mr);
			File d = m.getBaseDir();
			if (d != null) {
				String s = "<html>";
				while (f != null && !f.equals(d)) {
					s = s + f.getName() + "<br>";
					f = f.getParentFile();
				}
				return s.trim();
			}
		}
		return null;
	}
	*/
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int vrow, int vcol) {
		Component comp = super.prepareRenderer(renderer, vrow, vcol);
		
		if (comp instanceof JComponent) {
			JComponent jcomp = (JComponent)comp;
			// need to convert before sending to table model
			int row = convertRowIndexToModel(vrow);
			int col = convertColumnIndexToModel(vcol);
			FileTableModel model = getFileTableModel();
			File currentFile = PlayFrame.frame.getButtonPanel().currentFile();
			File rowFile = model.getFile(row);
			boolean playing = currentFile != null && rowFile != null && currentFile.equals(rowFile);
			
			// sets inner border
			//jcomp.setBorder(new LineBorder(Color.red));
			
			if (getSelectionModel().isSelectedIndex(row)) {
				if (playing) {
					jcomp.setFont(boldTableFont);
				}
			} else {
				if (playing) {
					jcomp.setBackground(gray10);
					jcomp.setFont(boldTableFont);
				} else {
					int i = model.getParentIndex(row);
					jcomp.setBackground((i & 1) == 0 ? tableBackgroundColour : gray14);
				}
			}
			
//			jcomp.setFont(playing ? boldTableFont : tableFont);	
		}
		return comp;
	}
	
}
