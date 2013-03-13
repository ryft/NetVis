package netvis.ui;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public TablePanel() {

		JLabel titleLabel = new JLabel("Results");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		@SuppressWarnings("serial")
		TableModel dataModel = new AbstractTableModel() {
			public int getColumnCount() {
				return 5;
			}

			public int getRowCount() {
				return 5;
			}

			public Object getValueAt(int row, int col) {
				return new String();
			}
		};
		JTable table = new JTable(dataModel);
		JScrollPane scrollPane = new JScrollPane(table);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(titleLabel);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(scrollPane);
		add(Box.createVerticalGlue());
	}

}
