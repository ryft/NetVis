package netvis.visualizations.comets;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;

import javax.media.opengl.GL2;

import netvis.visualizations.helperlib.Helper;

public class Map {

	// IPs mapped to nodes
	HashMap<String, Node> nodes;
	HashMap<String, Texture> textures;
	
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
		
		// Look whether the node already exists
		Node find = nodes.get(dip);
		
		if (find == null)
		{
			find = AddNode (0, 0, dip, "server");
		} else
		{
			// Randomized entry
			//find.AddSatelite (sip, 100, rand.nextDouble()*Math.PI);
			find.AddSatelite (sip, 100, ii * Math.PI/10);
			
			ii += 1;
		}
	}

	private Node AddNode (int x, int y, String name, String textureName) 
	{
		Node lemur = new Node(x, y, textures.get(textureName));
		nodes.put(name, lemur);
		
		return lemur;
	}
}
