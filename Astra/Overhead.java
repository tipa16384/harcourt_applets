import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class Overhead extends Component
					  implements PropertyChangeListener
{
	GraphInfo info;
	
	static final int gratingWidth = 5;
	
	public Overhead( GraphInfo info )
	{
		this.info = info;
		info.addPropertyChangeListener(this);
	}
	
	public void propertyChange( PropertyChangeEvent evt )
	{
		if( !evt.getPropertyName().equals("slider") )
			recalc();
	}
	
	void recalc()
	{
		repaint(info.RESPONSIVENESS);
	}
	
	public void update( Graphics g )
	{
		GraphInfo.yell(this);
		super.update(g);
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(100,100);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(250,100);
	}
	
	public void paint( Graphics g )
	{
		Dimension size = getSize();
		
		// find out the maximum spacing for the screen; "0" is considered
		// to be the minimum.
		
		long t, x, y, x1;
		
		// divide our space into several units
		// there are maxSpacing*maxSlits possible "units".
		// of which slitSpacing*numSlits are actually used.
		
		long maxSpacing = info.getMaxSlitSpacing();
		long maxSlits = info.getMaxNumberOfSlits();
		long maxWidth = info.getMaxSlitWidth();
		long spacing = info.getSlitSpacing();
		long N = info.getNumberOfSlits();
		long width = info.getSlitWidth();
		int options = info.getOptions();
		
		// if there is no n-slit slider, and we don't have to
		// "fake" the waves, then max number of slits is the
		// given number of slits
		if( (options&(Differ.NUMSLITS|Differ.FAKEWAVES)) == 0 )
			maxSlits = 2;
		else if( (options&Differ.FAKESPACE) != 0 )
			maxSlits = N;
		
		// if there is no slitwidth slider, max width is the
		// given width
		if( (options&Differ.SLITWIDTH) == 0 )
			maxWidth = width;
		
		// if there is no slitspacing slider, max spacing is
		// the given spacing
		if( (options&Differ.SLITSPACING) == 0 )
			maxSpacing = spacing;
			
		// slitHeight - the height of one slit, in pixels.
		
		long slitHeight;
		
		// slitWidth - just the opening size, in pixels

		long slitWidth;
		
		// if this is a diffraction grating, all bets are off.
		// fake displayed number of slits to however many slits
		// will fit in the graphic.		
		if( (options & Differ.FAKESLITS) != 0 )
		{
			slitWidth = 1;
			slitHeight = spacing/width;
			if( slitHeight < 1 ) slitHeight = 1;
			slitHeight += slitWidth;
			N = size.height / slitHeight;
		}
		
		else if( (options & Differ.FAKESPACE) != 0 )
		{
//			N = Math.min(maxSlits,10);
			slitHeight = (size.height*2) / (N*3);
			slitWidth = 1;
		}
		
		else
		{
			long fakeMaxSlits = Math.min(maxSlits,10);
			
			slitHeight = (size.height*(spacing+width))/((maxSpacing+maxWidth)*fakeMaxSlits);
			slitWidth = (size.height*width)/((maxSpacing+maxWidth)*fakeMaxSlits);	
		}
		
		// just being careful. slit should always be visible.
		if( slitWidth < 1 ) slitWidth = 1;
		
		if( slitHeight <= slitWidth ) slitHeight = slitWidth+1;
		
		// this is the light source; have to make this a series of
		// standing waves...
		
//		g.setColor( getForeground() );
//		g.drawRect( 0, 0, 2*gratingWidth, size.height-1 );

		g.setColor( info.findColor(0.75) );
		
		final long arcsize = (20*info.getWavelength())/(info.getMaxWavelength()+info.getMinWavelength());;
		
		{
			int i = 1;
			int l = 5*gratingWidth+1;
			
			for( ; i < l; i += arcsize )
			{
				g.drawLine( i, 1, i, size.height-1 );
			}
		}

//		g.fillRect( 1, 1, 2*gratingWidth, size.height-2 );
		
		// this draws the moving screen
		// significantly, x1 is the right boundary of the content area
		// between the screen and the slitscreen
		
		x1 = 6*gratingWidth+((size.width-7*gratingWidth)*info.getDistanceToScreen())/info.getMaxDistanceToScreen();
		g.setColor( getForeground() );
		g.fillRect( (int)x1, 0, gratingWidth, size.height );
		
		x = 5*gratingWidth;
		g.fillRect( (int)x, 0, gratingWidth, size.height );
		
		g.setColor( new Color(250,250,250) );
		g.fillRect( (int)x+gratingWidth, 0, (int)(x1-x-gratingWidth), size.height );
		
		g.setColor( getBackground() );
		
		if( ((options & Differ.FAKESPACE) == 0) || (N == 1) )
		{
			y = (size.height-(N-1)*slitHeight-slitWidth)/2;
			for( t=0; t<N; ++t, y += slitHeight )
			{
				g.fillRect( (int)x, (int)y, gratingWidth, (int)slitWidth );
			}
		}
		
		else if( N > 1 )
		{
			int hgt = size.height - size.height/3;
			for( t=0; t<N; ++t )
			{
				y = size.height/6 + (t*hgt)/(N-1);
				g.fillRect( (int)x, (int)y, gratingWidth, (int)slitWidth );
			}
		}
				
		g.setColor( info.findColor(0.75) );

		if( ((options &  Differ.FAKEWAVES) != 0) && (N > 3) )
		{
			N = 3;
			slitHeight *= 2;
		}

		y = (size.height-(N-1)*slitHeight)/2;
		x += gratingWidth;
		g.setClip( (int)x, 0, (int)(x1-x), size.height );

		long numArcs = (x1-x)/arcsize+2;
		
		for( long arc=0; arc<numArcs; ++arc )
		{
			long rad = arcsize*arc;
			
			if( arc < 10 )
			{
				y = (size.height-(N-1)*slitHeight)/2;
				for( t=0; t<N; ++t, y += slitHeight )
				{
					g.drawOval((int)(x-rad),(int)(y-rad),(int)rad*2,(int)rad*2);
				}
			}
			
			else
			{
				y = size.height/2;
				g.drawOval((int)(x-rad),(int)(y-rad),(int)rad*2,(int)rad*2);
			}
		}
		
		// remember x1 from way up a screen? We use that now as the right
		// boundary with which to write the word "screen"
		
		// "screen"... I used to love that word. I remember I once scratched
		// that word into a big granite boulder hidden in the woods. It took
		// me all one summer to chisel "screen" into the boulder deep enough
		// so that people in ages to come with journey to the boulder to
		// meditate in wonder upon that single, most elegant of words. People
		// used to call me Screen, and I didn't mind.
		//
		// I never liked wooden doors.
		
		final String thatWondrousMagicalMysteriousWord = "Screen";
		
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		g.setColor( GraphInfo.SEPARATE_COLOR );
		
		int wonderfulWidth = fm.stringWidth(thatWondrousMagicalMysteriousWord);
		int happyHeight = fm.getDescent()+fm.getLeading();
		g.drawString( thatWondrousMagicalMysteriousWord,
			(int)x1 - wonderfulWidth,
			size.height - happyHeight );
			
	}
}
