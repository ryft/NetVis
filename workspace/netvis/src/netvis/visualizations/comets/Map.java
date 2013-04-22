package netvis.visualizations.comets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL2;

import netvis.visualizations.gameengine.Painter;
import netvis.visualizations.gameengine.Position;
import netvis.visualizations.gameengine.TexturePool;

public class Map {

	// IPs mapped to nodes
	HashMap<String, Node> nodes;
	
	List<Node> nodesl;
	
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
		
		nodes = new HashMap<String, Node> ();
		nodesl= new ArrayList<Node> ();

		// Load all the necessary textures
		TexturePool.LoadTexture ("server", 	Map.class.getResource("resources/server.png"));
		TexturePool.LoadTexture ("basic", 	Map.class.getResource("resources/basic.png"));
	}
	
	public void DrawEverything(GL2 gl) {
		Painter.DrawGrid (base, gl);
		for (Node i : nodes.values())
			Painter.DrawNode (base, i, gl);
	}
	
	public void StepAnimation (long time)
	{
		for (Node i : nodes.values())
			i.StepSatelites(time);
	}

	public void SetSize(int w, int h, GL2 gl) {
		width = w;
		height = h;
	}

	public void SuggestNode(String sip, String dip) {
		// Suggests the existence of the node in the network to be displayed
		
		// Look whether the node already exists
		Node find = nodes.get(dip);
		
		if (find == null)
		{
			Position p = FindPosition (nodes.size());
			find = AddNode (p.x, p.y, dip, "basic");
		} 
		
		// Randomized entry
		//find.AddSatelite (sip, 100, rand.nextDouble()*Math.PI);
		
		// Make their tilts nicely shifted
		find.AddSatelite (sip, 100, find.getEntities().size() * Math.PI/10);
	}
	
	public void SortNodes ()
	{
		Collections.sort (nodesl, new Comparator<Node> () {

			@Override
			public int compare(Node n1, Node n2) {
				return n2.getWarning() - n1.getWarning();
			}
			
		});
		
		// Now display them in the order
		for (int i=0; i<nodesl.size(); i++)
		{
			Node n = nodesl.get(i);
			
			Position p = this.FindPosition(i);
			
			n.setCenter (p);
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

	private Node AddNode (int x, int y, String name, String textureName) 
	{
		Node lemur = new Node(x, y, TexturePool.get(textureName), name);
		nodes.put (name, lemur);
		nodesl.add (lemur);
		
		return lemur;
	}
	
	public Node FindClickedNode (int x, int y)
	{
		for (Node n : nodes.values())
		{
			double distance = Math.sqrt (Math.pow(n.center.x - x, 2) + Math.pow(n.center.y - y, 2));
			if (distance < base-10)
				return n;
		}
		return null;
	}

	public double ZoomOn(Node n) {
		double screenratio = (1.0 * width) / height;
		if (screenratio < Math.sqrt(3.0))
		{
			return (base * Math.sqrt(3.0)) / width;
		}
		return (1.0 * base) / height;
	}
}
