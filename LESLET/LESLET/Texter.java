import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

public class Texter extends Component
					implements Serializable
{
	String text = "";
	int width=0, height=0;
	
	transient Vector wordList = new Vector();
	transient Vector lineList = new Vector();
	
	transient int selPosition = -1;
	transient int ow = -1;
	
	transient boolean active = true;
	
	public Texter()
	{
		this("");
	}
	
	public Texter( String s )
	{
		setText(s);
	}
	
	public String toString()
	{
		return getClass().getName()+"["+getBounds()+","+text+"]";
	}
	
	public Object dupe()
	{
		Texter nt = new Texter( text );
		nt.setForeground( getForeground() );
		nt.setFont( getFont() );
		Dimension size = getSize();
		nt.setSize( size.width, size.height );
		return nt;
	}

	private void writeObject(java.io.ObjectOutputStream out)
				throws IOException
	{
		//System.out.println("Texter.writeObject");
		out.writeObject(text);
		out.writeObject( new Integer(width) );
		out.writeObject( new Integer(height) );
		Rectangle bounds = getBounds();
		out.writeObject(bounds);
		//System.out.println("---> bounds are "+bounds);
	}
	
	private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException
	{
		String state = "";
		
		try
		{
			state = "reading text";
			text = (String) in.readObject();
			width = ((Integer) in.readObject()).intValue();
			height = ((Integer) in.readObject()).intValue();
			
			state = "setting text";
			setText( text );
			state = "reading bounds";
			Rectangle bounds = (Rectangle) in.readObject();
			state = "setting bounds";
			setBounds( bounds );
			//System.out.println("---> bounds are "+bounds);
		}
		
		catch( IOException e )
		{
			System.err.println("Texter.readObject failed while "+state+": "+e);
			throw e;
		}
		
		catch( ClassNotFoundException e )
		{
			System.err.println("Texter.readObject failed while "+state+": "+e);
			throw e;
		}
		
		catch( Exception e )
		{
			System.err.println("Texter.readObject failed while "+state+": "+e);
		}
	}

	public void lull()
	{
		active = false;
		//repaint();
	}
	
	public void anger()
	{
		active = true;
		requestFocus();
		//repaint();
	}
	
	public String getText()
	{
		return text;
	}
	
	protected void processKeyEvent( KeyEvent e )
	{
		if( active && selPosition >=0 )
		{
			int code = e.getKeyCode();
			char ch = e.getKeyChar();
			
			switch( e.getID() )
			{
				case KeyEvent.KEY_TYPED:
					{
						//debug("key event "+e);
						debug("char is "+ch);
						
						switch( ch )
						{
							default:
								insertCharacter( ch );
								break;
							
							case KeyEvent.VK_BACK_SPACE:
								deleteCharacter(true);
								break;
						}
					}
					break;
				
				case KeyEvent.KEY_PRESSED:
					{
						//debug("key event "+e);
						debug("code is "+e.getKeyText(code));
						
						switch( code )
						{
							case KeyEvent.VK_DELETE:
								deleteCharacter(false);
								break;
							
							case KeyEvent.VK_LEFT:
								cursorLeft();
								break;
							
							case KeyEvent.VK_RIGHT:
								cursorRight();
								break;
						}
					}
					break;
			}
		}
		
		super.processKeyEvent( e );
	}
	
	void deleteCharacter( boolean toLeft )
	{
		debug("deleteCharacter "+toLeft);
		
		if( toLeft )
		{
			if( selPosition > 0 )
			{
				String s = getText();
				adjustText( s.substring(0,selPosition-1) +
						 s.substring(selPosition) );
				--selPosition;
			}
		}
		
		else
		{
			String s = getText();
			adjustText( s.substring(0,selPosition) +
					 s.substring(selPosition+1) );
			s = getText();

			if( selPosition > s.length() )
				selPosition = s.length();
		}
	}

	void cursorLeft()
	{
		debug("cursorLeft");
		--selPosition;
		if( selPosition < 0 )
			selPosition = 0;
		repaint();
	}
	
	void cursorRight()
	{
		debug("cursorRight");
		++selPosition;
		int len = getText().length();
		if( selPosition > len )
			selPosition = len;
		repaint();
	}
	
	protected void processMouseEvent( MouseEvent e )
	{
		if( active )
		{
			switch( e.getID() )
			{
				case MouseEvent.MOUSE_CLICKED:
					setCursorPosition( e.getPoint() );
					break;
			}
		}
				
		super.processMouseEvent(e);
	}
	
	void setCursorPosition( Point p )
	{
		Dimension size = getSize();
		FontMetrics fm = getFontMetrics(getFont());
		int fh = fm.getHeight();
		int nlines = lineList.size();
		
		int row = p.y / fh;
		
		debug("click point is "+p+" and fh is "+fh+", therefore row is "+row);
		debug("a bit of trivia: there are "+nlines+" lines!");
		
		if( row >= nlines )
		{
			selPosition = text.length();
		}
		
		else
		{
			int i;
			int wid;
			
			selPosition = 0;
			
			for( i=0; i < row; ++i )
				selPosition += ((String)lineList.elementAt(i)).length();

			String line = (String) lineList.elementAt(i);
			
			wid = fm.stringWidth(line);
			debug("the width of our very special line is "+wid);
			
			if( p.x >= wid )
			{
				debug("being less than the click, cursor goes at the end");
				selPosition += line.length();
			}
			
			else
			{
				int linelen = line.length();
				
				for( i=0; i<linelen; ++i )
				{
					String s = line.substring(0,i);
					wid = fm.stringWidth(s);
					if( wid > p.x )
					{
						if( i > 0 )
							selPosition += i-1;
						
						break;
					}
					
					else if( wid == p.x )
					{
						selPosition += i;
						break;
					}
				}
				
				if( i == linelen )
					selPosition += line.length();
			}
		}

		debug("selPosition is "+selPosition);
		
		repaint();
	}

	public void insertCharacter( char ch )
	{
		//debug("insert "+ch);
		
		adjustText( text.substring(0,selPosition) + ch +
				 text.substring(selPosition) );
		
		++selPosition;
	}
	
	public void setText( String s )
	{
		adjustText( s );
		selPosition = -1;
	}
	
	void adjustText( String s )
	{
		String old = text;
		text = s;
		
		makeWordList();
		makeLineList();

		repaint();
	}

	public void setBounds( int x, int y, int w, int h )
	{
		super.setBounds( x, y, w, h );
		
		//System.out.println("set bounds to "+w+"x"+h);
		
		if( w > 0 && h > 0 )
		{
			width = w;
			height = h;
		}
		
		if( ow != w )
		{
			ow = w;
			makeWordList();
			makeLineList();
			Component parent = getParent();
			if( parent != null ) parent.doLayout();
			//repaint();
		}
	}
	
	public void setFont( Font f )
	{
		super.setFont( f );
		makeLineList();
		repaint();
	}
	
	public void setForeground( Color col )
	{
		super.setForeground( col );
		repaint();
	}
	
	void makeWordList()
	{
//		debug("makeWordList");
		
		try
		{
			wordList = new Vector();
	
			if( getText() != null )
			{
				Vector words = new Vector();
				
				char [] chars = getText().toCharArray();
				int cl = chars.length;
				Word word = null;		
				
				for( int i=0; i<cl; ++i )
				{
					char ch = chars[i];
					boolean fairlyPale = Character.isWhitespace(ch);
					
					if( word == null ||
						ch == '\n' ||
						fairlyPale != word.getsLostInASnowstorm() )
					{
						word = new Word(ch);
						wordList.addElement(word);
						if( ch == '\n' )
							word = null;
					}
					
					else
					{
						word.addChar(ch);
					}
				}
			}
		}
		
		catch( Exception e )
		{
			debug("in makeWordList: "+e);
		}
	}

	void makeLineList()
	{
//		debug("makeLineList");
		String state = "starting makeLineList";
		
		try
		{
			state = "allocating lineList";
			
			lineList = new Vector();
			
			state = "seeing if getText returns null";
					
			if( getText() != null )
			{
				Dimension size = getSize();
				
				//debug("current size is "+size);
			
				state = "getting font & metrics";
					
				Font f = getFont();
				if( f == null ) return;
				FontMetrics fm = getFontMetrics(f);
				int len;
				int i;
				
				state = "looking at wordList";
				
				len = wordList.size();
				String aline = null;
				int lw = 0;
				
				for( i = 0; i < len; ++i )
				{
					Word w = (Word) wordList.elementAt(i);

					state = "getting "+w;
					
					state = "setting word width";
					
					w.setWidth(fm);
					int ww = w.getWidth();
					
					if( w.getText().charAt(0) == '\n' )
					{
						state = "return char processing";
						
						aline = "";
						lineList.addElement(aline);
						lw = 0;
						continue;
					}
					
					else if( aline == null || (lw+ww) > size.width )
					{
						state = "whitespace removal";
						
						if( w.getsLostInASnowstorm() )
						{
							aline = null;
							continue;
						}
						
						else
						{
							state = "adding word to line";
							
							aline = "";
							lineList.addElement(aline);
							lw = 0;
						}
					}
					
					if( aline != null )
					{
						state = "modifying line";
						
						lineList.removeElement(aline);
						aline += w.getText();
						lineList.addElement(aline);
					}
		
					lw += ww;
				}
			}
		}
		
		catch( Exception e )
		{
			debug("makeLineList: while "+state+": "+e);
		}
	}
	
	public Dimension getMinimumSize()
	{
		//System.out.println("getMinimumSize -- "+width+"x"+height);
		
		Dimension dim;
		
		if( width <= 0 || height <= 0 )
		{
			FontMetrics fm = getFontMetrics(getFont());
			dim = new Dimension(128,fm.getHeight());
		}
		
		else
		{
			FontMetrics fm = getFontMetrics(getFont());
			makeLineList();
			dim = new Dimension(width,fm.getHeight()*lineList.size());
		}
		
		//System.out.println(" ---->>>> returns "+dim);
		
		return dim;
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void validate()
	{
		//debug("validate");

		makeLineList();
		super.validate();
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		try
		{
			Dimension size = getSize();
			Font f = getFont();
			FontMetrics fm = getFontMetrics(f);
			
			g.setColor( getForeground() );
			g.setFont( f );
			
			int len = lineList.size();
			int i;
			
			int cursorPos = 0;
	
			for( i = 0; i < len; ++i )
			{
				String line = (String) lineList.elementAt(i);
				int j;
				int mark = -1;
				
				int newpos = cursorPos + line.length();
				
				try
				{
					if( active && cursorPos <= selPosition && 
						(selPosition < newpos || ((i == (len-1))&&(selPosition >= newpos))) )
					{
						String temps = line.substring(0,selPosition-cursorPos);
						mark = fm.stringWidth(temps);
					}
				}
				
				catch( java.lang.StringIndexOutOfBoundsException soobe )
				{
					debug("in paint: string index out of bounds");
				}
				
				int ly = i * fm.getHeight();
				
				g.drawString(line,0,ly+fm.getAscent());
				
				if( mark >= 0 )
				{
					g.drawLine(mark,ly,mark,ly+fm.getHeight()-1);
				}
	
				cursorPos = newpos;
			}
		}
		
		catch( Exception e )
		{
			debug("in paint: "+e);
		}
	}
	
	static boolean debug = true;
	static void debug( String s )
	{
		if( debug )
			System.err.println("Texter:: "+s);
	}
	
	class Word
	{
		String text = "";
		boolean blank;
		int width = 0;;
				
		public Word( char ch )
		{
			text = new String( new char[]{ ch } );
			blank = Character.isWhitespace(ch);
		}
		
		public void addChar( char ch )
		{
			text += ch;
		}
		
		public String getText()
		{
			return text;
		}
		
		public boolean getsLostInASnowstorm()
		{
			return blank;
		}

		public void setWidth( FontMetrics fm )
		{
			width = fm.stringWidth(text);
		}
		
		public int getWidth()
		{
			return width;
		}
	}
}
