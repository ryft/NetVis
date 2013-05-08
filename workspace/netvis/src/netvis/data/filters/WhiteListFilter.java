package netvis.data.filters;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import netvis.data.DataController;
import netvis.data.model.PacketFilter;

public abstract class WhiteListFilter implements PacketFilter {
	
	final DataController dataController;
	protected ArrayList<String> whiteList;
	protected ArrayList<String> blackList;
	private WLTableModel tableModel;
	private JTable table;
	private JScrollPane scrollPane;
	private JDialog dialogBox;
	private JButton button;
	private JPanel filterPanel;

	public WhiteListFilter(DataController dataController, String attribute) {
		this.dataController = dataController;
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
		dialogBox.add(scrollPane);
		dialogBox.setMinimumSize(new Dimension (300, 200));
		button = new JButton("Filter by " + attribute);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				dialogBox.setLocation(button.getLocationOnScreen());
				dialogBox.setVisible(true);
			}

		});
		filterPanel.add(button);
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
	
	
	
	@SuppressWarnings("serial")
	private class WLTableModel extends AbstractTableModel {
		
		private int rowCount = 1;
		private String[] columnNames = new String[]{"Include", "Exclude"};
		private ArrayList<String> includeList = new ArrayList<String>();
		private ArrayList<String> excludeList = new ArrayList<String>();
		
		public WLTableModel() {
			super();
			includeList.add("");
			excludeList.add("");
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
