import java.awt.*;

public class IntegerTextField extends RawLabel
{
	public int low = 1;
	public int high = 5;
	public int increment = 1;
	
	public IntegerTextField( String initialValue )
	{
		super(initialValue);
	}
	
	public IntegerTextField( int ival )
	{
		super( Integer.toString(ival) );
	}
	
	public void setText( String s )
	{
		//System.out.println("setting text to "+s);
		super.setText(s);
	}
	
	public int pinValue( int val )
	{
		if( val < low )
			return low;
		else if( val > high )
			return high;
		else
			return val;
	}
	
	public void setValue( int val )
	{
		setText( Integer.toString(pinValue(val)) );
	}

	public int getValue()
	{
		String s = getText();
		int res;
		
		try
		{
			res = pinValue( Integer.parseInt(s) );
		}
		
		catch( Exception e )
		{
			res = low;
		}
		
		return res;
	}
	
	public void increment()
	{
		setValue( getValue() + increment );
	}
	
	public void decrement()
	{
		setValue( getValue() - increment );
	}
}
