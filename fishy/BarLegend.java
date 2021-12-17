import java.awt.*;

public class BarLegend extends Panel
{
	Fishy fishy;
	GraphInfo info;
	Font f;
	FontMetrics fm;
	
	public BarLegend( Fishy fishy, GraphInfo info )
	{
		super( new GridLayout(1,0,2,2) );
		
		this.fishy = fishy;
		this.info = info;
		
		int len = Fish.getNumTypes();
		for( int i=0; i<len; ++i )
		{
			add( new Legend(i) );
		}
		
		f = info.fontPlain;
		fm = getFontMetrics( f );
		
		setFont( f );
		setForeground( Color.black );
	}
	
	class Legend extends Component
	{
		int which;
		Color color;
		
		final int boxSize = 10;
		
		public Legend( int which )
		{
			this.which = which;
			setName(Fish.getName(which));
			color = Fish.getColor(which);
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(
				fm.stringWidth(getName())+boxSize+5,
				fm.getHeight() );
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			String name = getName();

			int tw = boxSize+5+fm.stringWidth(name);
			int x0;
			
			x0 = (dim.width-tw)/2;			
			
			g.setColor( color );
			g.fillRect( x0, 0, boxSize, boxSize );
			g.setColor( getForeground() );
			g.drawRect( x0, 0, boxSize, boxSize );
			
			g.drawString( name, x0+boxSize+5, fm.getAscent()-1 );
		}
	}
}
