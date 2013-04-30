package netvis.data.filters;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import netvis.data.DataController;
import netvis.data.model.Packet;
import netvis.data.model.PacketFilter;

public class AddressFilter implements PacketFilter {
	
	final DataController dataController;
	private ArrayList<String> whiteList;
	private ArrayList<String> blackList;
	private IPTableModel tableModel;
	private JTable table;
	private JScrollPane scrollPane;
	private JPanel filterPanel;
	
	public AddressFilter(DataController dataController) {
		this.dataController = dataController;
		blackList = new ArrayList<String>();
		whiteList = new ArrayList<String>();
		tableModel = new IPTableModel();
		table = new JTable(tableModel);
		scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		filterPanel = new JPanel();
		filterPanel.setLayout(new BorderLayout());
		filterPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setMinimumSize(new Dimension(35, 100));
		scrollPane.setMaximumSize(new Dimension(35, 100));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		ArrayList<String> newWhiteList = new ArrayList<String>();
		ArrayList<String> newBlackList = new ArrayList<String>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String includeIP = (String) table.getValueAt(i, 0);
			String excludeIP = (String) table.getValueAt(i, 1);
			if (includeIP != "") 
				newWhiteList.add(includeIP);
			if (excludeIP != "")
				newBlackList.add(excludeIP);
		}
		whiteList = newWhiteList;
		blackList = newBlackList;
		dataController.filterUpdated();
	}

	@Override
	public boolean filter(Packet packet) {
		String dip = packet.dip;
		String sip = packet.sip;
		return ((whiteList.isEmpty() || whiteList.contains(dip) || whiteList.contains(sip)) &&
				(!blackList.contains(dip) && !blackList.contains(sip)));
	}

	@Override
	public String description() {
		return "Filter by IP address";
	}

	@Override
	public JComponent getPanel() {
		return filterPanel;
	}
	
	@SuppressWarnings("serial")
	private class IPTableModel extends AbstractTableModel {
		
		private int rowCount = 1;
		private String[] columnNames = new String[]{"Include", "Exclude"};
		private ArrayList<String> includeList = new ArrayList<String>();
		private ArrayList<String> excludeList = new ArrayList<String>();
		
		public IPTableModel() {
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
				return includeList.get(row);
			}
			else
				return excludeList.get(row);
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return true;
		}
		
		@Override
		public void setValueAt(Object value, int row, int column) {
			boolean rowCountChanged = false;
			String text = (String) value;
			if (column == 0)
				includeList.set(row, text);
			else
				excludeList.set(row, text);
			if (row == rowCount - 1 && text != "") {
				rowCount++;
				rowCountChanged = true;
				includeList.add("");
				excludeList.add("");
			}
			if (text == "") {
				int newrowCount = Math.max(includeList.size(), excludeList.size());
				if (newrowCount != rowCount) {
					rowCount = newrowCount;
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
