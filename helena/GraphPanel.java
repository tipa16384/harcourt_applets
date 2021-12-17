import java.awt.*;
import java.applet.*;
import java.lang.reflect.Method;
import java.util.*;
import util.DoubleBufferPanel;
import java_cup.runtime.*;

public class GraphPanel extends DoubleBufferPanel
						implements PointEventListener
{
	Main main;
	GraphInfo info;
	Graph profit, stupid;
	static int lineWidth = 2;
	
	Vector values = new Vector();
	Vector colors = new Vector();
	
	static final Color specialPoint = GraphInfo.POSITIVE_COLOR;
	static final Color regularPoint = GraphInfo.NEGATIVE_COLOR;
	static final Color axisColor = GraphInfo.FORCE_VECTOR_COLOR;
	static final Color functionColor = new Color(50,153,50);
	
	public GraphPanel( Main main, GraphInfo info )
	{
		super( new GridLayout(1,0,lineWidth,lineWidth) );
		
		this.main = main;
		this.info = info;

		profit = new ProfitGraph( new ProfitVsPrice(), "Profit", "Price", 400, 50, -400, -10, 100, 10, "cents", "cents", "2" );
		stupid = new Graph( new StupidFunction(), "Cups Sold", "Price", 70, 50, -10, -10, 10, 10, null, "cents", "1" );
		
		setBackground( Color.black );
		
		add( stupid );
		add( profit );
		
		plot( 5, specialPoint );
		plot( 15, specialPoint );
		plot( 25, specialPoint );
	}
	
	class ParsedFunction extends parser
						 implements Function
	{
		final String formula;
		
		public ParsedFunction( String formula ) throws java.lang.Exception
		{
			this.formula = formula;
			
			setScanner(new TermScanner(formula));
			
			// let's find out WHY it can't find this thing!
			
			parse();
		}
		
		public double value( double x )
		{
			Vector opcodes = getOpcodeStack();
			int len = opcodes.size();
			Stack stack = new Stack();
			
			stack.push( new Double(0) );
			
			for( int i=0; i<len; ++i )
			{
				Opcode op = (Opcode) opcodes.elementAt(i);
				
				switch( op.opcode )
				{
					case sym.VARIABLE:
						stack.push( new Double(x) );
						break;
					
					case sym.NUMBER:
						stack.push( new Double((double)op.value) );
						break;
					
					case sym.UMINUS:
						{
							Double v1 = (Double) stack.pop();
							stack.push( new Double(-v1.doubleValue()) );
						}
						break;
					
					case sym.PLUS:
						{
							Double v2 = (Double) stack.pop();
							Double v1 = (Double) stack.pop();
							stack.push( new Double(v1.doubleValue()+v2.doubleValue()) );
						}
						break;
					
					case sym.MINUS:
						{
							Double v2 = (Double) stack.pop();
							Double v1 = (Double) stack.pop();
							stack.push( new Double(v1.doubleValue()-v2.doubleValue()) );
						}
						break;
					
					case sym.TIMES:
						{
							Double v2 = (Double) stack.pop();
							Double v1 = (Double) stack.pop();
							stack.push( new Double(v1.doubleValue()*v2.doubleValue()) );
						}
						break;
					
					case sym.DIVIDE:
						{
							Double v2 = (Double) stack.pop();
							Double v1 = (Double) stack.pop();
							stack.push( new Double(v1.doubleValue()/v2.doubleValue()) );
						}
						break;
					
					default:
						System.err.println("Unknown opcode "+op.opcode);
						break;
				}
			}
			
			return ((Double)stack.pop()).doubleValue();
		}
	}
	
	public void plot( PointEvent pe )
	{
		plot( pe.getX(), regularPoint );
	}
	
	public void graph( String formula )
	{
		Function func = null;
		
		try
		{
			func = new ParsedFunction(formula);
		}
		
		catch( Exception e )
		{
		}
		
		stupid.setOtherFunc( func );
		profit.setOtherFunc( func );
	}
	
	public Insets getInsets()
	{
		return new Insets(0,lineWidth,lineWidth,lineWidth);
	}
	
	private void plot( double x, Color color )
	{
		//System.out.println("Plot "+x);
		
		values.addElement( new Double(x) );
		colors.addElement( color );
		repaint();
	}
	
	class ProfitVsPrice implements Function
	{
		public double value( double x )
		{
			return (60.0-2.0*x) * (x-6.0);
		}
	}
	
	class StupidFunction implements Function
	{
		public double value( double x )
		{
			return (60.0-2.0*x);
		}
	}
	
	class ProfitGraph extends Graph
	{
		public ProfitGraph( Function func, String yLabel, String xLabel,
									 double maxY, double maxX,
									 double minY, double minX,
									 double yTick, double xTick,
									 String yUnits, String xUnits, String graphLabel )
		{
			super(func,yLabel,xLabel,maxY,maxX,minY,minX,yTick,xTick,yUnits,xUnits,graphLabel);
		}
		
		double getOtherValue( double x )
		{
			double y = super.getOtherValue(x);
			return y * (x-6.0);
		}
	}
	
	class Graph extends Component
	{
		final Function func;			// graphed function
		Function other;					// other graphed function
		
		final String yLabel, xLabel;	// labels for axes; used to form graph title
		final String xUnits, yUnits;	// units for axes
		final String graphLabel;		// label for the graph
		
		final double maxX;				// max X value
		final double maxY;				// max Y value
		final double minX;				// min X value
		final double minY;				// min Y value
		final double xTick, yTick;		// tickmark spacing
		
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
		
		public Graph( Function func, String yLabel, String xLabel,
									 double maxY, double maxX,
									 double minY, double minX,
									 double yTick, double xTick,
									 String yUnits, String xUnits, String graphLabel )
		{
			this.func = func;
			this.xLabel = xLabel;
			this.yLabel = yLabel;
			this.maxX = maxX;
			this.maxY = maxY;
			this.minX = minX;
			this.minY = minY;
			this.yTick = yTick;
			this.xTick = xTick;
			this.xUnits = xUnits;
			this.yUnits = yUnits;
			this.graphLabel = graphLabel;
				
			setBackground( info.CONTROL_COLOR );
			setForeground( axisColor );
			
			font = info.fontBigBold;
			fm = getFontMetrics(font);
			topgap = fm.getHeight();
			setFont( font );
			
			tickFont = info.fontPlain;
			tickFontMetrics = getFontMetrics(tickFont);
			
			microFont = new Font("SansSerif",Font.PLAIN,9);
			microFontMetrics = getFontMetrics(microFont);
			
			title = yLabel + " vs. " + xLabel;
			
			other = null;
		}
		
		public void setOtherFunc( Function other )
		{
			this.other = other;
			repaint();
		}
		
		double getOtherValue( double x )
		{
			return other.value(x);
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
					String ts;
					
					if( xUnits == null )
						ts = xLabel;
					else
						ts = xLabel+" ("+xUnits+")";
						
					int tw = tickFontMetrics.stringWidth(ts);
					g.drawString( ts, dim.width-gap-tw, iy+tickFontMetrics.getAscent()+tickFontMetrics.getHeight() );
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
					String ts;
					
					if( yUnits == null )
						ts = yLabel;
					else
						ts = yLabel+" ("+yUnits+")";
						
					int tw = tickFontMetrics.stringWidth(ts);
					iy = getIY(maxY - maxY % yTick);
					ix = getIX(0.0)+tickSize+2;
					g.drawString( ts, ix, iy+tickFontMetrics.getAscent()/2-1 );
				}
			}

			// draw the other function
			if( other != null )
			{
				g.setColor( functionColor );
				
				for( int ix = gap+1; ix < dim.width-gap; ++ix )
				{
					double x0, x1, y0, y1;
					
					x0 = getX(ix-1);
					x1 = getX(ix);
					y0 = getOtherValue(x0);
					y1 = getOtherValue(x1);
					
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

			// draw the points
			{			
				int len = values.size();
				int i;
				
				g.setFont(microFont);

				for( i=len-1; i>=0; --i )
				{
					Double val = (Double) values.elementAt(i);
					Color col = (Color) colors.elementAt(i);
					
					double x = val.doubleValue();
					double y = func.value(x);
					
					if( x >= minX && x <= maxX && y >= minY && y <= maxY )
					{
						int ix = getIX( x );
						int iy = getIY( y );
						
						g.setColor(col);
						g.fillOval( ix - dotSize/2, iy - dotSize/2, dotSize, dotSize );
						
						if( col == specialPoint )
						{
							String s = "(" +
									   Integer.toString((int)Math.rint(x)) +
									   "," +
									   Integer.toString((int)Math.rint(y)) +
									   ")";
							
							int sw = microFontMetrics.stringWidth(s);
							
							int rx;
							
							if( x == 5.0 || x == 25.0 )
								rx = ix + dotSize + 1;
							else
								rx = ix - dotSize - sw;
							
							int ry = iy-microFontMetrics.getHeight()/2;
							int rw = sw;
							//rx -= 6/2;
							//rw += 6;
							int rh = microFontMetrics.getHeight();
							
							//g.setColor( Color.white );
							//g.fillRect( rx, ry, rw, rh );
							
							//g.setColor( col );
							//g.drawRect( rx, ry, rw, rh );
							g.drawString( s, rx+(rw-sw)/2, ry+microFontMetrics.getAscent() );
						}
					}
				}
			}
						
			// draw the title
			{
				int tw = fm.stringWidth(title);
				g.setColor( Color.black );
				g.setFont( font );
				g.drawString(title,3,fm.getAscent());
			}
			
			// draw the graph label
			if( graphLabel != null )
			{
				Font gfont = new Font("SansSerif",Font.BOLD,24);
				FontMetrics fm = getFontMetrics(gfont);
				g.setColor( info.AXIS_COLOR );
				g.setFont( gfont );
				
				int tw = fm.stringWidth(graphLabel);
				
				g.drawString(graphLabel,dim.width-tw-5,fm.getAscent());
			}
		}
	
		double getX( int ix )
		{
			double x;

			ix -= gap;
			x = minX + ( (double)ix * (maxX-minX) ) / ((double)xRange);
			return x;
		}
	
		int getIX(double x )
		{
			int ix = (int) Math.rint( ((x-minX)*xRange)/(maxX-minX) );
			return ix+gap;
		}
	
		int getIY( double y )
		{
			int iy = (int) Math.rint( ((y-maxY)*yRange)/(minY-maxY) );
			return iy+gap+topgap;
		}
	}
}
