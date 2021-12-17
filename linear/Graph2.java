import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.applet.*;
import util.DoubleBufferPanel;
import java.util.Random;

public class Graph2 extends Component implements PropertyChangeListener
{
	GraphInfo info;
	Function fLine;

	final int labelGap = 12;
	final int gap = 0;
	final int innerGap = 2;
	final int tickMark = 3;
	final int yTickOffset = -15;
	final int pointDiam = 6;

	final int xGap = 16;
	final int yGap = 16;
		
	// the point corresponding to the origin of the graph
	double X = 0.0;
	double Y = 0.0;
	
	// at scale 1, the number of pixels per tick
	int xTickScale = 10;
	int yTickScale = 10;
	
	// the graph value corresponding to a tick
	final double xTickInc = 1.0;
	final double yTickInc = 1.0;
	
	FontMetrics fm;
	
	final Color vernierColor = new Color( 255, 204, 204 );
	final Color darkGridColor = new Color( 153, 255, 255 );
	final Color selectionColor = new Color( 0, 104, 104 );
	final Color functionColor = new Color( 0, 204, 153 );
	final Color darkAxisColor = new Color( 104, 104, 104 );

	final double [] xPoints = { 0.250, 0.640, 1.000, 1.960, 4.000 };
	final double [] mGuess =  { 0.550, 1.300, 1.000, 2.000, 3.250 };
	
	Random rand = new Random();
	
	public Graph2( GraphInfo info )
	{
		this.info = info;
		
		info.addPropertyChangeListener( this );
		
		info.setP(xPoints[2]);
		
		setFont( GraphInfo.fontBigBold );
		fm = getFontMetrics( getFont() );
		
		addMouseListener( new Mouser() );
		//addMouseMotionListener( new Mover() );
	}
	
	public void propertyChange( PropertyChangeEvent e )
	{
		if( GraphInfo.new_function.equals(e.getPropertyName()) )
		{
			setCenterY(getInfo().getCurrentFunction().value(getCenterX()));
			repaint();
		}
		
		if( GraphInfo.new_scale.equals(e.getPropertyName()) )
		{
			setCenterX(info.getP());
			setCenterY(getInfo().getCurrentFunction().value(getCenterX()));
			repaint();
		}
		
		if( GraphInfo.new_slope.equals(e.getPropertyName()) )
		{
			makeComputerGuess();
			repaint();
		}
		
		if( GraphInfo.repaint.equals(e.getPropertyName()) )
		{
			repaint();
		}
		
		if( GraphInfo.select.equals(e.getPropertyName()) )
		{
			info.reset();
			repaint();
		}
	}

	void makeComputerGuess()
	{
		double error;
		double m;
		double xp = info.getP();
		
		for( int i=0; i<xPoints.length; ++i )
		{
			if( xPoints[i] == xp )
			{
				info.setComputerSlope( mGuess[i] );
				break;
			}
		}
	}
	
	GraphInfo getInfo()
	{
		return info;
	}
	
	void setCenterX( double newX )
	{
		X = newX;
		repaint();
	}
	
	void setCenterY( double newY )
	{
		Y = newY;
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
		double PY;
		Dimension dim = getSize();
		int x0 = gap+innerGap+xGap+labelGap;
		int y0 = dim.height-gap-innerGap-yGap;
		Function func = info.getCurrentFunction();
		
		for( int i=0; i<xPoints.length; ++i )
		{
			double tx = xPoints[i];
			double ty = func.value(tx);
			int ix = x0 + getX(tx);
			int iy = y0 + getY(ty);
			
			if( Math.abs(ix-x) <= pointDiam/2 &&
				Math.abs(iy-y) <= pointDiam/2 )
			{
				info.setP(tx);
				break;
			}
		}
	}
	
