import java.awt.*;
import java.util.Random;

public class RaceTrack extends Component
					   implements Resettable
{
	double finishTime;
	double time;
	Horse horse;
	
	static int iconWidth = 16;
	static int iconHeight = 16;
	
	double [] checkTimes;
	
	Image image;
	
	public RaceTrack( Horse horse )
	{
		setForeground( Color.black );
	
		this.horse = horse;
		
		checkTimes = new double[4];
		
		try
		{
			image = Utility.getImage( this, "horseicon.gif" );
			//System.out.println("Image is "+image);

			MediaTracker mt = new MediaTracker(this);
			mt.addImage( image, 0 );
			mt.waitForAll();
			
			iconWidth = image.getWidth(this);
			iconHeight = image.getHeight(this);
			
			//System.out.println("w,h = "+iconWidth+","+iconHeight);
		}
		
		catch( Exception e )
		{
			System.err.println("Waiting for image bombed with "+e);
		}
		
		reset();
	}
	
	public void reset()
	{
		final double fract = 0.2;
		
		finishTime = horse.raceTime();

		for( int i=0; i<checkTimes.length; ++i )
		{
			checkTimes[i] = (finishTime * (double)(i+1))/checkTimes.length +
						fract*GraphInfo.rand.nextDouble()*finishTime -
						fract*finishTime*0.5;
		}

		setTime(0.0);
	}
	
	public double getFinishTime()
	{
		return finishTime;
	}
	
	public Horse getHorse()
	{
		return horse;
	}
	
	public void setTime( double time )
	{
		if( this.time != time )
		{
			this.time = time;
			repaint();
		}
	}
		
	public boolean isRaceOver()
	{
		return time >= finishTime;
	}

	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		
		int y = dim.height / 2;
		
		g.setColor( getForeground() );
		
		g.drawLine( 0, y, dim.width, y );
		
		double courseLength = (double)(dim.width-iconWidth);
		double t = Math.min(time,finishTime);
		
		double coursePosition;
		
		if( time <= checkTimes[0] )
		{
			coursePosition = (courseLength*t)/(4.0*checkTimes[0]);
		}
		
		else if( time <= checkTimes[1] )
		{
			coursePosition = courseLength/4 + (courseLength/4*(t-checkTimes[0]))/(checkTimes[1]-checkTimes[0]);
		}
		
		else if( time <= checkTimes[2] )
		{
			coursePosition = courseLength/2 + (courseLength/4*(t-checkTimes[1]))/(checkTimes[2]-checkTimes[1]);
		}
		
		else
		{
			coursePosition = (3*courseLength)/4 + (courseLength/4*(t-checkTimes[2]))/(finishTime-checkTimes[2]);
		}

		int iPos = (int) Math.floor(coursePosition);
		
		g.drawImage(image,iPos,y-iconHeight/2,iconWidth,iconHeight,this);
	}
}
