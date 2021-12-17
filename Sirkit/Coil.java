import java.awt.*;

// support for describing a resistor.

public class Coil extends Element
{
	double henri = 20.0 * 1E-3;
	Color color = new Color(0,102,0);
	
	// in milliHenris - mult is 1E-3
	static final String [] henriList =
		{
			"10 mH",
			"20 mH",
			"50 mH",
			"100 mH",
			"150 mH",
			"200 mH"
		};
	
	String getImageName()
	{
		return "coil.gif";
	}
	
	public Color getColor()
	{
		return color;
	}

	public String getCheckboxName()
	{
		return "L";
	}
	
	public double reactance( double angle )
	{
		return angle * henri;
	}
	
	public double getHenri()
	{
		return henri;
	}
	
	public void setHenri( double h )
	{
		henri = h;
		retrace();
	}

	public double getValue( int sel, double t0 )
	{
		if( sel == CircuitElement.VOLTAGE )
		{
			if( circuit != null )
			{
				double t = toSeconds(t0);
				double I0 = 1.0; //circuit.getI0(t0);
				double w = 1.0; //circuit.getAngle();
				double phi = 1.0; //circuit.getPhi();
				
				return reactance(w)*I0*Math.sin(w*t-phi-Math.PI/2.0);
			}
		}
		
		return 0.0;
	}

	public double getPhase( long tau )
	{
		return 0.0;
	}
}
