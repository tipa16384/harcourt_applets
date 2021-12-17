import java.awt.*;
import java.applet.*;
import java.lang.reflect.Method;
import java.util.*;

public class Graph extends Component
{
	final String yLabel, xLabel;	// labels for axes; used to form graph title
	
	static public final int USE_CONTENT_COLOR = 0x0001;
	static public final int GRAPH_BORDER      = 0x0002;
	static public final int SUPPRESS_X_ZERO   = 0x0004;
	static public final int SUPPRESS_X_TICKS  = 0x0008;
	static public final int CENTER_X_LABEL	  = 0x0010;
	static public final int DRAW_Y_GRID		  = 0x0020;
	static public final int SUPPRESS_Y_ZERO   = 0x0040;
	static public final int SUPPRESS_Y_TICKS  = 0x0080;

	static public final int NORMAL    = SUPPRESS_X_ZERO |
										SUPPRESS_X_TICKS |
										SUPPRESS_Y_ZERO |
										SUPPRESS_Y_TICKS;
										
	static public final int ALTERNATE = USE_CONTENT_COLOR |
										GRAPH_BORDER |
										CENTER_X_LABEL |
										DRAW_Y_GRID |
										SUPPRESS_Y_ZERO;
	
	int style = NORMAL;
	
	
	double maxX;					// max X value
	double maxY;					// max Y value
	double minX;					// min X value
	double minY;					// min Y value
	double xTick, yTick;			// tickmark spacing
	
	final int gap = 10;				// gap between edge and graph
	int topgap;						// additional gap at the top
	int leftgap;					// additional gap at the left
	int rightgap;					// additional gap at right
	int bottomgap;					// additional gap at the bottom

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
	
	Color contentColor = null;

	Vector functionEntries = new Vector();
	Vector pointEntries = new Vector();
	Vector xMarks = new Vector();
	Vector yMarks = new Vector();
	
	boolean showTitle = true;
	
	public Graph( String yLabel, String xLabel,
					double maxY, double maxX,
					double minY, double minX,
					double yTick, double xTick )
	{
		tickFont = GraphInfo.fontPlain;
		tickFontMetrics = getFontMetrics(tickFont);
		
		microFont = new Font("SansSerif",Font.PLAIN,9);
		microFontMetrics = getFontMetrics(microFont);
		
		font = GraphInfo.fontBigBold;
		fm = getFontMetrics(font);
		setFont( font );
		
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		setXMinMax( minX, maxX );
		setYMinMax( minY, maxY );
		this.yTick = yTick;
		this.xTick = xTick;
			
		showTitle();
		
		title = yLabel + " vs. " + xLabel;
	}
	
	public void setStyle( int style )
	{
		this.style = style;
		setYMinMax( minY, maxY );
		repaint();
	}
	
	public void setContentColor( Color color )
	{
		contentColor = color;
		repaint();
	}
	
	public void showTitle()
	{
		showTitle = true;
		topgap = fm.getHeight();
		repaint();
	}
	
	public void hideTitle()
	{
		showTitle = false;
		topgap = 0;
		repaint();
	}
	
	public void setYMinMax( double yMin, double yMax )
	{
		this.maxY = yMax;
		this.minY = yMin;
		
		if( minX < 0.0 )
		{
			leftgap = tickFontMetrics.stringWidth(Integer.toString((int)minX))/2;
			System.out.println("minX="+minX+" leftgap="+leftgap);
		}
		else
			leftgap = tickFontMetrics.stringWidth(Integer.toString((int)yMax))+tickSize;

		if( minY < 0.0 )
			bottomgap = tickFontMetrics.getHeight()/2-1;
		else if( xLabel != null )
			bottomgap = 2*tickFontMetrics.getHeight();
		else
			bottomgap = tickFontMetrics.getHeight();

		repaint();
	}
	
	public void setXMinMax( double xMin, double xMax )
	{
		this.minX = xMin;
		this.maxX = xMax;
		rightgap = tickFontMetrics.stringWidth(Integer.toString((int)xMax))/2;
		
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
		this.xFrom = Math.max(minX,xFrom);
		this.xTo = Math.min(maxX,xTo);
		repaint();
	}
	
	public int addFunction( Function func )
	{
		return addFunction( func, getForeground() );
	}
	
	public int addFunction( Function func, Color color )
	{
		return addFunction( func, color, null );
	}
	
