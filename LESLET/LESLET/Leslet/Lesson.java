import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Lesson extends ParsedClass
{
	static Vector pages = new Vector();
	
	Container buttonBar;
	Container pageThing;
	Vector pageButtons = new Vector();
	
	transient Page currentPage = null;
	
	public Lesson()
	{
		setLayout( new BorderLayout() );
		buttonBar = new Panel( new TwoDLayout() );
		add( buttonBar, BorderLayout.WEST );
		pageThing = new Panel( new BorderLayout() );
		add( pageThing, BorderLayout.CENTER );
		Main.lesson = this;
	}
	
	public void setPage( Page page )
	{
		debug("adding page "+page+" to lesson");
		pages.addElement( page );
			
		PageButton pb = new PageButton(page);
		pageButtons.addElement(pb);
		buttonBar.add( pb );
	}

	public void finish()
	{
		debug("compiled lesson");
		debug(pages.size()+" pages.");

		if( pages.size() >= 1 )
			gotoPage( 0 );
	}
	
	void gotoPage( int index )
	{
		int len = pages.size();
		
		if( index >= 0 && index < len )
		{
			Page page = (Page) pages.elementAt(index);
			gotoPage( page );
		}
	}

	void gotoPage( Page page )
	{
		pageThing.removeAll();

		page.setBounds(0,0,0,0);
		pageThing.add( page, BorderLayout.CENTER );
		currentPage = page;
		
		//page.invalidate();
		//page.validate();
		invalidate();
		validate();
		/*
		doLayout();
		repaint();
		*/
		int i, len = pageButtons.size();
		for( i=0; i<len; ++i )
		{
			PageButton pb = (PageButton) pageButtons.elementAt(i);
			pb.repaint();
		}
	}

	class PageButton extends Component
	{
		Page page;
		
		public PageButton( Page page )
		{
			this.page = page;
			enableEvents(AWTEvent.MOUSE_EVENT_MASK);
			setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
		}

		protected void processMouseEvent( MouseEvent e )
		{
			if( e.getID() == MouseEvent.MOUSE_CLICKED )
			{
				gotoPage(page);
			}
			
			super.processMouseEvent(e);
		}

		public void paint( Graphics g )
		{
			Font f = getFont();
			FontMetrics fm = getFontMetrics(f);
			Color fg, bg;
			
			if( page == currentPage )
			{
				bg = red;
				fg = Color.white;
			}
			
			else
			{
				bg = gray;
				fg = Color.black;
			}
			
			Dimension size = getSize();
			g.setColor( bg );
			g.fillRect( 0, 0, size.width-1, size.height-1 );
			
			g.setColor( fg );
			String s = page.getTitle();
			int w = fm.stringWidth(s);
			int x = (size.width-w)/2;
			int h = fm.getAscent()+fm.getDescent();
			int y = (size.height-h)/2+fm.getAscent();
			g.drawString( s, x, y );
		}
		
		public Dimension getMinimumSize()
		{
			Font f = getFont();
			FontMetrics fm = getFontMetrics(f);
			
			int height = 2 * fm.getHeight();
			int width = fm.stringWidth(page.getTitle()) + 16;
			
			return new Dimension(width,height);
		}
		
		public Dimension getPreferredSize()
		{
			return getMinimumSize();
		}
	}
}
