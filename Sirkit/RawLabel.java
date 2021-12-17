import java.awt.*;

public class RawLabel extends Component
{
	final int pad = 0;
	
	static final int NONE = 0;
	static final int ITALIC = 0x0001;
	static final int SUPER = 0x0002;
	static final int SUB = 0x0004;
	static final int COLOR = 0x0008;
	static final int END = 0x8000;
	static final char QUOTE = '~';

	static final Color variableColor = new Color(153,51,0);
	
	int [] skank = null;
	int skanky = NONE;
	int alignment = Label.LEFT;
	int valign = Label.CENTER;
	
	boolean underlined = false;
		
	public RawLabel( String text, int align, int valign )
	{
		setText( text );
		setAlignment( align );
		setVerticalAlignment( valign );
	}
	
	public RawLabel( String text, int align )
	{
		this( text, align, Label.CENTER );
	}
	
	public RawLabel()
	{
		this("",Label.LEFT);
	}
	
	public RawLabel( String text )
	{
		this(text,Label.LEFT);
	}
	
	public RawLabel( int align )
	{
		this("",align);
	}
	
	public void setAlignment( int align )
	{
		alignment = align;
		repaint();
	}
	
	public void setVerticalAlignment( int align )
	{
		valign = align;
		repaint();
	}
	
	public boolean getUnderline()
	{
		return underlined;
	}
	
	public void setUnderline(boolean under)
	{
		if( under != underlined )
		{
			underlined = under;
			repaint();
		}
	}
	
	public Font getFont()
	{
		Font f = super.getFont();
		if( f == null )
		{
			f = new Font("Serif",Font.PLAIN,12);
			setFont( f );
		}
		
		return f;
	}
	
	public Dimension getMinimumSize()
	{
		String name = getName();
		Font f = getFont();
		
		FontMetrics fm = getFontMetrics(f);
		int wid = 2*pad;
		int hgt = fm.getAscent()+fm.getDescent()+1+(2*pad);
		
		if( name != null )
			wid += fm.stringWidth(name);
		
		if( (skanky & SUB) != 0 )
			hgt += 4;
		
		if( (skanky & ITALIC) != 0 )
			wid += 2;

		if( underlined )
			++hgt;

		return new Dimension(wid,hgt);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void setText( String text )
	{
		if( text == null || text.length() == 0 )
		{
			skank = null;
			setName( "" );
			skanky = NONE;
		}
		
		else
		{
			int i;
			int curskank = NONE;
			
			StringBuffer sb = new StringBuffer();
			int len = text.length();
			skank = new int[len];
			skanky = NONE;
						
			for( i = 0; i < len; ++i )
				skank[i] = 0;
			
			boolean quote = false;
			
			for( i = 0; i < len; ++i )
			{
				char ch = text.charAt(i);
				
				if( quote )
				{
					switch( ch )
					{
						case 'i': curskank ^= ITALIC; break;
						case '^': curskank ^= SUPER; break;
						case 'v': curskank ^= SUB; break;
						case '!': curskank ^= COLOR; break;
						case '0': curskank = NONE; break;
						default:
							{
								switch(ch)
								{
				/* 'x' sign */		case '+': ch = '\u00D7'; break;
				/* alpha */			case 'a': ch = '\u03B1'; break;
				/* lambda */		case 'l': ch = '\u03BB'; break;
				/* micron */		case 'm': ch = '\u03BC'; break;
				/* omega */			case 'W': ch = '\u03A9'; break;
				///* tuo */			case 'T': ch = '\u03A4'; break;
				/* small tuo */		case 't': ch = '\u03C4'; break;
				/* small omega */	case 'w': ch = '\u03C9'; break;
				/* epsilon */		case 'e': ch = '\u03B5'; break;
				/* sigma */			case 's': ch = '\u03C3'; break;
				/* superscript 0 */	case 'o': ch = '\u00B0'; break;
				/* A-circle */		case 'A': ch = '\u00C5'; break;
								}
									
								skank[sb.length()] = curskank;
								sb.append(ch);
								skanky |= curskank;
							}
							
							break;
					}
					
					quote = false;
				}
				
				else if( ch == QUOTE )
					quote = true;
				
				else
				{
					skank[sb.length()] = curskank;
					sb.append(ch);
					skanky |= curskank;
				}
			}
			
			setName( new String(sb) );
	
			Component c = getParent();
			if( c != null )
			{
				c.invalidate();
				c.validate();
			}

			else
			{
				Dimension ps = getPreferredSize();
				Dimension cs = getSize();
				int w, h;
				
				setSize( Math.max(ps.width,cs.width),
						 Math.max(ps.height,cs.height) );
			}
		}
		
		repaint();
	}
	
	public String getText()
	{
		return getName();
	}
	
	public void paint( Graphics g )
	{
		paint( g, 0, 0, true );
	}
	
	public void paint( Graphics g, int x0, int y0, boolean erase )
	{
		String name = getName();
		
		//System.out.println("Displaying \""+name+"\" at ("+x0+","+y0+")");
		
		if( name == null || name.length() == 0 ) return;
		
		Dimension size = getSize();
		
		if( erase )
		{
			g.setColor( getBackground() );
			g.fillRect( x0,y0, size.width, size.height );
		}
				
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		
		g.setFont(f);
		g.setColor( getForeground() );
		
		int wid = fm.stringWidth(name);
		int x;
		
		switch( alignment )
		{
			case Label.CENTER:
				x = (size.width-wid)/2;
				break;
			
			case Label.RIGHT:
				x = size.width-wid;
				break;
			
			default:
				x = 0;
				break;
		}
		
		x += x0;
		
		int y;
		
		switch( valign )
		{
			default:
				y = y0;
				break;
			
			case Label.CENTER:
				y = (size.height-fm.getAscent())/2 + y0;
				break;
			
			case Label.RIGHT:
				y = (size.height-fm.getHeight());
				break;
		}
		
		y += fm.getAscent()-1;
		
		if( skanky == NONE )
			g.drawString( name, x, y );
		
		else
		{
			int start = 0;
			int i;
			int len = name.length();
			int curskank = END;

			for( i=0;; ++i )
			{
				int psk = (i>=len) ? END : skank[i];
				
				if( psk == curskank )
					continue;
				
				if( i > start )
					x += draw( g, name.substring(start,i), x, y, curskank );
				
				start = i;
				curskank = psk;
				
				if( i >= len ) break;
			}
		}

		if( underlined )
		{
			int yy = fm.getHeight()-1;
			
			g.drawLine(x0,yy,size.width,yy);
		}

	}
	
	private int draw( Graphics g, String text, int x, int y,
				int curskank )
	{
		Font f = getFont();
		String name = f.getName();
		int style = f.getStyle();
		int size = f.getSize();
		Color hue = null;
				
		if( (curskank & ITALIC) != 0 )
			style |= Font.ITALIC;
		
		if( (curskank & (SUB|SUPER)) != 0 )
		{
			size -= 1;
			if( size < 6 ) size = 6;
		}
		
		if( (curskank & SUB) != 0 )
			y += 4;
		
		if( (curskank & SUPER) != 0 )
			y -= 2;
		
		if( (curskank & COLOR) != 0 )
		{
			hue = g.getColor();
			g.setColor(variableColor);
		}
		
		Font xf = new Font(name,style,size);
		
		FontMetrics fm = getFontMetrics(xf);
		
		g.setFont(xf);
		g.drawString( text, x, y );
		g.setFont( f );
		
		if( hue != null )
			g.setColor(hue);
		
		return fm.stringWidth(text);
	}
}
