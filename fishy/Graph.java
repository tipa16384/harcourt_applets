import java.awt.*;
import java.applet.*;
import java.lang.reflect.Method;
import java.util.*;

public class Graph extends Component
{
	final String yLabel, xLabel;	// labels for axes; used to form graph title
	
	double maxX;					// max X value
	final double maxY;				// max Y value
	double minX;					// min X value
	final double minY;				// min Y value
	double xTick, yTick;			// tickmark spacing
	
	final int gap = 10;				// gap between edge and graph
	final int topgap;				// additional gap at the top
	final int dotSize = 5;			// size of point dot
	final int tickSize = 3;			// length of a tickmark
	
	final Font font;
	final FontMetrics fm;
	final String title;				// calculated title
	final Font tickFont;
	final FontMetrics tickFontMetrics;
	final Font microFont;
	final FontMetrics microFontMetrics;
			
	int x0=0, y0=0;
	double xRange=1.0, yRange=1.0;
	double xFrom, xTo;
	
	static final Color functionColor = new Color(50,153,50);
	static final Color specialPoint = GraphInfo.POSITIVE_COLOR;
	static final Color regularPoint = GraphInfo.NEGATIVE_COLOR;

	Vector colors = new Vector();
	Vector functions = new Vector();
	Vector labels = new Vector();
	
	public Graph( String yLabel, String xLabel,
					double maxY, double maxX,
					double minY, double minX,
					double yTick, double xTick )
	{
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		setXMinMax( minX, maxX );
		this.maxY = maxY;
		this.minY = minY;
		this.yTick = yTick;
		this.xTick = xTick;
			
		setBackground( GraphInfo.CONTROL_COLOR );
		setForeground( GraphInfo.FORCE_VECTOR_COLOR );
		
		font = GraphInfo.fontBigBold;
		fm = getFontMetrics(font);
		topgap = fm.getHeight();
		setFont( font );
		
		tickFont = GraphInfo.fontPlain;
		tickFontMetrics = getFontMetrics(tickFont);
		
		microFont = new Font("SansSerif",Font.PLAIN,9);
		microFontMetrics = getFontMetrics(microFont);
		
		title = yLabel + " vs. " + xLabel;
	}
	
	public Vector getFunctions()
	{
		return functions;
	}
	
	public void setXMinMax( double xMin, double xMax )
	{
		this.minX = xMin;
		this.maxX = xMax;
		setRenderBounds( xMin, xMax );
	}
	
	public void setTicks( double xTick, double yTick )
	{
		this.xTick = xTick;
		this.yTick = yTick;
		repaint();
	}
	
	public void setRenderBounds( double xFrom, double xTo )
	{
		this.xFrom = xFrom;
		this.xTo = xTo;
		repaint();
	}
	
	public void addFunction( Function func )
	{
		addFunction( func, getForeground() );
	}
	
	public void addFunction( Function func, Color color )
	{
		addFunction( func, color, null );
	}
	
	public void addFunction( Function func, Color color, String label )
	{
		colors.addElement( color );
		functions.addElement( func );
		labels.addElement( label );
		
		repaint();
	}
	
	public void removeAllFunctions()
	{
		colors.removeAllElements();
		functions.removeAllElements();
		labels.removeAllElements();
		repaint();
	}
	
	public void removeFunction( int idx )
	{
		if( idx < functions.size() )
		{
			colors.removeElementAt(idx);
			functions.removeElementAt(idx);
			labels.removeElementAt(idx);
			repaint();
		}
	}
	
	public Function getFunction( int idx )
	{
		if( idx < functions.size() )
		{
			return (Function) functions.elementAt(idx);
		}

		else
		{
			return null;
		}
	}
	
	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		
		g.setColor( getBackground() );
		g.fillRect( 0, 0, dim.width, dim.height );
		
		xRange = (double)(dim.width-2*gap);
		yRange = (double)(dim.height-2*gap-topgap);
		
