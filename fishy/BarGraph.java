import java.awt.*;
import java.util.Vector;

public class BarGraph extends Component
{
	GraphInfo info;
	
	int maxVal = 25;
	int tick = 5;
	
	final int yGap = 10;
	final int xGap = 10;
	final int tickSize = 5;
	
	Vector bars;
	
	public BarGraph( GraphInfo info )
	{
		this.info = info;
		
		bars = new Vector();
		
		setBackground( info.CONTROL_COLOR );
		setFont( info.fontBold );
	}
	
	public void addBar( Bar bar )
	{
		bars.addElement( bar );
		repaint();
	}
	
	public void clear()
	{
		bars.removeAllElements();
		repaint();
	}
	
	public void setMaxVal( int mv )
	{
		maxVal = mv;
		repaint();
	}
	
	public void setTick( int tk )
	{
		tick = tk;
		repaint();
	}
	
	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		Font font = getFont();
		String s;
		int i;
		int numbars = bars.size();
		int ysize, xsize;
		
		g.setColor( getBackground() );
		g.fillRect( 0, 0, dim.width, dim.height );
		
		FontMetrics fm = getFontMetrics( font );
		
		s = Integer.toString(maxVal);
		int yAxisOffset = fm.stringWidth(s)+5;
		int xAxisOffset = fm.getHeight()/2;
		
		g.setColor( getForeground() );
		
		g.drawLine( yAxisOffset, xGap,
					yAxisOffset, dim.height-xAxisOffset );
		
		g.drawLine( yAxisOffset, dim.height-xAxisOffset,
					dim.width-yGap, dim.height-xAxisOffset );
		
		ysize = dim.height - yGap - xAxisOffset;
		xsize = dim.width - xGap - yAxisOffset;
		
		for( i=0; i<=maxVal; i += tick )
		{
			int y = dim.height-xAxisOffset-(i*ysize)/maxVal;
			
			g.drawLine( yAxisOffset, y, yAxisOffset+tickSize, y );
			
			s = Integer.toString(i);
			g.drawString( s, yAxisOffset-fm.stringWidth(s)-2, y+fm.getAscent()/2 );
		}
		
		int ascent = fm.getAscent();
		
		for( i=0; i<numbars; ++i )
		{
			Bar bar = (Bar) bars.elementAt(i);
			
			int barSpace = xsize/numbars;
			int barWidth = ((barSpace*7)/10);
			int x0 = yAxisOffset+i*barSpace + barSpace/2;
			double yadj = (bar.getValue()*(double)ysize)/(double)maxVal;
			int y0 = dim.height-xAxisOffset-(int)Math.rint(yadj);
			
			g.setColor( bar.getColor() );
			g.fillRect( x0-barWidth/2, y0, barWidth, dim.height-xAxisOffset-y0 );
			
			g.setColor( getForeground() );
			g.drawRect( x0-barWidth/2, y0, barWidth, dim.height-xAxisOffset-y0 );
		}

		int minx = dim.width*2;

		for( i=0; i<numbars; ++i )
		{
			Bar bar = (Bar) bars.elementAt(i);
			
			String label = bar.getName();
			
			int x0 = dim.width-xGap-fm.stringWidth(label)-ascent-2;
			minx = Math.min(minx,x0);
		}
	}
}
