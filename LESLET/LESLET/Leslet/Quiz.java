import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Quiz extends ParsedClass
{
	public String heading = "untitled quiz";
	
	Vector components = new Vector();
	SelectionButton selected = null;
	
	int count = 0;
	
	final static int DIAM = 32;
	
	public Quiz()
	{
		setLayout( new TwoDLayout() );
	}

	public void setAnswer( Answer object )
	{
		debug("adding answer "+object+" to quiz");
		components.addElement( object );
		
		Panel p = new Panel( new BorderLayout() );
		p.add( object, BorderLayout.CENTER );
		p.add( new SelectionButton(count++,object), BorderLayout.WEST );
		add( p );
	}
	
	public String toString()
	{
		return getClass().getName()+"("+heading+")";
	}
	
	void select( SelectionButton b )
	{
		if( selected != null )
		{
			selected.selected = false;
			selected.repaint();
		}
		
		selected = b;
		
		if( selected != null )
		{
			selected.selected = true;
			selected.repaint();
		}
	}

	Window w = null;
	Panel popup = null;
	
	static final int inset = 8;
	
	class PopupPanel extends Panel
	{
		public PopupPanel()
		{
			super( new BorderLayout() );
			setBackground( new Color(204,204,204) );
			setForeground( Color.black );
		}
		
		public Insets getInsets()
		{
			return new Insets(inset,inset,inset,inset);
		}
		
		public void paint( Graphics g )
		{
			super.paint(g);
			
			g.setColor( getForeground() );
			Dimension dim = getSize();
			g.drawRect( 0, 0, dim.width-1, dim.height-1 );
		}
	}
	
	void popup( int which, Component object )
	{
		Answer ans = (Answer) components.elementAt(which);
		Ifselect res = ans.ifselect;
		res.setFont( ans.getFont() );

		Dimension adim = ans.getSize();
		int w = adim.width-2*inset;

		res.setSize( w, adim.height );
		res.doLayout();
		
		int h = res.getPreferredSize().height;
		res.setSize( w, h );
		//System.out.println("Response size is "+res.getSize());
	
		popup = new PopupPanel();
		add( popup, 0 );
		popup.add( res, BorderLayout.CENTER );
		popup.setSize( w+2*inset, h+2*inset );
		res.validate();
		popup.validate();
		
		Point oloc = ans.getLocation();
		Point ploc = ans.getParent().getLocation();
		
		popup.setLocation( oloc.x+ploc.x, oloc.y+ploc.y );
		
	/*
		Component c, parent;
		
		for( c = this; c != null && !(c instanceof Frame); c = c.getParent() )
			;
		
		w = new PopupWindow( (Frame) c );
		w.setLayout( new BorderLayout() );
		w.add( res, BorderLayout.CENTER );
		
		Insets insets = w.getInsets();
		
		w.setSize( res.getWidth()+insets.left+insets.right,
				   res.getHeight()+insets.top+insets.bottom );
		
		Point loc = ans.getLocationOnScreen();
		w.setLocation( loc.x, loc.y );
		w.doLayout();
		w.show();
	*/
	
	}

	class PopupWindow extends Window
	{
		public PopupWindow( Frame f )
		{
			super( f );
			setForeground( Color.black );
			setBackground( Color.white );
		}
		
		public Insets getInsets()
		{
			return new Insets(3,3,3,3);
		}
		
		public void paint( Graphics g )
		{
			//System.out.println("repainting response window");
			
			int len = getComponentCount();
			
			for( int i=0; i<len; ++i )
			{
				Component c = getComponent(i);
				//System.out.println("#"+(i+1)+": "+c);
			}
			
			super.paint( g );
			
			Dimension size = getSize();
			
			g.setColor( getForeground() );
			g.drawRect( 0, 0, size.width-1, size.height-1 );
		}
		
	}

	void pulldown()
	{
		if( w != null )
		{
			w.dispose();
			w = null;
		}

		if( popup != null )
		{
			remove( popup );
			popup = null;
		}
	}

	class SelectionButton extends Component
	{
		int which;
		boolean selected;
		Component object;
		
		public SelectionButton( int which, Component object )
		{
			this.object = object;
			setBackground(red);
			setForeground(Color.white);
			setFont( new Font("Serif",Font.BOLD,16) );
			this.which = which;
			enableEvents( AWTEvent.MOUSE_EVENT_MASK );
			setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
		}
		
		protected void processMouseEvent( MouseEvent e )
		{
			switch( e.getID() )
			{
				case MouseEvent.MOUSE_ENTERED:
					select( this );
					break;
				
				case MouseEvent.MOUSE_EXITED:
					select( null );
					break;
				
				case MouseEvent.MOUSE_PRESSED:
					popup( which, object );
					break;
				
				case MouseEvent.MOUSE_RELEASED:
					pulldown();
					break;
			}
		}
		
		public Dimension getMinimumSize()
		{
			return new Dimension((DIAM*3)/2,DIAM);
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension((DIAM*3)/2,DIAM);
		}
		
		public void paint( Graphics g )
		{
			Dimension size = getSize();
			int x = (size.width-DIAM)/2;
			int y = (size.height-DIAM)/2;
			
			g.setColor( selected ? red : gray );
			g.fillOval( x, y, DIAM, DIAM );
			
			g.setColor( selected ? Color.white : Color.black );
			String nstr = Integer.toString(which+1);
			
			Font f = getFont();
			g.setFont(f);
			FontMetrics fm = getFontMetrics(f);
			int sw = fm.stringWidth(nstr);
			int dx = (DIAM-sw)/2;
			int fh = fm.getAscent();
			int dy = (DIAM+fh)/2-2;
			g.drawString( nstr, x+dx, y+dy );
		}
	}
}
