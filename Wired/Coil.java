package Wired;

import java.awt.*;

// support for describing a resistor.

public class Coil extends Element
{
	static final double scale = 1E3;
	double henri = 100.0;
	Color color = new Color(0,102,0);
	
	public String getImageName()
	{
		return "coil.gif";
	}
	
	public Color getColor()
	{
		return color;
	}

	public String getMeterLabel()
	{
		return "~!V~vL~v~!";
	}
	
	public String getCheckboxName()
	{
		return getMeterLabel();
	}
	
	public double reactance( double angle )
	{
		return angle * henri / scale;
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

	public double getValue( long t0 )
	{
		if( circuit != null )
		{
			double t = toSeconds(t0);
			double I0 = circuit.getI0(t0);
			double w = circuit.getAngle();
			double phi = circuit.getPhi();
			
			return -reactance(w)*I0*Math.sin(w*t-phi-Math.PI/2.0);
		}
		
		return 0.0;
	}

	public double getPhase( long t0 )
	{
		if( circuit != null )
		{
			double t = toSeconds(t0);
			double I0 = circuit.getI0(t0);
			double w = circuit.getAngle();
			double phi = circuit.getPhi();
			
			return -reactance(w)*I0*Math.cos(w*t-phi-Math.PI/2.0);
		}
		
		return 0.0;
	}
}
