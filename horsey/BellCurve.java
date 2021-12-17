import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BellCurve extends Component
{
	final static int width = 190;
	final static int height = 150;
	
	Horse nag;
	double xLow, xHigh;

	double [] calced;
	double highestCalc = 0.0;

	static Color figureColor = Color.red;
	
	static String title = "Distribution of Race Times";
	static String units = "m";		// minutes
	
	public BellCurve( Horse h, double xLow, double xHigh )
	{
		this.nag = h;
		this.xLow = xLow;
		this.xHigh = xHigh;
		
		calced = new double[width];
		
		generateSamples();
	}
	
	private void generateSamples()
	{
		int i;
		
		double std = nag.getStandardDeviation();
		double mean = nag.getMean();
		
		double k1 = std*Math.sqrt(2.0*Math.PI);
		double k2 = 2.0 * std * std;
		
		for( i=0; i<width; ++i )
		{
			double x, y;
			double di = (double)i;
			double dw = (double)width;
			double range = xHigh - xLow;
			
			x = xLow + (range * di)/dw - mean;
			
			y = Math.exp(-(x*x)/k2)/k1;
			calced[i] =	y;
			highestCalc = Math.max(highestCalc,y);
		}
		
		//System.out.println("Highest Calced = "+highestCalc);
	}
	
	public void paint( Graphics g )
	{
		final int gap = 10;
		int th = height - 2*gap;
		final int spacing = 10;
		int i;
		
		Color fg = GraphInfo.SEPARATE_COLOR;
		Color grid = new Color(0,153,204);
		
		// draw graph
		
		g.setColor( fg );
		
		for( i=0; i<width; ++i )
		{
			int dy = (int)Math.round((calced[i]*th)/highestCalc);
			if( dy > 0 )
				g.drawLine( i, height-gap, i, height-gap-dy );
		}

		// draw grid
		
		g.setColor( grid );
		g.drawRect(0,gap,width-1,th);
		
		for( i=spacing; i<width; i+=spacing )
		{
			g.drawLine(i,gap,i,gap+th);
		}
		
		for( i=spacing; i<th; i+=spacing )
		{
			g.drawLine(0,i+gap,width,i+gap);
		}

		Font font = GraphInfo.fontPlain;
		FontMetrics fm = getFontMetrics(font);
		
		g.setColor( figureColor );
		g.setFont( font );
		
		String s = Double.toString(xLow)+units;
		g.drawString( s, 0, height );
		
		s = Double.toString(xHigh)+units;
		g.drawString( s, width-fm.stringWidth(s), height );
		
		double mean = nag.getMean();
		int x = (int)Math.round(((mean-xLow)*(double)width)/(xHigh-xLow));
		s = Double.toString(mean)+units;
		g.drawString( s, x-fm.stringWidth(s)/2, height );
		
		g.drawString( title, (width-fm.stringWidth(title))/2, gap-2 );
		
		g.setColor( Color.black );
		g.drawLine( x, gap, x, th+gap );
		
		// now draw the variables

		{
			int fh = fm.getAscent()+fm.getDescent();
			int xgap = 2;
			
			String lines[] = new String[3];
			
			int boxHeight = lines.length*fh;
			
			lines[0] = "Mean: "+nag.getMean()+"m";
			lines[1] = "Std.Dev: "+nag.getStandardDeviation()+"m";
			
			double payoff = nag.getPayoff();
			
			lines[2] = "Payoff: ";
			
			if( payoff >= 1.0 ) lines[2] += (int)Math.rint(payoff)+":1";
			else lines[2] += "1:"+(int)Math.rint(1.0/payoff);
			
			int boxWidth = 0;
			
			for( i=0; i<lines.length; ++i )
				boxWidth = Math.max(boxWidth,fm.stringWidth(lines[i]));

			boxWidth += 2*xgap;

			int bx;
			int by = gap+4;
			
			if( nag.getJustify() )
				bx = getSize().width-boxWidth-4;
			else
				bx = 4;
			
			g.setColor( getBackground() );
			g.fillRect( bx, by, boxWidth, boxHeight );
			g.setColor( Color.black );
			g.drawRect( bx, by, boxWidth, boxHeight );
			
			for( i=0; i<lines.length; ++i )
			{
				int ly = by + i*fh + fm.getAscent();
				int lx = bx + xgap;
				
				g.drawString( lines[i], lx, ly );
			}
		}
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(width,height);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
}
