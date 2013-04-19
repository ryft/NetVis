package netvis.visualizations.gameengine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.Timer;

public class ValueAnimator {
	
	final int LINEAR = 1;
	final int CUBIC  = 2;
	final int SINE   = 3;
	
	// Animation style
	protected int style;

	protected double value;
	
	protected double start;
	protected double goal;
	protected double finalGoal; public double getGoal () {return finalGoal;};
	
	Queue<Double> pendingGoals;
	Queue<Long> pendingDurations;

	
	protected long duration;
	protected long timeleft;
	protected long laststamp;
	
	Timer anim;
	
	public ValueAnimator (double v)
	{
		style = SINE;
		
		value = v;
		start = v;
		goal = v;
		finalGoal = v;
		
		pendingGoals = new ConcurrentLinkedQueue<Double> ();
		pendingDurations = new ConcurrentLinkedQueue<Long> ();
		
		ActionListener inner = new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent evnt) {
				// Find current stamp
				Date d = new Date();
				
				long timepassed = d.getTime() - laststamp;
				if (timepassed > timeleft)
					timepassed = timeleft;

				timeleft -= timepassed;
				laststamp = d.getTime();
				
				if (style == CUBIC)
					ValueAnimator.this.CubicProg();
				if (style == LINEAR)
					ValueAnimator.this.LinearProg();
				if (style == SINE)
					ValueAnimator.this.SineProg();
				
				if (timeleft <= 0)
				{
					value = goal;
					
					anim.stop();
					ValueAnimator.this.ContinueAnimation();
				}
			}
		};
		
		anim = new Timer (10, inner);
	}
	
	public void SetStyle (int st)
	{
		style = st;
	}
	
	public void MoveTo (double g, long d)
	{
		if (d == 0)
		{
			anim.stop();
			
			pendingGoals.clear();
			pendingDurations.clear();
			
			goal  = g;
			finalGoal = g;
			value = g;
			return;
		}
		
		finalGoal = g;
		pendingGoals.add(g);
		pendingDurations.add(d);
		
		if (!anim.isRunning())
		{
			ContinueAnimation();
		};
	}
	
	private void ContinueAnimation()
	{
		if (pendingGoals.size() != 0)
		{
			double newgoal   = pendingGoals.remove();
			long newduration = pendingDurations.remove();
			
			// Start is the current value
			start = value;
			goal  = newgoal;
			
			duration = newduration;
			timeleft = newduration;
			laststamp = (new Date()).getTime();
			
			anim.start();
		}
	}
		
	public void LinearProg()
	{
		value = (goal * (duration - timeleft) + start * timeleft)  / duration;
	}
	
	public void CubicProg()
	{
		// Slope at 0
		double slopeb = 3.0/2.0;
		double slopea = (-slopeb/3.0);

		double x = 2.0 * (duration - timeleft) / duration - 1.0;
		double y = slopea * Math.pow(x, 3) + slopeb * x;
		
		value = start + (goal - start) * (y + 1.0)/2;
	}
	
	public void SineProg()
	{
		double x = 2.0 * (duration - timeleft) / duration - 1.0;
		double y = Math.sin (Math.PI/2.0 * x);
		
		value = start + (goal - start) * (y + 1.0)/2;
	}
	
	public double toDouble()
	{
		return value;
	}
}
