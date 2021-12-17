import java.awt.*;

// Arrow is actually another name for the classic 'vector', but since
// there is already a Vector class, there's this one.
// While Arrow is a subclass of Component, it can also be drawn
// if given a Graphic object to draw into, similar to a line (in
// fact, you can use it like that if you like).

public class Arrow extends Component
{
	// vectors are kept in polar coordinates OR rectangular
	// coordinates (but not both at once). Regardless, the origin
	// of the vector is ALWAYS at (0,0), and the terminus is
	// relative to that.

	Fector fector;
	
	// the converstion factor between units and pixels; the
	// default conversion factor is 1.0, so 1.0 units is 1 pixel.
	double unitsPerPixel;
	
	// set this final variable to true if +y is up. Normally,
	// +y is down, but that's not very graph-like.
	final boolean YUP = true;

	// width of an arrow in pixels.
	double lineWidth;

	// length of the cap in units
	final double capLenMult = 4.0;
	
	// width of the cap in units
	final double capWidMult = 3.0;
		
	// initializer
	public Arrow()
	{
		this( 0, 0 );
	}
	
	// Arrows are in rectangular coordinates by default.
	public Arrow( double length, double angle )
	{
		setFector( new Fector( angle, length ) );
		setForeground( Color.black );
		setLineWidth(1.0);
		setUnitsPerPixel(1);
	}
	
	// initialize with this other Arrow.
	public Arrow( Arrow a )
	{
		setFector( new Fector(a.getFector()) );
		setLineWidth( a.getLineWidth() );
		setUnitsPerPixel( a.getUnitsPerPixel() );
		setForeground( a.getForeground() );
	}	

	public void setFector( Fector f )
	{
		this.fector = f;
		//repaint();
	}
	
	public Fector getFector()
	{
		return fector;
	}

	// how many units to a pixel?
	public void setUnitsPerPixel( double u )
	{
		if( u == 0.0 )
		{
			throw new ArithmeticException(getClass().getName()+": unitsPerPixel cannot be 0");
		}
		
		unitsPerPixel = u;
	}
	
	public double getUnitsPerPixel()
	{
		return unitsPerPixel;
	}

	// how wide is the line?
	public void setLineWidth( double w )
	{
		if( w <= 0.0 )
			throw new ArithmeticException(getClass().getName()+": line width cannot be <= 0");

		lineWidth = w;
	}

	// get length of the cap
	double getCapLength()
	{
		double len = lineWidth * unitsPerPixel * capLenMult;
		return (len > fector.getLength()) ? fector.getLength() : len;
	}
	
	// get width of the base of the cap
	double getCapWidth()
	{
		double len = lineWidth * unitsPerPixel * capWidMult;
		return len;
	}
	
	public double getLineWidth()
	{
		return lineWidth;
	}

	// draw the cap to the arrow (the arrowhead)
	void drawCap( Graphics g, int x, int y )
	{
//		System.out.println("drawing cap");
		int [] xs = new int[4];
		int [] ys = new int[4];
		
		// draw the cap
		DPoint p = fector.getEndpoint();
		double clen = getCapLength();
		double cwid = getCapWidth();
		
//		System.out.println("p="+p+"   clen="+clen+"  cwud="+cwid);
		
		xs[0] = xs[3] = x + (int)Math.round(p.x);
		ys[0] = ys[3] = y + (int)Math.round(p.y);
		
		Fector a = fector.normal();
		a.setLength( cwid/2.0 );
//		System.out.println("Normal is "+a);
		
		Fector b = new Fector(fector);
		b.setLength( b.getLength()-clen );
//		System.out.println("b is "+b);
		DPoint p0 = b.getEndpoint();
		DPoint p1 = a.getEndpoint();
//		System.out.println("p0 is "+p0+" p1 is "+p1);
//		System.out.println("x is "+x+" y is "+y);
		
		xs[1] = x + (int)Math.round(p0.x+p1.x);
		ys[1] = y + (int)Math.round(p0.y+p1.y);
		xs[2] = x + (int)Math.round(p0.x-p1.x);
		ys[2] = y + (int)Math.round(p0.y-p1.y);
		
//		printArray("xs=",xs);
//		printArray("ys=",ys);
		
//		g.setColor( Color.red );
		g.fillPolygon( xs, ys, 4 );
	}

