package netvis.visualizations.comets;

import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import javax.media.opengl.GL2;

import netvis.visualizations.helperlib.Helper;

public class Map {

	// IPs mapped to nodes
	HashMap<String, Node> nodes;
	HashMap<String, Texture> textures;
	
	// Basic size of the node
	int base = 500;
	
	// Connections to be drawn
	HashMap<String, Connection> connections;
	
	Helper help;
	
	Random rand;
	
	int ii = 0;
	
	public Map (int width, int height)
	{
		help = new Helper (width, height);
		rand = new Random();
		
		nodes = new HashMap<String, Node> ();
		
		textures = new HashMap<String, Texture> ();

		// Load all the necessary textures
		LoadTexture ("server", Map.class.getResource("resources/server.png"));
	}
	
	public void LoadTexture (String name, URL resource) {

		textures.put(name, new Texture (resource));
	}

	
	public void DrawEverything(GL2 gl) {
		
		help.DrawCircle(0, 0, 10, gl);
		for (Node i : nodes.values())
			help.DrawNode(i, gl);
	}
	
	public void StepNodes ()
	{
		for (Node i : nodes.values())
			i.StepSatelites();
	}

	public void SetSize(int width, int height) {
		help.SetSize(width, height);
	}

	public void SuggestNode(String sip, String dip) {
		// Suggests the existence of the node in the network to be displayed
		int x = 0;
		int y = 0;
		
		// Look whether the node already exists
		Node find = nodes.get(dip);
		
		if (find == null)
		{
			int s = nodes.size() + 1;
			int innerring = (int) Math.floor(Math.sqrt((s - 1/4.0) / 3.0) - 0.5);
			int outerring  = innerring + 1;
			int k = innerring;
			int shift = s - 3 * (k*k + k) - 1;
			
			// Move to the desired ring
			x += Math.sqrt(3) * base * outerring * Math.cos(Math.PI/3);
			y += Math.sqrt(3) * base * outerring * Math.sin(Math.PI/3);
			
			// Move the shift times
			double angle = 0;
			for (int i=0; i<shift; i++)
			{
				if (i % outerring == 0)
				{
					angle -= Math.PI/3;
				}
				x += Math.sqrt(3) * base * Math.cos(angle);
				y += Math.sqrt(3) * base * Math.sin(angle);

			}

			find = AddNode (x, y, dip, "server");
		} 
		// Randomized entry
		//find.AddSatelite (sip, 100, rand.nextDouble()*Math.PI);
		find.AddSatelite (sip, 100, ii * Math.PI/10);
			
		ii += 1;
	}

	private Node AddNode (int x, int y, String name, String textureName) 
	{
		Node lemur = new Node(x, y, textures.get(textureName), name);
		nodes.put (name, lemur);
		
		return lemur;
	}
}
