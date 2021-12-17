import java.awt.*;
import java.util.Random;

public class ParallelPlates extends Example
{
	static final boolean debug = false;

	//int R = 3;				//moved to global in Example
	int ixrad = 25;
	final int maxD = 50;
	final int maxR = 5;
	
	double xrad = (double)ixrad;
	double xdam = (double)(ixrad+ixrad);
	double maxpot = 0;
	double minpot = 0;
		
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Parallel Plates";
	}
	
	// draw the example.
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		final int inc = maxD/maxR;
		final int jrad = (R*inc)/2;
		
		g.setColor( GraphInfo.POSITIVE_COLOR );
		g.fillRect(10,Op.y-jrad-iInnerRad,2*Op.x-20,iInnerRad);
		
		g.setColor( GraphInfo.NEGATIVE_COLOR );
		g.fillRect(10,Op.y+jrad,2*Op.x-20,iInnerRad);
	}
	
	public Rectangle getHighlight()
	{
		final int inc = maxD/maxR;
		final int jrad = (R*inc)/2;
					//
		int sy = jrad+iInnerRad+6;
		return new Rectangle( 25, Op.y-sy, 2*Op.x-50, 2*sy );
	}
	
	// set up the example around the given origin.
	
	public void setExample( GraphPaper paper, int ex, DPoint O )
	{
		debug("Setting up "+this+" around "+O);
//		this.paper = paper;
		
		Random rand = new Random();
		
		switch( ex )
		{
			case 0: Q=2; R=3; break;
			case 1: Q=1; R=5; break;
			case 2: Q=4; R=3; break;
			case 3: Q=3; R=4; break;
		}
		
		debug("  Q="+Q+"  R="+R);

		derangedH = Double.NEGATIVE_INFINITY;
		derangedL = Double.POSITIVE_INFINITY;

		maxpot = potential( (double)R, 5, R, 0 );
		minpot = 0;
	}
	
	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		//double lQ = (double)q * -GraphInfo.C;
		double lQ = (double)q * 1E-6;
		double lR = (double)a;
		double pot = 0.0;
		
		if( x < 0.0 )
			pot = 0.0;
		else if( x < lR )
			pot = 4.0 * Math.PI * GraphInfo.Ke * lQ * x * 1E-6;
		else
			pot = 4.0 * Math.PI * GraphInfo.Ke * lQ * lR * 1E-6;
		
		//debug("potential(x="+x+",q="+q+",a="+a+") is "+pot);
		
		return pot;
	}
	
	// should we draw a yellow line horizontally through
	// the image?
	
	public boolean isRestrictedGraph()
	{
		return true;
	}

	// return true if this contains only positive charges
	
	public boolean isPositiveOnly()
	{
		return false;
	}

	public DPoint xMinMax()
	{
		//Op.y = maxR;
		debug("xMinMax on "+Op.y);
		return new DPoint( mungeX((double)-Op.y), mungeX((double)Op.y), 0.0 );
		//return new DPoint( mungeX(-((double)Op.y/2)), mungeX((double)Op.y/2), 0.0 );
		//return new DPoint( -1.0, 5.0, 0.0 );
	}

	public DPoint yMinMax()
	{
		DPoint d = new DPoint();

		d.x = 0;
		d.y = potential( 5.0, 5, 5, 0 );

		return d;
	}

	public double mungeX( double y )
	{
		double munge = y + (double)(R*maxD) / (double)(2*maxR);
		//debug("munged "+y+" to "+smushX(munge) );
		return smushX(munge) ;
	}
	
	public double smushX( double y )
	{
		return y * (double)maxR / (double)maxD;
	}

	double derangedH = Double.NEGATIVE_INFINITY;
	double derangedL = Double.POSITIVE_INFINITY;
	
	public double calcDpot( GraphPaper paper, DPoint dorigin )
	{
		// dorigin.x is -150..150
		// dorigin.y is -50..50
		
		double p = dorigin.y;
		double y;
		
				//the point needs reversing,
				// because the positive plate is now on top
		y = mungeX(-p); // * ((double)maxR / (double)maxD);		//R - 
		
		//debug(dorigin+" becomes ("+p+","+y+")");

		return potential( y, Q, R, 0 );
	}
	
	// get the label for "X" axis for this example
	
	public String getXLabel()
	{
		return "~!y~!(\u03BCm)";
	}
	
	// get the label for "Y" axis for this example
	
	public String getYLabel()
	{
		//yMag = -18;
		//return "V(fV)";
		yMag = 0;
		return "~!V~!(V)";
	}
	
	public int calcpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot = calcDpot( paper, dorigin );
		boolean talk = false;
		
		if( dpot > derangedH )
		{
			derangedH = dpot;
			talk = true;
		}
		
		if( dpot < derangedL )
		{
			derangedL = dpot;
			talk = true;
		}
		
		int pot;

		if( dpot > 0 )		
			pot = (int) (((double)hipot * dpot)/maxpot);
		else
			pot = 0;
		
		/*
		if( talk )
			debug("H="+derangedH+" L="+derangedL+" maxpot="+maxpot+" pot="+pot);
		 */
		 
		return pot;
	}

	// add the sliders for this example to the given panel, which
	// is presumed to have a simple layout manager which can be
	// used with the plain "add(Component)" method.
	
	public void addSliders( Panel p )
	{
		Qslider = new GordySlider("~!~s", 1, 5, 1, "~mC/m~^2");
		Qslider.addAdjustmentListener(this);
		p.add( Qslider, new Rectangle(2,2,168,20) );
		
		Rslider = new GordySlider("~!d", 1, 5, 1, "~mm");
		Rslider.addAdjustmentListener(this);
		p.add( Rslider, new Rectangle(2,22,168,20) );
	}

	static void debug( String s )
	{
		if( debug )
		{
			System.out.println("ParallelPlates:: "+s);
		}
	}
}