	private void printArray( String label, int [] ar )
	{
		int len = ar.length;
		int i;
		
		System.out.print(label);
		for( i=0; i<len; ++i )
			System.out.print(ar[i]+" ");
		System.out.println();
	}

	// draw the arrow from (x,y).
	public void drawArrow( Graphics g, int x, int y )
	{
		drawArrow( g, x, y, true );
	}
	
	void drawArrow( Graphics g, int x, int y, boolean drawCap )
	{
		Color oldColor = g.getColor();
		g.setColor( getForeground() );
		
		double clen = getCapLength();
		if( drawCap && (clen > 0.0) )
		{
			// draw line short of cap

			Arrow a = new Arrow(this);
			a.fector.setLength( a.fector.getLength()-(0.9*clen) );
			a.drawArrow( g, x, y, false );
			
			drawCap( g, x, y );
		}
		
		else
		{
			DPoint p = fector.getEndpoint();
			
			if( lineWidth == 1.0 )
				g.drawLine( x, y, x+(int)Math.round(p.x), y+(int)Math.round(p.y) );
			else
				drawThick( g, x, y, x+(int)Math.round(p.x), y+(int)Math.round(p.y) );
		}
		
		g.setColor( oldColor );
	}
	
	// draw a really thick arrow from (x,y) to (x2,y2)
	private void drawThick( Graphics g, int x1, int y1, int x2, int y2 )
	{
		// see if we can get away with a rectangle
		if( y1 == y2 )
		{
			//System.out.println("horizontal line");
			if( x1 > x2 ) { int t = x1; x1 = x2; x2 = t; }
			g.fillRect( x1, y1-(int)Math.round(lineWidth/2), x2-x1, (int)Math.round(lineWidth) );
		}
		
		// a vertical rectangle, perhaps?
		
		else if( x1 == x2 )
		{
			//System.out.println("vertical line");
			if( y1 > y2 ) { int t = y1; y1 = y2; y2 = t; }
			g.fillRect( x1-(int)Math.round(lineWidth/2), y1, (int)Math.round(lineWidth), y2-y1 );
		}
		
		// oh dear, none of those... darn...
		
		else
		{
			//System.out.println("tilted line");
			//System.out.println("this="+this);
			Fector a = fector.normal();
			a.setLength( (lineWidth/2.0) * unitsPerPixel );
			
			//System.out.println("normal is "+a);
			
			int x[], y[];
			x = new int[5];
			y = new int[5];
			
			DPoint p, p2;
			int x0, y0;
			
			p = a.getEndpoint();
			a.rotate(Math.PI);
			//System.out.println("after rotation, normal="+a);
			p2 = a.getEndpoint();
			//System.out.println("normal="+p+" opposite="+p2);
			x[0] = x[4] = x1+(int)Math.round(p.x);
			y[0] = y[4] = y1+(int)Math.round(p.y);
			x[1] = x2+(int)Math.round(p.x);
			y[1] = y2+(int)Math.round(p.y);
			x[2] = x2+(int)Math.round(p2.x);
			y[2] = y2+(int)Math.round(p2.y);
			x[3] = x1+(int)Math.round(p2.x);
			y[3] = y1+(int)Math.round(p2.y);
			g.fillPolygon( x, y, 5 );
		}
	}
	
	// util string printer
	public String toString()
	{
		return getClass().getName()+"["+fector+
			",upp="+unitsPerPixel+",lw="+
			lineWidth+"]";
	}
}
