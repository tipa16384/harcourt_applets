package Wired;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.beans.*;

public class Oscilloscope extends Component implements TimeSource
{
	static final Color labelColor = GraphInfo.VARIABLE_COLOR;
	static final Color axisColor = Color.black;
	static final Color slideColor = new Color(153,153,153);

	final double maxAmplitude = 1000.0;
	
	String xAxisLabel = "t";
	String yAxisLabel = "I";

	Vector traces = new Vector();

	long updateInterval = 1000;	// msec between updates
	long time = 0;
	int numSamples = 50;

	static ActionListener listeners = null;
		
	double frequency = 50.0;
	double amplitude = 1.0;
	double extraAmplitude = 1.0;

	// location of the slider in absolute pixels

	static int sliderPosition = 0;
	static int xZero = 0;
	
	public Oscilloscope()
	{
		Font f;
		FontMetrics fm;
		
		f = new Font("Serif",Font.PLAIN,12);
		fm = getFontMetrics(f);
		xZero = fm.charWidth('M')+2;

		setFont( f );
		enableEvents(AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
	
	public void processMouseEvent( MouseEvent e )
	{
		mouseDroppings(e);
		super.processMouseEvent(e);
	}
	
	public void processMouseMotionEvent( MouseEvent e )
	{
		mouseDroppings(e);
		super.processMouseMotionEvent(e);
	}
	
	public void setSliderPosition( int n )
	{
		sliderPosition = n;
		retrace();
	}
	
	public int getSliderPosition()
	{
		return sliderPosition;
	}
	
	void mouseDroppings( MouseEvent e )
	{
		int id = e.getID();
		int x = e.getX();
		
		switch( id )
		{
			case MouseEvent.MOUSE_PRESSED:
			case MouseEvent.MOUSE_DRAGGED:
				setSliderPosition(x);
				
				if( x < xZero )
					broadcast( new ActionEvent(this,0,Wired.INSTRUCTIONS) );
				else
					broadcast( new ActionEvent(this,0,Wired.POPUP) );
				
				retrace();
				break;
		}
	}
	
	void retrace()
	{
		broadcast( new ActionEvent(this,0,Wired.RETRACE) );
	}
	
	public void addTrace( CircuitElement ce )
	{
		if( !traces.contains(ce) )
		{
			traces.addElement( ce );
			repaint();
		}
	}
	
	public void removeTrace( CircuitElement ce )
	{
		traces.removeElement(ce);
		repaint();
	}
	
	public void removeAllTraces()
	{
		traces.removeAllElements();
		repaint();
	}
	
	public void setXAxisLabel( String s )
	{
		xAxisLabel = s;
		repaint();
	}
	
	public void setYAxisLabel( String s )
	{
		yAxisLabel = s;
		repaint();
	}
	
	public void setExtraAmplitude( double amp )
	{
		extraAmplitude = amp;
		retrace();
	}
	
	public void setAmplitude( double amp )
	{
		amplitude = amp;
		retrace();
	}
	
	public double getAmplitude()
	{
		return amplitude;
	}
	
	public void setFrequency( double freq )
	{
		frequency = freq;
		retrace();
	}
	
	public double getFrequency()
	{
		return frequency;
	}

	public long getTime()
	{
		return getTime(sliderPosition-xZero);
	}
	
	public long getTime( int pos )
	{
		int width = getSize().width - xZero;
		
		// get t0 in microseconds.
		long t0 = ((long)pos*1000*1000)/((long)width);
		
		t0 = (long) ((double)t0 / frequency);
		
		return t0;
	}
	
	public void paint( Graphics g )
	{
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		Dimension size = getSize();
		int yZero;
		int mwidth = fm.charWidth('M');
		int xLabWid = fm.stringWidth(xAxisLabel)+5;
		int lag = 3;
		int xMax = size.width - xLabWid - lag;
		
		setFont( f );
		
		yZero = size.height / 2;
		
		g.setColor( labelColor );
		g.drawString( yAxisLabel, mwidth - fm.stringWidth(yAxisLabel), fm.getAscent() );
		g.drawString( xAxisLabel, xMax+lag, yZero+fm.getAscent() );
		
		g.setColor( axisColor );
		g.drawLine( xZero, 0, xZero, size.height );
		g.drawLine( xZero, yZero, xMax, yZero );

		if( sliderPosition >= xZero && sliderPosition < xMax )
		{
			g.setColor( slideColor );
			g.drawLine( sliderPosition, 0, sliderPosition, size.height );
		}
		
		int len = traces.size();
		int el, i, x;
		
		for( el = 0; el < len; ++el )
		{
			CircuitElement ce = (CircuitElement) traces.elementAt(el);
			
			if( !ce.showTrace() )
				continue;
			
			int oldy = 0;
			int oldx = 0;
							
			g.setColor( ce.getColor() );
			
			int width = xMax - xZero;
			
			for( i = 0, x=xZero; i <= width; ++i, ++x )
			{
				// get t0 in microseconds.
				long t0 = getTime(i);
				int y = yVal( ce, t0, size.height );

				if( x == sliderPosition )
				{
					g.fillOval( x-1, y-1, 4, 4 );
				}
				
				if( i > 0 )
				{
					g.drawLine( oldx, oldy, x, y );
				}
				
				oldy = y;
				oldx = x;
			}
		}
	}
	
	public int yVal( CircuitElement ce, long t0, int h )
	{
		double iamp = maxAmplitude/(amplitude*extraAmplitude);
		double v = ce.getValue( t0 );
		double t2 = ((double)h * (iamp - v))/(2.0 * iamp);
		return (int)Math.round(t2);
	}
	
	public int xVal( CircuitElement ce, long t0, int w )
	{
		double iamp = maxAmplitude/(amplitude*extraAmplitude);
		double v = -ce.getPhase( t0 );
		double t2 = ((double)w * (iamp - v))/(2.0 * iamp);
		return (int)Math.round(t2);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension( 115, 65 );
	}

	// handle the action listener for detecting state changes.
	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}
	
	public void broadcast( ActionEvent e )
	{
		if( listeners != null )
		{
			listeners.actionPerformed(e);
		}
	}
}
