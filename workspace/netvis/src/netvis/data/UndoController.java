package netvis.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class UndoController {
	public final static UndoController INSTANCE = new UndoController();
	final Stack<UndoAction> undoMoves;
	JPanel panel;
	JButton undoButton;
	
	private UndoController(){
		undoMoves = new Stack<UndoAction>();
		 panel = new JPanel();
		 undoButton = new JButton("Back");
		 
		 undoButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					executeUndo();
				}
		 });
		 panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		 panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
	}
	protected void updateButton() {
		if (panel.getComponents().length == 0 && !undoMoves.empty()){
			panel.add(undoButton);
		}
		if (panel.getComponents().length != 0 && undoMoves.empty()){
			panel.remove(undoButton);
		}
	}
	public void addUndoMove(UndoAction undoMove){
		undoMoves.push(undoMove);
		updateButton();
	}
	public void executeUndo(){
		if (!undoMoves.empty())
			undoMoves.pop().execute_undo();
		updateButton();
	}
	public void clearUndoStack(){
		undoMoves.clear();
		updateButton();
	}
	public JPanel getPanel(){
		return panel;
	}
}
