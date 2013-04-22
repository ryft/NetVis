package netvis.visualizations.comets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;

import netvis.visualizations.gameengine.Node;
import netvis.visualizations.gameengine.Painter;
import netvis.visualizations.gameengine.Position;
import netvis.visualizations.gameengine.TexturePool;

public class Map {

	class NodeWithPosition
	{
		public NodeWithPosition (Node a, Position b) {
			node = a;
			pos  = b;
		}
		public Node node;
		public Position pos;
	}

	// IPs mapped to nodes
	HashMap<String, NodeWithPosition> nodes;
	
	List<NodeWithPosition> nodesl;
	
	// Basic size of the node
	int base = 400;
	int width;
	int height;
	
	// Connections to be drawn
	HashMap<String, Connection> connections;
	
	Random rand;
	
	public Map (int w, int h)
	{
		width = w;
		height = h;
		
		rand = new Random();
		
		nodes = new HashMap<String, NodeWithPosition> ();
		nodesl= new ArrayList<NodeWithPosition> ();

		// Load all the necessary textures
		TexturePool.LoadTexture ("server", 	Map.class.getResource("resources/server.png"));
		TexturePool.LoadTexture ("basic", 	Map.class.getResource("resources/basic.png"));
	}
	
	public void DrawEverything(GL2 gl) {
		Painter.DrawGrid (base, gl);
		for (NodeWithPosition i : nodes.values())
		{
			int x = i.pos.x;
			int y = i.pos.y;
	
			gl.glPushMatrix();
				gl.glTranslated (x, y, 0.0);
				i.node.Draw (base, new MapPainter(), gl);
			gl.glPopMatrix();
		}
	}
	
	public void StepAnimation (long time)
	{
		for (NodeWithPosition i : nodes.values())
			i.node.UpdateAnimation(time);
	}

	public void SetSize(int w, int h, GL2 gl) {
		width = w;
		height = h;
	}

	public void SuggestNode(String sip, String dip) {
		// Suggests the existence of the node in the network to be displayed
		
		// Look whether the node already exists
		NodeWithPosition find = nodes.get(dip);
		
		if (find == null)
		{
			find = AddNode (dip, "basic");
		} 

		find.node.UpdateWithData (sip);
	}
	
	public void SortNodes ()
	{
		Collections.sort (nodesl, new Comparator<NodeWithPosition> () {

			@Override
			public int compare(NodeWithPosition n1, NodeWithPosition n2) {
				return n2.node.Priority() - n1.node.Priority();
			}
			
		});
		
		// Now display them in the order
		for (int i=0; i<nodesl.size(); i++)
		{
			NodeWithPosition n = nodesl.get(i);
			
			n.pos = this.FindPosition(i);
		}
	}
	
	private Position FindPosition (int num) 
	{
		double x = 0.0;
		double y = 0.0;

		int s = num + 1;
		int innerring = (int) Math.floor(Math.sqrt((s - 1/4.0) / 3.0) - 0.5);
		int outerring  = innerring + 1;
		int k = innerring;
		int shift = s - 3 * (k*k + k) - 1;
		
		// Move to the desired ring
		x += Math.sqrt(3) * base * outerring * Math.cos(Math.PI/3);
		y += Math.sqrt(3) * base * outerring * Math.sin(Math.PI/3);
		
		// Move the shift times
		double angle = 0;
		if (innerring % 2 == 1)
			x -= Math.sqrt(3) * base;

		for (int i=0 - (innerring % 2); i<shift - (innerring % 2); i++)
		{
			if (i % outerring == 0)
			{
				angle -= Math.PI/3;
			}
			x += Math.sqrt(3) * base * Math.cos(angle);
			y += Math.sqrt(3) * base * Math.sin(angle);
		}
		
		return new Position (x,y);
	}

	private NodeWithPosition AddNode (String name, String textureName) 
	{
		CometHeatNode front = new CometHeatNode (TexturePool.get(textureName), name);
		GraphNode 	  back  = new GraphNode (name);
		
		FlipNode lemur = new FlipNode (front, back);
		
		Position p = FindPosition (nodes.size());
		NodeWithPosition k = new NodeWithPosition (lemur, p);
		
		nodes.put (name, k);
		nodesl.add (k);
		
		return k;
	}
	
	public NodeWithPosition FindClickedNode (int x, int y)
	{
		for (NodeWithPosition n : nodes.values())
		{
			Position pos = n.pos;
			double distance = Math.sqrt (Math.pow(pos.x - x, 2) + Math.pow(pos.y - y, 2));
			if (distance < base-10)
				return n;
		}
		return null;
	}

	public double ZoomOn () {
		double screenratio = (1.0 * width) / height;
		if (screenratio < Math.sqrt(3.0))
		{
			return (base * Math.sqrt(3.0)) / width;
		}
		return (1.0 * base) / height;
	}
}
