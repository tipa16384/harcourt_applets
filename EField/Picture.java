import java.awt.*;
import	java.net.*;

public class Picture extends Component implements Runnable
{
	static	boolean	addBorder = true;

	URL				codeBase;
	MediaTracker	tracker;
	Image			img = null;
	static	int		width = 16;
	static	int		height = 16;
		
	public Picture( String imageName )
	{
		codeBase = getClass().getResource(imageName);
		System.out.println("codeBase is "+codeBase+" for imageName "+imageName);
		img = null;
		if( codeBase != null )
		{
			img = getToolkit().getImage(codeBase);
			System.out.println("Image is now "+img);
		    tracker = new MediaTracker( this );
		    tracker.addImage( img, 0);
		    Thread runner = new Thread( this );
		    runner.start();
		}
	}
	
	public Dimension getMinimumSize()
	{
		if( img != null )
		{
			width = img.getWidth( this );
			height = img.getHeight( this );
		}
		return new Dimension((width/2),(height/2));
	}
	
	public Dimension getPreferredSize()
	{
		if( img != null )
		{
			width = img.getWidth( this );
			height = img.getHeight( this );
		}
		return new Dimension( width, height );
	}
	
  			/** turn the painting of a border around this panel */
	public void setHasBorders( boolean condition )
	{
		addBorder = condition;
	}
	
	public boolean getHasBorders(  )
	{
		return ( addBorder );
	}
  
	public void paint( Graphics g )
	{
		if( addBorder )				//draw a border around the plates area
		{
	        g.setColor(Color.black);
	        g.drawRect(0, 0, width - 1, height - 1);
	        g.setColor(Color.gray);
	        g.drawRect(1, 1, width - 3, height - 3);
	        g.setColor(Color.black);
	        g.drawRect(2, 2, width - 5, height - 5);
        }

		if( img != null )
		{
			g.drawImage( img, 0, 0, this );
			System.out.println("Painting Image "+img);
		}
	}

	public void run()					//Let mediaTracker wait for image loaded
	{
		int	i = 0;
		do
		{
			try { tracker.waitForID(0); }
			catch( InterruptedException e ) {;}
			System.out.println("tracker status "+tracker.statusID(0,true));
		}
		while( (tracker.statusID(0,true) != 8) && (++i < 4) );
		invalidate();
		this.getParent().doLayout();
		//repaint();
	}
}
