// format a double so that it's always in scientific notation, with three
// digits of precision. Return the format as a string with the RawLabel-type
// encoding to handle the superscripted exponent. Numbers between 0 and 10 do
// not have exponents (which would be 10^0, so why bother?)

public abstract class DoubleFormat
{
	static String format( double d )
	{
		int E = 0;
		boolean negative = false;
		
		// normalize d to the range 0 .. 9.9999999~

		if( d < 0.0 ) { negative = true; d *= -1.0; }
		while( d >= 10.0 ) { E++; d /= 10.0; }
		while( d > 0.0 && d < 1.0 ) { E--; d *= 10.0; }

		// bring it down to two decimal places
		
		d = Math.rint(d * 100.0) / 100.0;
		
		// now convert it to a string.
		
		String s = Double.toString(d);
		
		if( negative ) s = "-"+s;
		if( E != 0 ) s = s+"~+10~^"+E+"~^";
		
		return s;
	}
}