	public int addFunction( Function func, Color color, String label )
	{
		int idx = functionEntries.size();
		functionEntries.addElement( new FunctionEntry( func, color, label ) );
		repaint();
		return idx;
	}
	
	public void removeAllFunctions()
	{
		functionEntries.removeAllElements();
		repaint();
	}
	
	public void removeFunction( int idx )
	{
		try
		{
			functionEntries.removeElementAt(idx);
			repaint();
		}
		
		catch( Exception e )
		{
		}
	}

	public void getPoints( Vector xv, Vector yv )
	{
		xv.removeAllElements();
		yv.removeAllElements();
		
		int len = pointEntries.size();
		
		for( int i=0; i<len; ++i )
		{
			PointEntry pe = (PointEntry) pointEntries.elementAt(i);
			
			xv.addElement( new Double(pe.getX()) );
			yv.addElement( new Double(pe.getY()) );
		}
	}

	public int addPoint( double x, double y )
	{
		return addPoint( x, y, getForeground() );
	}

	public int addPoint( double x, double y, Color color )
	{
		return addPoint( x, y, color, null );
	}

	public int addPoint( double x, double y, Color color, String label )
	{
		return addPoint( new PointEntry(x,y,color,label) );
	}
	
	public int addPoint( Object o )
	{
		int idx = pointEntries.size();
		
		if( o instanceof PointEntry )
		{
			pointEntries.addElement( o );
			repaint();
		}
		
		return idx;
	}
	
	public void removePoint( int idx )
	{
		try
		{
			pointEntries.removeElementAt(idx);
			repaint();
		}
		
		catch( Exception e )
		{
		}
	}
	
	public void removeAllPoints()
	{
		pointEntries.removeAllElements();
		repaint();
	}
	
	public void removeAllXMarks()
	{
		xMarks.removeAllElements();
		repaint();
	}
	
	public void removeAllYMarks()
	{
		yMarks.removeAllElements();
		repaint();
	}
	
	public void removeXMark( int i )
	{
		if( i < xMarks.size() )
		{
			xMarks.removeElementAt(i);
			repaint();
		}
	}
	
	public void removeYMark( int i )
	{
		if( i < yMarks.size() )
		{
			yMarks.removeElementAt(i);
			repaint();
		}
	}
	
	public void replaceXMark( int idx, double val, String label, Color color )
	{
		replaceXMark(idx,val,label,color,false);
	}
	
	public void replaceXMark( int idx, double val, String label, Color color, boolean mirror )
	{
		Mark mark = new Mark(val,label,color,mirror);
		
		if( idx < xMarks.size() )
		{
			xMarks.setElementAt(mark,idx);
			repaint();
		}
		
		else
		{
			xMarks.addElement(mark);
		}
	}
	
	public int setXMark( double val, String label, Color color )
	{
		int idx = xMarks.size();
		xMarks.addElement( new Mark(val,label,color) );
		return idx;
	}

	public int setXMark( double val, String label, Color color, boolean mirror )
	{
		int idx = xMarks.size();
		xMarks.addElement( new Mark(val,label,color,mirror) );
		return idx;
	}

	public int setYMark( double val, String label, Color color )
	{
		int idx = yMarks.size();
		yMarks.addElement( new Mark(val,label,color) );
		return idx;
	}

	public int setYMark( double val, String label, Color color, boolean mirror )
	{
		int idx = yMarks.size();
		yMarks.addElement( new Mark(val,label,color,mirror) );
		return idx;
	}
	
	boolean active( int flag )
	{
		return (style & flag) == flag;
	}

	public Color getContentColor()
	{
		return (contentColor == null) ? getBackground() : contentColor;
	}

	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		Color gridColor = Color.black;
				
		g.setColor( getBackground() );
		g.fillRect( 0, 0, dim.width, dim.height );

		int ixRange = dim.width-rightgap-leftgap;
		int iyRange = dim.height-gap-topgap-bottomgap;
		
		xRange = (double)ixRange;
		yRange = (double)iyRange;
		
