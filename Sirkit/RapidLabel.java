package util;

import java.awt.*;
import java.util.Vector;
//import NPres.HeaderPanel;

// implements a wrapped label class.

public class RapidLabel extends Component
{
	String text;
	int alignment;
	int width;
	boolean squish = true;
	Vector lines = null;
	transient int lwidth = 0;
	
	public RapidLabel()
	{
		this("", Label.LEFT, true);
	}
	
	public RapidLabel( String text )
	{
		this( text, Label.LEFT, true );
	}
	
	public RapidLabel( String text, boolean narrow )
	{
		this( text, Label.LEFT, narrow );
	}
	
	public RapidLabel( String text, int alignment )
	{
		this( text, alignment, true );
	}
	
	public RapidLabel( String text, int alignment, boolean narrow )
	{
		this( text, alignment, 320, narrow );
	}
	
	public RapidLabel( String text, int alignment, int width )
	{
		this( text, alignment, width, true );
	}

	public RapidLabel( String text, int alignment, int width, boolean narrow )
	{
		squish = narrow;
		setFont( new Font( "Serif", Font.PLAIN, 12 ) );
//		setBackground( Color.white );
//		setBackground( SystemColor.text );
		setText( text );
		setAlignment( alignment );
		setWidth( width );
	}

	private void burst()
	{
		if( lwidth != width )
			forceburst();
	}

	private void forceburst()
	{
		//System.out.println();
		//System.out.println("forceburst "+text);
		
		lwidth = width;
		lines = new Vector();
		
		int w = lwidth - 2*getGap();
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		int fh = fm.getHeight();
		int tl;
		String txt = text;
		
		nextline: while( (tl=txt.length()) > 0 )
		{
			int sw = fm.stringWidth(txt);
			
			if( sw <= w )
			{
				lines.addElement(txt);
				//System.out.println("   added short line "+txt);
				return;
			}
			
			int idx = tl;
			
			for(;;)
			{
				int nidx = txt.lastIndexOf(' ',idx-1);

				if( nidx < 0 )
				{
					//System.out.println("   width overrun, cutting from 0.."+idx);
					String s = txt.substring(0,idx);
					lines.addElement( s );
					//System.out.println("   added overrun line "+s);
					txt = txt.substring(s.length());
					continue nextline;
				}
				
				//System.out.println("   scanning width at index "+nidx);
				String sub = txt.substring(0,nidx);
				//System.out.println("   text to that point is "+sub);
				
				if( fm.stringWidth(sub) <= w )
				{
					//System.out.println("   sufficiently short, adding "+sub);
					lines.addElement( sub );
					txt = txt.substring(nidx+1);
					continue nextline;
				}
				
				idx = nidx;
			}
		}
		
		//System.out.println("burst into "+lines.size()+" lines");
	}
	
	public void setText( String text )
	{
		this.text = text;
		lwidth = -1;
		if( isShowing() ) repaint();
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setAlignment( int alignment )
	{
		this.alignment = alignment;
		if( isShowing() ) repaint();
	}
	
	public int getAlignment()
	{
		return alignment;
	}
	
	private void setWidth( int width )
	{
		this.width = width;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public void setBounds( int x, int y, int w, int h )
	{
//		System.out.println("RL: bounds = "+w+","+h);
		setWidth( w );
		super.setBounds(x,y,w,getPreferredSize().height);
	}
	
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}
	
	public Dimension getPreferredSize()
	{
//		System.out.println("Font is "+font);
//		System.out.print("ps for width = "+width);
		burst();
		FontMetrics fm = getFontMetrics(getFont());
		int len = lines.size();
		return new Dimension( width+2*getGap(), len*fm.getHeight()+2*getGap() );
	}
	
	private int getTextGap()
	{
		return squish ? 0 : 3;
	}
	
	private int getEdgeGap()
	{
		return squish ? 0 : 4;
	}
	
	private int getGap()
	{
		return squish ? 0 : (getTextGap()+getEdgeGap());
	}
	
	public void paint( Graphics g )
	{
		burst();
		Dimension size = getSize();
		int gap = getGap();
		int egap = getEdgeGap();
				
		g.setColor( getBackground() );
		g.fillRect( egap, egap, size.width-2*egap, size.height-2*egap );

		Font f = getFont();
		g.setFont( f );
		g.setColor( getForeground() );
		FontMetrics fm = getFontMetrics(f);
		
		int i;
		int len = lines.size();
		
		for( i=0; i<len; ++i )
		{
			String s = (String) lines.elementAt(i);
			int x;
	
			switch( alignment )
			{
				default:
					x = 0;
					break;
					
				case Label.CENTER:
					x = (size.width - fm.stringWidth(s)) / 2;
					break;
				
				case Label.RIGHT:
					x = size.width - fm.stringWidth(s);
					break;
			}

			g.drawString( s, x+gap, gap + i*fm.getHeight() + fm.getAscent() );
		}
	}

    public String toString()
    {
		String str = ",align=";
		switch (alignment)
		{
			case Label.LEFT:   str += "left"; break;
			case Label.CENTER: str += "center"; break;
			case Label.RIGHT:  str += "right"; break;
		}
	
		return "RapidLabel[("+getSize().width+","+
				getSize().height+")" + str + ",text=" + text+"]";
    }
}
