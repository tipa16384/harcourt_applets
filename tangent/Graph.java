import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.applet.*;
import util.DoubleBufferPanel;

public class Graph extends Component
{
	GraphInfo info;
	Function fLine;
	Function currentFunction;
		
	final int gap = 5;
	final int innerGap = 5;
	final int tickMark = 3;
	final int yTickOffset = -15;
	final double tickVal = 0.5;
	final int pointDiam = 5;
	final double noP = -100.0;
	int tick = 1;
	int centerX = 20;
	int centerY = 20;
	
	final int tickOutOfBounds = 10;
	final int graphOutOfBounds = 10;
	final int lineOutOfBounds = 1000;
	final double infiniteSlope = 100.0;
	
	final String pString = "P";
	double P = -0.5;
	boolean showP = true;
	
	double slope = 1.0;
	boolean showLine = true;

	FontMetrics fm;
	
	public Graph( GraphInfo info )
	{
		this.info = info;
		
		info.addPropertyChangeListener( new PropertyChangeListener()
			{
				public void propertyChange( PropertyChangeEvent e )
				{
					if( GraphInfo.new_function.equals(e.getPropertyName()) )
					{
						repaint();
					}
					
					if( GraphInfo.new_scale.equals(e.getPropertyName()) )
					{
						setFocus();
						setTick();
						repaint();
					}
				}
			} );
		
		setFocus();
		
		setFont( GraphInfo.fontPlainSmall );
		fm = getFontMetrics( getFont() );
		
		fLine = new LineFunc();
		
		addMouseListener( new Mouser() );
		addMouseMotionListener( new Mover() );
	}
	
	void setFocus()
	{
		int scale = info.getScale();
		
		if( scale == 1 )
			centerX = centerY = 0;
		else
		{
			centerX = -(int)((P*(double)tick)/tickVal)*scale;
			centerY = -ifunc2( P, 0, tick )*scale;
		}
	}
	
	void updateSlope( int x, int y )
	{
		Dimension dim = getSize();
		
		int y0 = ifunc2( P, dim.height/2, tick );
		int x0 = dim.width/2 + (int)((P*(double)tick)/tickVal);
		int dx = x - x0;
		int dy = y0 - y;
		
		//System.err.print("dx="+dx+" dy="+dy);
		
		if( Math.abs(dx) <= 1 )
			slope = infiniteSlope;
		else
			slope = (double)dy / (double)dx;
		
		//System.err.println(" slope="+slope);
		
		repaint();
	}

	class Mover extends MouseMotionAdapter
	{
	    public void mouseDragged(MouseEvent e)
	    {
    		updatePOrLine(e.getX(),e.getY());
	    }
	}

	class Mouser extends MouseAdapter
	{
    	public void mouseClicked(MouseEvent e)
    	{
    		updatePOrLine(e.getX(),e.getY());
    	}
    	
    	public void mousePressed(MouseEvent e)
    	{
    		updatePOrLine(e.getX(),e.getY());
    	}
	}
	
	void updatePOrLine( int x, int y )
	{
		x -= centerX;
		y -= centerY;
		
		double ip = whichP( x, y );
		
		if( ip == noP )
			updateSlope( x, y );
		else
		{
			P = ip;
			repaint();
		}
	}
	
	double whichP( int x, int y )
	{
		double ip;
		
		if( info.getScale() == 1 )
			for( ip=-1.0; ip <= 1.0; ip += 0.5 )
			{
				int xp = getPX(ip) - pointDiam/2;
				int yp = getPY(ip) - pointDiam/2;
				
				if( x >= xp && x <= (xp+pointDiam) &&
					y >= yp && y <= (yp+pointDiam) )
					return ip;
			}
		
		return noP;
	}
	
	public void setBounds( int x, int y, int w, int h )
	{
		super.setBounds(x,y,w,h);
		setTick();
	}
	
	void setTick()
	{
		Dimension size = getSize();

		int minAxis = Math.min(size.width,size.height);
		tick = (info.getScale()*minAxis)/8;
	}
	
	private void focusin( Graphics g )
	{
		g.translate(centerX,centerY);
	}
	
	private void focusout( Graphics g )
	{
		g.translate(0,0);
	}
	
