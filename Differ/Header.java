import java.awt.*;
import java.awt.event.*;
import Wired.*;

// header definition

class Header extends PaddedPanel
{
	ActionListener listeners=null;
	
	public Header( String title )
	{
		super( new BorderLayout(), 0, 0, 2, 0 );
		
		Panel p = new Panel();
		FakeButton b;
		
		b = new FakeButton("Reset");
		b.setBackground( Color.red );
		b.setForeground( Color.white );
		b.setSize(b.getPreferredSize());
		b.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					broadcast( new ActionEvent(this,0,Differ.RESET) );
				}
			} );
		
		p.add( b );
		add( p, BorderLayout.EAST );
		
		RawLabel l;

		if( Utility.debug )
			title += " ["+GraphInfo.dateInfo+"]";
		
		l = new RawLabel( title );
		l.setFont( new Font("SansSerif",Font.BOLD,14) );
		l.setSize( l.getPreferredSize() );
		add( l, BorderLayout.WEST );
	}
	
	// handle the action listener for detecting state changes.
	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}
	
	public void broadcast( ActionEvent e )
	{
		if( listeners != null )
		{
			listeners.actionPerformed(e);
		}
	}
	
	public void paint( Graphics g )
	{
		super.paint( g );
		
		Dimension size = getSize();
		g.drawLine( 0, size.height-1, size.width, size.height-1 );
	}
}

