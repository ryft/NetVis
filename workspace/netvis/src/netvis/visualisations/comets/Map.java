package netvis.visualisations.comets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GL2;

import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.Painter;
import netvis.visualisations.gameengine.Position;
import netvis.visualisations.gameengine.TexturePool;

public class Map {

	class NodeWithPosition {
		public NodeWithPosition(Node a, Position c, Position p) {
			node = a;
			coo = c;
			pos = p;
		}

		public Node node;
		public Position pos;
		public Position coo;
	}

	// IPs mapped to nodesByName
	HashMap<String, NodeWithPosition> nodesByName;

	HashMap<Position, NodeWithPosition> nodesByPosition;

	List<NodeWithPosition> nodesl;

	// Basic size of the node
	int base = 400;
	int width;
	int height;

	// Connections to be drawn
	HashMap<String, Connection> connections;

	Random rand;

	class NamedThreadFactory implements ThreadFactory {
		int i = 0;

		public Thread newThread(Runnable r) {
			return new Thread(r, "Node animating thread #" + (i++));
		}
	}

	// Animation of the nodesByName can be parallelised
	ExecutorService exe = new ThreadPoolExecutor(4, 8, 5000, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory());

	public Map(int w, int h) {
		width = w;
		height = h;

		rand = new Random();

		nodesByName = new HashMap<String, NodeWithPosition>();
		nodesByPosition = new HashMap<Position, NodeWithPosition>();
		nodesl = new ArrayList<NodeWithPosition>();

		// Load all the necessary textures
		TexturePool.LoadTexture("server", Map.class.getResource("resources/server.png"));
		TexturePool.LoadTexture("basic", Map.class.getResource("resources/basic.png"));
	}

	public void DrawEverything(GL2 gl) {
		Painter.DrawGrid(base, gl);
		for (NodeWithPosition i : nodesByName.values()) {
			int x = i.pos.x;
			int y = i.pos.y;

			gl.glPushMatrix();
			// Transpose it to the right spot
			gl.glTranslated(x, y, 0.0);

			// Draw it
			i.node.Draw(base, new MapPainter(), gl);
			gl.glPopMatrix();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void StepAnimation(final long time) throws InterruptedException, ExecutionException {
		ArrayList<Future<?>> list = new ArrayList<Future<?>>();
		for (final NodeWithPosition i : nodesByName.values())
			list.add(exe.submit(new Callable() {
				@Override
				public Object call() throws Exception {
					i.node.UpdateAnimation(time);
					return null;
				}
			}));

		for (Future<?> i : list)
			i.get();
	}

	public void SetSize(int w, int h, GL2 gl) {
		width = w;
		height = h;
	}

	public void SuggestNode(String sip, String dip) {
		// Suggests the existence of the node in the network to be displayed

		// Look whether the node already exists
		NodeWithPosition find = nodesByName.get(dip);

		if (find == null) {
			find = AddNode(sip, dip, "basic");
		}

		find.node.UpdateWithData(sip);
	}

	public void SortNodes() {
		if (true)
			return;

		/*
		 * TODO This code is unreachable -- Is it still here so it can be used
		 * later?
		 * 
		 * Collections.sort(nodesl, new Comparator<NodeWithPosition>() {
		 * 
		 * @Override public int compare(NodeWithPosition n1, NodeWithPosition
		 * n2) { return n2.node.Priority() - n1.node.Priority(); }
		 * 
		 * });
		 * 
		 * // Now display them in the order for (int i = 0; i < nodesl.size();
		 * i++) { NodeWithPosition n = nodesl.get(i);
		 * 
		 * Position posit = FindPosition(i); Position coord =
		 * CoordinateByPosition(posit);
		 * 
		 * n.pos = posit; n.coo = coord; }
		 */
	}

	private Position FindPosition(int num) {
		double x = 0.0;
		double y = 0.0;

		int s = num + 1;
		int innerring = (int) Math.floor(Math.sqrt((s - 1 / 4.0) / 3.0) - 0.5);
		int outerring = innerring + 1;
		int k = innerring;
		int shift = s - 3 * (k * k + k) - 1;

		// Move to the desired ring
		if (outerring % 2 == 1)
			x += Math.sqrt(3) * base / 2.0;
		y += base * outerring * 1.5;

		// Move the shift times
		double angle = 0;

		for (int i = 0 - (outerring / 2); i < shift - (outerring / 2); i++) {
			if (i % outerring == 0) {
				angle -= Math.PI / 3;
			}
			x += Math.sqrt(3) * base * Math.cos(angle);
			y += Math.sqrt(3) * base * Math.sin(angle);
		}

		// System.out.println("Node #" + num + " is on the ring : " + outerring
		// + " with shift " + shift);

		return new Position(x, y);
	}

	private Position PositionByCoordinate(Position coor) {
		int coorx = coor.x;
		int coory = coor.y;

		double r3 = Math.sqrt(3.0);
		Position p = new Position(r3 * coorx * base, 1.5 * coory * base);

		// Odd rows are shifted
		if (coory % 2 != 0)
			p.x += r3 * base / 2.0;

		return p;
	}

	private Position CoordinateByPosition(Position pos) {
		int posx = pos.x;
		int posy = pos.y;

		double r3 = Math.sqrt(3.0);
		Position p = new Position(0, 0);

		p.y = (int) Math.round(posy / (base * 1.5));
		if (p.y % 2 != 0)
			p.x = (int) Math.round((posx - base * r3 / 2.0) / (base * r3));
		else
			p.x = (int) Math.round(posx / (base * r3));

		return p;
	}

	private NodeWithPosition AddNode(String near, String name, String textureName) {
		CometHeatNode front = new CometHeatNode(TexturePool.get(textureName), name);
		GraphNode back = new GraphNode(name);

		FlipNode lemur = new FlipNode(front, back);

		Position posit;
		Position coord;
		NodeWithPosition nearnode = nodesByName.get(near);
		if (nearnode == null) {
			// Place it in the middle
			posit = FindPosition(nodesByName.size());
			coord = CoordinateByPosition(posit);
		} else {
			// Place it close to the specified node
			int current = 0;
			while (true) {
				posit = FindPosition(current);
				coord = CoordinateByPosition(posit);
				coord.x += nearnode.coo.x;
				coord.y += nearnode.coo.y;
				posit = PositionByCoordinate(coord);

				if (nodesByPosition.get(coord) != null)
					current++;
				else
					break;
			}
		}
		NodeWithPosition k = new NodeWithPosition(lemur, coord, posit);

		// System.out.println("Node " + name + " placed in coords : " + coord.x
		// + ", " + coord.y);

		nodesByName.put(name, k);
		nodesl.add(k);
		nodesByPosition.put(coord, k);

		return k;
	}

	public NodeWithPosition FindClickedNode(int x, int y) {
		// Optimised version
		Position p = new Position(x, y);
		Position c = CoordinateByPosition(p);

		NodeWithPosition node = nodesByPosition.get(c);
		return node;

		/*
		 * for (NodeWithPosition n : nodesByName.values()) { Position pos =
		 * n.pos; double distance = Math.sqrt (Math.pow(pos.x - x, 2) +
		 * Math.pow(pos.y - y, 2)); if (distance < base-10) return n; } return
		 * null;
		 */
	}

	public double ZoomOn() {
		double screenratio = (1.0 * width) / height;
		if (screenratio < Math.sqrt(3.0)) {
			return (base * Math.sqrt(3.0)) / width;
		}
		return (1.0 * base) / height;
	}
}
