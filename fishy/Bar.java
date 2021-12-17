import java.awt.*;

public class Bar
{
	String name;
	double value;
	Color color = null;
	
	public Bar( String name, double value, Color color )
	{
		this.name = name;
		this.value = value;
		this.color = color;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getValue()
	{
		return value;
	}
	
	public Color getColor()
	{
		return color;
	}
}
