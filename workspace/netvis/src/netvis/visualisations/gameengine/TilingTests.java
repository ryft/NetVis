package netvis.visualisations.gameengine;

import org.junit.Test;
import org.junit.Assert;

public class TilingTests {

	@Test
	public void PositionTest1 ()
	{
		Position p = Units.CoordinateByRingAndShift(5, 2, 0);
		Assert.assertEquals (p.x, 0);
		Assert.assertEquals (p.y, 9);
	}
}
