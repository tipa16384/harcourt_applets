package Wired;

import java.awt.*;

// support for describing a resistor.

public class Capacitor extends Element
{
	double farad = 100.0;
	Color color = new Color(0,102,255);
	static final double scale = 1E6;
		
	public String getImageName()
	{
		return "capacitor.gif";
	}
	
	public String getMeterLabel()
	{
		return "~!V~vC~v~!";
	}
	
	public Color getColor()
	{
		return color;
	}

	public String getCheckboxName()
	{
		return getMeterLabel();
	}
	
	public double reactance( double angle )
	{
		return scale/(angle*farad);
	}
	
	public void setFarad( double f )
	{
		farad = f;
		//System.out.println("setFarad("+f+")");
		retrace();
	}
	
	public double getFarad()
	{
		return farad;
	}

	public double getValue( long t0 )
	{
		//System.out.println("Capacitor: getValue()");
	
		if( circuit != null )
		{
			double t = toSeconds(t0);
			double I0 = circuit.getI0(t0);
			double w = circuit.getAngle();
			double phi = circuit.getPhi();
			
			double V = reactance(w)*I0*Math.sin(w*t-phi-Math.PI/2.0);
			
			//double V0 = circuit.getECM(t0);
			
			//System.out.println("V="+DoubleFormat.format(V)+
			//				"   V0="+DoubleFormat.format(V0)+
			//				"   dV="+DoubleFormat.format(Math.abs(V-V0)));
			
			return V;
		}
		
		return 0.0;
	}

	public double getPhase( long t0 )
	{
		//System.out.println("Capacitor: getValue()");
	
		if( circuit != null )
		{
			double t = toSeconds(t0);
			double I0 = circuit.getI0(t0);
			double w = circuit.getAngle();
			double phi = circuit.getPhi();
			
			double V = reactance(w)*I0*Math.cos(w*t-phi-Math.PI/2.0);
			
			//double V0 = circuit.getECM(t0);
			
			//System.out.println("V="+DoubleFormat.format(V)+
			//				"   V0="+DoubleFormat.format(V0)+
			//				"   dV="+DoubleFormat.format(Math.abs(V-V0)));
			
			return V;
		}
		
		return 0.0;
	}
}
