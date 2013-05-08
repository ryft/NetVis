package netvis.data.filters;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import netvis.ApplicationFrame;
import netvis.data.DataController;
import netvis.data.model.PacketFilter;

public abstract class WhiteListFilter implements PacketFilter {
	
	final DataController dataController;
	final ApplicationFrame frame;
	protected ArrayList<String> whiteList;
	protected ArrayList<String> blackList;
	private WLTableModel tableModel;
	private JTable table;
	private JScrollPane scrollPane;
	private JDialog dialogBox;
	private JButton openButton;
	private JButton cancelButton;
	private JButton okButton;
	private JPanel tablePanel;
	private JPanel buttonPanel;
	private JPanel filterPanel;
	private boolean wasFullScreen;
	
	public WhiteListFilter(DataController dataController, String attribute, ApplicationFrame frame) {
		this.dataController = dataController;
		this.frame = frame;
		blackList = new ArrayList<String>();
		whiteList = new ArrayList<String>();
		tableModel = new WLTableModel();
		table = new JTable(tableModel);
		scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		filterPanel = new JPanel();
		
		dialogBox = new JDialog();
		dialogBox.setModal(true);
		dialogBox.setTitle("Filter by " + attribute);
		dialogBox.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialogBox.setMinimumSize(new Dimension (300, 200));
		
		openButton = new JButton("Filter by " + attribute);
		openButton.addActionListener(new OpenButtonListener());
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelButtonListener());
		okButton = new JButton("OK");
		okButton.addActionListener(new OkButtonListener());
		
		tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
		tablePanel.add(Box.createRigidArea(new Dimension(0,5)));
		tablePanel.add(scrollPane);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelButton);
		
		dialogBox.setLayout(new BorderLayout());
		dialogBox.add(tablePanel, BorderLayout.CENTER);
		dialogBox.add(buttonPanel, BorderLayout.PAGE_END);
		filterPanel.add(openButton);
		
		wasFullScreen = false;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ArrayList<String> newWhiteList = new ArrayList<String>();
		ArrayList<String> newBlackList = new ArrayList<String>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String include = (String) table.getValueAt(i, 0);
			String exclude = (String) table.getValueAt(i, 1);
			if (include != "") 
				newWhiteList.add(include);
			if (exclude != "")
				newBlackList.add(exclude);
		}
		whiteList = newWhiteList;
		blackList = newBlackList;
		dataController.filterUpdated();
	}
	
	@Override
	public JComponent getPanel() {
		return filterPanel;
	}
	
	private class OpenButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if(frame.isFullScreen()) {
				wasFullScreen = true;
				frame.toggleFullScreen();
			}
			else
				wasFullScreen = false;
			tableModel.saveBackup();
			dialogBox.setLocation(openButton.getLocationOnScreen());
			dialogBox.setVisible(true);
		}
		
	}
	
	private class OkButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			dialogBox.setVisible(false);
			if (wasFullScreen)
				frame.toggleFullScreen();
		}
		
	}
	
	private class CancelButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			table.editingCanceled(null);
			dialogBox.setVisible(false);
			tableModel.restoreBackup();		
			
			if (wasFullScreen)
				frame.toggleFullScreen();
		}
		
	}
	
	@SuppressWarnings("serial")
	private class WLTableModel extends AbstractTableModel {
		
		private int rowCount = 1;
		private String[] columnNames = new String[]{"Include", "Exclude"};
		private ArrayList<String> includeList = new ArrayList<String>();
		private ArrayList<String> excludeList = new ArrayList<String>();
		private int rowCountBackup;
		private ArrayList<String> includeListBackup;
		private ArrayList<String> excludeListBackup;
		
		public WLTableModel() {
			super();
			includeList.add("");
			excludeList.add("");
		}
		
		@SuppressWarnings("unchecked")
		public void saveBackup() {
			rowCountBackup = Integer.valueOf(rowCount);
			includeListBackup = (ArrayList<String>) includeList.clone();
			excludeListBackup = (ArrayList<String>) excludeList.clone();
		}
		
		public void restoreBackup() {
			rowCount = rowCountBackup;
			includeList = includeListBackup;
			excludeList = excludeListBackup;
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return rowCount;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (column == 0) {
				try {
					return includeList.get(row);
				} catch(IndexOutOfBoundsException e){
					return "";
				}
			}
			else
				try {
					return excludeList.get(row);
				} catch(IndexOutOfBoundsException e){
					return "";
				}
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return true;
		}
		
		@Override
		public void setValueAt(Object value, int row, int column) {
			boolean rowCountChanged = false;
			String text = (String) value;
			
			if (text.trim().length() == 0 && row != rowCount - 1) {
				if (column == 0)
					includeList.remove(row);
				else
					excludeList.remove(row);
				int newrowCount = Math.max(includeList.size(), excludeList.size());
				if (newrowCount != rowCount) {
					rowCount = newrowCount +1;
					rowCountChanged = true;
				}
			}
			else {
				if (column == 0)
					if (row >= includeList.size())
						includeList.add(row, text);
					else
						includeList.set(row, text);
				else{
					if (row >= excludeList.size())
						excludeList.add(row, text);
					else
						excludeList.set(row, text);
				}
				if (row == rowCount - 1) {
					rowCount++;
					rowCountChanged = true;
				}
			}	
			if (rowCountChanged)
				fireTableDataChanged();
			else
				fireTableCellUpdated(row, column);
		}
		
	}
}
