import java.awt.*;
import java.awt.event.*;
import Wired.Arrow;
import Wired.DoubleFormat;

public class CoaxModel extends Model
{
	GordySlider aslider=null,
				bslider=null,
				lslider=null,
				qslider=null;

	RawLabel label;
					
	final int scale = 6;				// for scaling distances
	final int pad = 4;

	public CoaxModel( String name )
	{
		super(name);

		label = new RawLabel();
		label.setFont( new Font("SanSerif", Font.PLAIN, 10 ) );
		label.setForeground( Color.black );
	}
	
	public DPoint shiftPoint( Component c, int x, int y )
	{
		DPoint p = super.shiftPoint( c, x, y );
		p.x /= (double) scale;
		p.y /= (double) scale;
		return p;
	}

	public void addSliders( Container c )
	{
		c.add( aslider = new GordySlider( "~!a", 1, 5, 2, "mm" ) );
		c.add( bslider = new GordySlider( "~!b", 2, 10, 5, "mm" ) );
		c.add( lslider = new GordySlider( "~!L", 1, 20, 5, "m" ) );
		c.add( qslider = new GordySlider( "~!Q", 1, 20, 10, "nC" ) );
		
		super.addSliders( c );
		
		AdjustmentListener al = new PlayNicely();
		aslider.addAdjustmentListener( al );
		bslider.addAdjustmentListener( al );
	}

	public Point centerPoint(Component c)
	{
		Dimension size = c.getSize();
		return new Point(size.width/2-45,size.height/2);
	}

	public boolean inDielectric( Component c, int x, int y )
	{
		Point p = centerPoint(c);
		int len = (int)Math.sqrt((double)((x-p.x)*(x-p.x)+(y-p.y)*(y-p.y)));
		
		return 
			( len <= bslider.getValue()*scale ) &&
			( len >= aslider.getValue()*scale );
	}

	public double getEField( Component c, double x, double y, Dielectric d )
	{
		double dk = (d == null) ? 1.0 : d.getConstant();
		double len = Math.sqrt(x*x+y*y);
//		System.out.println(p+" is "+len+" away from ("+x+","+y+")");
		return getCharge()/(2.0*Math.PI*GraphInfo.e0*getLength()*len*dk);
	}

	private double log( double n )
	{
		return Math.log(n)/Math.log(2.0);
	}

	private int getNLines( Dielectric d )
	{
		double av = getOuterSize();
		double bv = getCoreSize();
		double len = (av+bv)/2.0;
		
		double E = getCharge()/(2.0*Math.PI*GraphInfo.e0*getLength()*len*d.getConstant());
		
		E = log(E);
		
		final double minE = 0.0;
		final double maxE = log(126000.0);
		final double minline = 3;
		final double maxline = 23;
		
		double x = (E-minE)/(maxE-minE) * (maxline-minline) + minline;
		
		return (int) (x+0.5);
	}

	private int getLeftEdge( Component c )
	{
		int maxrad = bslider.getMaximum() * scale + pad + 15;
		return centerPoint(c).x + maxrad/2 + 30;
	}

