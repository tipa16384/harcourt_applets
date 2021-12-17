package Wired;

// format a double so that it's always in scientific notation, with three
// digits of precision. Return the format as a string with the RawLabel-type
// encoding to handle the superscripted exponent. Numbers between 0 and 10 do
// not have exponents (which would be 10^0, so why bother?)

public abstract class DoubleFormat
{
	static public boolean standardNotation = false;
	static public int threshhold = 0;
	
	static public String format( double d )
	{
		int E = 0;
		boolean negative = false;
		String s;
		
		// normalize d to the range 0 .. 9.9999999~

		if( d == -0.0 ) d = 0.0;

		if( d < 0.0 ) { negative = true; d *= -1.0; }
		
		if( d != 0.0 && (d < Math.pow(10,(double)-threshhold) ||
			d > Math.pow(10,(double)threshhold)) )
		{
			while( d >= 10.0 ) { E++; d /= 10.0; }
			while( d > 0.0 && d < 1.0 ) { E--; d *= 10.0; }
	
			// bring it down to two decimal places
			
			double d0 = Math.rint(d*10.0)/10.0;
			d = Math.rint(d * 100.0) / 100.0;
			
			// now convert it to a string.
			
			s = Double.toString(d);
			
			// if we cut off the trailing zero, add it in.
			
			if( d == d0 ) s += "0";
			
			if( negative ) s = "-"+s;

			s = !standardNotation ? (s+"~+10~^"+E+"~^")
								  : (s+"E"+E);
		}		
	
		else
		{
			d = Math.rint(d*100.0)/100.0;
			double d0 = Math.rint(d*10.0)/10.0;
			
			s = Double.toString(d);
			
			// if we cut off the trailing zero, add it in.
			
			if( d == d0 ) s += "0";

			if( negative ) s = "-"+s;
		}
		
		return s;
	}
}
