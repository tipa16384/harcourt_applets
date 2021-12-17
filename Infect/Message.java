import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class Message extends Canvas
{
	String message = "This is a TEST!";
		
	public Message()
	{
		setFont( GraphInfo.fontBigBold );
		setForeground( Color.white );
		setBackground( Color.black );
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(100,getFontMetrics(getFont()).getHeight());
	}
	
	public void setString( String s )
	{
		message = s;
		repaint();
	}
	
	public void setText( String s )
	{
		setString(s);
	}
	
	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		
		Font font = getFont();
		FontMetrics fm = getFontMetrics(font);
		
		g.setFont( font );
		
		int tw = fm.stringWidth(message);
		
		g.drawString( message, (dim.width-tw)/2, fm.getAscent() );
	}
}
