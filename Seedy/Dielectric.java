import java.awt.Color;

public class Dielectric
{
	String name;
	double constant;
	Color color;
	
	public Dielectric( String name, double constant, Color color )
	{
		this.name = name;
		this.constant = constant;
		this.color = color;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getConstant()
	{
		return constant;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setColor( Color color )
	{
		this.color = color;
	}
	
	public String toString()
	{
		return getClass().getName()+"["+name+",K="+constant+"]";
	}
}
