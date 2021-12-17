import java.awt.*;

class TextPanel extends Component
{
	String [] text;
	FontMetrics fm;
	final int xOffset = 22;
	
	public TextPanel( String [] text )
	{
		this.text = text;
		Font f = new Font("SansSerif",Font.PLAIN,9);
		setFont( f );
		fm = getFontMetrics( f );
	}
	
	public Dimension getMinimumSize()
	{
		int h = text.length * fm.getHeight();
		int w = 0;
		
		for( int i=0; i<text.length; ++i )
		{
			w = Math.max(w,fm.stringWidth(text[i]));
		}
		
		return new Dimension(w+xOffset,h);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void paint( Graphics g )
	{
		g.setColor( Color.black );
		g.setFont( getFont() );
		for( int i=0; i<text.length; ++i )
		{
			int y = i*fm.getHeight()+fm.getAscent()-1;
			g.drawString(text[i],xOffset,y);
		}
	}
}
