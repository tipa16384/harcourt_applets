import java.awt.*;
import java.awt.event.*;

public class StandardControl extends Component
{
	private FlipbookController controller = null;
	
	public StandardControl( FlipbookController controller )
	{
		this.controller = controller;
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
		Dimension size = getMinimumSize();
		setSize(size.width,size.height);
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(76,13);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void paint( Graphics g )
	{
		Dimension size = getSize();
	
		//System.out.println("painting control at "+size);
		
		g.setColor( ParsedClass.gray );
		g.fillRect( 0, 0, size.width-1, size.height-1 );
		
		g.setColor( Color.black );
		g.setFont( new Font("SanSerif", Font.PLAIN, 10 ) );
		g.drawString( getLabel(), 8, 10 );
		
		int xPoints[] = { 57, 62, 62 };
		int yPoints[] = { 6, 1, 11 };
		
		int xPoints2[] = { 68, 73, 68 };
		int yPoints2[] = { 1, 6, 11 };
				
		if ( (controller.getCurrentStep() != 1) && (controller.getCurrentStep() != controller.getTotalSteps()) )
		{
			g.setColor( ParsedClass.red );
			
			g.drawPolygon( xPoints, yPoints, 3 );
			g.fillPolygon( xPoints, yPoints, 3 );
			
			g.drawPolygon( xPoints2, yPoints2, 3 );
			g.fillPolygon( xPoints2, yPoints2, 3 );
		}
		else if ( controller.getCurrentStep() == 1 )
		{
			g.setColor( ParsedClass.orange );
			g.drawPolygon( xPoints, yPoints, 3 );
			g.fillPolygon( xPoints, yPoints, 3 );
			
			g.setColor( ParsedClass.red );
			g.drawPolygon( xPoints2, yPoints2, 3 );
			g.fillPolygon( xPoints2, yPoints2, 3 );
		}
		else // last step
		{
			g.setColor( ParsedClass.red );
			g.drawPolygon( xPoints, yPoints, 3 );
			g.fillPolygon( xPoints, yPoints, 3 );
			
			g.setColor( ParsedClass.orange );
			g.drawPolygon( xPoints2, yPoints2, 3 );
			g.fillPolygon( xPoints2, yPoints2, 3 );
		}
	}
	
	protected String getLabel()
	{
		return ( controller.getCurrentStep() + " of " + controller.getTotalSteps() );
	}
	
	protected void processMouseEvent( MouseEvent e )
	{
		if( e.getID() == MouseEvent.MOUSE_CLICKED )
		{
	    	Point p = e.getPoint();
	    	    	
			if ( (p.x >= 55) && (p.x <= 64) && (p.y >= 1) && (p.y <= 13) )
			{
				controller.stepBackward();
			}
			else if ( (p.x >= 66) && (p.x <= 75) && (p.y >= 1) && (p.y <= 13) )
			{
				controller.stepForward();
			}
		}
	}
}