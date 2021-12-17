import java.util.EventListener;

public interface PointEventListener extends EventListener
{
	public void plot( PointEvent point );
	public void graph( String formula );
}
