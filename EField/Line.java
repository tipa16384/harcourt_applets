import java.awt.*;

public class Line extends Component
{
	int width;
	
	public Line( int w )
	{
		width = w;
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(16,width);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void paint( Graphics g )
	{
		g.setColor( getForeground() );
		
		if( width == 1 )
			g.drawLine( 0, 0, getSize().width, 0 );
		else
			g.fillRect( 0, 0, getSize().width, width );
	}
}
