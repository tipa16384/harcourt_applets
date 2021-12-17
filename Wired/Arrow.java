package Wired;

import java.awt.*;

public abstract class Arrow
{
	static final int defaultWidth = 1;
	
	static public void drawArrow( Graphics g, int x1, int y1, int x2, int y2 )
	{
		drawArrow( g, x1, y1, x2, y2, defaultWidth );
	}
	
	static public void drawArrow( Graphics g, int x1, int y1, int x2, int y2, int width )
	{
		drawLine( g, x1, y1, x2, y2, width );
	}
	
	static public void drawLine( Graphics g, int x1, int y1, int x2, int y2 )
	{
		drawLine( g, x1, y1, x2, y2, defaultWidth );
	}

	static public void drawLine( Graphics g, int x1, int y1, int x2, int y2, int width )
	{
		drawLine( g, x1, y1, x2, y2, width, 0, 0 );
	}

	static public void drawLine( Graphics g, int x1, int y1, int x2, int y2, int width, int cap, int capwidth )
	{
		if( width <= 0 || (x1 == x2 && y1 == y2) ) return;
		
		if( cap != 0 && capwidth != 0 )
		{
			drawCap( g, x1, y1, x2, y2, cap, capwidth );
		}

		if( width == 1 )
		{
			g.drawLine( x1, y1, x2, y2 );
		}
		
		else
		{
			int dx = x2-x1;
			int dy = y2-y1;
			
			if( dx == 0 )
			{
				if( dy < 0 ){ int t=y1; y1=y2; y2=t; }
				g.fillRect( x1-width/2, y1, width, Math.abs(dy) );
			}
			
			else if( dy == 0 )
			{
				if( dx < 0 ){ int t=x1; x1=x2; x2=t; }
				g.fillRect( x1, y1-width/2, Math.abs(dx), width );
			}
			
			else
			{
				double tdx, tdy;
				double ddx, ddy;
				double scale;
				double linelen;
				
				ddx = (double) dy;
				ddy = (double) -dx;
				linelen = Math.sqrt(ddx*ddx+ddy*ddy);
				
				scale = (double) width / linelen;
				tdx = ddx * scale;
				tdy = ddy * scale;
				
				int idx = (int)Math.round(tdx);
				int idy = (int)Math.round(tdy);
				
				int [] xs = new int[4];
				int [] ys = new int[4];
				
				xs[0] = x1 - (int)Math.round(tdx/2);
				ys[0] = y1 - (int)Math.round(tdy/2);
				
				xs[1] = xs[0] + idx;
				ys[1] = ys[0] + idy;
				
				xs[2] = xs[1] + dx;
				ys[2] = ys[1] + dy;
				
				xs[3] = xs[2] - idx;
				ys[3] = ys[2] - idy;
				
				g.fillPolygon( xs, ys, 4 );
			}
		}
	}

	static public void drawCap( Graphics g, int ix1, int iy1, int ix2, int iy2, int cap, int capwidth )
	{
		double dx0 = (double)(ix2-ix1);
		double dy0 = (double)(iy2-iy1);
		double lcap = (double)cap;
		double scale;
		
		scale = lcap / Math.sqrt(dx0*dx0+dy0*dy0);
		
		// kick out if the arrowhead is going to be longer than the line.
		
		if( scale >= 1.0 ) return;
		
		double tdx0 = dx0 * scale;
		double tdy0 = dy0 * scale;
		
		scale = (double)capwidth / lcap;
		double tdx = tdy0 * scale;
		double tdy = -tdx0 * scale;

		int [] xs = new int[3];
		int [] ys = new int[3];
		
		xs[0] = (int)Math.round((double)ix2-tdx0-tdx/2);
		ys[0] = (int)Math.round((double)iy2-tdy0-tdy/2);
		
		xs[1] = xs[0] + (int)Math.round(tdx);
		ys[1] = ys[0] + (int)Math.round(tdy);
		
		xs[2] = ix2;
		ys[2] = iy2;

//		System.out.println("fillPoly("+xs+","+ys+")");
		
		g.fillPolygon( xs, ys, 3 );
	}
}