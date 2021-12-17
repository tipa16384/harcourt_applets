import java.awt.*;

public class Text extends ParsedClass
{
	public String color = null;
	public String style = null;
	
	public int getStyle()
	{
		int si = 0;
		int [] styles = { Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD|Font.ITALIC };
		
		if( style != null )
		{
			if( style.indexOf("bold") >= 0 )
			{
				++si;
			}
			
			if( style.indexOf("italic") >= 0 )
			{
				si += 2;
			}
		}

		return styles[si];
	}

	public Color getColor()
	{
		if( color == null )
			return null;
		
		String [] names = 
			{ "white", "lightGray", "gray", "darkGray",
			  "black", "red", "pink", "orange", "yellow",
			  "green", "magenta", "cyan", "blue" };
		
		Color [] colors =
			{
				Color.white,
				Color.lightGray,
				Color.gray,
				Color.darkGray,
				Color.black,
				Color.red,
				Color.pink,
				Color.orange,
				Color.yellow,
				Color.green,
				Color.magenta,
				Color.cyan,
				Color.blue
			};
		
		for( int i=0; i<names.length; ++i )
		{
			if( color.equalsIgnoreCase(names[i]) )
				return colors[i];
		}
		
		if( color.charAt(0) == '#' )
		{
			if( color.length() == 7 )
			{
				int r = 0, g = 0, b = 0;
				
				try
				{
					r = Integer.parseInt(color.substring(1,3),16);
					g = Integer.parseInt(color.substring(3,5),16);
					b = Integer.parseInt(color.substring(5,7),16);
				}
				
				catch( Exception e )
				{
				}
				
				return new Color(r,g,b);
			}
		}
		
		return null;
	}

	public String toString()
	{
		return getClass().getName()+"[style="+getStyle()+",color="+getColor()+",text="+characters+"]";
	}
}
