import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;

public class Adjuster extends PaddedPanel
							  implements AdjustmentListener
{
	GraphInfo info;
	String setprop;

	public Adjuster( GraphInfo info, String prop, boolean wide )
	{
		super( new BorderLayout(), 0, wide?20:5, 0, wide?20:5 );
	
		String txtprop;
		String untprop;
		String minprop;
		String maxprop;
		String getprop;
		String varprop;
		GordySlider slider;
	
		this.info = info;
		
		char [] chars = prop.toCharArray();
		if( Character.isLowerCase(chars[0]) )
			chars[0] = Character.toUpperCase(chars[0]);
		String caprop = new String(chars);
		
		minprop = "getMin"+caprop;
		maxprop = "getMax"+caprop;
		getprop = "get"+caprop;
		setprop = "set"+caprop;
		txtprop = "get"+caprop+"Text";
		untprop = "get"+caprop+"Units";
		varprop = "get"+caprop+"Var";
		
		String label = (String)weedle(txtprop);
		if( label != null )
		{
			Component c = new RawLabel(label,Label.LEFT);
			c.setFont( new Font("SansSerif",Font.PLAIN,11) );
			add( c, BorderLayout.NORTH );
		}

		Integer omin = (Integer)weedle(minprop);
		Integer omax = (Integer)weedle(maxprop);
		Integer oval = (Integer)weedle(getprop);
		
		int min = (omin == null) ? 1 : omin.intValue();
		int max = (omax == null) ? 10 : omax.intValue();
		int val = (oval == null) ? min : oval.intValue();

		slider = new GordySlider((String)weedle(varprop),min,max,val,(String)weedle(untprop));
		slider.addAdjustmentListener(this);
		
		add( slider, BorderLayout.CENTER );
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		int val = e.getValue();
		
		try
		{
			Class [] cl = { int.class };
			Object [] ol = { new Integer(val) };
			
			Method m = info.getClass().getMethod(setprop,cl);
			m.invoke(info,ol);
		}
		
		catch( NoSuchMethodException nsme )
		{
			System.err.println("no such method: "+setprop);
		}
		
		catch( Exception ex )
		{
			System.err.println("While invoking "+setprop+", got "+ex);
		}
		
	}
	
	Object weedle( String name )
	{
		Object o = null;
		
		try
		{
			Class [] cl = {};
			Object [] ol = {};
			
			Method m = info.getClass().getMethod(name,cl);
			o = m.invoke(info,ol);
		}
		
		catch( NoSuchMethodException nsme )
		{
			System.err.println("no such method: "+name);
		}
		
		catch( Exception e )
		{
			System.err.println("While invoking "+name+", got "+e);
		}
		
		return o;
	}
}
