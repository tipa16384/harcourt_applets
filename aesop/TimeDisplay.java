import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class TimeDisplay extends Component
						 implements Adjustable
{
	static final String [] imageFiles =
		{
			"0.gif",
			"1.gif",
			"2.gif",
			"3.gif",
			"4.gif",
			"5.gif",
			"6.gif",
			"7.gif",
			"8.gif",
			"9.gif",
			"displaybackground.gif"
		};
	
	Image [] images = new Image[imageFiles.length];

	int val = 0;

	public TimeDisplay()
	{
		readImages();
	}
	
	public Dimension getPreferredSize()
	{
		Image image = images[10];
		
		return new Dimension(image.getWidth(this),image.getHeight(this));
	}
		
	void readImages()
	{
		try
		{
			MediaTracker mt = new MediaTracker(this);
			
			for( int i=0; i<imageFiles.length; ++i )
			{
				Image image = Utility.getImage(this,imageFiles[i]);
				images[i] = image;
				mt.addImage(image,0);
			}
			
			mt.waitForAll();
		}
		
		catch( Exception e )
		{
			System.err.println("An error occurred while loading images - "+e);
		}
	}

	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		Dimension pdim = getPreferredSize();
		
		int x0 = (dim.width-pdim.width)/2;
		int y0 = (dim.height-pdim.height)/2;
		
		g.drawImage( images[10], x0, y0, this );
		
		int dx = images[0].getWidth(this)+2;
		x0 += 16;
		y0 += 2;
		
		drawDigit( g, val/100, x0, y0 );
		drawDigit( g, (val%100)/10, x0+dx, y0 );
		drawDigit( g, val%10, x0+2*dx, y0 );
	}

	void drawDigit( Graphics g, int idx, int x, int y )
	{
		g.drawImage( images[idx], x, y, this );
	}

    /**
     * Gets the orientation of the adjustable object.
     */
    public int getOrientation()
    {
    	return Adjustable.HORIZONTAL;
    }

    /**
     * Sets the minimum value of the adjustable object.
     * @param min the minimum value
     */
    public void setMinimum(int min)
    {
    }

    /**
     * Gets the minimum value of the adjustable object.
     */
    public int getMinimum()
    {
    	return 0;
    }

    /**
     * Sets the maximum value of the adjustable object.
     * @param max the maximum value
     */
    public void setMaximum(int max)
    {
    }

    /**
     * Gets the maximum value of the adjustable object.
     */
    public int getMaximum()
    {
    	return 999;
    }

    /**
     * Sets the unit value increment for the adjustable object.
     * @param u the unit increment
     */
    public void setUnitIncrement(int u)
    {
    }

    /**
     * Gets the unit value increment for the adjustable object.
     */
    public int getUnitIncrement()
    {
    	return 1;
    }

    /**
     * Sets the block value increment for the adjustable object.
     * @param b the block increment
     */
    public void setBlockIncrement(int b)
    {
    }

    /**
     * Gets the block value increment for the adjustable object.
     */
    public int getBlockIncrement()
    {
    	return 100;
    }

    /**
     * Sets the length of the proportionl indicator of the
     * adjustable object.
     * @param v the length of the indicator
     */
    public void setVisibleAmount(int v)
    {
    }

    /**
     * Gets the length of the propertional indicator.
     */
    public int getVisibleAmount()
    {
    	return 100;
    }

    /**
     * Sets the current value of the adjustable object. This
     * value must be within the range defined by the minimum and
     * maximum values for this object.
     * @param v the current value 
     */
    public void setValue(int v)
    {
    	val = Math.min(100,Math.max(v,0));
    	repaint();
    }

    /**
     * Gets the current value of the adjustable object.
     */
    public int getValue()
    {
    	return val;
    }

    /**
     * Add a listener to recieve adjustment events when the value of
     * the adjustable object changes.
     * @param l the listener to recieve events
     * @see AdjustmentEvent
     */    
    public void addAdjustmentListener(AdjustmentListener l)
    {
    }

    /**
     * Removes an adjustment listener.
     * @param l the listener being removed
     * @see AdjustmentEvent
     */ 
    public void removeAdjustmentListener(AdjustmentListener l)
    {
    }
}