		// draw the axes
		{
			g.setFont( tickFont );
			
			double x, y;
			int ix, iy;
			int t;
			
			g.setColor( getForeground() );
			
			ix = getIX(0.0);
			iy = getIY(0.0);
			
			g.drawLine( gap, iy, dim.width-gap, iy );
			g.drawLine( ix, gap+topgap, ix, dim.height-gap );
			
			t = tickFontMetrics.getAscent();

			// x ticks
			
			iy = getIY(0.0);

			for( x = minX; x <= maxX; x += xTick )
			{
				//System.out.println(x);
				if( x != 0.0 )
				{
					ix = getIX(x);
					g.drawLine( ix, iy-tickSize, ix, iy+tickSize );
					
					if( x == minX || x == maxX )
					{
						String s = Integer.toString((int)x);
						g.drawString( s, ix-tickFontMetrics.stringWidth(s)/2, iy+tickSize+t );
					}
				}
			}
			
			// x label
			{
				int tw = tickFontMetrics.stringWidth(xLabel);
				g.drawString( xLabel, dim.width-gap-tw, iy+tickFontMetrics.getAscent()+tickFontMetrics.getHeight() );
			}
			
			// y ticks
			
			ix = getIX(0.0);
			
			for( y = minY; y <= maxY; y += yTick )
			{
				if( y != 0.0 )
				{
					iy = getIY(y);
					g.drawLine( ix-tickSize, iy, ix+tickSize, iy );
					
					if( y == minY || y == maxY )
					{
						String s = Integer.toString((int)y);
						g.drawString( s, ix-tickSize-tickFontMetrics.stringWidth(s), iy+t/2-1 );
					}
				}
			}
			
			// y label
			{
				int tw = tickFontMetrics.stringWidth(xLabel);
				iy = getIY(maxY - maxY % yTick);
				ix = getIX(0.0)+tickSize+2;
				g.drawString( yLabel, ix, iy+tickFontMetrics.getAscent()/2-1 );
			}
		}

		// draw the other function
		{
			int len = functions.size();
			
			for( int ifunc=0; ifunc < len; ++ifunc )
			{
				Function func = (Function) functions.elementAt(ifunc);
				Color functionColor = (Color) colors.elementAt(ifunc);
				int xStart, xEnd;
				
				xStart = getIX(xFrom);
				xEnd = getIX(xTo);
				
				g.setColor( functionColor );
			
				for( int ix = xStart+1; ix <= xEnd; ++ix )
				{
					double x0, x1, y0, y1;
					
					x0 = getX(ix-1);
					x1 = getX(ix);
					
					y0 = func.value(x0);
					y1 = func.value(x1);
					
					if( x0 >= minX && x0 <= maxX &&
						x1 >= minX && x1 <= maxX &&
						y0 >= minY && y0 <= maxY &&
						y1 >= minY && y1 <= maxY )
					{
						int iy0 = getIY( y0 );
						int iy1 = getIY( y1 );
						g.drawLine( ix-1, iy0, ix, iy1 );
					}
				}
			}
		}
					
		// draw the title
		{
			int tw = fm.stringWidth(title);
			g.setColor( Color.black );
			g.setFont( font );
			g.drawString(title,5,fm.getAscent());
		}
		
		// draw the key
		{
			int len = labels.size();
			int y = gap;
			int yh = tickFontMetrics.getHeight();
			final int lineLen = 32;
			
			g.setFont( tickFont );
			
			for( int ilab=0; ilab < len; ++ilab )
			{
				String label = (String) labels.elementAt(ilab);
				
				if( label != null )
				{
					g.setColor( (Color) colors.elementAt(ilab) );
					
					int lw = tickFontMetrics.stringWidth(label);
					
					g.fillRect(dim.width-gap-lineLen,y+yh/2-1,lineLen,2);
					
					g.drawString( label, dim.width-gap-lineLen-lw-5, y+tickFontMetrics.getAscent()-1 );
					
					y += yh;
				}
			}
		}
	}

	public double getX( int ix )
	{
		double x;

		ix -= gap;
		x = minX + ( (double)ix * (maxX-minX) ) / ((double)xRange);
		return x;
	}

	public int getIX(double x )
	{
		int ix = (int) Math.rint( ((x-minX)*xRange)/(maxX-minX) );
		return ix+gap;
	}

	public int getIY( double y )
	{
		int iy = (int) Math.rint( ((y-maxY)*yRange)/(minY-maxY) );
		return iy+gap+topgap;
	}
}
