import java.awt.*;
import java.util.Random;

public class PerpendicularDipole extends AxisDipole
{
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Dipole (perpendicular bisector)";
	}

	public Rectangle getHighlight()
	{
		return new Rectangle( Op.x-iInnerRad, 5, 2*iInnerRad, 2*Op.y-10 );
	}

	// get the label for "X" axis for this example
	
	public String getXLabel()
	{
		return "~!y~!(cm)";
	}
	
	// return the range of the Y axis for the X axis
	
	public DPoint xMinMax()
	{
		return new DPoint(-50.0,50.0,0.0);
	}

	public DPoint yMinMax()
	{
		DPoint d = new DPoint();
		
					//Beware changing these numbers
					// MIGHT also need to change AxisDipole
		d.x = RoundDown(super.potential(-3,50,9,0));
		d.y = RoundUp(super.potential( 3,50,9,0));
		
		minpot = d.x * 1E-2;
		maxpot = d.y * 1E-2;
		
		//System.out.println("AxisDipole::minpot="+minpot+" maxpot="+maxpot);
		
		return d;
	}

	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		return 0.0;
	}
}
