package Wired;

public class CircuitSpecifier
{
	String name, layout;
	
	public CircuitSpecifier( String name, String layout )
	{
		this.name = name;
		this.layout = layout;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getLayout()
	{
		return layout;
	}
}
