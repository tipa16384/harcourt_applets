import java.awt.*;
import java.util.Random;

public class ChargedSphere extends Example
{
	//int R=25;				//moved to global in Example
	double maxpot, minpot;
	
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Insulating Charged Sphere";
	}
	
	// draw the example.
	
	public void paint( Graphics g )
	{
		super.paint(g);
		g.setColor( GraphInfo.POSITIVE_COLOR );
		//g.setXORMode( Color.white );
		g.fillOval(Op.x-(int)R,Op.y-(int)R,(int)(2*R),(int)(2*R));
		//g.setPaintMode();
	}
	
	// set up the example around the given origin.
	
	public void setExample( GraphPaper paper, int ex, DPoint O )
	{
		//System.out.println("Setting up "+this+" around "+O);
		
		switch( ex )
		{
			case 0: Q=4; R=10; break;
			case 1: Q=3; R=10; break;
			case 2: Q=4; R=20; break;
			case 3: Q=5; R=5; break;
		}
		
		//System.out.println("  Q="+Q+"  R="+R);
		
		minpot = 0.0;
		maxpot = potential(0,Q,R,0);
		
		//System.out.println("maxpot is "+maxpot);
	}
	
	public DPoint yMinMax()
	{
		DPoint d = new DPoint();
		
		d.x = 0.0;
		d.y = potential(0,5,5,0);
		
		return d;
	}
	
	double derangedH = Double.NEGATIVE_INFINITY;
	double derangedL = Double.POSITIVE_INFINITY;
	
	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		double dpot;
		double lQ = (double)q * 1E-9;
		double lR = (double)a * 1E-2;
		double lx = (double)x * 1E-2;

		if( lx < lR )
			dpot = (GraphInfo.Ke * lQ / (lR+lR)) * ( 3 - ((lx)*(lx))/(lR*lR) );
		else
			dpot = GraphInfo.Ke * lQ / (lx);
		
		//System.out.println("potential("+x+","+q+","+a+","+b+") = "+dpot);
		
		return dpot;
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
	
	public int calcpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot = calcDpot( paper, dorigin );

		//System.out.println("dpot is "+dpot);

		int pot = (int) (((double)hipot * dpot)/maxpot);
		
		return pot;
	}

	// get the label for "X" axis for this example
	
	public String getXLabel()
	{
		return "~!r~!(cm)";
	}
}
