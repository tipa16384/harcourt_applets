import java.awt.*;
import java.util.Random;

public class ConductingSphere extends ChargedSphere
{
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Conducting Sphere";
	}
	
	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		double dpot;
		double lQ = (double)q * 1E-9;
		double lR = (double)a * 1E-2;
		double lx = (double)x * 1E-2;
		
		if( lx < lR )
			dpot = (GraphInfo.Ke * lQ / lR);
		else
			dpot = GraphInfo.Ke * lQ / lx;
		
		//System.out.println("potential("+x+","+q+","+a+","+b+") = "+dpot);
		
		return dpot;
	}

	// get the label for "X" axis for this example
	
	public String getXLabel()
	{
		return "~!r~!(cm)";
	}
	
}
