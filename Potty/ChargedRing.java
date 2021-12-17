import java.awt.*;
import java.util.Random;

public class ChargedRing extends Example
{
	//int R = 25;				//moved to global in Example

	double maxpot = 1.0;
	double minpot = 0.0;
		
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Uniformly Charged Ring";
	}
	
	// draw the example.
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		g.setColor( GraphInfo.POSITIVE_COLOR );
		
		g.fillOval( Op.x-iInnerRad, Op.y-R-iInnerRad, 2*iInnerRad, 2*iInnerRad );
		g.fillOval( Op.x-iInnerRad, Op.y+R-iInnerRad, 2*iInnerRad, 2*iInnerRad );
	}
	
	// set up the example around the given origin.
	
	public void setExample( GraphPaper paper, int ex, DPoint O )
	{
		Random rand = new Random();
		
		switch( ex )
		{
			case 0: Q=3; R=25; break;
			case 1: Q=2; R=30; break;
			case 2: Q=1; R=40; break;
			case 3: Q=5; R=20; break;
		}
		
		System.out.println("  Q="+Q+"  R="+R);

		double count = 10.0;
		
		final double PI2 = Math.PI*2.0;
		final double delta = PI2/count;
		
		paper.removeAll();			//clear out any old charges

		for( double theta=0.0; theta<PI2; theta += delta )
		{
			double y = (double)R * Math.sin(theta);
			double z = (double)R * Math.cos(theta);
			newChargeAt(paper,O.relative(0,y,z),false);
		}

		maxpot = potential(0.0,Q,R,0);
		
		derangedH = Double.NEGATIVE_INFINITY;
		derangedL = Double.POSITIVE_INFINITY;
	}
	
	// return the minimum and maximum values for Y
	
	public DPoint yMinMax()
	{
		DPoint d = new DPoint();
		
		d.x = 0.0;
		d.y = potential(0.0,5,20,0);
		
		System.out.println("maxpot = "+maxpot);
		
		return d;
	}
	
	double derangedH = Double.NEGATIVE_INFINITY;
	double derangedL = Double.POSITIVE_INFINITY;
	
	public int calcpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot = calcDpot( paper, dorigin );

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

		int pot = (int) (((double)hipot * dpot)/maxpot);
		
		return pot;
	}

	// add the sliders for this example to the given panel, which
	// is presumed to have a simple layout manager which can be
	// used with the plain "add(Component)" method.
	
	public void addSliders( Panel p )
	{
		Qslider = new GordySlider("~!Q", 1, 5, 2, "nC");
		Qslider.addAdjustmentListener(this);
		p.add( Qslider, new Rectangle(2,2,168,20) );
		
		Rslider = new GordySlider("~!a", 10, 50, 25, "cm");
		Rslider.addAdjustmentListener(this);
		p.add( Rslider, new Rectangle(2,22,168,20) );
	}

	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		double lQ = (double)q * 1E-9;
		double lR = (double)a * 1E-2;
		double lx = (double)x * 1E-2;
		
		double dpot = GraphInfo.Ke * lQ / Math.sqrt((lx*lx)+(lR*lR));
		//System.out.println("Ring x="+lx+" q="+lQ+" V="+dpot );
		return dpot;
	}
	
	public double calcDpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot;
		
		dpot = Charge.calcPotential(paper.getComponents(),dorigin);
		dpot *= 1E-9 / (-GraphInfo.C * 1E-2);		//adjust for what ranges we use
		//System.out.println("ChargedRing::Potential was "+dpot);
		return dpot;
	}
	
	// should we draw a yellow line horizontally through
	// the image?
	
	public boolean isRestrictedGraph()
	{
		return true;
	}
}
