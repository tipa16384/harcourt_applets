package Wired;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.beans.*;

public class Phasor extends Component
					implements ActionListener
{
	Vector traces = new Vector();
	
	public Phasor()
	{
		setForeground( Color.black );
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension( 65, 65 );
	}

	public void paint( Graphics g )
	{
		Dimension size = getSize();
		
		int dd = Math.min(size.width,size.height);
		int dx = (size.width-dd)/2;
		int dy = (size.height-dd)/2;
		
		int x0 = size.width/2;
		int y0 = size.height/2;
		
		g.setColor( getForeground() );
		g.drawLine( x0, dy, x0, dy+dd );
		g.drawLine( dx, y0, dx+dd, y0 );
		
		int len = traces.size();
		int i;
		
		for( i = 0; i < len; ++i )
		{
			TraceElement te = (TraceElement) traces.elementAt(i);

			if( te != null )
			{
				if( te.ce.showTrace() )
				{
					int y = te.ts.yVal(te.ce,te.ts.getTime(),dd);
					int x = te.ts.xVal(te.ce,te.ts.getTime(),dd);
					g.setColor( te.ce.getColor() );
					
					Arrow.drawLine( g, x0, y0, dx+x, dy+y, 2, 8, 8 );
				}
			}
		}
	}
	
	public void actionPerformed( ActionEvent e )
	{
		String cmd = e.getActionCommand();
		
		if( cmd.equals(Wired.RETRACE) )
		{
			repaint();
		}
	}
	
	public void add( Oscilloscope ts, CircuitElement ce )
	{
		traces.addElement( new TraceElement(ts,ce) );
	}
	
	public void removeAllTraces()
	{
		traces.removeAllElements();
		repaint();
	}
	
	class TraceElement
	{
		Oscilloscope ts;
		CircuitElement ce;
		
		public TraceElement( Oscilloscope ts, CircuitElement ce )
		{
			this.ts = ts;
			this.ce = ce;
		}
	}
}
