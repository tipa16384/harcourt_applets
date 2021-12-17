package Wired;

public class DoubleListElement
{
	String text;
	double value;
	
	public DoubleListElement( String text, double value )
	{
		this.text = text;
		this.value = value;
	}
	
	public String getName()
	{
		return text;
	}
	
	public double getValue()
	{
		return value;
	}
}