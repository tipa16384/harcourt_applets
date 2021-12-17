package Wired;

import java.awt.event.*;
import java.awt.*;

public class PowerSource extends Element
{
	double amplitude = 150.0;	// volts
	double frequency = 200.0;	// hertz
	
	Color color = new Color(102,102,102);
	
	public PowerSource()
	{
	}
	
	public String getImageName()
	{
		return "power.gif";
	}

	public String getMeterLabel()
	{
		return "~!V~v~E~v~!";
	}
	
	public double getAmplitude()
	{
		return amplitude;
	}
	
	public void setAmplitude( double a )
	{
		amplitude = Math.max(1.0,Math.min(250.0,a));
		retrace();
	}
	
	public String getCheckboxName()
	{
		return "~!~e~!";
	}

	public double getValue( long t0 )
	{
		return amplitude * Math.sin(getAngle()*toSeconds(t0));
	}
	
	public double getPhase( long t0 )
	{
		return amplitude * Math.cos(getAngle()*toSeconds(t0));
	}
	
	public double getFrequency()
	{
		return frequency;
	}
	
	public void setFrequency( double f )
	{
		//System.out.println("PowerSource.setFrequency("+f+")");
		frequency = Math.max(1.0,Math.min(2000.0,f));
		retrace();
	}

	public Color getColor()
	{
		return color;
	}
}
