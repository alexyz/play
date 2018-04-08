package as.play;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TextDialog extends JDialog implements ActionListener {
	
	public TextDialog (JComponent parent, String title, String text) {
		super(SwingUtilities.getWindowAncestor(parent), title, ModalityType.DOCUMENT_MODAL);
		JTextArea textArea = new JTextArea();
		textArea.setText(text);
		textArea.setEditable(false);
		textArea.setCaretPosition(0);
		JScrollPane scroller = new JScrollPane(textArea);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(scroller, BorderLayout.CENTER);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		setModal(true);
		setContentPane(contentPanel);
		setPreferredSize(new Dimension(480, 320));
		pack();
		setLocationRelativeTo(parent);
	}
	
	@Override
	public void actionPerformed (ActionEvent e) {
		setVisible(false);
	}
}
