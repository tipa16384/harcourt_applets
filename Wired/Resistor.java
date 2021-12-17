package Wired;

import java.awt.*;

// support for describing a resistor.

public class Resistor extends Element
{
	double ohm = 100.0;
	Color color = new Color(255,0,0);
	
	public Resistor()
	{
		phiMult = 0.0;
	}
	
	public double reactance( double angle )
	{
		return ohm;
	}
	
	public double getValue( long t0 )
	{
		if( circuit != null )
		{
			double t = toSeconds(t0);
			double I0 = circuit.getI0(t0);
			double w = circuit.getAngle();
			double phi = circuit.getPhi();
			
			return reactance(w)*I0*Math.sin(w*t-phi);
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
			
			return reactance(w)*I0*Math.cos(w*t-phi);
		}
		
		return 0.0;
	}
	
	public String getImageName()
	{
		return "resistor.gif";
	}
	
	public String getMeterLabel()
	{
		return "~!V~vR~v~!";
	}
	
	public Color getColor()
	{
		return color;
	}

	public String getCheckboxName()
	{
		return getMeterLabel();
	}
	
	public double getOhm()
	{
		return ohm;
	}
	
	public void setOhm( double ohm )
	{
		this.ohm = ohm;
		retrace();
	}
}
