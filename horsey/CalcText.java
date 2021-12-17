import java.awt.*;

public class CalcText extends Component
{
	String text;
	int style;
	int height;
	int width;
	Font font;
	FontMetrics fm;
	boolean shadow;
	
	public CalcText( String text, int style, int height, boolean shadow )
	{
		this.style = style;
		this.height = height;
		this.shadow = shadow;
		
		if( GraphInfo.isMac )
			font = new Font("SansSerif",style,height-4);
		else
			font = new Font("SansSerif",style,height-2);

		fm = getFontMetrics(font);

		setText(text);
	}
	
	public CalcText( String text, int style, int height )
	{
		this( text, style, height, true );
	}
	
	public void setText( String text )
	{
		this.text = text;
		width = fm.stringWidth(text);
		repaint();
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(width,height);
	}
	
	public void paint( Graphics g )
	{
		g.setFont( font );
		
		int ascent = fm.getAscent();
		int w = fm.stringWidth( text );
		int tw = getSize().width;
		
		int x = (tw-w)/2;
		int y = ascent-1;
		
		if( shadow )
		{
			g.setColor( Color.black );
			g.drawString( text, x+2, y+2 );
		}
					
		g.setColor( getForeground() );
		g.drawString( text, x, y );
	}
}