	public void paint( Graphics g )
	{
		super.paint( g );
		
		g.setFont( getFont() );
		FontMetrics gm = getFontMetrics(getFont());
				
		Dimension dim = getSize();
		
		int x, y, w, h;
		
		x = y = gap;
		w = dim.width-2*gap;
		h = dim.height-2*gap;
		
		x += innerGap;
		y += innerGap;
		w-=2*innerGap;
		h-=2*innerGap;

		g.setClip( x, y, w, h );
				
		// the screen point x0,y0 corresponds to virtual point X,Y
		int x0 = x + xGap + labelGap;
		int y0 = y + h - yGap;
		
		xTickScale = (2*(w-xGap-labelGap))/9;
		yTickScale = (2*(h-yGap))/18;
				
		double lowx, highx, lowy, highy;

		g.setColor( darkAxisColor );	
		// draw the vertical ticks
		if( true )
		{
			double px;
						
			for( px = -xTickInc; ; px -= xTickInc )
			{
				int tx = x0+getX(px);
				if( tx < x )
				{
					lowx = px;
					break;
				}
			}
			
			for( px = xTickInc; ; px += xTickInc )
			{
				int tx = x0+getX(px);
				if( tx > (x+w) )
				{
					highx = px;
					break;
				}
			}

			for( px = Math.max(lowx,0.0); px <= highx; px += xTickInc )
			{
				int tx = x0 + getX(px);				
				int tsy = y0 + getY(0.0);
				
				g.drawLine( tx, tsy, tx, y+h );
				
				if( px > 0 )
				{
					String s = Integer.toString((int)px);
					g.drawString( s, tx-fm.stringWidth(s), tsy+fm.getAscent() );
				}
			}
		}
		
		// draw the horizontal ticks
		if( true )
		{
			double px;
						
			for( px = -xTickInc; ; px -= xTickInc )
			{
				int tx = y0+getY(px);
				if( tx > (y+h) )
				{
					lowy = px;
					break;
				}
			}
			
			for( px = xTickInc; ; px += xTickInc )
			{
				int tx = y0+getY(px);
				if( tx < y )
				{
					highy = px;
					break;
				}
			}

			lowy = Math.max(lowy,0.0);

			for( px = lowy; px <= highy; px += xTickInc )
			{
				int ty = y0 + getY(px);
				int tsx = x0 + getX(0.0);
				g.drawLine( x+labelGap, ty, tsx, ty );
				if( px > 0 )
				{
					String s = Integer.toString((int)px);
					g.drawString(s, x+labelGap-fm.stringWidth(s)-2, ty+fm.getAscent()/2 );
				}
			}
		}
		
		g.setColor( darkAxisColor );

		// draw the axes
		g.drawLine( x0+getX(0.0), y, x0+getX(0.0), y+h );		
		g.drawLine( x+labelGap, y0+getY(0.0), x+w, y0+getY(0.0) );
		
		// draw the 'P' marker line
		g.setColor( vernierColor );
		
		if( false )
		{
			int tx = x0 + getX(info.getP());
			g.drawLine( tx, y, tx, y+h );
			int ty = y0 + getY( info.getCurrentFunction().value(info.getP()) );
			g.drawLine( x, ty, x+w, ty );
		}
		
		g.setColor( functionColor );
		
		// draw the graph
		{
			Function func = info.getCurrentFunction();
			//System.out.println( "Func is "+func );
			for( int tx = x; tx <= x+w; ++tx )
			{
				double xval = getIX(tx-x0);
				if( xval >= 0.0 )
				{
					int vy1 = y0 + getY(func.value(xval));
					int vy2 = y0 + getY(func.value(getIX(tx-x0+1)));
					g.drawLine( tx, vy1, tx+1, vy2 );
				}
			}
		}
		
		// draw the breakout points
		g.setColor( Color.black );
		
		{
			for( int i=0; i<xPoints.length; ++i )
			{
				double dx = xPoints[i];
				
				int tx = x0 + getX(dx);
				int ty = y0 + getY(info.getCurrentFunction().value(dx));
				g.drawLine( tx, ty-pointDiam/2, tx, ty-pointDiam/2+pointDiam );
				g.drawLine( tx-pointDiam/2, ty, tx-pointDiam/2+pointDiam, ty );
			}
		}
		
		// draw the point 'P'
		g.setColor( selectionColor );
		{
			double dy = info.getCurrentFunction().value(info.getP());
			int tx = x0 + getX( info.getP() );
			int ty = y0 + getY( dy );
			g.fillOval(tx-pointDiam/2,ty-pointDiam/2,pointDiam,pointDiam);
			
			dy = Math.rint(dy*1000.0)/1000.0;
			
			String s = "("+info.getP()+","+dy+")";
			FontMetrics fm = getFontMetrics( getFont() );
			int fh = fm.getAscent();
			int sw = fm.stringWidth(s);
			int sx;
			
			if( tx > (x+w/2) )
				sx = tx - sw - pointDiam - 4;
			else
				sx = tx + pointDiam + 4;
			
			g.drawString( s, sx, ty );
		}
		
		// draw formula information
		if( false )
		{
			g.setColor( Color.gray );
			String s = "f(x)=x^3/2";
			int height = fm.getHeight();
			int width = fm.stringWidth(s);
			g.drawString( s, x+(w-width)/2, y+h );
		}
		
		// draw user & computer guess
		{
			Dimension dimmy = getSize();
			double dxoffs = 20.0;
			double m;
			double dy;
			double dx1;
			double dy1;
			double dx2;
			double dy2;
			int x1, y1;
			
			x1 = x0 + getX(0.0);
			y1 = y0 + getY(0.0);
			g.setClip( x1, 0, dimmy.width-x1-4, y1 );
			
			if( info.userSlopeVisible() )
			{
				m = info.getUserSlope();
				dy = info.getCurrentFunction().value(info.getP());
				dx1 = info.getP()-dxoffs;
				dy1 = dy-dxoffs*m;
				dx2 = info.getP()+dxoffs;
				dy2 = dy+dxoffs*m;
							
				g.setColor( Color.red );
				g.drawLine( x0+getX(dx1), y0+getY(dy1),
							x0+getX(dx2), y0+getY(dy2) );
			}

			if( info.computerSlopeVisible() )
			{
				m = info.getComputerSlope();
				dy = info.getCurrentFunction().value(info.getP());
				dx1 = info.getP()-dxoffs;
				dy1 = dy-dxoffs*m;
				dx2 = info.getP()+dxoffs;
				dy2 = dy+dxoffs*m;
				
				g.setColor( Color.blue );
				g.drawLine( x0+getX(dx1), y0+getY(dy1),
							x0+getX(dx2), y0+getY(dy2) );
			}
		}
	}
	
	double getCenterX()
	{
		return X;
	}
	
	double getCenterY()
	{
		return Y;
	}
	
	int getX( double xval )
	{
		double dist = xval - getCenterX();
		double tdx = ((double)(info.getScale()*xTickScale)*dist)/xTickInc;
		
		return (int)Math.rint(tdx);
	}
	
	int getY( double yval )
	{
		double dist = getCenterY() - yval;
		double tdy = ((double)(info.getScale()*yTickScale)*dist)/yTickInc;
		
		return (int)Math.rint(tdy);
	}
	
	double getIX( int ix )
	{
		double dx = (double)ix;
		dx = dx * xTickInc / ((double)(info.getScale()*xTickScale));
		return dx + getCenterX();
	}
	
	double getIY( int iy )
	{
		double dy = (double) iy;
		dy = dy * yTickInc / ((double)(info.getScale()*yTickScale));
		return getCenterY() - dy;
	}
}
