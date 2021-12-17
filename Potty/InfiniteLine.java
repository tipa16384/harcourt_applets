import java.awt.*;
import java.util.Random;
import Graph.*;

public class InfiniteLine extends Example
{
	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Infinite Line of Charge";
	}
	
	// The ID, usually the zero-based index where it appears
	// in the choice box; but it doesn't have to be.
	
	public int getID()
	{
		return ElecPot.INFINITE_LINE;
	}
	
	// draw the example.
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		g.setColor( GraphInfo.POSITIVE_COLOR );
		g.fillOval(Op.x-iInnerRad,Op.y-iInnerRad,2*iInnerRad,2*iInnerRad);
	}
	
	// set up the example around the given origin.
	
	public void setup( GraphPaper paper, DPoint O )
	{
		final double zot = 1.0;
		
		newChargeAt(paper,O,false);

		for( int i=0; i<res/2; ++i )
		{
			newChargeAt(paper,O.relative(0,0,(double)i*zot),false);
			newChargeAt(paper,O.relative(0,0,-(double)i*zot),false);
		}
	}
	
	public void setupPlot( Graph graph, DataSet ds2 )
	{
		for( int r = 0; r < 125; r += 1 )
		{
			double R = (double) r;
//			double V = 2.0 /** GraphInfo.Ke*/ * 1.0 * Math.log(R) / 1E3;
			double V = -2.0 * 1.0 * Math.log(R/375.0);
			ds2.addDataPoint(R,V/5E2);
		}
	}
}
