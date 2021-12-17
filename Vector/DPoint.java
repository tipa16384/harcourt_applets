import java.awt.Point;

public class DPoint
{
	public double x=0, y=0, z=0;
	public int special = 0;
	
	public DPoint()
	{
		this(0.0,0.0,0.0);
	}
	
	public DPoint( double x, double y )
	{
		this(x,y,0);
	}
	
	public DPoint( double x, double y, double z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public DPoint( Point p, double scale )
	{
		this( p.x, p.y, scale );
	}
	
	public DPoint( int x, int y, double scale )
	{
		this.x = ((double)x)/scale;
		this.y = ((double)y)/scale;
		this.z = z;
	}
	
	public DPoint( DPoint d )
	{
		x = d.x;
		y = d.y;
		z = d.z;
	}
	
	static public DPoint setDPoint( DPoint d, int x, int y, double scale )
	{
		if( d == null )
			d = new DPoint( x, y, scale );
		else
		{
			d.x = ((double)x)/scale;
			d.y = ((double)y)/scale;
		}
	
		return d;
	}

	public DPoint relative(double dx,double dy,double dz)
	{
		DPoint d = new DPoint(this);
		d.x += dx;
		d.y += dy;
		d.z += dz;
		return d;
	}
	
	public void copyFrom( DPoint p )
	{
		x = p.x;
		y = p.y;
		z = p.z;
		special = p.special;
	}
	
	public Point toPoint( double scale )
	{
		return new Point( (int)(scale*x+0.5), (int)(scale*(y)+0.5) );
	}
	
	public String toString()
	{
		return "DPoint("+x+","+y+","+z+")";
	}
}
