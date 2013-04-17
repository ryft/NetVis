package netvis.visualizations.comets;

public class Position
{
	public Position(int posx, int posy) {
		x = posx;
		y = posy;
	}
	
	public Position(double posx, double posy) {
		x = (int) Math.round(posx);
		y = (int) Math.round(posy);
	}
	
	public int x;
	public int y;
}
