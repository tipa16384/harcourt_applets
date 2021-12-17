import java.util.*;
import java.io.*;
import java.awt.*;

public class Paragraph extends ParsedClass
{
	public String heading = null;
	Texter rl;
	
	public Paragraph()
	{
		setLayout(new BorderLayout());
		rl = new Texter();
		add( rl, BorderLayout.CENTER );
	}
	
	public void appendCharacters( String s )
	{
		s = s.replace('\n',' ');
		super.appendCharacters( s );
		rl.setText( characters );
	}
	
	public void setText( Text to )
	{
		debug("adding "+to);
		appendCharacters( to.characters );
	}
	
	public Insets getInsets()
	{
		Insets insets = super.getInsets();
		
		if( heading != null )
		{
			Font f = getFont();
			if( f != null )
			{
				FontMetrics fm = getFontMetrics(f);
				insets.top += fm.getHeight();
			}
		}
		
		return insets;
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		if( heading != null )
		{
			Insets insets = getInsets();
			java.awt.Font f = getFont();
			f = new Font(f.getName(),Font.BOLD,f.getSize());
			g.setFont(f);
			FontMetrics fm = getFontMetrics(f);
			g.setColor(getForeground());
			g.drawString( heading, insets.left, fm.getAscent() );
		}
	}
	
	public String toString()
	{
		return getClass().getName()+"[bounds="+getBounds()+"]";
	}
}
