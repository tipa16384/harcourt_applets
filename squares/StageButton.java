import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class StageButton extends Component
{
	String message;
	boolean enabled;
	GraphInfo info;
	
	FontMetrics fm;
	
	Color disabledFG = Color.white;
	Color disabledBG = new Color(0x66,0x66,0x66);
	Color enabledFG = Color.white;
	Color enabledBG = GraphInfo.POSITIVE_COLOR;
	
	static final int gap = 2;
	
	Vector als = new Vector();
	
	public StageButton( String name, String message, GraphInfo info )
	{
		this.message = message;
		this.info = info;
		setName(name);
		setEnabled(true);
		setFont(info.fontBigBold);
		enableEvents( AWTEvent.MOUSE_EVENT_MASK );
	}
	
	public void setEnabled( boolean enabled )
	{
		this.enabled = enabled;
		repaint();
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}

	public void setFont( Font font )
	{
		super.setFont(font);
		fm = getFontMetrics(font);
	}

	public Dimension getPreferredSize()
	{
		String name = getName();
		int sw = fm.stringWidth(name);
		return new Dimension(sw+2*gap,fm.getHeight()+2*gap);
	}
	
	public Color getForeground()
	{
		return isEnabled() ? enabledFG : disabledFG;
	}
	
	public Color getBackground()
	{
		return isEnabled() ? enabledBG : disabledBG;
	}
	
	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		
		g.setColor( getBackground() );
		g.fillRect( 0, 0, dim.width, dim.height );
		
		g.setColor( getForeground() );
		g.setFont( getFont() );
		
		String name = getName();
		int sw = fm.stringWidth( name );
		int asc = fm.getAscent();
		
		g.drawString( name, (dim.width-sw)/2, (dim.height+asc)/2-2 );
	}

	public void addActionListener( ActionListener al )
	{
		if( !als.contains(al) )
		{
			als.addElement(al);
		}
	}
	
	public void removeActionListener( ActionListener al )
	{
		if( als.contains(al) )
		{
			als.removeElement(al);
		}
	}

	protected void processMouseEvent( MouseEvent e )
	{
		// the button is enabled even when it looks like it isn't
		if( true || isEnabled() )
		{
			int id = e.getID();
			
			switch( id )
			{
				case MouseEvent.MOUSE_CLICKED:
					{
						int len = als.size();
						int i;
						
						for( i=0; i<len; ++i )
						{
							ActionListener al = (ActionListener) als.elementAt(i);
							ActionEvent ae = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"boom");
							al.actionPerformed( ae );
						}
					}
					break;
			}
		}
				
		super.processMouseEvent(e);
	}	
}
