package Wired;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

public class NumedicChoice extends Choice
{
	String getter = null;
	String setter = null;
	Object target = null;
	Method mgetter = null;
	Method msetter = null;
	double mult = 1.0;
	
	public NumedicChoice( Object target, String getter, String setter, String [] choices, double mult )
	{
		this.target = target;
		this.getter = getter;
		this.setter = setter;
		this.mult = mult;
							
		mgetter = Utility.findMethod( target, getter, null );
		
		Class [] params = { double.class };
		
		msetter = Utility.findMethod( target, setter, params );
		
		//System.out.println("current value is "+getValue());
		
		enableEvents(AWTEvent.ITEM_EVENT_MASK);

		int selection = 0;
		double value = getValue();
		
		for( int i=0; i<choices.length; ++i )
		{
			String s;
			
			s = choices[i];
			
			add( s );
			
			if( value == Utility.parseDouble(s) )
				selection = i;
		}

		select( selection );
	}
	
	public NumedicChoice( Object target, String funcName, String [] choices, double mult )
	{
		this(target,"get"+Utility.capital(funcName),"set"+Utility.capital(funcName),choices,mult);
	}
	
	protected void processItemEvent( ItemEvent e )
	{
		//System.out.println("got "+e);
		
		try
		{
			double val = Utility.parseDouble(getSelectedItem());
			setValue(val);
		}
		
		catch( NumberFormatException nfe )
		{
			System.err.println("Bad number format");
		}
		
		super.processItemEvent(e);
	}
	
	
	double getValue()
	{
		return Utility.getValue(target,mgetter)*mult;
	}

	void setValue( double val )
	{
		Utility.setValue( target, msetter, val/mult );
	}	
}
