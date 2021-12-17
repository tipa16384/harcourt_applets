import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class GordySlider extends Component
						 implements Adjustable, MouseListener, MouseMotionListener
{
	int val=50;
	int min=0, max=100;
	int delta = 1;
	String label=null;
	String units=null;

	Vector listeners = new Vector(0);
	
	static final Color arrowColor = new Color(255,0,0);
	static final Color sliderColor = new Color(99,99,99);
	static final int arrowSize = 6;
	static final int separation = arrowSize;
	
	static final int LABEL = 0;
	static final int LEFT = 1;
	static final int RIGHT = 2;
	static final int SLIDER = 3;
	static final int LINE = 4;
	static final int VALUE = 5;
	static final int LEN = VALUE+1;

	String [] names = { "label", "left arrow", "right arrow", "slider", "line", "value" };
	
	transient int where = SLIDER;
	transient int pressed = -1;
	RawLabel labelEngine = null;
	RawLabel valueEngine = null;
	Dimension labelSize = null;
	Dimension valueSize = null;
	
	public GordySlider( String label, int min, int max, int val )
	{
		this( label, min, max, val, null );
	}
	
	public GordySlider( String label, int min, int max, int val, String units )
	{
		this.label = label;
		this.min = min;
		this.max = max;
		this.units = units;
		
		addMouseListener( this );
		addMouseMotionListener( this );
		
		if( label != null )
		{
			labelEngine = new RawLabel(label);
			labelEngine.setFont(new Font("SansSerif",Font.PLAIN,12));
			labelEngine.setForeground( sliderColor );
			labelSize = labelEngine.getMinimumSize();
		}
				
		valueEngine = new RawLabel(Label.CENTER);
		valueEngine.setFont(new Font("SansSerif",Font.PLAIN,9));
		valueEngine.setForeground( sliderColor );
		valueSize = valueEngine.getPreferredSize();
		
		setValue( val, true );
	}
	
	public static String trim( String s, int ml )
	{
		StringBuffer b = new StringBuffer();
		int len = s.length();
		int i;
		int state = 0;
		int mc = 0;
		
		if( s.indexOf('E') < 0 && (s.indexOf('.') == 1 && s.indexOf('0') == 0) )
			ml = 4;
		else
			ml = 3;
		
		for( i=0; i<len; ++i )
		{
			char c = s.charAt(i);
			
			switch( state )
			{
				case 0:
					if( c == '.' )
					{
						state = 1;
					}
					b.append(c);
					break;
				
				case 1:
					if( c == 'E' )
					{
						b.append("~+10~^");
						state = 2;
					}
					
					else if( mc++ < ml )
					{
						b.append(c);
					}
					break;
				
				case 2:
					b.append(c);
					break;
			}
		}
		
		return new String(b);
	}
	
	public Dimension getMinimumSize()
	{
		Rectangle r = getRectangle(VALUE);
		return new Dimension(100,r.y+r.height);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public Rectangle getRectangle( int state )
	{
		Dimension size = getSize();
		final int buffer=2;
		Rectangle r = null;
		
		switch( state )
		{
			case LABEL:
				{
					if( labelSize != null )
						r = new Rectangle(0,0,labelSize.width,labelSize.height);
					else
						r = new Rectangle();
				}
				break;

			case LEFT:
				{
					Rectangle r1 = getRectangle(LABEL);
					r = new Rectangle(r1.x+r1.width+buffer,0,arrowSize,arrowSize*2+1);
				}
				break;
				
			case RIGHT:
				{
					r = new Rectangle(size.width-arrowSize,0,arrowSize,arrowSize*2+1);
				}
				break;

			case LINE:
				{
					Rectangle r1 = getRectangle(LEFT);
					Rectangle r2 = getRectangle(RIGHT);
					r = new Rectangle(r1.x+r1.width+buffer,0,
							size.width-r2.width-buffer-r1.x-r1.width-buffer,arrowSize+2);
					r.x += arrowSize;
					r.width -= 2*arrowSize;
				}
				break;

			case SLIDER:
				{
					Rectangle r1 = getRectangle(LINE);
	
					int offs = ((val-min)*r1.width)/(max-min);
	
					r = new Rectangle(r1.x+offs-arrowSize,0,arrowSize*2,arrowSize);
				}
				break;
				
			case VALUE:
				{
					Rectangle r1 = getRectangle(LINE);
					r = new Rectangle(r1.x,r1.y+r1.height,r1.width,valueSize.height);
				}
				break;
		}

		//System.out.println("bounds for "+names[state]+" = "+r);

		return (r == null) ? new Rectangle() : r;		
	}
	
	private String getValueString()
	{
		String s = trim(Double.toString(val),2);
		
		if( s.length() > 0 && units != null && units.length() > 0 )
			s += " " + units;
		
		return s;
	}
	
	private void setColor( Graphics g, int which )
	{
		Color c;
		
		switch( which )
		{
			default:
				c = Color.green;
				break;
				
			case LABEL:
			case SLIDER:
			case LINE:
			case VALUE:
				c = sliderColor;
				break;
				
			case LEFT:
			case RIGHT:
				c = arrowColor;
				break;
		}
		
		if( which == pressed )
			c = c.darker();
		
		g.setColor( c );
	}
	
	public void paint( Graphics g )
	{
		Rectangle r;
		
		if( labelEngine != null )
		{
			r = getRectangle(LABEL);
			labelEngine.paint( g, r.x, r.y, false );
		}
				
		drawTriangle(g,LEFT);
		drawTriangle(g,RIGHT);
		drawTriangle(g,SLIDER);
		
		r = getRectangle(LINE);
		setColor(g,LINE);
		g.drawLine( r.x, r.y+r.height-1, r.x+r.width, r.y+r.height-1 );
		
		r = getRectangle(VALUE);

		valueEngine.setSize( r.width, valueSize.height );
		valueEngine.paint( g, r.x, r.y, false );
	}
	
	private void drawTriangle( Graphics g, int sel )
	{
		Rectangle r = getRectangle(sel);
		setColor(g,sel);
		
		int i;
		int [] xp = new int[4];
		int [] yp = new int[4];
				
		switch( sel )
		{
			case LEFT:
				xp[0] = xp[3] = r.x;
				yp[0] = yp[3] = r.y+arrowSize;
				xp[1] = r.x+r.width;
				yp[1] = r.y;
				xp[2] = r.x+r.width;
				yp[2] = r.y+r.height;
				break;
			
			case RIGHT:
				xp[0] = xp[3] = r.x;
				yp[0] = yp[3] = r.y;
				xp[1] = r.x+r.width;
				yp[1] = r.y+arrowSize;
				xp[2] = r.x;
				yp[2] = r.y+r.height;
				break;
			
			case SLIDER:
				xp[0] = xp[3] = r.x;
				yp[0] = yp[3] = r.y;
				xp[1] = r.x+r.width;
				yp[1] = r.y;
				xp[2] = r.x+arrowSize;
				yp[2] = r.y+arrowSize;
				break;
		}

		g.fillPolygon(xp,yp,4);
	}

	private int getClick(MouseEvent e)
	{
		where = -1;
		
		Point p = e.getPoint();
		for( int i=0; i<LEN; ++i )
		{
			Rectangle r = getRectangle(i);
			if( r.contains(p) )
			{
				where = i;
				break;
			}
		}
		
		return where;
	}
	
	private void jog(int mult)
	{
		setValue(val+mult);
	}

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e)
    {
    	switch( getClick(e) )
    	{
    		case LEFT: jog(-delta); break;
    		case RIGHT: jog(delta); break;
    		case LINE:
	    		{
					Rectangle r;
					
					r = getRectangle(LINE);
					
					int x = Math.min(Math.max(e.getX()-r.x/*+arrowSize*/+1,0),r.width);
					setValue(min+(x*(max-min))/r.width);
	    		}
    			break;
    	}
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e)
    {
    	pressed = getClick(e);
    	//System.out.println("Clicked on component "+names[x]);
    	repaint(pressed);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e)
    {
    	Rectangle r = getRectangle(pressed);
    	pressed = -1;
    	repaint(r.x,r.y,r.width,r.height);
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e)
    {
		if( where == SLIDER )
		{
			Rectangle r;
			
			r = getRectangle(LINE);
			
			int x = Math.min(Math.max(e.getX()-r.x/*+arrowSize*/+1,0),r.width);
			setValue(min+(x*(max-min))/r.width);
		}
    }

    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons no down).
     */
    public void mouseMoved(MouseEvent e) { }

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
    	this.min = min;
    	repaint();
    }

    /**
     * Gets the minimum value of the adjustable object.
     */
    public int getMinimum()
    {
    	return min;
    }

    /**
     * Sets the maximum value of the adjustable object.
     * @param max the maximum value
     */
    public void setMaximum(int max)
    {
    	this.max = max;
    	repaint();
    }

    /**
     * Gets the maximum value of the adjustable object.
     */
    public int getMaximum()
    {
    	return max;
    }

    /**
     * Sets the unit value increment for the adjustable object.
     * @param u the unit increment
     */
    public void setUnitIncrement(int u)
    {
    	this.delta = u;
    }

    /**
     * Gets the unit value increment for the adjustable object.
     */
    public int getUnitIncrement()
    {
    	return delta;
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
    	return 1;
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
    	return arrowSize;
    }

    /**
     * Sets the current value of the adjustable object. This
     * value must be within the range defined by the minimum and
     * maximum values for this object.
     * @param v the current value 
     */
    public void setValue(int v)
    {
    	setValue( v, false );
    }
    
    private void setValue( int v, boolean force )
    {
    	if( v < min ) v = min;
    	else if( v > max ) v = max;
    	
    	if( force || (v != val) )
    	{
    		Main applet = Main.getApplet(this);
    		
    		if( applet != null && applet.isCrippled() )
    		{
				//System.out.println("Crippled");
				val = v;
	    		repaint();
			}
			
			else
			{
				//System.out.println("Uncrippled");
				Rectangle r1 = getRectangle(SLIDER);
				val = v;
				Rectangle r2 = getRectangle(SLIDER);
				if( r1.x < r2.x )
				{
					r1 = new Rectangle(r1.x,r1.y,r2.x+r2.width-r1.x,r1.height);
				}
				else
				{
					r1 = new Rectangle(r2.x,r1.y,r1.x+r1.width-r2.x,r1.height);
				}
				//System.out.println("repaint "+r1);
				repaint(r1.x,r1.y,r1.width,r1.height);
				repaint(VALUE);
			}
			
			valueEngine.setText( getValueString() );
			
	    	broadcast();
		}
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
    	if( !listeners.contains(l) )
    	{
    		listeners.addElement(l);
    		broadcast();
    	}
    }

    /**
     * Removes an adjustment listener.
     * @param l the listener being removed
     * @see AdjustmentEvent
     */ 
    public void removeAdjustmentListener(AdjustmentListener l)
    {
    	listeners.removeElement(l);
    }
    
    private void broadcast()
    {
    	int len = listeners.size();
    	
    	if( len > 0 )
    	{
    		AdjustmentEvent ae = new AdjustmentEvent( this, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
    				AdjustmentEvent.TRACK, val );
    		for( int i=0; i<len; ++i )
    			((AdjustmentListener)listeners.elementAt(i)).adjustmentValueChanged(ae);
    	}
    }

	// repaint a specific component
	
	private void repaint( int sel )
	{
		repaint( 0, sel );
	}
	
	private void repaint( int msec, int sel )
	{
		if( sel >= 0 )
		{
			//System.out.println("Repaint("+msec+","+names[sel]+")");
			
			Rectangle r = getRectangle(sel);
			repaint( msec, r.x, r.y, r.width, r.height );
		}
	}
}
