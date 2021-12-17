import java.awt.*;
import java.util.Random;

public class PointCharge extends ChargedSphere
{
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Point Charge";
	}
	
	// set up the example around the given origin.
	
	public void setExample( GraphPaper paper, int ex, DPoint O )
	{
		super.setExample(paper,ex,O);
		R = 5;
		//maxpot = potential(1.0,Q,0,0);
		maxpot = potential(R,Q,0,0);
		//System.out.println("maxpot is "+maxpot+" R="+R);
	}
	
	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		double dpot;
		double lQ = (double)q * 1E-9;
		double lx = (double)x * 1E-2;
		
		if( lx == 0 ) lx = 0.1;
		
		dpot = GraphInfo.Ke * lQ / lx;
		
		//System.out.println("potential("+x+","+q+","+a+","+b+") = "+dpot);
		
		return dpot;
	}
	
	// add the sliders for this example to the given panel, which
	// is presumed to have a simple layout manager which can be
	// used with the plain "add(Component)" method.
	
	public void addSliders( Panel p )
	{
		Qslider = new GordySlider("~!Q", 1, 5, 2, "nC");
		Qslider.addAdjustmentListener(this);
		p.add( Qslider, new Rectangle(2,2,168,20) );
	}

	public double calcDpot( GraphPaper paper, DPoint O )
	{
		double dist = Math.sqrt(O.x*O.x+O.y*O.y);
		double dpot = potential( dist, Q, R, 0 );

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
	
	//static int lastPot = -1;

	public int calcpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot = calcDpot( paper, dorigin );

		int pot = (int) (((double)hipot * dpot)/maxpot);

		//if( pot != lastPot )
			//System.out.println("calcpot return "+pot+" not == "+lastPot);
		//lastPot = pot;

		return pot;
	}
	// get the label for "X" axis for this example
	
	public String getXLabel()
	{
		return "~!r~!(cm)";
	}
	
}
