package netvis.visualisations.gameengine;

public class Position {
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

	@Override
	public int hashCode() {
		return 1024 * 1024 * x + y;
	}

	@Override
	public boolean equals(Object t) {
		if (t == null)
			return false;

		Position that = (Position) t;
		return (this.x == that.x) && (this.y == that.y);
	}
}
