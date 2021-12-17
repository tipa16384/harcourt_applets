package Wired;

import java.awt.event.*;
import java.awt.*;

public abstract class Element extends GenericIcon
					 implements CircuitElement
{
	ActionListener listeners = null;
	RawCheckbox checkbox = null;
	static Circuit circuit = null;
	
	double phiMult = 1.0;
	
	boolean visible;
	
	public Element()
	{
		super( (Image)null, null, "element" );
		setIcon( Utility.getImage(this,getImageName()) );
	}

	public void setVisible( boolean v )
	{
		visible = v;
	}
	
	public boolean isVisible()
	{
		return visible;
	}

	public double R( double angle )
	{
		return visible ? reactance(angle) : 0.0;
	}
	
	public double reactance( double angle )
	{
		return 0.0;
	}

	public void retrace()
	{
		if( circuit != null )
		{
			ActionEvent e = new ActionEvent(this,0,Wired.RETRACE);
			circuit.broadcast(e);
		}
	}

	public String getImageName()
	{
		return "power.gif";
	}

	public String getMeterLabel()
	{
		return "V";
	}
	
	public String getMeterUnits()
	{
		return "V";
	}

	static public double toSeconds( long t0 )
	{
		return ((double)t0)/1E6;
	}

	public double getAngle()
	{
		if( circuit == null )
			return 0.0;
		else
			return circuit.getAngle();
	}

	public static void setCircuit( Circuit c )
	{
		//System.out.println("Element.circuit set to "+c);
		circuit = c;
	}
	
	public String getCheckboxName()
	{
		return "?";
	}

	public RawCheckbox getCheckbox()
	{
		if( checkbox == null )
		{
			checkbox = new RawCheckbox(getCheckboxName(),Label.LEFT,true);
			checkbox.setSpacing(20);
			checkbox.setVerticalAlignment( Label.RIGHT );
			checkbox.addItemListener( new ItemListener()
				{
					public void itemStateChanged( ItemEvent e )
					{
						retrace();
					}
				} );
		}
		
		return checkbox;
	}

	public boolean showTrace()
	{
		return getCheckbox().getState();
	}

	public void setBounds( int x, int y, int w, int h )
	{
		Dimension size = getPreferredSize();
		
		super.setBounds( x+(w-size.width)/2,
						 y+(h-size.height)/2,
						 size.width, size.height );
	}

	// handle the action listener for detecting state changes.
	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}
	
	public Color getColor()
	{
		return GraphInfo.SEPARATE_COLOR;
	}
}
