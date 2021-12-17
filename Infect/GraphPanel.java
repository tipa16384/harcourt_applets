import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;

public class GraphPanel extends DoubleBufferPanel
						implements PropertyChangeListener
{
	Infect infect;
	GraphInfo info;
	
	Graph graph;
	Image yLabel;
	Image xLabel;
	
	public GraphPanel( Infect infect, GraphInfo info )
	{
		super(new BorderLayout(5,5));
		this.infect = infect;
		this.info = info;
		
		readImages();
		
		setBackground( infect.getColor() );
		
		graph = new Graph(null, null,
					10.0, 390.0,
					7.0, 0,
					1.0, 30.0);
		
		graph.setXWinnow(90.0);
		graph.hideTitle();
		graph.setContentColor( new Color(0xcc,0xcc,0xcc) );
		graph.setStyle( Graph.ALTERNATE );
		
		if( false )
		{
			graph.addPoint(0,5.23E7,Color.red);
			graph.addPoint(30,4.29E7,Color.red);
			graph.addPoint(60,5.78E7,Color.red);
			graph.addPoint(90,5.94E7,Color.red);
			graph.addPoint(120,9.42E7,Color.red);
			graph.addPoint(150,21.5E7,Color.red);
			graph.addPoint(180,63.5E7,Color.red);
			graph.addPoint(210,135E7,Color.red);
			graph.addPoint(240,278E7,Color.red);
			graph.addPoint(270,295E7,Color.red);
			graph.addPoint(300,318E7,Color.red);
			graph.addPoint(330,288E7,Color.red);
			graph.addPoint(360,245E7,Color.red);
			graph.addPoint(390,216E7,Color.red);
		}
				
		add( graph, BorderLayout.CENTER );
		add( new ImageIcon( yLabel ), BorderLayout.WEST );
		add( new ImageIcon( xLabel ), BorderLayout.SOUTH );
				
		info.addPropertyChangeListener(this);
	}

	void readImages()
	{
		yLabel = Utility.getImage(this,"ylabel.gif");
		xLabel = Utility.getImage(this,"xlabel.gif");
		
		try
		{
			MediaTracker mt = new MediaTracker(this);
			mt.addImage( yLabel, 0 );
			mt.addImage( xLabel, 0 );
			mt.waitForAll();
		}
		
		catch( Exception e )
		{
		}
	}
	
	public void propertyChange( PropertyChangeEvent pce )
	{
		String pname = pce.getPropertyName();
		
		if( pname.equals(info.reset_applet) )
		{
			graph.removeAllPoints();
		}
		
		if( pname.equals(info.new_point) )
		{
			DPoint dp = (DPoint) pce.getNewValue();
			graph.addPoint(dp.x,dp.y,Color.red);
		}
	}
	
	class ImageIcon extends Component 
	{
		Image image;
		
		public ImageIcon( Image image )
		{
			this.image = image;
		}
		
		public Dimension getPreferredSize()
		{
			int iw = image.getWidth(this);
			int ih = image.getHeight(this);
			
			return new Dimension(iw,ih);
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			Dimension dim0 = getPreferredSize();
			
			g.drawImage( image, (dim.width-dim0.width)/2,
								(dim.height-dim0.height)/2,
								this );
		}
	}
}
