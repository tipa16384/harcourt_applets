import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.beans.*;

public class Oscilloscope extends Component implements TimeSource
{
	final static boolean debug = true;
		
	static final Color labelColor = GraphInfo.VARIABLE_COLOR;
	static final Color axisColor = Color.black;
	static final Color slideColor = new Color(153,153,153);
	
	final static boolean ClipMaxs = false;			//whether out of range values clip to min/max or just don't show

	public double maxAmplitude = 1000.0;
	
	String xAxisLabel = "t";
	String yAxisLabel = "I";
	
	int DispTypeSel = 0;			//this oscil. displays CircuitElement.VOLTAGE

	Vector traces = new Vector();

	long updateInterval = 1000;	// msec between updates
	long time = 0;
	int numSamples = 50;

	static ActionListener listeners = null;
		
	double frequency = 50.0;			//50.0;
	double amplitude = 1.0;
	double extraAmplitude = 1.0;

	// location of the slider in absolute pixels

	static int sliderPosition = -1;
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
	
	void mouseDroppings( MouseEvent e )
	{
		int id = e.getID();
		int x = e.getX();
		
		switch( id )
		{
			case MouseEvent.MOUSE_PRESSED:
			case MouseEvent.MOUSE_DRAGGED:
				setSliderPosition(x - xZero);
				
				if( x < xZero )
					broadcast( new ActionEvent(this,0,CC2.INSTRUCTIONS) );
				else
					broadcast( new ActionEvent(this,0,CC2.POPUP) );
				
				broadcast( new ActionEvent(this,0,CC2.RETRACE) );
				break;
		}
	}
	
	void retrace()
	{
		//debug("retrace called");
		//broadcast( new ActionEvent(this,0,CC2.RETRACE) );
		repaint();
	}
	
	public void addTrace( CircuitElement ce )
	{
		if( !traces.contains(ce) )
		{
			traces.addElement( ce );
			debug("addTrace("+ce+")");
			repaint();
		}
	}
	
	public void removeTrace( CircuitElement ce )
	{
		traces.removeElement(ce);
		debug("removeTrace("+ce+")");
		repaint();
	}
	
