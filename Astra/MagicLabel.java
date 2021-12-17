import java.awt.*;
import java.awt.event.*;

public class MagicLabel extends Label
{
	static final String fontName = "SansSerif";
	static final int fontSize = 12;
	
	static Color highlightColor = null;
	static Color normalColor = null;
	
	Font fonts[] =
		{
			new Font(fontName,Font.PLAIN,fontSize),
			new Font(fontName,Font.ITALIC,fontSize),
			new Font(fontName,Font.BOLD,fontSize),
			new Font(fontName,Font.BOLD+Font.ITALIC,fontSize)
		};

	int fontbase = 0;
	boolean selected = false;
	int index;
	
	public MagicLabel( String s, int index )
	{
		super( s, Label.CENTER );
	
		this.index = index;
		
		setForeground(Color.white);
		setBackground(null);
		
		if( s.charAt(0) == '!' )
		{
			fontbase = 1;
			setText( s.substring(1) );
		}
		
		setFont( fonts[fontbase] );
		
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}
	
	protected void processMouseEvent( MouseEvent e )
	{
		switch( e.getID() )
		{
			case MouseEvent.MOUSE_ENTERED:
				{
					if( highlightColor == null )
					{
						if( normalColor == null )
						{
							normalColor = getBackground();
							if( normalColor == null )
								normalColor = Color.black;
						}
						
						highlightColor = normalColor.brighter();
					}
					
					setBackground( highlightColor );
					repaint();
				}
				
				break;

			case MouseEvent.MOUSE_EXITED:
				{
					if( normalColor == null )
						setBackground(null);
					else
						setBackground(normalColor);
						
					repaint();
				}
				
				break;
		}
		
		super.processMouseEvent(e);
	}
	
	public void scan( String s )
	{
		if( s.indexOf(getText()) < 0 )
		{
			if( selected )
			{
				setFont( fonts[fontbase] );
				selected = false;
				repaint();
			}
		}
		
		else if( !selected )
		{
			setFont( fonts[fontbase+2] );
			selected = true;
			repaint();
		}
	}
}
