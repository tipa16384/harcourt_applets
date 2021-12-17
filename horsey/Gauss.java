import java.util.Random;

public class Gauss
{
	double standardDeviation;
	double mean;
	
	public Gauss( double sd, double mean )
	{
		standardDeviation = sd;
		this.mean = mean;
	}
	
	// scale a number randomly selected from the normal
	// distribution for the specific standard deviation
	// and mean set during initialization.
	public double getValue()
	{
		// getValue = f(x) * std.dev + mean;
		return normal() * standardDeviation + mean;
	}
	
	// return a value weighted to a normal distribution
	// the value is for a normal distribution with mean 0
	// and standard deviation 1.
	public double normal()
	{
		double fac, r, v1, v2;
		
		do
		{
			v1 = 2.0 * GraphInfo.rand.nextDouble() - 1;
			v2 = 2.0 * GraphInfo.rand.nextDouble() - 1;
			r = v1*v1+v2*v2;
		}
		while( r >= 1 );
		
		fac = Math.sqrt( -2.0 * Math.log(r)/r );
		
		return v2 * fac;
	}
}
