package netvis.visualisations.maps;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import netvis.visualisations.Visualisation;
import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.Position;
import netvis.visualisations.gameengine.Units;

public class MapController {
	
	Point oldpos = null;
	Map map = null;

	public MapController (Map m, Visualisation vis) {
		
		// Set up the connected map
		map = m;
		
		// Now add the keyboard listener which will be responsible for zooming
		vis.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_EQUALS) {
					map.ZoomIn();
				}
				;
				if (e.getKeyChar() == KeyEvent.VK_MINUS) {
					map.ZoomOut();
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		vis.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0) {
					map.ZoomIn();
				}
				if (e.getWheelRotation() > 0) {
					map.ZoomOut();
				}
			}
		});

		vis.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				double viewfield = map.viewfieldanim.toDouble();
				if (oldpos != null) {
					map.middlex.MoveTo (map.middlex.getGoal() - (e.getX() - oldpos.x) * viewfield, 0);
					map.middley.MoveTo (map.middley.getGoal() + (e.getY() - oldpos.y) * viewfield, 0);
					// map.middlex -= (e.getX()-oldpos.x)*viewfield;
					// map.middley += (e.getY()-oldpos.y)*viewfield;
				}
				;
				oldpos = e.getPoint();
			}
		});

		vis.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				double viewfield = map.viewfieldanim.toDouble();
				int x = (int) Math.round (map.middlex.toDouble() + (e.getX() - (map.width / 2)) * viewfield);
				int y = (int) Math.round (map.middley.toDouble() - (e.getY() - (map.height / 2)) * viewfield);

				Node n = map.FindClickedNode(x, y);
				Position c = Units.MetaCoordinateByPosition (1, map.base, new Position(x, y));
				Position p = Units.MetaPositionByCoordinate (1, map.base, c);
				if (n != null) n.MouseClick(e);

				if (e.getClickCount() == 2) {
					// Zoom on the selected node - such that it will fill the
					// screen
					double goal = map.ZoomOn();
					map.viewfieldanim.MoveTo(goal, 1000);
				}

				if (n != null) {
					// Move to the selected node
					map.middlex.MoveTo(p.x, 1000);
					map.middley.MoveTo(p.y, 1000);
				}

				e.consume();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// If it is over some node - drag this node - TODO

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				oldpos = null;
			}

		});
	}

}
