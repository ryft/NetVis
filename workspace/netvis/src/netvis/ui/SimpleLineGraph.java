package netvis.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
 
/**
 * JPanel displaying a very simple line graph for quick visual reference only.
 * Heavily adapted from code at http://www.coderanch.com/how-to/java/PlotTest
 */
@SuppressWarnings("serial")
public class SimpleLineGraph extends JPanel
{
	
	/**
	 * Create a simple line graph with a list of integer data points.
	 * It is assumed that for the x-axis, data points are uniformly distributed.
	 * @param data	the data points to plot on the y-axis
	 */
	public SimpleLineGraph(List<Integer> data) {
		this.data = data;
	}
	
    int padding = 2;
    boolean drawLine = true;
    boolean drawDots = true;
    int dotRadius = 2;

    // The y-coordinates of the points to be drawn; the x coordinates are evenly spaced
    protected List<Integer> data;
 
    @Override
    protected void paintComponent (Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        g2.drawLine(padding, padding, padding, h-padding);
        g2.drawLine(padding, h-padding, w-padding, h-padding);
        
        double xScale = (w - 2*padding) / (data.size() + 1);
        double dataMax = Collections.max(data);
        double maxValue = dataMax*1.2 ;
        double yScale = (h - 2*padding) / maxValue;
        
        // The origin location
        int x0 = padding;
        int y0 = h-padding;

        // Draw connecting line
        if (drawLine)
        {
            for (int j = 0; j < data.size()-1; j++)
            {
                int x1 = x0 + (int)(xScale * (j+1));
                int y1 = y0 - (int)(yScale * data.get(j));
                int x2 = x0 + (int)(xScale * (j+2));
                int y2 = y0 - (int)(yScale * data.get(j+1));
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        // Draw the points as little circles in red
        if (drawDots)
        {
            g2.setPaint(Color.red);
            for (int j = 0; j < data.size(); j++)
            {
                int x = x0 + (int)(xScale * (j+1));
                int y = y0 - (int)(yScale * data.get(j));
                g2.fillOval(x-dotRadius, y-dotRadius, 2*dotRadius, 2*dotRadius);
            }
        }
    }
}
