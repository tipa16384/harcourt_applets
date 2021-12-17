import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;

public class Meter extends RawLabel implements ActionListener
{
	TimeSource timer;
	CircuitElement target;
	String prefix;
	String suffix;
	
	public Meter( TimeSource timer, CircuitElement target )
	{
		this.timer = timer;
		this.target = target;
		
		timer.addActionListener(this);
		
		refresh();
	}
	
	public CircuitElement getTarget()
	{
		return target;
	}
	
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();
		d.width = Math.max(75,d.width);
		return d;
	}
	
	public void actionPerformed( ActionEvent evt )
	{
		//System.out.println("Meter got "+evt);
		
		if( evt.getActionCommand().equals(CC2.RETRACE)
		 || evt.getActionCommand().equals(CC2.POPUP) )
				refresh();
	}
	
	void refresh()
	{
		int sel = CircuitElement.VOLTAGE;
		if( target.getMeterUnits().equalsIgnoreCase("A") )
			sel = CircuitElement.CURRENT; 
		double value = target.getValue( sel, timer.getTime() );
		setText(target.getMeterLabel()+" = "+
				DoubleFormat.format(value)+
				" "+target.getMeterUnits());
	}
}
