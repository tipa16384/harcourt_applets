import java.awt.*;
import java.util.Random;

public class Planet
{
	String fileName, skyFile;
	double gravity, minHeight, maxHeight, yTicks;
	Image image = null;
	Image skyImage = null;
	String name;
	int status;
	
	public final static int WAITING = 0;
	public final static int FAILURE = 1;
	public final static int SUCCESS = 2;
	
	final static String [] resultString =
		{
			"Waiting", "Failure", "Success"
		};
	
	static Random rand = new Random();
	
	public Planet( String name, String fileName, String skyFile,
				double gravity, double minHeight, double maxHeight, double yTicks )
	{
		this.name = name;
		this.fileName = fileName;
		this.skyFile = skyFile;
		this.gravity = gravity;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.yTicks = yTicks;
		
		status = WAITING;
	}
	
	public void setSuccess( int succ )
	{
		status = succ;
	}
	
	public String getStatusString()
	{
		return getStatusString(status);
	}
	
	public static String getStatusString( int idx )
	{
		return resultString[idx];
	}
	
	public static String [] getStatusStrings()
	{
		return resultString;
	}
	
	public double getYTicks()
	{
		return yTicks;
	}
	
	public double getMaxHeight()
	{
		return maxHeight;
	}
	
	public double rollHeight()
	{
		return Math.rint(minHeight + rand.nextDouble() * (maxHeight-minHeight));
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getGravity()
	{
		return gravity;
	}
	
	public void loadImage( MediaTracker mt, Component comp )
	{
		//System.out.println("loading "+fileName);
		
		if( fileName != null )
		{
			image = Utility.getImage( comp, fileName );
			mt.addImage( image, 0 );
		}
		
		if( skyFile != null )
		{
			skyImage = Utility.getImage( comp, skyFile );
			mt.addImage( skyImage, 0 );
		}
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public Image getSkyImage()
	{
		return skyImage;
	}
}
