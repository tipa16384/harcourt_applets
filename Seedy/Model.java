import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;
import java.io.InputStream;
import Wired.Arrow;

public class Model extends Component implements AdjustmentListener
{
	GraphInfo info = null;
	ActionListener listeners = null;
	Main applet = null;
	RawLabel randomText;
	transient boolean probeShowing = false;
	transient Point from=null, to=null;

	static final boolean debug = true;
	
	public Model( String name )
	{
		setName( name );
		setFont( new Font("SansSerif",Font.PLAIN,10) );
		randomText = new RawLabel();
		randomText.setFont( new Font("SansSerif",Font.PLAIN,9) );
	}
	
	public void setApplet( Main applet )
	{
		this.applet = applet;
	}

	public void doLayout()
	{
		//System.out.println("Model.doLayout");
	}

	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}

	public void setInfo( GraphInfo info )
	{
		this.info = info;
	}
	
	public String toString()
	{
		return getClass().getName()+"["+getName()+"]";
	}
	
	public void addSliders( Container c )
	{
		// should be overridden, then call this one through super.
		
		Component [] clist = c.getComponents();
		
		for( int i=0; i<clist.length; ++i )
		{
			Component comp = clist[i];
			
			if( comp instanceof GordySlider )
			{
				GordySlider slider = (GordySlider) comp;
				
				slider.addAdjustmentListener( this );
			}
		}
		
		resetValues();
	}

	public void drawCircuit( Graphics g, Component c, Dielectric d )
	{
		final String line1 = "Click inside capacitor";
		final String line2 = "for ~!E~!, ~!~m~! and "+variable();
		
		Point p = getPopupPoint(c);
		randomText.setText(line1);
		randomText.paint( g, p.x, p.y, false );
		randomText.setText(line2);
		randomText.paint( g, p.x, p.y+randomText.getMinimumSize().height, false );
		
		//System.out.println("Model.paint probeShowing="+probeShowing);
		
		if( probeShowing )
		{
			g.setColor( GraphInfo.AXIS_COLOR );
			Arrow.drawLine( g, from.x, from.y, to.x, to.y, 1 );
		}
	}

	public void showLine( Component c, Point from, Point to )
	{
		//System.out.println("showLine("+from+","+to+")");
		probeShowing = true;
		this.from = from;
		this.to = to;
		c.repaint(100);
	}
	
	public void hideLine( Component c )
	{
		//System.out.println("hideLine()");
		probeShowing = false;
		c.repaint(100);
	}

	public double value( Component sirkit, int x, int y )
	{
		return 0.0;
	}

	public String units()
	{
		return "mm";
	}

	public String variable()
	{
		return "~!?~!";
	}

	public Point getPopupPoint( Component c )
	{
		return new Point(0,0);
	}

	void print( Graphics g, int line, String s )
	{
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);

		g.drawString( s, 0, fm.getHeight()*line + fm.getAscent() );
	}

	public Point centerPoint(Component c)
	{
		Dimension size = c.getSize();
		return new Point(size.width/2,size.height/2);
	}

	public DPoint shiftPoint( Component c, int x, int y )
	{
		Point p = centerPoint(c);
		return new DPoint(x-p.x,y-p.y,1.0);
	}

	public String getImageName()
	{
		return "disks.gif";
	}

	public void resetValues()
	{
	}

	public double getEField( Component c, double x, double y, Dielectric d )
	{
		return 0.0;
	}

	public double getEnergy( Component c, double x, double y, Dielectric d )
	{
		double E = getEField(c,x,y,d);
		
		return 0.5 * GraphInfo.e0 * E * E;
	}

	public boolean inDielectric( Component c, int x, int y )
	{
		return getClickBounds(c).contains(x,y);
	}

	public Rectangle getClickBounds( Component c )
	{
		Dimension size = c.getSize();
		return new Rectangle( 0, 0, size.width, size.height );
	}

	public Image getImage()
	{
		String s = getImageName();

		debug("looking for "+s);
		
						//The following is going way overboard!!!
						// It gets all browsers to work (load the correct image)
						//  BEWARE The order is important/critical
		Image image = null;
		int	attempt = 0;			//for diag
		URL url = null;
		
		// ATTEMPT #1
		// ICE==security crash, NetScape==null

		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the class loader to construct the URL");
			
			try
			{
				ClassLoader loader = getClass().getClassLoader();
				
				debug("??? loader is "+loader);
				
				url = loader.getResource(s);
				
				if( url != null )
					image = applet.getImage(url);
				
				debug("--- returned URL="+url+" and image="+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	

		// ATTEMPT #2
		// ?
		
		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the loader to get a resource as a stream");
			
			try
			{
				debug("             does not compute");
				
				Class cla = getClass();
				
				debug("             class is "+cla+" and 's' is "+s);
				
				InputStream is = cla.getResourceAsStream(s);
				
				debug("            is (InputStream) is "+is);
				
				byte [] but = new byte[is.available()];
				
				debug("            but["+is.available()+"] is "+but);
				
				is.read(but);
				
				debug("            data read");
				
				image = getToolkit().createImage(but);

				debug("--- returned image "+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	

		// ATTEMPT #3
		// 

		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the class to construct the URL");
			
			try
			{
				url = getClass().getResource(s);
				
				if( url != null )
					image = applet.getImage(url);
				
				debug("--- returned URL="+url+" and image="+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	

		// ATTEMPT #4
		// ICE==security crash, NetScape==null
		
		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the class loader to get a system resource");
			
			try
			{
				url = getClass().getClassLoader().getSystemResource(s);
				
				if( url != null )
					image = applet.getImage(url);
					
				debug("--- returned URL="+url+" and image="+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	

		// ATTEMPT #5
		// works for ICE
		
		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the codebase ("+applet.getCodeBase()+") to form the URL");
			
			if( applet == null )
			{
				debug("--- applet is null");
			}
			
			else
			{
				try
				{
					url = new URL(applet.getCodeBase(),s);
					
					if( url != null )
						image = applet.getImage(url);
						
					debug("--- returned URL="+url+" and image="+image);
				}
				
				catch( Exception e )
				{
					debug("--- threw "+e);
				}
			}
		}

		// ATTEMPT #6
		// works for ICE
		
		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the documentbase ("+applet.getDocumentBase()+") to form the URL");
			
			if( applet == null )
			{
				debug("--- applet is null");
			}
			
			else
			{
				try
				{
					url = new URL(applet.getDocumentBase(),s);
					
					if( url != null )
						image = applet.getImage(url);
						
					debug("--- returned URL "+url);
				}
				
				catch( Exception e )
				{
					debug("--- threw "+e);
				}
			}
		}

	/*
		s = url.toString();
		try { url = new URL( s ); }
		catch ( MalformedURLException me )
			{ System.out.println("MalformedURLException "+me ); }
	*/
		if( image == null )
			debug("*** couldn't construct an image for "+s+" (even though the URL was "+url+")");
		else
			debug("*** success with attempt "+attempt+" produces "+image+" from URL "+url);
		
		return image;
	}

	public int macBugNudge()
	{
		return applet.isCrippled() ? -1 : 0;
	}

    /**
     * Invoked when the value of the adjustable has changed.
     */   
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
    	//System.out.println(e);

		if( listeners != null )
		{
			listeners.actionPerformed( new ActionEvent(this,0,Kapasitenz.REDRAW) );
		}
    }

	static void debug( String s )
	{
		if( debug )
		{
			System.out.println("Model:: "+s);
		}
	}
}
