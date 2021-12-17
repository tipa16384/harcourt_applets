import java.awt.*;
import java.awt.event.*;

public class UnicodePage extends Component
{
	GraphInfo info;
	int page;
	
	public UnicodePage( GraphInfo info )
	{
		this.info = info;
		page = info.page;
		
		addMouseListener( new MouseAdapter()
			{
				public void mouseClicked( MouseEvent e )
				{
					if( ++page >= 256 )
						page = 0;
					repaint();
				}
			} );
	}
	
	public void paint( Graphics g )
	{
		Dimension size = getSize();
		Font f = new Font("SansSerif",Font.PLAIN,24);
		FontMetrics fm = getFontMetrics(f);
		int asc = fm.getAscent();
		
		g.setFont( f );
		g.setColor( new Color(204,204,255) );
		g.drawString( Integer.toString(page), 0, asc );
		
		Toolkit tools = getToolkit();
		
		g.setFont( new Font("SansSerif",Font.BOLD,24) );
		fm = getFontMetrics( g.getFont() );
		int asc2 = fm.getAscent();
		String [] zook = tools.getFontList();
		
/*		for( int i=0; i<zook.length; ++i )
		{
			g.drawString( zook[i], 0, asc + i*fm.getHeight() + asc2 );
		}
*/		
		f = new Font(info.fontName,Font.PLAIN,12);
		fm = getFontMetrics(f);
		asc = fm.getAscent();
		g.setFont(f);
		g.setColor( Color.blue );
		char [] cha = new char[1];
		
		for( int y=0; y<16; ++y )
			for( int x=0; x<16; ++x )
			{
				cha[0] = (char)(page*256 + y*16 + x);
				String s = new String(cha);
				g.drawString(s,(x*size.width)/16,(y*size.height)/16+asc);
			}
	}
}