		// draw the axes
		{
			g.setFont( tickFont );
			
			double x, y;
			int ix, iy;
			int t;
			
			ix = getIX(0.0);
			iy = getIY(0.0);
			
			if( active(USE_CONTENT_COLOR) )
			{
				g.setColor( getContentColor() );
				g.fillRect( getIX(minX), getIY(maxY), ixRange, iyRange );
			}

			if( active(DRAW_Y_GRID) )
			{
				gridColor = getContentColor().darker();
			}
			
			g.setColor( getForeground() );
			
			t = tickFontMetrics.getAscent();

			// x ticks
			
			iy = getIY(0.0);

			for( x = minX; x <= maxX; x += xTick )
			{
				//System.out.println(x);
				if( !active(SUPPRESS_X_ZERO) || (x != 0.0) )
				{
					ix = getIX(x);
					g.drawLine( ix, iy-tickSize, ix, iy+tickSize );
					
					if( !active(SUPPRESS_X_TICKS) || (x == minX || x == maxX) )
					{
						String s = Integer.toString((int)x);
						g.drawString( s, ix-tickFontMetrics.stringWidth(s)/2, iy+tickSize+t );
					}
				}
			}
			
			// x marks
			
			{
				int len = xMarks.size();
				int i;
				
				for( i=0; i<len; ++i )
				{
					Mark mark = (Mark) xMarks.elementAt(i);
					ix = getIX(mark.getValue());
					g.setColor(mark.getColor());
					g.drawLine( ix, iy-tickSize, ix, iy+tickSize );

					String s = mark.getLabel();
					int ty = (mark.isMirrored()) ? iy-tickSize-2 : iy+tickSize+t;
					g.drawString( s, ix-tickFontMetrics.stringWidth(s)/2, ty );
				}
			}
			
			g.setColor( getForeground() );
			
			// x label
			if( xLabel != null )
			{
				int tw = tickFontMetrics.stringWidth(xLabel);
				int sx;
				
				if( active(CENTER_X_LABEL) )
				{
					ix = getIX(minX);
					sx = ix + (ixRange-tw)/2;
				}
				else
					sx = getIX(maxX)-tw;
					
				g.drawString( xLabel, sx, iy+tickFontMetrics.getAscent()+tickFontMetrics.getHeight() );
			}
			
			// y ticks
			
			ix = getIX(0.0);
			
			for( y = minY; y <= maxY; y += yTick )
			{
				if( !active(SUPPRESS_Y_ZERO) || (y != 0.0) )
				{
					iy = getIY(y);
					
					if( active(DRAW_Y_GRID) )
					{
						g.setColor( gridColor );
						g.drawLine( getIX(minX), iy, getIX(maxX), iy );

						g.setColor( getForeground() );
						g.drawLine( ix-tickSize, iy, ix+tickSize, iy );
					}
					
					else
						g.drawLine( ix-tickSize, iy, ix+tickSize, iy );
					
					if( !active(SUPPRESS_Y_TICKS) || (y == minY || y == maxY) )
					{
						g.setColor( getForeground() );
						String s = Integer.toString((int)y);
						g.drawString( s, ix-tickSize-tickFontMetrics.stringWidth(s), iy+t/2-1 );
					}
				}
			}
			
			// y marks
			
			{
				int len = yMarks.size();
				int i;
				
				for( i=0; i<len; ++i )
				{
					Mark mark = (Mark) yMarks.elementAt(i);
					iy = getIY(mark.getValue());
					g.setColor(mark.getColor());
					g.drawLine( ix-tickSize, iy, ix+tickSize, iy );

					String s = Integer.toString((int)mark.getValue());
					g.drawString( s, ix-tickSize-tickFontMetrics.stringWidth(s), iy+t/2-1 );
				}
			}
			
			g.setColor( getForeground() );
			
			// y label
			if( yLabel != null )
			{
				iy = getIY(maxY - maxY % yTick);
				ix = getIX(0.0)+tickSize+2;
				g.drawString( yLabel, ix, iy+tickFontMetrics.getAscent()/2-1 );
			}
		}

