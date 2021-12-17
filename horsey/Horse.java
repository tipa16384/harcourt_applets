import java.awt.*;

public class Horse
{
	final static int historySize = 10;
	
	double mean;
	double standardDeviation;
	double payoff;
	String name;
	Gauss gauss;
	boolean justify = false;
	
	final double lowRange = 1.25;
	final double hiRange = 3.0;
	
	BellCurve bell;
	
	double [] history;
	
	public Horse( String name, double mean, double standardDeviation, double payoff )
	{
		this.name = name;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.payoff = payoff;
		gauss = new Gauss( standardDeviation, mean );
		bell = new BellCurve( this, lowRange, hiRange );
		
		justify = Math.abs(mean-lowRange) <
				  Math.abs(mean-hiRange);
		
		history = new double[historySize];
		
		for( int i=0; i<historySize; ++i )
			raceTime();
	}
	
	public boolean getJustify()
	{
		return justify;
	}
	
	public double getPayoff()
	{
		return payoff;
	}
	
	public void setPayoff( double payoff )
	{
		this.payoff = payoff;
	}
	
	public double [] getHistory()
	{
		return history;
	}
	
	public double raceTime()
	{
		return raceTime(true);
	}
	
	public double raceTime( boolean addToHistory )
	{
		double time = gauss.getValue();
	
		if( addToHistory )
		{	
			for( int i=0; i<history.length-1; ++i )
			{
				history[i] = history[i+1];
			}
	
			history[history.length-1] = time;
		}
				
		return time;
	}
	
	public BellCurve getBellCurve()
	{
		return bell;
	}
	
	public double getMean()
	{
		return mean;
	}
	
	public double getStandardDeviation()
	{
		return standardDeviation;
	}
	
	public String getName()
	{
		return name;
	}
}
