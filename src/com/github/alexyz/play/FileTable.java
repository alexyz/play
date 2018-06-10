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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

// display current in bold
// increment file counts
public class FileTable extends JTable {
	
	public static final Color tableBackgroundColour = UIManager.getDefaults().getColor("Table.background");
	public static final Font tableFont = UIManager.getDefaults().getFont("Table.font");
	public static final Font monoTableFont = new Font("Monospaced", 0, tableFont.getSize());
	public static final Font boldTableFont = tableFont.deriveFont(Font.BOLD);
	
	public FileTable() {
		setModel(new FileTableModel());
	}
	
	public FileTableModel getFileTableModel () {
		return (FileTableModel) super.getModel();
	}
	
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
				//FIXME this is annoying
				//return s.trim();
			}
		}
		return null;
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		Component comp = super.prepareRenderer(renderer, row, col);
		if (comp instanceof JComponent) {
			JComponent jcomp = (JComponent)comp;
			// need to convert before sending to table model
			int row2 = convertRowIndexToModel(row);
			int col2 = convertColumnIndexToModel(col);
			//			if (!getSelectionModel().isSelectedIndex(row)) {
			//				Color colour = ((MyTableModel<?>)getModel()).getColour(row2, col2);
			//				jcomp.setBackground(colour != null ? colour : tableBackgroundColour);
			//			}
			File f1 = PlayFrame.frame.getButtonPanel().currentFile();
			File f2 = getFileTableModel().getFile(row2);
			jcomp.setFont(f1 != null && f2 != null && f1.equals(f2) ? boldTableFont : tableFont);	
		}
		return comp;
	}
	
}
