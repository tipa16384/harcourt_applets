package Wired;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

public class NumedicField extends TextField
						  implements Incrementable
{
	String suffix = null;
	int chars = 0;
	String getter = null;
	String setter = null;
	Object target = null;
	Method mgetter = null;
	Method msetter = null;
	
	public NumedicField( Object target, String getter, String setter, String suffix, int chars )
	{
		super("");
		this.target = target;
		this.chars = (chars==0) ? 4 : chars;
		this.getter = getter;
		this.setter = setter;
		this.suffix = (suffix==null) ? "" : suffix;
		
		mgetter = Utility.findMethod( target, getter, null );
		
		Class [] params = { double.class };
		
		msetter = Utility.findMethod( target, setter, params );
		
		//System.out.println("current value is "+getValue());
		
		enableEvents(AWTEvent.TEXT_EVENT_MASK);
		
		resetText();
	}
	
	public void resetText()
	{
		String oldText = getText();
		String newText = Integer.toString((int)getValue());
		
		if( !newText.equals(oldText) )
		{
			setText(newText);
		}
	}
	
	public NumedicField( Object target, String getter, String setter )
	{
		this(target,getter,setter,null,0);
	}
	
	public NumedicField( Object target, String funcName )
	{
		this(target,"get"+Utility.capital(funcName),"set"+Utility.capital(funcName));
	}
	
	public void increment()
	{
		double newval = getValue() + 1.0;
		setValue(newval);
		setText(Integer.toString((int)newval));
	}
	
	public void decrement()
	{
		double newval = getValue() - 1.0;
		
		if( newval < 1.0 ) newval = 1.0;
		
		setValue(newval);
		setText(Integer.toString((int)newval));
	}
	
	protected void processTextEvent( TextEvent e )
	{
		//System.out.println("got "+e);
		
		try
		{
			double val = Utility.parseDouble(getText());
			setValue(val);
		}
		
		catch( NumberFormatException nfe )
		{
			System.err.println("Bad number format");
		}
		
		super.processTextEvent(e);
	}
	
	double getValue()
	{
		return Utility.getValue(target,mgetter);
	}

	void setValue( double val )
	{
		Utility.setValue( target, msetter, val );
		resetText();
	}	
}
