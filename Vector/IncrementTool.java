import java.awt.*;
import java.awt.event.*;

public class IncrementTool extends Component
{
	IntegerTextField field;
	
	final int height = 6;
	final int width = 2*height-1;
	final int gap = 2;
	final int totalHeight = 2*height+gap;
	
	public IncrementTool( IntegerTextField f )
	{
		setForeground( Color.red );
		field = f;
		if( field != null )
			addMouseListener( new MouseAdapter()
				{
					public void mousePressed( MouseEvent e )
					{
						int y = e.getY();
						int z = getSize().height/2;
						
						if( y < z )
							field.increment();
						else
							field.decrement();
					}
				} );
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(width+1,totalHeight+1);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void paint( Graphics g )
	{
		if( field != null )
		{
			g.setColor( getForeground() );
			for( int i=1; i<=height; ++i )
			{
				int len = 2*i-1;
				g.drawLine( height-i, i-1, height-i+len, i-1 );
				g.drawLine( height-i, totalHeight-i, height-i+len, totalHeight-i );
			}
		}
	}
}
