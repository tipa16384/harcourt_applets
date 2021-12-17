import java.awt.*;

public class RaceHistory extends Component
{
	final int columnGap = 10;
	final int indexWidth = 14;
	
	Horse [] horses;
	Horse horse;
	Font font;
	FontMetrics fm;
	int width;
	int height;
	int fh;
	
	int [] columnWidths;
	double [][] histories;
	
	public RaceHistory( GraphInfo info, Horse horse )
	{
		int i;
		
		Horse [] horses = info.getHorses();
		
		this.horses = horses;
		this.horse = horse;
		
		font = info.fontPlainSmall;
		setFont( font );
		fm = getFontMetrics( font );
		fh = fm.getHeight();
		
		width = 0;
		
		columnWidths = new int[horses.length];
		histories = new double[horses.length][];
		
		for( i=0; i<horses.length; ++i )
		{
			int w = fm.stringWidth( horses[i].getName() ) + columnGap;
			width += w;
			columnWidths[i] = w;
			histories[i] = horses[i].getHistory();
		}

		width += indexWidth;
		height = (histories[0].length+1) * fh;
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(width,height);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		
		g.setFont( font );

		g.setColor( GraphInfo.AXIS_COLOR );
		
		int y, x, j, i;
		String s;
		
		y = fm.getAscent();
		
		g.drawLine( 0, fh, dim.width, fh );
		
		x = indexWidth;
		
		for( i=0; i<horses.length; ++i )
		{
			g.drawLine( x, 0, x, dim.height );
			x += columnWidths[i];
		}
		
		for( i=0, x=indexWidth; i<horses.length; ++i )
		{
			int cw = columnWidths[i];
			
			if( horse == horses[i] )
			{
				g.fillRect( x, fh, cw, dim.height-fh );
				break;
			}
			
			x += cw;
		}

		double [] rh = horses[0].getHistory();
		g.setColor( Color.red );
		
		for( i=1; i<=rh.length; ++i )
		{
			s = Integer.toString(i);
			g.drawString( s, (indexWidth-fm.stringWidth(s))/2, fh*i + y );
		}
		
		y = fm.getAscent();
		
		for( i=0, x=indexWidth; i<horses.length; ++i )
		{
			int cw = columnWidths[i];
			s = horses[i].getName();
			
			g.drawString(s,x+(cw-fm.stringWidth(s))/2,y);
			
			x += cw;
		}
		
		for( i=0; i<histories[0].length; ++i )
		{
			y = fh*(i+1) + fm.getAscent();
			x = indexWidth;
			
			double lowest = histories[0][i];
			for( j=0; j<horses.length; ++j )
			{
				lowest = Math.min(lowest,histories[j][i]);
			}
			
			for( j=0; j<horses.length; ++j )
			{
				int cw = columnWidths[j];
				//System.err.print("getting histories["+j+"]["+i+"]... ");
				double val = histories[j][i];
				//System.err.println("done");
				
				g.setColor( (val==lowest) ? Color.blue : Color.black );
				
				val = Math.floor(val*100.0)/100.0;
				s = Double.toString(val);
				
				g.drawString(s,x+(cw-fm.stringWidth(s))/2,y);
				x += cw;
			}
		}
	}
}
