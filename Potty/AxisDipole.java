import java.awt.*;
import java.util.Random;

public class AxisDipole extends Example
{
	static final boolean debug = false;

	//int R=25;				//moved to global in Example
	double maxpot, minpot;
	GraphPaper paper = null;
	
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Dipole (along axis)";
	}
	
	// draw the example.
	
	public void paint( Graphics g )
	{
		super.paint(g);
		final int zrad = 2;

		g.setColor( GraphInfo.POSITIVE_COLOR );
		g.fillOval( Op.x+R-zrad, Op.y-zrad, 2*zrad, 2*zrad );
		g.setColor( GraphInfo.NEGATIVE_COLOR );
		g.fillOval( Op.x-R-zrad, Op.y-zrad, 2*zrad, 2*zrad );
	}
	
	// set up the example around the given origin.
	
	public void setExample( GraphPaper paper, int ex, DPoint O )
	{
		//debug("Setting up "+this+" around "+O);
		this.paper = paper;
		
		Random rand = new Random();
		
		switch( ex )
		{
			//case 0: Q=4; R=34; break;
			//case 1: Q=2; R=22; break;
			//case 2: Q=1; R=45; break;
			//case 3: Q=5; R=29; break;
			case 0: Q=40; R=36; break;
			case 1: Q=50; R=10; break;
			case 2: Q=10; R=27; break;
			case 3: Q=30; R=45; break;
		}
		
		debug("  Q="+Q+"  R="+R);
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

	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		double lQ = (double)q * 1E-9;
		double lR = (double)a * 1E-2;
		double lx = (double)x * 1E-2;
		double pot;
		
		if( lx == lR ) pot = Double.POSITIVE_INFINITY;
		else if( lx == -lR ) pot = Double.NEGATIVE_INFINITY;
		else pot = GraphInfo.Ke * lQ * ((1.0/Math.abs(lR-lx))-(1.0/Math.abs(lR+lx)));

		//debug("AxisDipole::x="+lx+" q="+lQ+" a="+lR+" pot="+pot);
		return( pot );
	}
	
	double derangedH = Double.NEGATIVE_INFINITY;
	double derangedL = Double.POSITIVE_INFINITY;
	
	public double calcDpot( GraphPaper paper, DPoint O )
	{
		O.y = -O.y;
		double z;
		double lQ = (double)Q * 1E-9;
		double lR = (double)R * 1E-2;
		double lx = (double)O.x * 1E-2;
		double ly = (double)O.y * 1E-2 *-1;	//needs negative/reversing for upward Y to be increasing
		double dpot = 0;
		
		if( ( ly == 0 ) && ( lx == lR ) ) dpot = Double.POSITIVE_INFINITY;
		else if( ( ly == 0 ) && ( lx == lR ) ) dpot = Double.NEGATIVE_INFINITY;
		else
		{
			dpot = GraphInfo.Ke * lQ * ((1.0/Math.sqrt((lR-lx)*(lR-lx)+(ly*ly)))-(1.0/Math.sqrt((lR+lx)*(lR+lx)+(ly*ly))));
			
			if( dpot < derangedL )
			{
				derangedL = dpot;
				//debug("Hi="+derangedH+" Lo="+derangedL);
			}
			
			if( dpot > derangedH )
			{
				derangedH = dpot;
				//debug("Hi="+derangedH+" Lo="+derangedL);
			}
		}

		//debug("AxisDipole::lx="+lx+" ly="+ly+" lq="+lQ+" la="+lR+" pot="+dpot);
		return dpot;
	}
	
	public int calcpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot = calcDpot( paper, dorigin );

		int pot;
		
		if( dpot > 0 ) pot = (int) (((double)hipot * dpot / 15)/maxpot);
		else if( dpot < 0 ) pot = (int) (((double)lopot * dpot / 15)/minpot);
		else pot = 0;
		
		return pot;
	}

	public DPoint yMinMax()
	{
		DPoint d = new DPoint();
		
					//Beware changing these numbers
					// MIGHT also need to change PerpendicularDipole
		d.x = RoundDown(potential(-3,50,9,0));
		d.y = RoundUp(potential( 3,50,9,0));
		
		minpot = d.x * 1E-2;
		maxpot = d.y * 1E-2;
		
		//debug("AxisDipole::minpot="+minpot+" maxpot="+maxpot);
		
		return d;
	}

	public double RoundUp( double d )
	{
		int i = 0;
		while( (d > 1000) )
		{
			d = Math.ceil(d / 10);
			i++;
		}
		while( i > 0 )
		{
			d = d * 10;
			i--;
		}
		return d;
	}		

	public double RoundDown( double d )
	{
		int i = 0;
		while( Math.abs(d) > 1000 )
		{
			d = Math.floor(d / 10);
			i++;
		}
		while( i > 0 )
		{
			d = d * 10;
			i--;
		}
		return d;
	}		

	// add the sliders for this example to the given panel, which
	// is presumed to have a simple layout manager which can be
	// used with the plain "add(Component)" method.
	
	public void addSliders( Panel p )
	{
		Qslider = new GordySlider("~!Q", 10, 50, 25, "nC");
		Qslider.addAdjustmentListener(this);
		p.add( Qslider, new Rectangle(2,2,168,20) );
		
		Rslider = new GordySlider("~!a", 10, 50, 25, "cm");
		Rslider.addAdjustmentListener(this);
		p.add( Rslider, new Rectangle(2,22,168,20) );
	}

	static void debug( String s )
	{
		if( debug )
		{
			System.out.println("AxisDipole:: "+s);
		}
	}
}
