import java.awt.*;

public class Title extends Component
{
	public Title( String name )
	{
		setName(name);
		setFont( GraphInfo.fontBiggerBold );
	}
	
	public Dimension getPreferredSize()
	{
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		
		return new Dimension(fm.stringWidth(getName()),fm.getHeight());
	}
	
	public void paint( Graphics g )
	{
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		Dimension dim = getSize();
		String name = getName();
		
		g.setFont( f );
		g.setColor( getForeground() );
		
		g.drawString( name, (dim.width-fm.stringWidth(name))/2, fm.getAscent() );
	}
}
