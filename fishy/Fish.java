import java.awt.*;
import java.util.Random;

public class Fish
{
	final static String [] species =
		{
			"Angelfish",
			"Tetra",
			"Siamese"
		};

	final static String [] imageFiles =
		{
			"angel.gif",
			"tetra.gif",
			"siamese.gif"
		};

	final static Color angelColor = new Color(204,104,204);
	final static Color tetraColor = new Color(255,204,153);
	final static Color siamColor = new Color(104,204,153);

	int type;
	
	static Random rand = new Random();

	public Fish()
	{
		this(Math.abs(rand.nextInt()) % species.length);
	}

	public Fish( int type )
	{
		this.type = type;
	}
	
	public Color getColor()
	{
		return getColor( type );
	}
	
	public static Color getColor( int which )
	{
		switch( which )
		{
			case 0: return angelColor;
			case 1: return tetraColor;
			case 2: return siamColor;
		}
		
		return Color.white;
	}
	
	public static Color getBGColor( int which )
	{
		switch( which )
		{
			case 0: return new Color(0xFF,0x66,0xFF);
			case 1: return new Color(0xFF,0xCC,0x99);
			case 2: return new Color(0x66,0xFF,0x99);
		}
		
		return Color.white;
	}
	
	public Color getBGColor()
	{
		return getBGColor( type );
	}
	
	public String getFileName()
	{
		return getFileName(type);
	}
	
	public static String getFileName( int t )
	{
		return imageFiles[t];
	}
	
	public String getName()
	{
		return species[type];
	}
	
	public static String getName( int f )
	{
		return species[f];
	}
	
	public int getType()
	{
		return type;
	}
	
	public static int getNumTypes()
	{
		return species.length;
	}
}

