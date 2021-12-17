import java.util.EventObject;

public class PointEvent extends EventObject
{
	double x, y;
	
	public PointEvent( Object source, double x, double y )
	{
		super(source);
		
		this.x = x;
		this.y = y;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
}
