import java.awt.*;
import	java.net.*;
import	java.applet.*;

public class Picture extends Component implements Runnable
{
	static	boolean	addBorder = false;

	URL				codeBase;
	MediaTracker	tracker;
	Image			img = null;
	int				width = 16;
	int				height = 16;
		
	public Picture( String imageName )
	{
		codeBase = getClass().getResource(imageName);
		System.out.println("codeBase is "+codeBase+" for imageName "+imageName);
		img = null;
		if( codeBase != null )
		{
			img = getToolkit().getImage(codeBase);
			//img = getImage( codeBase, imageName );
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
		int	scaling	= 1;
		int w = width / scaling;
		int h = height / scaling;

		if( img != null )
		{
			if( scaling == 1 )
				g.drawImage( img, 0, 0, this );
			else
				g.drawImage( img, 0, 0, w, h, this );
			System.out.println("Painting Image "+img);
		}

		if( addBorder )				//draw a border around the plates area
		{
	        g.setColor(Color.black);
	        g.drawRect(0, 0, w - 1, h - 1);
	        g.setColor(Color.gray);
	        g.drawRect(1, 1, w - 3, h - 3);
	        g.setColor(Color.black);
	        g.drawRect(2, 2, w - 5, h - 5);
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
