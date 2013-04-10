package netvis.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import netvis.data.model.Packet;

/**
 * A time controlled DataController that supports play/pausing the data
 * throughput, and also speeding it up or slowing it down.
 * Speed-up and slow-down double or halve speeds at the moment.
 * Internally, the class counts a number of user-controlled seconds since
 * startup which are then used by the DataFeeder to determine how many
 * packets to feed.
 */

public class TimeControlDataController extends DataController {
	
	protected TimeControlDataFeeder timeControlled;
	protected Timer secondsTimer;
	protected int seconds;  // internal time
	
	public TimeControlDataController(TimeControlDataFeeder dataFeeder, int updateInterval) {
		super(dataFeeder, updateInterval);
		this.timeControlled = dataFeeder;
		seconds = 0;
		ActionListener secondCounter = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		          seconds++;
		      }
		  };
		secondsTimer = new Timer(1000, secondCounter);
		secondsTimer.start();
	}
	
	/**
	 * The action of the timer.
	 */
	@Override
	protected List<Packet> getNewPackets() {
		return timeControlled.getNewPackets(seconds);
	}
	
	/**
	 * Time Control methods
	 */
	
	public void play() {
		timer.start();
		secondsTimer.start();
	}
	
	public void pause() {
		timer.stop();
		secondsTimer.stop();
	}
	
	public void slower() {
		secondsTimer.setDelay((int) (secondsTimer.getDelay() * 2));
	}
	
	public void faster() {
		secondsTimer.setDelay((int) (secondsTimer.getDelay() * 0.5));
	}
	
	public void skipToEnd() {
		secondsTimer.stop();
		seconds = 14 * 24 * 60 * 60; // assume CSVs don't capture more than 14 days
		// TODO this will break some statistics, in particular averages over time
	}
	
	// TODO support restarting playback? but this is task of DataFeeder..
	
	/**
	 * Getters
	 */
	
	public boolean isPlaying() {
		return secondsTimer.isRunning();
	}
	
	public int getTime() {
		return seconds;
	}

}