		// draw the other function
		{
			int len = functionEntries.size();
			
			for( int ifunc=0; ifunc < len; ++ifunc )
			{
				FunctionEntry fe = (FunctionEntry) functionEntries.elementAt(ifunc);
				Function func = fe.getFunction();
				Color functionColor = fe.getColor();
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
		
		// draw the points
		{
			int len = pointEntries.size();
			
			for( int ipoint=0; ipoint<len; ++ipoint )
			{
				PointEntry pe = (PointEntry) pointEntries.elementAt(ipoint);
				double x = pe.getX();
				double y = pe.getY();
				Color color = pe.getColor();
				String label = pe.getLabel();
				
				final int dotSize = 5;
				
				if( x >= xFrom && x <= xTo && y >= minY && y <= maxY )
				{
					g.setColor( color );
					int ix = getIX(x);
					int iy = getIY(y);
					
					g.fillOval(ix-dotSize/2,iy-dotSize/2,dotSize,dotSize);
				}
			}
		}
		
		// draw the title
		if( showTitle )
		{
			int tw = fm.stringWidth(title);
			g.setColor( getForeground() );
			g.setFont( font );
			g.drawString(title,5,fm.getAscent());
		}
		
		// draw the key
		{
			int len = functionEntries.size();
			int y = gap;
			int yh = tickFontMetrics.getHeight();
			final int lineLen = 32;
			
			g.setFont( tickFont );
			
			for( int ilab=0; ilab < len; ++ilab )
			{
				FunctionEntry fe = (FunctionEntry) functionEntries.elementAt(ilab);
				
				String label = fe.getLabel();
				
				if( label != null )
				{
					g.setColor( fe.getColor() );
					
					int lw = tickFontMetrics.stringWidth(label);
					
					g.fillRect(dim.width-gap-lineLen,y+yh/2-1,lineLen,2);
					
					g.drawString( label, dim.width-gap-lineLen-lw-5, y+tickFontMetrics.getAscent()-1 );
					
					y += yh;
				}
			}

			if( active(GRAPH_BORDER) )
			{
				g.setColor( getForeground() );
				
				int bx = getIX(maxX);
				int bx0 = getIX(minX);
				int bxz = getIX(0);
				
				int by = getIY(maxY);
				int by0 = getIY(minY);
				int byz = getIY(0);
				
				g.drawLine( bx, by, bx, by0 );
				g.drawLine( bx, by0, bx0, by0 );
				g.drawLine( bx0, by, bx0, by0 );
				g.drawLine( bx, by, bx0, by );
			}

			// draw the axes
			{
				g.setColor( getForeground() );
				
				int bx = getIX(maxX);
				int bx0 = getIX(minX);
				int bxz = getIX(0);
				
				int by = getIY(maxY);
				int by0 = getIY(minY);
				int byz = getIY(0);
				
				g.drawLine( bxz, by, bxz, by0 );
				g.drawLine( bx, byz, bx0, byz );
			}		
		}
	}

	public double getX( int ix )
	{
		double x;

		ix -= leftgap;
		x = minX + ( (double)ix * (maxX-minX) ) / ((double)xRange);
		return x;
	}

	public double getY( int iy )
	{
		double y;

		iy -= gap+topgap;
		y = maxY + ( (double)iy * (minY-maxY) ) / ((double)yRange);
		return y;
	}

	public int getIX( double x )
	{
		int ix = (int) Math.rint( ((x-minX)*xRange)/(maxX-minX) );
		return ix+leftgap;
	}

	public int getIY( double y )
	{
		int iy = (int) Math.rint( ((y-maxY)*yRange)/(minY-maxY) );
		return iy+gap+topgap;
	}

	static public PointEntry makePoint( double x, double y, Color color, String label )
	{
		return new PointEntry( x, y, color, label );
	}
}

class FunctionEntry
{
	Color color;
	Function func;
	String label;

	public FunctionEntry( Function func, Color color, String label )
	{
		this.func = func;
		this.color = color;
		this.label = label;
	}
	
	public Function getFunction()
	{
		return func;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public String getLabel()
	{
		return label;
	}
}

class Mark
{
	Color color;
	double val;
	String label;
	boolean mirror = false;

	public Mark( double val, String label, Color color )
	{
		this(val,label,color,false);
	}
	
	public Mark( double val, String label, Color color, boolean mirror )
	{
		this.val = val;
		this.color = color;
		this.label = label;
		this.mirror = mirror;
	}
	
	public double getValue()
	{
		return val;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public boolean isMirrored()
	{
		return mirror;
	}
}

class PointEntry
{
	Color color;
	String label;
	double x, y;
	
	public PointEntry( double x, double y, Color color, String label )
	{
		this.x = x;
		this.y = y;
		this.color = color;
		this.label = label;
	}

	public Color getColor()
	{
		return color;
	}
	
	public String getLabel()
	{
		return label;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}
	
	public String toString()
	{
		return getClass().getName()+"["+x+","+y+","+color+","+label+"]";
	}
}
