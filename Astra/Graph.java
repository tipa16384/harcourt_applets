import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class Graph extends Component
				   implements PropertyChangeListener
{
	GraphInfo info;
	static double maxI, minI;
	Color areaColor;
	final double scalingFactor = 2.718;
	final double magFactor = 1E-1;
		
	public Graph( GraphInfo info )
	{
		this.info = info;
		info.addPropertyChangeListener(this);
		maxI = Double.NEGATIVE_INFINITY;
		minI = Double.POSITIVE_INFINITY;
//		areaColor = info.findColor( info.findBestColor(204,255,255) );
		areaColor = new Color(240,240,240);
		enableEvents( AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK );
	}
	
	public void processMouseEvent( MouseEvent me )
	{
		if( me.getID() == MouseEvent.MOUSE_PRESSED )
		{
			info.setSliderPosition(fixX(me.getX()));
		}
		
		super.processMouseEvent(me);
	}
	
	public void processMouseMotionEvent( MouseEvent me )
	{
		if( me.getID() == MouseEvent.MOUSE_DRAGGED )
		{
			info.setSliderPosition(fixX(me.getX()));
		}
		
		super.processMouseMotionEvent(me);
	}
	
	int fixX(int x)
	{
		return Math.max(0,Math.min(getSize().width-1,x));
	}
	
	void boundIntensity( double I )
	{
		boolean changed = false;
		if( I < minI )
		{
			minI = I;
			changed = true;
		}
		if( I > maxI )
		{
			maxI = I;
			changed = true;
		}
		
		if( changed )
		{
			System.out.println("Imax="+maxI+"  Imin="+minI);
		}
	}
	
	public void propertyChange( PropertyChangeEvent evt )
	{
		if( evt.getPropertyName().equals("slider") )
			repaint();
		else
			recalc();
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(200,10);
	}
	
	void recalc()
	{
		repaint(info.RESPONSIVENESS);
	}
	
	static double cmToMeter( int cm )
	{
		return (double)cm / 100.0;
	}
	
	static double nmToMeter( int nm )
	{
		return (double)nm * 1E-9;
	}
	
	static double dmmToMeter( int dmm )
	{
		return (double)dmm * 1E-4;
	}
	
	static double microToMeter( int micro )
	{
		return (double)micro * 1E-6;
	}
	
//	move this stop into initialization, and store xMax as part of info?

	double getXMax()
	{
		double lamda0 = nmToMeter((info.getMaxWavelength()+info.getMinWavelength())/2);
		double d0 = microToMeter((info.getMaxSlitSpacing()+info.getMinSlitSpacing())/2);
		double L0 = cmToMeter((info.getMaxDistanceToScreen()+info.getMinDistanceToScreen())/2);
				
		double x1 = (lamda0 * L0)/d0;
		double xMax = scalingFactor * x1;
		
		return xMax;
	}
		
//     end move

	double pixelsToX ( int pixels)
	{
		Dimension size = getSize();
		// find out the maximum spacing for the screen; "0" is considered
		// to be the minimum.

		double x;
		x = 2.0 * getXMax() * (double)(pixels-size.width/2) / (double)size.width;

		return x;			// return meters		
	}

	public double getSliderDistance()
	{
		return pixelsToX(info.getSliderPosition() );	// return meters
	}
	
	double Intensity( double x)	// returns intensity at a given position x (in meters)
	{
		double L, lamda, N, d, w, phi, delphi, theta, inter, diff;
		
		lamda = nmToMeter(info.getWavelength());
		N = (double)info.getNumberOfSlits();
		d = microToMeter(info.getSlitSpacing());
		w = microToMeter(info.getSlitWidth());
		L = cmToMeter(info.getDistanceToScreen());

//		double mI = 0.0;
		
			double sintheta = x / Math.sqrt(x*x+L*L);
			phi = (Math.PI * w * sintheta) / lamda;
			
			if( phi == 0.0 )
				diff = 1.0;
			else
				diff = Math.sin(phi)/phi;
			
			delphi = Math.PI * d * sintheta/lamda;
			if( delphi == 0.0 )
				inter = 1.0;
			else
				inter = Math.sin(N*delphi)/Math.sin(delphi)/N;
			
			double I = inter*inter*diff*diff;
			
//			mI = Math.max(I,mI);
			
			//boundIntensity(I);

//		return mI;
		  return I;
	
	}
	
	public double getIntensity()
	{
		
		double x;
		
		x = pixelsToX( info.getSliderPosition() );
		
		return Intensity( x);
	}
	
	public void paint( Graphics g )
	{
		
		Dimension size = getSize();
		double x;
		
		g.setColor( getForeground() );
				
//		int xmin = -size.width/2;
//		int xmax = xmin + size.width;
// 		now i runs from 0 to size.width, so 
// 		in plot routine need to subtract -size.width/2 to keep correct.
		
		for( int i=0; i<size.width; ++i )
		{
						
			x = pixelsToX( i );
		
			plot(g,i-size.width/2,size.width,size.height,Intensity(x));
		}
	}

	int oldy = 0;
	
	void plot( Graphics g, int x1, int w, int h, double I )
	{
		int oh = h;
		int h0 = h/8;
		h -= h0*2;
		
		int x = x1 + (w >> 1);
		int y = h - 1 - (int)((double)h*I);
		
		g.setColor( areaColor );
		g.drawLine(x,h0+y,x,h0+h);
		
		if( x > 0 )
		{
			g.setColor( getForeground() );
			g.drawLine(x-1,h0+oldy,x,h0+y);
		}
		
		oldy = y;
		
		if( x1 == 0 )
		{
			g.setColor( GraphInfo.SEPARATE_COLOR );
			g.drawLine( x, 0, x, oh );
		}

		if( x == info.getSliderPosition() )
		{
			g.setColor( Color.red );
			g.drawLine( x, 0, x, oh );
		}
	}
}
