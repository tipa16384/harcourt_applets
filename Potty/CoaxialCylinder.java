import java.awt.*;
import java.util.Random;

public class CoaxialCylinder extends Example
{
	//int A = 5;				//moved to global in Example
	//int B = 10;				//moved to global in Example

	double minpot = -1;
	double maxpot = 1;
		
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Infinite Coaxial Cylinder";
	}
	
	// draw the example.
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		g.setColor( GraphInfo.POSITIVE_COLOR );
		g.fillOval(Op.x-A,Op.y-A,2*A,2*A);
		g.setColor( GraphInfo.NEGATIVE_COLOR );
		g.drawOval(Op.x-B,Op.y-B,2*B,2*B);
	}
	
	double derangedH;
	double derangedL;

	// set up the example around the given origin.
	
	public void setExample( GraphPaper paper, int ex, DPoint O )
	{
		Random rand = new Random();
		
		switch( ex )
		{
			case 0: Q=2; A=10; B=40; break;
			case 1: Q=3; A=20; B=45; break;
			case 2: Q=5; A=15; B=50; break;
			case 3: Q=3; A=5; B=30; break;
		}
		
		System.out.println("  Q="+Q+"  A="+A+"  B="+B);

		maxpot = potential(0.0,Q,A,B);
		minpot = potential((double)B,Q,A,B);
				
		derangedH = Double.NEGATIVE_INFINITY;
		derangedL = Double.POSITIVE_INFINITY;
	}
	
	// add the sliders for this example to the given panel, which
	// is presumed to have a simple layout manager which can be
	// used with the plain "add(Component)" method.
	
	public void addSliders( Panel p )
	{
		Qslider = new GordySlider("~!~l", 1, 5, 1, "nC/m");
		Qslider.addAdjustmentListener(this);
		p.add( Qslider, new Rectangle(2,2,168,20) );
		//System.out.println("Qslider size="+Qslider.getMinimumSize());
		
		Rslider = new GordySlider("~!a", 1, 20, 10, "mm");
		Rslider.addAdjustmentListener(this);
		p.add( Rslider, new Rectangle(2,22,168,20) );
		
		Bslider = new GordySlider("~!b", 20, 50, 25, "mm");
		Bslider.addAdjustmentListener(this);
		p.add( Bslider, new Rectangle(2,42,168,20) );
	}

	// get the label for "X" axis for this example
	
	public String getXLabel()
	{
		return "~!r~!(mm)";
	}
	
	// get the label for the "Y" axis for this example
	
	public String getYLabel()
	{
		return "~!V~!(V)";
	}
	
	// return the minimum and maximum values for Y
	
	public DPoint yMinMax()
	{
		DPoint d = new DPoint();
		
		d.x = 0.0;
		d.y = potential(0.0,5,5,50);
		
		return d;
	}
	
	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		double lQ = (double)q * 1E-9;
		double lA = (double)a;
		double lB = (double)b;
		
		double dpot;
		
		if( x < lA )
			dpot = 2.0 * GraphInfo.Ke * lQ * Math.log(lB/lA);
		else if( x < lB )
			dpot = 2.0 * GraphInfo.Ke * lQ * (Math.log(lB/lA) - Math.log(x/lA));
		else
			dpot = 0.0;
		
		return dpot;
	}
	
	public double calcDpot( GraphPaper paper, DPoint O )
	{
		double dpot = potential( Math.sqrt(O.x*O.x+O.y*O.y), Q, A, B );

		if( dpot < derangedL )
		{
			derangedL = dpot;
			//System.out.println("Hi="+derangedH+" Lo="+derangedL);
		}
		
		if( dpot > derangedH )
		{
			derangedH = dpot;
			//System.out.println("Hi="+derangedH+" Lo="+derangedL);
		}

		return dpot;
	}
	
	public int calcpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot = calcDpot( paper, dorigin );

		int pot = (int) (((double)hipot * dpot)/maxpot);
		
		return pot;
	}

	// return true if this contains only positive charges
	
	public boolean isPositiveOnly()
	{
		return false;
	}
}
