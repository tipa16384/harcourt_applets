import java.awt.*;
import util.DoubleBufferPanel;

public class GraphPanel extends DoubleBufferPanel
{
	Melon melon;
	GraphInfo info;
	Graph graph;
	Message message;
	Planet planet = null;
	double altitude = 0.0;
	
	public GraphPanel( Melon melon, GraphInfo info )
	{
		super( new BorderLayout() );

		this.melon = melon;
		this.info = info;
		
		graph = new Graph( "Height (meters)",
						   "Time (seconds)",
						   1600,20,
						   0,0,
						   200,2 );

		graph.hideTitle();
		
		add( graph, BorderLayout.CENTER );
		
		message = new Message();
		message.setBackground( Color.black );
		message.setForeground( Color.green );
		add( message, BorderLayout.SOUTH );

		setMessage();
	}
	
	public void reset()
	{
		graph.removeAllFunctions();
		graph.removeAllXMarks();
		graph.removeAllYMarks();
	}
	
	public void hurl( double vel )
	{
		//System.out.println("GraphPanel: v0="+vel+" alt="+altitude);
		
		graph.setRenderBounds(0,0);
		graph.addFunction( new Trajectory(vel, planet.getGravity(), altitude), Color.yellow );
	}
	
	public void setHurlTime( double secs )
	{
		graph.setRenderBounds(0,secs);
		secs =  Math.rint(secs*10.0)/10.0;
		graph.replaceXMark(1,secs,secs+"s",Color.yellow,true);
	}
	
	public void setPlanet( Planet planet )
	{
		this.planet = planet;
		graph.setYMinMax(0,planet.getMaxHeight());
		graph.setTicks(2,planet.getYTicks());
		
		setMessage();
	}

	public void setAltitude( double alt )
	{
		this.altitude = alt;
		graph.setYMark(alt,"altitude "+alt+"m",Color.yellow);
		graph.setXMark(10.0,"10.0s",Color.yellow);
		setMessage();
	}
	
	void setMessage()
	{
		if( planet != null )
		{
			message.setText("Gravity: "+"-"+planet.getGravity()+
				"m/s/s  Height: "+altitude+"m");
		}
		
		else
		{
			message.setText("No planet selected.");
		}
	}
	
	class Trajectory implements Function
	{
		double vel, acc, alt;
		
		public Trajectory( double vel, double acc, double alt )
		{
			this.vel = vel;
			this.acc = acc;
			this.alt = alt;
		}

		public double value( double x )
		{
			double v = alt - vel*x - (acc*x*x)/2;
			return v;
		}
	}
}