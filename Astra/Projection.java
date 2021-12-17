import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class Projection extends Graph
{
	int [][] error1 = null;
	int [][] error2 = null;
	boolean first = true;
	
	public Projection( GraphInfo info )
	{
		super(info);
	}
	
	public void propertyChange( PropertyChangeEvent evt )
	{
		if( !evt.getPropertyName().equals("slider") )
			recalc();
	}
	
	static int getRed(int col)
	{
		return (col>>16) & 0xFF;
	}
	
	static int getGreen(int col)
	{
		return (col>>8) & 0xFF;
	}
	
	static int getBlue(int col)
	{
		return col & 0xFF;
	}
	
	void plot( Graphics g, int x, int w, int h, double I )
	{
		int i;
		int j;
		Color oldColor = Color.black;
		
		x += w >> 1;
		
		if( error1 == null || error1.length < (h+1) )
		{
			error1 = new int[h+1][3];
			error2 = new int[h+1][3];
		}
		
		int [][] current = first ? error1 : error2;
		int [][] next = first ? error2 : error1;
		
		first = !first;
		
		if( x == 0 )
			for( i=0; i<h; ++i )
				for( j=0; j<3; ++j )
					current[i][j] = next[i][j] = 0;
		else
			for( i=0; i<h; ++i )
				for( j=0; j<3; ++j )
					next[i][j] = 0;
		
		int perfect = info.findColorRGB(I);
		int pr = getRed(perfect);
		int pg = getGreen(perfect);
		int pb = getBlue(perfect);
		
		int starty = 0;
		
		for( j=0; j<h; ++j )
		{
			int best = info.findBestColor(
						pr+current[j][0],
						pg+current[j][1],
						pb+current[j][2]);

			int br = getRed(best);
			int bg = getGreen(best);
			int bb = getBlue(best);
			
			int part1, part2;
			
			// implement Floyd-Steinberg error correction.
			
			if( true )
			{
				part1 = ((pr-br)*2)/5;
				part2 = ((pr-br))/5;
				current[j+1][0] += part1;
				next[j][0] += part1;
				next[j+1][0] += part2;
				
				part1 = ((pg-bg)*2)/5;
				part2 = ((pg-bg))/5;
				current[j+1][1] += part1;
				next[j][1] += part1;
				next[j+1][1] += part2;
				
				part1 = ((pb-bb)*2)/5;
				part2 = ((pb-bb))/5;
				current[j+1][2] += part1;
				next[j][2] += part1;
				next[j+1][2] += part2;
			}
			
			else
			{
				current[j+1][0] = pr-br;
				current[j+1][1] = pg-bg;
				current[j+1][2] = pb-bb;
			}
			
			Color newColor = info.findColor(best);
			if( !newColor.equals(oldColor) )
			{
				if( j > starty )
				{
					g.setColor(oldColor);
					g.drawLine(x,starty,x,j);
				}

				starty = j;				
				oldColor = newColor;
			}
			
			//g.drawLine(x,j,x,j+1);
		}
		
		g.setColor( oldColor );
		g.drawLine(x,starty,x,h);
	}
}
