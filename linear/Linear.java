import java.awt.*;
import java.applet.*;
import util.DoubleBufferPanel;

public class Linear extends Panel
{
	Main main;
	GraphInfo info;
	
	public Linear( Main main, GraphInfo info )
	{
		super( new GridLayout(0,1) );
		
		Panel p;
		Component c;
		
		this.main = main;
		this.info = info;
		
		info.setCurrentFunction( new Curve() );
		
		p = new GraphPanel();
		p.setBackground( info.CONTROL_COLOR );
		p.add( new Graph2(info), BorderLayout.CENTER );
		p.add( new Legend(), BorderLayout.EAST );
		c = new Label("Click on a point");
		c.setFont( info.fontBiggerBold );
		p.add( c, BorderLayout.NORTH );
		add( p );
		
		add( new Controls(info) );
		
		invalidate();
		doLayout();
		repaint();

		main.reset();
	}
	
	class Curve implements Function
	{
		public double value( double x )
		{
			if( x <= 0.0 ) return 0.0;
			return Math.pow(x,1.5);
		}
	}
	
	class GraphPanel extends DoubleBufferPanel
	{
		public GraphPanel()
		{
			super( new BorderLayout() );
		}
		
		public Insets getInsets()
		{
			return new Insets(4,4,4,4);
		}
	}
	
	class Legend extends Component
	{
		Image image;
		
		public Legend()
		{
			try
			{
				image = Utility.getImage( this, "legend.gif" );
				if( image != null )
				{
					MediaTracker mt = new MediaTracker(this);
					mt.addImage(image,0);
					mt.waitForAll();
				}
			}
			
			catch( Exception e )
			{
				System.err.println("While getting legend image - "+e);
				image = null;
			}
		}
		
		public Dimension getPreferredSize()
		{
			Dimension dim = super.getPreferredSize();
			if( image != null )
			{
				dim.width = Math.max(dim.width,image.getWidth(this));
				dim.height = Math.max(dim.height,image.getHeight(this));
			}
			return dim;
		}

		public Dimension getMinimumSize()
		{
			Dimension dim = super.getMinimumSize();
			if( image != null )
			{
				dim.width = Math.max(dim.width,image.getWidth(this));
				dim.height = Math.max(dim.height,image.getHeight(this));
			}
			return dim;
		}

		public void paint( Graphics g )
		{
			super.paint(g);
			
			Dimension dim = getSize();
			
			if( image != null )
			{
				int ih = image.getHeight(this);
				g.drawImage( image, 0, dim.height-ih, this );
			}
		}
	}
}