	public void drawCircuit( Graphics g, Component c, Dielectric d )
	{
		final Dimension size = c.getSize();
		int radius;
		int av = aslider.getValue();
		int bv = bslider.getValue();
		Dimension lsize = label.getMinimumSize();
		
		Point p0 = centerPoint(c);
		
		final int x0 = p0.x;
		final int y0 = p0.y;

		int leftedge = getLeftEdge(c);
		
		Point boxtop = new Point( leftedge-4, y0+y0/2-35 );
		
		Rectangle box = new Rectangle(boxtop.x,boxtop.y,lsize.width+8,lsize.height+4);
		int ty = box.y + 4;
		
		final int wireSeparation = 10;
		
		// draw the outer wire
		g.setColor( GraphInfo.SEPARATE_COLOR );
		Arrow.drawLine( g, x0, y0+wireSeparation, leftedge, ty+wireSeparation, 2 );

		// draw the cathode
		g.setColor( GraphInfo.NEGATIVE_COLOR );
		radius = bv * scale + pad;
		g.fillOval(x0-radius,y0-radius,radius*2,radius*2);
		
		// the dielectric
		g.setColor( d.getColor() );
		radius = bv * scale;
		g.fillOval(x0-radius,y0-radius,radius*2,radius*2);
		
		// the inner wire
		g.setColor( GraphInfo.SEPARATE_COLOR );
		Arrow.drawLine( g, x0, y0, leftedge, ty, 2 );
		
		// all those little arrows
		g.setColor( GraphInfo.FIELD_COLOR );
		
		double dist = 15.0;
		double r = (double) (((bv+av)*scale)/2);
		double cosTheta = (2.0 * r * r - dist * dist) / (2.0 * r * r);
		double theta = Math.acos(cosTheta);
		double PI2 = Math.PI*2.0;
		
		int nlines = getNLines(d);
		int [] xs = new int[4];
		int [] ys = new int[4];
		
		double ysize = 8.0;
		double xsize = 8.0;
		
		for( int i=0; i<nlines; ++i )
		{
			theta = (double)i * PI2 / (double)nlines;
			double ct = Math.cos(theta);
			double st = Math.sin(theta);
			
			int dx = (int) ((double)radius * ct);
			int dy = (int) ((double)radius * st);
			
			g.drawLine(x0,y0,x0+dx,y0+dy);
			
			double r1 = r+ysize/2.0;
			
			xs[0] = xs[3] = (int)(r1*ct)+x0;		ys[0] = ys[3] = (int)(r1*st)+y0;
			
			r1 -= ysize;
			
			double cosAlpha = (2.0 * r1 * r1 - xsize*xsize) / (2.0 * r1 * r1);
			double alpha = Math.acos(cosAlpha)/2.0;
			
			xs[1] = x0+(int)(r1*Math.cos(theta+alpha));	ys[1] = y0+(int)(r1*Math.sin(theta+alpha));
			xs[2] = x0+(int)(r1*Math.cos(theta-alpha));	ys[2] = y0+(int)(r1*Math.sin(theta-alpha));
			g.fillPolygon( xs, ys, 4 );
		}
		
		// and the anode
		g.setColor( GraphInfo.POSITIVE_COLOR );
		radius = av * scale;
		g.fillOval(x0-radius,y0-radius,radius*2,radius*2);
		
		// get the voltage
		double kappa = d.getConstant();
		double V = getVoltage(kappa);
		
		label.setText("Voltmeter");
		label.paint( g, leftedge, boxtop.y-13, false );
		
		label.setText( "~!V~! = "+DoubleFormat.format(V) + " V" );
		lsize = label.getMinimumSize();
		box = new Rectangle(boxtop.x,boxtop.y,lsize.width+8,lsize.height+4);
		
		g.setColor( Color.white );
		g.fillRect( box.x, box.y, box.width, box.height );
		g.setColor( Color.black );
		g.drawRect( box.x, box.y, box.width, box.height );
		ty -= 2;
		label.paint( g, leftedge, ty, false );
		
		ty += lsize.height+10;
		label.setText( "~!C~! = "+DoubleFormat.format(getCapacitance(kappa)) + " F" );
		label.paint( g, leftedge, ty, false );
		
		ty += lsize.height;
		label.setText( "~!U~! = "+DoubleFormat.format(getStoredEnergy(kappa)) + " J" );
		label.paint( g, leftedge+macBugNudge(), ty, false );
		
		super.drawCircuit(g,c,d);
	}
	
	public double value( Component sirkit, int x, int y )
	{
		DPoint d = shiftPoint( sirkit, x, y );
		return Math.sqrt(d.x*d.x+d.y*d.y);
	}

	public String units()
	{
		return "mm";
	}

	public String variable()
	{
		return "~!r~!";
	}

	public Point getPopupPoint( Component c )
	{
		return new Point( getLeftEdge(c)-20, 3 );
	}

	public String getImageName()
	{
		return "coax.gif";
	}
	
	public void resetValues()
	{
		aslider.setValue( aslider.getMinimum() );
		bslider.setValue( bslider.getMaximum() );
		lslider.setValue( lslider.getMinimum() );
		qslider.setValue( qslider.getMaximum() );
	}

	double getCoreSize()
	{
		return (double)aslider.getValue() / 1000.0;		// mm to m
	}
	
	double getOuterSize()
	{
		return (double)bslider.getValue() / 1000.0;		// mm to m
	}
	
	double getLength()
	{
		return (double)lslider.getValue();				// m
	}
	
	// get charge
	
	public double getCharge()
	{
		return (double)qslider.getValue() * 1E-9;		// nano C to C
	}
	
	// get voltage
	
	double getVoltage( double K )
	{
		return getCharge() / getCapacitance(K);
	}

	// get capacitance
	
	double getCapacitance( double K )
	{
		return (2.0*Math.PI*GraphInfo.e0*getLength()*K) / Math.log(getOuterSize()/getCoreSize());
	}
	
	// get the stored energy
	
	double getStoredEnergy( double K )
	{
		double V = getVoltage(K);
		return 0.5 * getCapacitance(K) * V * V;
	}
	
	class PlayNicely implements AdjustmentListener
	{
		final int close = 1;
		
	    public void adjustmentValueChanged(AdjustmentEvent e)
	    {
	    	int av = aslider.getValue();
	    	int bv = bslider.getValue();
	    	
	    	if( (av+close) >= bv )
	    	{
	    		Adjustable al = e.getAdjustable();
	    		if( al instanceof GordySlider )
	    		{
	    			GordySlider us = (GordySlider) al;
	    			
	    			if( us == aslider )
	    			{
	    				bslider.setValue(av+close+1);
	    			}
	    			
	    			else
	    			{
	    				aslider.setValue(bv-(close+1));
	    			}
	    		}
	    	}
	    }
	}
}
