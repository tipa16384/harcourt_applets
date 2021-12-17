import java.awt.*;
import java.util.Random;

public class ChargedDisk extends Example
{
	int R = 25;

	double maxpot = 1.0;
		
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Charged Disk";
	}
	
	// draw the example.
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		g.setColor( GraphInfo.POSITIVE_COLOR );
		g.fillRect(Op.x-iInnerRad/2,Op.y-R,iInnerRad,R*2);
	}
	
	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		double lQ = (double)q * 1E-9;
		double lR = (double)a * 1E-2;
		double lx = (double)x * 1E-2;
		
		double dpot = (2.0 * GraphInfo.Ke * lQ / (lR*lR)) *
			( Math.sqrt(lx*lx + lR*lR) - Math.abs(lx) );
		
		//System.out.println("ChargedDisk::x="+lx+" dpot="+dpot);
		return dpot;
	}
	
	double derangedH = Double.NEGATIVE_INFINITY;
	double derangedL = Double.POSITIVE_INFINITY;
	
	public double calcDpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot;
		
		dpot = Charge.calcPotential(paper.getComponents(),dorigin);
		dpot *= 1E-9 / (-GraphInfo.C * 1E-2);		//adjust for what ranges we use
		//System.out.println("ChargedDisk::Potential was "+dpot);
		return dpot;
	}
	
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

	// get the label for the "Y" axis for this example
	
	public String getYLabel()
	{
		return "~!V~!(V)";
	}
	
	public DPoint yMinMax()
	{
		DPoint d = new DPoint();
		
		d.x = 0.0;
		d.y = potential(0,3,10,0);
		//System.out.println("ChargedDisk::yMinMax pot=="+d.y);
		return d;
	}

	// set up the example around the given origin.
	
	public void setExample( GraphPaper paper, int ex, DPoint O )
	{
		Random rand = new Random();
		
		switch( ex )
		{
			case 0: Q=3; R=40; break;
			case 1: Q=2; R=50; break;
			case 2: Q=5; R=20; break;
			case 3: Q=3; R=10; break;
		}
		
		//System.out.println("  Q="+Q+"  R="+R);

		double rad = (double)R;
		double diam = rad*2.0;
		double rad2 = rad*rad;
		
		int count = 0;
		
		int res = applet.isCrippled() ? 16 : 104;
		
		paper.removeAll();			//clear out any old charges

		while( count < res/4 )
		{
			
			double y = (double)rand.nextFloat()*diam - rad;
			double z = (double)rand.nextFloat()*diam - rad;
			if( (y*y+z*z) <= rad2 )
			{
				++count;
				newChargeAt(paper,O.relative(0,y,z),false);
				newChargeAt(paper,O.relative(0,-y,z),false);
				newChargeAt(paper,O.relative(0,y,-z),false);
				newChargeAt(paper,O.relative(0,-y,-z),false);
			}
		}

		maxpot = potential(0.0,Q,R,0);
		
		//System.out.println("maxpot="+maxpot);
		
		derangedH = Double.NEGATIVE_INFINITY;
		derangedL = Double.POSITIVE_INFINITY;
	}
	
	
	// should we draw a yellow line horizontally through
	// the image?
	
	public boolean isRestrictedGraph()
	{
		return true;
	}


	// add the sliders for this example to the given panel, which
	// is presumed to have a simple layout manager which can be
	// used with the plain "add(Component)" method.
	
	public void addSliders( Panel p )
	{
		Qslider = new GordySlider("~!Q", 1, 5, 2, "nC");
		Qslider.addAdjustmentListener(this);
		p.add( Qslider, new Rectangle(2,2,168,20) );
		
		Rslider = new GordySlider("~!a", 10, 50, 2, "cm");
		Rslider.addAdjustmentListener(this);
		p.add( Rslider, new Rectangle(2,22,168,20) );
	}
}