	public void removeAllTraces()
	{
		debug("removeAllTrace");
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
	
	public void setDispTypeSel( int selector )
	{
		DispTypeSel = selector;
		retrace();
	}
	
	public double getDispTypeSel()
	{
		return DispTypeSel;
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

	public void setSliderPosition( int n )
	{
		if( n >= 0 )							//don't go negitive (time) on us
			sliderPosition = n + xZero;
		else
			sliderPosition = -1;
		//debug("setSliderPosition to "+n);
		retrace();
	}
	
	public int getSliderPosition()
	{
		return (sliderPosition - xZero);
	}
	
	public double getTime()
	{
		double ret;
		
		if( (sliderPosition - xZero) > 0 )
			ret = getTime( sliderPosition - xZero );
		else
			ret = getTime( 0 );

		//debug("getTime() "+(sliderPosition-xZero)+" returns "+ret);
		return ret;
	}
	
	double minTime = 100000.0;		//for debug
	double maxTime = -100000.0;		//for debug
		
	public double getTime( int pos )
	{
		if( pos < 0 )
			debug("getTime("+pos+") is MINUS");
		
		int width = getSize().width - xZero;
		
		// get d in microseconds.
		//double d = ((long)pos*1000*1000)/((long)width);
		// get d in milliseconds.
		double d = ((double)pos*1000)/((double)width);
		
		//if( d < 0 )
		//	debug("getTime("+pos+") is MINUS  getSize()=="+getSize().width+" xZero=="+xZero);
		
		minTime = Math.min( d, minTime );
		maxTime = Math.max( d, maxTime );

		return d / frequency;
	}
	
	public void paint( Graphics g )
	{
		minTime = 100000.0;		//for debug
		maxTime = -100000.0;		//for debug
		
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		Dimension size = getSize();
		debug("paint size=="+size+" bounds=="+getBounds());
		int yZero;
		int mwidth = fm.charWidth('M');
		int xLabWid = fm.stringWidth(xAxisLabel)+5;
		int lag = 3;
		int xMax = size.width - xLabWid - lag;
		int yMax = 0;
		int yMin = size.height;
		
		setFont( f );
		
		yZero = size.height / 2;
		
		g.setColor( labelColor );
		g.drawString( yAxisLabel, mwidth - fm.stringWidth(yAxisLabel), fm.getAscent() );
		g.drawString( xAxisLabel, xMax+lag, yZero+fm.getAscent() );
		
		g.setColor( axisColor );
		g.drawLine( xZero, yMax, xZero, yMin );
		g.drawLine( xZero, yZero, xMax, yZero );

		if( sliderPosition >= xZero && sliderPosition < xMax )
		{
			g.setColor( slideColor );
			g.drawLine( sliderPosition, yMax, sliderPosition, yMin );
		}
		
		int len = traces.size();
		int el, i, x;
		int y = yMax;
		
		//debug("paint number of traces=="+len);

		for( el = 0; el < len; ++el )
		{
			CircuitElement ce = (CircuitElement) traces.elementAt(el);
			
			if( !ce.showTrace(CircuitElement.CURRENT) && !ce.showTrace(CircuitElement.VOLTAGE) )
				continue;
			
			//debug("painting "+xZero+" to "+(xMax-xZero)+" ce=="+ce);
								
			for( int trac=0 ; trac < 2 ; trac++ )
			{
				if( (trac & 1) == 1 )			//odd trac does the opposite charge/discharge
					((Circuit)ce).toggleSwitch();

				//debug("painting trac "+trac+" for ce=="+ce);

				g.setColor( ((Circuit)ce).getSwitchColor() );
				
				RawLabel taulabel;
				int tauVGap = 0;
				if( ((Circuit)ce).isCharging() )
				{
					taulabel = new RawLabel("~t~vc", Label.LEFT, Label.LEFT);
					tauVGap = -taulabel.getMinimumSize().height +2;
				}
				else
				{
					tauVGap = 0;
					taulabel = new RawLabel("~t~vd", Label.LEFT, Label.LEFT);
				}
				
				double t0;
				double oldt0 = -1.0;
	
				double tauTime = ce.getTau();			
	
				int oldx = xZero;
				int oldy = yMax;
				if( DispTypeSel == CircuitElement.VOLTAGE )
					oldy = VVal( ce, 0.0, yMin );
				else
					oldy = IVal( ce, 0.0, yMin );
				
				int width = xMax - xZero;
	
				//autorange( ce, width, yMin );
				
				for( i = 0, x=xZero; i <= width; ++i, ++x )
				{
					// get t0 in microseconds.
					//if( i < 0 )
					//	debug("paint iiiiiii is now=="+i);
					t0 = getTime(i);
					
					if( DispTypeSel == CircuitElement.VOLTAGE )
						y = VVal( ce, t0, yMin );
					else
						y = IVal( ce, t0, yMin );
	
					if( ClipMaxs || ((y >= yMax) && (y <= yMin)) )		//beware yMax is 0 and yMin is a high int
					{								//if we want clipped min/max lines
						//if( x == sliderPosition )
						//{
						//	g.fillOval( x-1, y-1, 4, 4 );
						//}
						
						if( (oldt0 < tauTime) && (t0 >= tauTime) )
						{
							g.fillOval( x-1, y-1, 4, 4 );
							taulabel.paint(g, x+2, y+tauVGap, false );
						}
					}
					else		//we don't want extra stuff draw when off oscil min/max
					{
						oldy = y;			//this should stop line drawing
						oldx = x;			// until we come back into range
					}
										
					if( (oldy != y) && (i > 0) )
					{
						g.drawLine( oldx, oldy, x, y );
						oldy = y;
						oldx = x;
					}
					
					oldt0 = t0;
				}
								//draw any left over length of line to the end
				if( (oldx != x-1) && (i > 0) )
				{
					g.drawLine( oldx, oldy, x-1, y );
				}

				if( (trac & 1) == 1 )			//odd trac does the opposite charge/discharge
					((Circuit)ce).toggleSwitch();
			}
		}
		//debug("paint minTime=="+minTime+", maxTime=="+maxTime);
	}
	
	public int VVal( CircuitElement ce, double t0, int h )
	{
		double iamp = maxAmplitude/(amplitude*extraAmplitude);
		//double iamp = amplitude/extraAmplitude;
		double v = ce.getValue( CircuitElement.VOLTAGE, t0 );
		//if( v != 0 )
		//	debug("yVal getValue() v=="+v+" iamp=="+iamp+" h=="+h);
		if( ClipMaxs )							//if we want clipped min/max lines
		{
			if( v < -iamp )
				v = -iamp;
			if( v > iamp )
				v = iamp;
		}
		double t2 = ((double)h * (iamp - v))/(2.0 * iamp);
		return (int)Math.round(t2);
	}
	
	public int IVal( CircuitElement ce, double t0, int h )
	{
						//MaxAmplitude is the max that 'I' should ever reach
						//amplitude is a variable zoom factor,
						//  allowing the graph range to be smaller
						//    therefor showing smaller 'I' within seeable range
						//extraAmplitude is a variable fudge factor
						//   it might need to be added to max/zoom rather then extra divider
		double iamp = maxAmplitude/(amplitude*extraAmplitude);
		//double iamp = amplitude/extraAmplitude;
		double I = ce.getValue( CircuitElement.CURRENT, t0 );
		if( ClipMaxs )							//if we want clipped min/max lines
		{
			if( I < -iamp )
				I = -iamp;
			else if( I > iamp )							//clip to max/min
				I = iamp;
		}				//
		double t2 = ((double)h * (iamp - I))/(2.0 * iamp);
		return (int)Math.round(t2);
	}
	
						//find and set freq and maxAmplitude and timebase
						// so that the scales create some interesting scope pictures
						//adjust maxAmplitude so that size.height is full
						//adjust frequency so that min_VVal() and max_VVal() (or IVal())
						// are from 0 to width (which is graph width)
	public void autorange( CircuitElement ce, int width, int high )
	{
		int yl, yr, tl, tr;
		int maxy = -100;
		int miny = 100000;
		double f;
		
		//yl = testVal( ce, 0, high );
		//yr = testVal( ce, width, high );
		//f = frequency * 2;
		//tr = testVal( ce, width, high );
		//while( t 
		frequency = ce.getTau() * Math.PI * 3000.0;
	}
	
	public int testVal( CircuitElement ce, int i, int high )
	{
				double t0 = getTime(i);
				
				if( DispTypeSel == CircuitElement.VOLTAGE )
					return VVal( ce, t0, high );
				else
					return IVal( ce, t0, high );
	}

	public Dimension getPreferredSize()
	{
		return new Dimension( 115, 65 );
	}

	// handle the action listener for detecting state changes.
	public void addActionListener( ActionListener l )
	{
		//debug("addActionListener "+l);
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

	static void debug( String s )
	{
		if( debug )
			System.out.println("Oscilloscope::"+s);
	}
}
