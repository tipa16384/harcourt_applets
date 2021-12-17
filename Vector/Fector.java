// the name 'Vector' has already been used...

public class Fector
{
	public double azimuth = 0.0;		// x<->y angle
	public double declination = 0.0;	// z angle
	public double length = 0.0;
	public transient double potential = 0.0;

	public Fector()
	{
		this(0,0,0);
	}
	
	public Fector( double azimuth )
	{
		this( azimuth, 0, 1.0 );
	}

	public Fector( double azimuth, double length )
	{
		this( azimuth, 0, length );
	}
	
	public Fector( double azimuth, double declination, double length )
	{
		setAzimuth( azimuth );
		setDeclination( declination );
		setLength( length );
	}

	public Fector( Fector f )
	{
		this( f.azimuth, f.declination, f.length );
	}

	public String toString()
	{
		int daz = (int)((azimuth*180.0)/Math.PI+0.5);
		int dec = (int)((declination*180.0)/Math.PI+0.5);
		return getClass().getName()+"[az="+daz+",dec="+dec+",len="+length+"]";
	}

	public double getAzimuth()
	{
		return azimuth;
	}
	
	public void setAzimuth( double azimuth )
	{
		this.azimuth = fixAngle(azimuth);
	}
	
	static public double fixAngle( double ang )
	{
		final double pi2 = Math.PI*2.0;
		
		while( ang < 0.0 ) ang += pi2;
		while( ang >= pi2 ) ang -= pi2;
		return ang;
	}
	
	public double getDeclination()
	{
		return declination;
	}
	
	public void setDeclination( double declination )
	{
		final double pi2 = Math.PI*2.0;
		
		while( declination < 0.0 ) declination += pi2;
		while( declination >= pi2 ) declination -= pi2;
		this.declination = declination;
	}
	
	public double getLength()
	{
		return length;
	}
	
	public void setLength( double length )
	{
		if( length < 0 )
		{
			rotate(Math.PI);
			this.length = -length;
		}
		
		else
		{
			this.length = length;
		}
	}
	
	public void setEndpoint( double x, double y )
	{
		setEndpoint( x, y, 0.0 );
	}
	
	public void setEndpoint( DPoint d )
	{
		setEndpoint( d.x, d.y, d.z );
	}
	
	public static int toDeg( double rad )
	{
		return (int)(rad*180.0/Math.PI+0.5);
	}
	
	public int toDeg()
	{
		return toDeg(getAzimuth());
	}
	
	public void invert()
	{
		setAzimuth( getAzimuth()+Math.PI );
		declination = -declination;
	}
	
	public void setEndpoint( double x, double y, double z )
	{
		//System.out.println("setEndpoint("+x+","+y+","+z+")");
		double lenSquared = x*x+y*y;
		double len = Math.sqrt(lenSquared);
		double lenny = Math.sqrt(lenSquared+z*z);
		//System.out.println("   len="+len+" (or "+lenny+")");
		
		double ang = Math.asin(y/len);
		//System.out.println("   ang="+toDeg(ang));
		if( x < 0 )
		{
			ang = Math.PI-ang;
			//System.out.println("   reflecting to "+toDeg(ang));
		}
		setLength( lenny );
		//System.out.println("   length set to "+getLength());
		setAzimuth( ang );
		//System.out.println("   azimuth set to "+toDeg(getAzimuth()));

		setDeclination(Math.asin(z/lenny));
		//System.out.println("   dec set to "+toDeg(getDeclination()));
	}

	public DPoint getEndpoint()
	{
		double r = length*Math.cos(declination);

		return new DPoint(r*Math.cos(azimuth),
						  r*Math.sin(azimuth),
						  length*Math.sin(declination));
	}
	
	final boolean pot2 = true;
	
	public void add( Fector f )
	{
		DPoint d1 = getEndpoint();
		DPoint d2 = f.getEndpoint();
		setEndpoint( d1.x+d2.x, d1.y+d2.y, d1.z+d2.z );
		double pot = f.getLength();
		
		if( pot2 )
			potential -= pot*pot;
		else
			potential -= pot;
	}
	
	public void subtract( Fector f )
	{
		DPoint d1 = getEndpoint();
		DPoint d2 = f.getEndpoint();
		setEndpoint( d1.x-d2.x, d1.y-d2.y, d1.z-d2.z );
		double pot = f.getLength();
		
		if( pot2 )
			potential += pot*pot;
		else
			potential += pot;
	}
	
	public Fector normal()
	{
		Fector f = new Fector(this);
		// note: "normal()" will not work with a non-zero
		// declination.
		f.rotate(Math.PI/2,0);
		return f;
	}
	
	public void rotate( double az )
	{
		rotate( az, 0 );
	}
	
	public void rotate( double az, double dec )
	{
		setAzimuth(azimuth+az);
		setDeclination(declination+dec);
	}
	
	public Fector unit()
	{
		Fector f = new Fector(this);
		f.setLength(1.0);
		return f;
	}
}
