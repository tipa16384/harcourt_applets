import java.awt.Color;

public class SineElement implements CircuitElement
{
	Color color;
	double phase;
	double freq;
	
	public SineElement( double phase, double freq, Color color )
	{
		this.color = color;
		this.phase = phase;
		this.freq = freq;
	}
	
	public double getValue( int selector, long tau )
	{
		switch( selector )
		{
			case CircuitElement.VOLTAGE:
				return getVoltage( tau );

			case CircuitElement.INDUCTANCE:
				return getInductance( tau );
		}
		
		return 0.0;
	}
	
	public boolean showTrace( int sel )
	{
		return true;
	}
	
	public double getVoltage( long t )
	{
		return Math.sin(((double)t)/freq+phase);
	}
	
	public double getInductance( long t )
	{
		return Math.cos(((double)t)/freq+phase);
	}
	
	public Color getColor()
	{
		return color;
	}
}
