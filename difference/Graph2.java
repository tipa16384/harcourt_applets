import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.applet.*;
import util.DoubleBufferPanel;

public class Graph2 extends Component
{
	GraphInfo info;
	Function fLine;

	final int gap = 5;
	final int innerGap = 5;
	final int tickMark = 3;
	final int yTickOffset = -15;
	final int pointDiam = 6;
		
	// the point corresponding to the center of the graph
	double X = 0.0;
	double Y = 0.0;
	
	// at scale 1, the number of pixels per tick
	final int tickScale = 16;
	
	// the graph value corresponding to a tick
	final double tickInc = 0.5;
	
	// the X coordinate of the named point
	double P = 0.0;
	boolean showP = false;
	
	FontMetrics fm;
	
	Color vernierColor = new Color( 255, 204, 204 );
	
	public Graph2( GraphInfo info )
	{
		this.info = info;
		
		info.addPropertyChangeListener( new PropertyChangeListener()
			{
				public void propertyChange( PropertyChangeEvent e )
				{
					if( GraphInfo.new_function.equals(e.getPropertyName()) )
					{
						setCenterY(getInfo().getCurrentFunction().value(getCenterX()));
						repaint();
					}
					
					if( GraphInfo.new_scale.equals(e.getPropertyName()) )
					{
						setCenterX(getP());
						setCenterY(getInfo().getCurrentFunction().value(getCenterX()));
						repaint();
					}
				}
			} );
		
		setFont( GraphInfo.fontBigBold );
		fm = getFontMetrics( getFont() );
		
		addMouseListener( new Mouser() );
		addMouseMotionListener( new Mover() );
	}
	
	GraphInfo getInfo()
	{
		return info;
	}
	
	double getP()
	{
		return P;
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
		double oldP = P;
		P = getIX(x-getSize().width/2);
		//System.err.println("Changing P from "+oldP+" to "+P);
		showP = true;
		repaint();
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

		g.setClip( x, y, w, h );
				
		// the screen point x0,y0 corresponds to virtual point X,Y
		int x0 = x + w/2;
		int y0 = y + h/2;
		
		double lowx, highx, lowy, highy;

		g.setColor( GraphInfo.GRID_COLOR );	
		// draw the vertical ticks
		{
			double px;
						
			for( px = -tickInc; ; px -= tickInc )
			{
				int tx = x0+getX(px);
				if( tx < x )
				{
					lowx = px;
					break;
				}
			}
			
			for( px = tickInc; ; px += tickInc )
			{
				int tx = x0+getX(px);
				if( tx > (x+w) )
				{
					highx = px;
					break;
				}
			}

			for( px = lowx; px <= highx; px += tickInc )
			{
				int tx = x0 + getX(px);
				g.drawLine( tx, y, tx, y+h );
			}
		}
		
		// draw the horizontal ticks
		{
			double px;
						
			for( px = -tickInc; ; px -= tickInc )
			{
				int tx = y0+getY(px);
				if( tx > (y+h) )
				{
					lowy = px;
					break;
				}
			}
			
			for( px = tickInc; ; px += tickInc )
			{
				int tx = y0+getY(px);
				if( tx < y )
				{
					highy = px;
					break;
				}
			}

			for( px = lowy; px <= highy; px += tickInc )
			{
				int tx = y0 + getY(px);
				g.drawLine( x, tx, x+w, tx );
			}
		}
		
		g.setColor( Color.black );

		// draw the axes
		g.drawLine( x0+getX(0.0), y, x0+getX(0.0), y+h );		
		g.drawLine( x, y0+getY(0.0), x+w, y0+getY(0.0) );
		
		// draw the 'P' marker line
		if( showP )
		{
			g.setColor( vernierColor );
			
			int tx = x0 + getX(P);
			g.drawLine( tx, y, tx, y+h );
			int ty = y0 + getY( info.getCurrentFunction().value(P) );
			g.drawLine( x, ty, x+w, ty );
		}
		
		g.setColor( Color.blue );
		
		// draw the graph
		{
			Function func = info.getCurrentFunction();
			//System.out.println( "Func is "+func );
			for( int tx = x; tx <= x+w; ++tx )
			{
				int vy1 = y0 + getY(func.value(getIX(tx-x0)));
				int vy2 = y0 + getY(func.value(getIX(tx-x0+1)));
				g.drawLine( tx, vy1, tx+1, vy2 );
			}
		}
		
		// draw the point 'P'
		if( showP )
		{
			g.setColor( Color.red );

			int tx = x0 + getX(P);
			int ty = y0 + getY( info.getCurrentFunction().value(P) );
			g.fillOval(tx-pointDiam/2,ty-pointDiam/2,pointDiam,pointDiam);
		}
		
		// draw scale information
		{
			String s = info.getScale()+"x";
			int height = fm.getHeight();
			int width = fm.stringWidth(s);
			g.drawString( s, x, y+h-height );
		}
	}
	
	double getCenterX()
	{
		return (info.getScale()<=1) ? 0.0 : X;
	}
	
	double getCenterY()
	{
		return (info.getScale()<=1) ? 0.0 : Y;
	}
	
	int getX( double xval )
	{
		double dist = xval - getCenterX();
		double tdx = ((double)(info.getScale()*tickScale)*dist)/tickInc;
		
		return (int)Math.rint(tdx);
	}
	
	int getY( double yval )
	{
		double dist = getCenterY() - yval;
		double tdy = ((double)(info.getScale()*tickScale)*dist)/tickInc;
		
		return (int)Math.rint(tdy);
	}
	
	double getIX( int ix )
	{
		double dx = (double)ix;
		dx = dx * tickInc / ((double)(info.getScale()*tickScale));
		return dx + getCenterX();
	}
	
	double getIY( int iy )
	{
		double dy = (double) iy;
		dy = dy * tickInc / ((double)(info.getScale()*tickScale));
		return getCenterY() - dy;
	}
}
