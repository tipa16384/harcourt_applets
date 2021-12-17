package Wired;

import java.awt.*;

class PoopUp extends Panel
{
	public PoopUp( Component c1, Component c2, String label, String units )
	{
		super( new BorderLayout() );
		
		if( label != null )
			add( new RawLabel(label), BorderLayout.NORTH );
		
		if( units != null )
			add( new RawLabel(units), BorderLayout.EAST );
		
		if( c2 == null )
			add( c1, BorderLayout.CENTER );
		else
		{
			Panel p = new Panel(new BorderLayout());
			p.add( c2, BorderLayout.EAST );
			p.add( c1, BorderLayout.CENTER );
			add( p, BorderLayout.CENTER );
		}
	}
}