	public void paint( Graphics g )
	{
		super.paint( g );
		
		g.setFont( getFont() );
		
		Dimension dim = getSize();
		
		int x, y, w, h;
		
		x = y = gap;
		w = dim.width-2*gap;
		h = dim.height-2*gap;
		
		g.setColor( GraphInfo.AXIS_COLOR );
		g.drawRect( x, y, w, h );
		
		x += innerGap;
		y += innerGap;
		w-=2*innerGap;
		h-=2*innerGap;
		
		int x0 = x+w/2;
		int y0 = y+h/2;
		
		focusout(g);
		g.setClip(x,y,w,h);
		focusin(g);
		
		g.setColor( GraphInfo.GRID_COLOR );
		
		int x1, y1;
		
		for( x1=(w/2)%tick-tickOutOfBounds*tick; x1 < w+tickOutOfBounds*tick; x1 += tick )
//			g.drawLine( x+x1, y, x+x1, y+h );
			g.drawLine( x+x1, -lineOutOfBounds, x+x1, lineOutOfBounds );
		
		for( y1=(h/2)%tick-tickOutOfBounds*tick; y1 < h+tickOutOfBounds*tick; y1 += tick )
//			g.drawLine( x, y+y1, x+w, y+y1 );
			g.drawLine( -lineOutOfBounds, y+y1, lineOutOfBounds, y+y1 );
		
		// draw the graph
		g.setColor( GraphInfo.NEGATIVE_COLOR );
//		g.setClip(x,y,w,h);
		
		for( x1=x0-graphOutOfBounds*tick; x1<x0+graphOutOfBounds*tick; ++x1 )
//		for( x1=x0-dim.width/2; x1<x0+dim.width/2; ++x1 )
		{
			int y2 = ifunc1(x1,x0,y0,tick);
			int y3 = ifunc1(x1+1,x0,y0,tick);
			
			g.drawLine(x1,y2,x1+1,y3);
		}

		//focusout(g);
		//g.setClip(null);
		g.setColor( Color.black );
		
		// y-axis
		
		g.drawLine( x0, 0, x0, dim.height );
		for( y1=(h/2)%tick-tickOutOfBounds*tick; y1 < h+tickOutOfBounds*tick; y1 += tick )
		{
			int y2 = y1+y;
			double markVal = (double)((y0-y2)/tick) * tickVal;
			
			//System.out.println("y0="+y0+" y2="+y2+" v="+markVal);
			
			g.drawLine( x0-tickMark, y2, x0+tickMark, y2 );
			
			if( y0 != y2 )
			{
				String s = Double.toString(markVal);
				int sw = fm.stringWidth(s);
				int xm = x0 - sw/2 + yTickOffset;
				int ym = y2 + fm.getAscent()/2;
				g.drawString( s, xm, ym );
			}
		}
				
		// x-axis
		g.drawLine( 0, y0, dim.width, y0 );
		for( x1=(w/2)%tick-tickOutOfBounds*tick; x1 < w+tickOutOfBounds*tick; x1 += tick )
		{
			int x2 = x1+x;
			double markVal = (double)((x0-x2)/tick) * -tickVal;

			//System.out.println("x0="+x0+" x2="+x2+" v="+markVal);
			
			g.drawLine( x2, y0-tickMark, x2, y0+tickMark );

			if( x0 != x2 )
			{
				String s = Double.toString(markVal);
				int sw = fm.stringWidth(s);
				int xm = x2 - sw/2;
				int ym = y0 + fm.getAscent() + tickMark;
				g.drawString( s, xm, ym );
			}
	
			if( Math.abs(markVal) <= 1.0 )
			{
				int y2 = getPY(markVal);
				
				g.fillOval(x2-pointDiam/2,y2-pointDiam/2,pointDiam,pointDiam);
			}
		}

		// P
		if( showP )
		{
			Font font = GraphInfo.fontBigBold;
			g.setColor( GraphInfo.POSITIVE_COLOR );
			g.setFont( font );
			
			int y2 = getPY(P);
			int x2 = getPX(P);
			
			//int y2 = ifunc2( P, y0, tick );
			//int x2 = x0 + (int)((P*(double)tick)/tickVal);
			
			x2 -= getFontMetrics(font).stringWidth(pString);
			g.drawString( pString, x2, y2 );
		}

		if( showLine )
		{			
			int xa = -lineOutOfBounds;
			int ya = ifunc1(fLine,xa,x0,y0,tick);
			
			int xz = lineOutOfBounds;
			int yz = ifunc1(fLine,xz,x0,y0,tick);
			
			//g.setClip(x,y,w,h);
			g.drawLine(xa,ya,xz,yz);
		}
	}
	
	int getPY( double p )
	{
		Dimension dim = getSize();
		int y0 = dim.height/2;
		
		return ifunc2( p, y0, tick );
	}
	
	int getPX( double p )
	{
		Dimension dim = getSize();
		int x0 = dim.width/2;
		
		return x0 + (int)((p*(double)tick)/tickVal);
	}
	
	int ifunc1( int x, int x0, int y0, int tick )
	{
		return ifunc1( info.currentFunction, x, x0, y0, tick );
	}
	
	int ifunc1( Function func, int x, int x0, int y0, int tick )
	{
		double xPrime = ((double)(x0-x) * -tickVal)/(double)tick;
		return ifunc2( func, xPrime, y0, tick );
	}
	
	int ifunc2( double xPrime, int y0, int tick )
	{
		return ifunc2( info.currentFunction, xPrime, y0, tick );
	}
	
	int ifunc2( Function func, double xPrime, int y0, int tick )
	{
		double yPrime = func.value( xPrime );
		int y = y0 - (int)((yPrime * (double)tick)/tickVal);
		return y;
	}
	
	class LineFunc implements Function
	{
		public double value( double x )
		{
			double yPrime = info.currentFunction.value(P);
			return yPrime + (x-P)*slope;
		}
	}
}
