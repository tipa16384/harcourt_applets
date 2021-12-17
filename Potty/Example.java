import java.awt.*;
import Graph.*;
import java.net.*;
import java.awt.event.*;
import Images.Images;
import java.io.InputStream;
import java.applet.*;

public abstract class Example implements AdjustmentListener
{
	static final boolean debug = false;
	
	int Q = 1;		// the maximum charge.
	int R;				//a state.global for plotPanel to test for specialTextPopup show/hide
	int A;				//a state.global for plotPanel to test for specialTextPopup show/hide
	int B;				//a state.global for plotPanel to test for specialTextPopup show/hide
	int mode = -1;
		
	// some constants common to all examples.

	static final double rad = 25.0;
	static public final double innerRad = 3.0;
	static final int irad = (int) rad;
	static final int iInnerRad = (int) innerRad;
	static final double diam = rad*2;
	static final double rad2 = rad*rad;
	static public final Point Op = new Point(125,50);
	static final Images datastore = new Images();
	
	static public final int CHARGEDSPHERE = 0;
	static public final int INFINITELINE = 1;
	static public final int PARALLELPLATES = 2;
	static public final int INFINITECOAX = 3;
	static public final int PERPENDICULARDIPOLE = 4;
	static public final int AXISDIPOLE = 5;
	static public final int CHARGEDRING = 6;
	static public final int CHARGEDDISK = 7;	
	static public final int CONDUCTINGSPHERE = 8;	
	static public final int POINTCHARGE = 9;	

	public int position = 0;
	public int	xMag = 0;
	public int	yMag = 0;		//was 6
	
	GordySlider Qslider = null;
	GordySlider Rslider = null;
	GordySlider Bslider = null;
	
	ActionListener listeners = null;

	// the ID for this example. Filled in during initialization.

	int id = -1;
	
	Main applet;
	
	public void setApplet( Main applet )
	{
		this.applet = applet;
	}

	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}

	// should we draw a yellow line horizontally through
	// the image?
	
	public boolean isRestrictedGraph()
	{
		return false;
	}
	
	// return true if this contains only positive charges
	
	public boolean isPositiveOnly()
	{
		return true;
	}

	// The name, as it appears in the choice box.
	
	public String getName()
	{
		return "Example";
	}
	
	// The ID, usually the zero-based index where it appears
	// in the choice box; but it doesn't have to be.
	
	public int getID()
	{
		if( this instanceof ParallelPlates )
			return PARALLELPLATES;
		if( this instanceof CoaxialCylinder )
			return INFINITECOAX;
		if( this instanceof PerpendicularDipole )
			return PERPENDICULARDIPOLE;
		if( this instanceof AxisDipole )
			return AXISDIPOLE;
		if( this instanceof ChargedRing )
			return CHARGEDRING;
		if( this instanceof ChargedDisk )
			return CHARGEDDISK;
		if( this instanceof ConductingSphere )
			return CONDUCTINGSPHERE;
		if( this instanceof PointCharge )
			return POINTCHARGE;
		if( this instanceof ChargedSphere )		//BEWARE pointcharge and conduitingshpere
			return CHARGEDSPHERE;				// MUST test before chargedsphere  (they extend it)
		
		return -1;
	}
	
	// draw the example.
	
	public void paint( Graphics g )
	{
		g.setFont( new Font("SanSerif",Font.PLAIN,9) );
		FontMetrics fm = g.getFontMetrics(g.getFont());
		g.drawString("2D Diagram", 1, fm.getAscent() );

		if( (!applet.isCrippled() || (mode != ElecPot.VIEW_MODE)) && isRestrictedGraph() )
		{
			g.setColor( GraphInfo.CONTROL_COLOR );
			Rectangle r = getHighlight();

			if( applet.isCrippled() )
			{
				g.fillRect( r.x, r.y, r.width, r.height );
			}

			else
			{			
				g.setXORMode( Color.white );
				g.fillRect( r.x, r.y, r.width, r.height );
				g.setPaintMode();
			}
				//special one time diag crosshair ****please remove
				// which does NOT work here.
			//g.drawLine((r.width/4),r.height/2,((r.width/2)*3),r.height/2);
			//g.drawLine((r.width/2),(r.height/4),(r.width/2),((r.height/2)*3));
		}
	}
	
	public Rectangle getHighlight()
	{
		return new Rectangle( 10, Op.y-iInnerRad, 2*Op.x-20, 2*iInnerRad );
	}
	
	// set up the example around the given origin.
	
	public void setExample( GraphPaper paper, int ex, DPoint O )
	{
	}
	
	// yet another interface to the base potential equation; I hope this
	// will be the one to supplant all others.
	
	public double potential( double x, int q, int a, int b )
	{
		debug("example::pot");
		return 0.0;
	}
	
	// add the specified charge to the given GraphPaper

	void newChargeAt( GraphPaper paper, DPoint O, boolean negative )
	{
		Charge c = new Charge(GraphInfo.info,(negative?-1:1));
		c.setOrigin(O);
		c.setVisible(false);
		paper.add(c,false);
	}
	
	// return the maximum charge.
	
	public double getCharge()
	{
		return (double) Q;
	}
	
	// add the sliders for this example to the given panel, which
	// is presumed to have a simple layout manager which can be
	// used with the plain "add(Component)" method.
	
	public void addSliders( Panel p )
	{
		Qslider = new GordySlider("~!Q", 1, 5, 2, "nC");
		Qslider.addAdjustmentListener(this);
		p.add( Qslider, new Rectangle(2,2,168,20) );
		
		Rslider = new GordySlider("~!R", 5, 50, 15, "cm");
		Rslider.addAdjustmentListener(this);
		p.add( Rslider, new Rectangle(2,22,168,20) );
	}
	
	// get the label for "X" axis for this example
	
	public String getXLabel()
	{
		return isRestrictedGraph() ? "~!x~!(cm)" : "~!r~!(\u03BCm)";
	}
	
	// get the label for the "Y" axis for this example
	
	public String getYLabel()
	{
		return "~!V~!(V)";
	}
	
	// return the minimum and maximum values for X
	
	public DPoint xMinMax()
	{
		DPoint d = new DPoint();
		
		if( isRestrictedGraph() )
		{
			d.x = -125.0;
			d.y = 125.0;
		}
		
		else
		{
			d.x = 0.0;
			d.y = 125.0;
		}
		
		return d;
	}
	
	// return the minimum and maximum values for Y
	
	public DPoint yMinMax()
	{
		DPoint d = new DPoint();
		
		if( isPositiveOnly() )
		{
			d.x = 0.0;
			d.y = 0.025;
		}
		
		else
		{
			d.x = -0.025;
			d.y = 0.025;
		}
		
		return d;
	}
	
	// potential calculation grabbed from GraphBackground. Here so that
	// it can be overridden by those who have there own ideas about
	// how to calculate the potential.

	DPoint dorigin = null;
	final int lopot = -10;
	final int hipot = 10;
	
	public double calcDpot( GraphPaper paper, DPoint dorigin )
	{
		return Charge.calcPotential(paper.getComponents(),dorigin);
	}
	
	public int calcpot( GraphPaper paper, int x, int y )
	{
		dorigin = DPoint.setDPoint(dorigin,x,y,paper.scale);
		return calcpot( paper, dorigin );
	}
	
	public int calcpot( GraphPaper paper, DPoint dorigin )
	{
		double dpot = calcDpot(paper,dorigin);
		
		int pot = (int)(dpot*600.0);

		if( pot < lopot ) pot = lopot;
		else if( pot > hipot ) pot = hipot;
		
		return pot;
	}
	
	// setup the Graph object for this graph.
	
	public void setupGraph( Graph graph )
	{
		graph.setLabels(getXLabel(),getYLabel());

		DPoint d;
		
		d = xMinMax();
		
		graph.setXAxis( d.x, d.y, 0, 0, xMag );
		
		d = yMinMax();
		
		graph.setYAxis( d.x, d.y, 0.0, 0.0, yMag);
	}
	
	public void setupPlot( Graph graph, DataSet ds2 )
	{
	}

	// get the iamge for the rendered picture
	
	public Image get3Dimage()
	{
		return aThousandPaths( get3Dname() );
	}

	// get the name of the file containing the rendered picture
	
	public String get3Dname()
	{
		int id = getID();

		//if( id == CONDUCTINGSPHERE )		//special code to share artwork
		//	id = CHARGEDSPHERE;				// with CHARGEDSPHERE

		String s = "";
		
		if( id >= 0 )
		{
			s = "apart";

			id = (id+1)*2;
			
			if( id < 10 ) s += "0";
			
			s += id + ".gif";
		}
		return s;
	}
	
	public Image aThousandPaths( String name )
	{
		String s = name;
		String s1 = "Images/"+name;

						//The following is going way overboard!!!
						// It gets all browsers to work (load the correct image)
						//  BEWARE The order is important/critical
		Image image = null;
		URL url = null;
		int	attempt = 0;			//for diag

		if( image == null )	// ATTEMPT #1    ICE==security crash, NetScape==null
		{
			attempt++; 
			debug("attempt #"+attempt+" - use the class loader to construct the URL");
			try
			{
				ClassLoader loader = getClass().getClassLoader();
				debug("??? loader is "+loader);
				
				url = loader.getSystemResource(s1);
				if( url != null )
					image = applet.getImage(url);
				debug("--- returned URL="+url+" and image="+image);
			}
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}
		if( image == null )	// ATTEMPT #2    ICE==security crash, NetScape==null
		{
			attempt++; 
			debug("attempt #"+attempt+" - use the class loader to construct the URL");
			try
			{
				ClassLoader loader = getClass().getClassLoader();
				debug("??? loader is "+loader);
				
				url = loader.getSystemResource(s);
				if( url != null )
					image = applet.getImage(url);
				debug("--- returned URL="+url+" and image="+image);
			}
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}
		if( image == null )	// ATTEMPT #3    ICE==security crash, NetScape==null
		{
			attempt++; 
			debug("attempt #"+attempt+" - use the class loader to construct the URL");
			try
			{
				ClassLoader loader = getClass().getClassLoader();
				debug("??? loader is "+loader);
				
				url = loader.getResource(s1);
				if( url != null )
					image = applet.getImage(url);
				debug("--- returned URL="+url+" and image="+image);
			}
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}
		if( image == null )	// ATTEMPT #4    ICE==security crash, NetScape==null
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
		if( image == null )	// ATTEMPT #5    ICE==security crash, NetScape==null
		{
			attempt++;
			debug("attempt #"+attempt+" - use the loader to get a resource as a stream");
			try
			{
				InputStream is = getClass().getResourceAsStream(s1);
				
				byte [] but = new byte[is.available()];
				is.read(but);
				image = Toolkit.getDefaultToolkit().createImage(but);

				debug("--- returned image "+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	
		if( image == null )	// ATTEMPT #6    ICE==security crash, NetScape==null
		{
			attempt++;
			debug("attempt #"+attempt+" - use the loader to get a resource as a stream");
			try
			{
				InputStream is = getClass().getResourceAsStream(s);
				
				byte [] but = new byte[is.available()];
				is.read(but);
				image = Toolkit.getDefaultToolkit().createImage(but);

				debug("--- returned image "+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	
/*
		if( url == null )
		{
			try
			{
				attempt++;
				url = new URL(applet.getCodeBase(),s1);			//works for ICE
			}
			catch( MalformedURLException me )
			{ debug("On attempt "+attempt+" MalformedURLException "+me ); }
		}
		if( url == null )
		{
			try
			{
				attempt++;
				url = new URL(applet.getCodeBase(),s);			//works for ICE
			}
			catch( MalformedURLException me )
			{ debug("On attempt "+attempt+" MalformedURLException "+me ); }
		}
		if( url == null )
		{
			try
			{
				attempt++;
				url = new URL(applet.getDocumentBase(),s1);			//works for ICE
			}
			catch( MalformedURLException me )
			{ debug("On attempt "+attempt+" MalformedURLException "+me ); }
		}
		if( url == null )
		{
			try
			{
				attempt++;
				url = new URL(applet.getDocumentBase(),s);			//works for ICE
			}
			catch( MalformedURLException me )
			{ debug("On attempt "+attempt+" MalformedURLException "+me ); }
		}
*/	
		debug("get 3D image attempt "+attempt+" is "+url+" "+image);
		
		return image;
	}
	
	// get the name of the formula picture
	
	public Image formulaImage()
	{
		String s = "formula"+(getID()+1)+".gif";
		
		return aThousandPaths(s);
	}
	
	// trigger graph redraw
	
	public void triggerRedraw()
	{
	   	if( listeners != null )
    	{
			double q = (Qslider==null) ? 1 : (double)Qslider.getValue();
			double a = (Rslider==null) ? 10 : (double)Rslider.getValue();
			double b = (Bslider==null) ? 20 : (double)Bslider.getValue();

			if( (Bslider!=null) && (a >= b) )	//Special case for coax
			{
				debug("b=="+b+" a+1="+(a+1));
				b = a + 1;
				Bslider.setValue( (int)b );
			}

    		ActionEvent ae = new ActionEvent( new DPoint(q,a,b), 0, "redraw" );
    		listeners.actionPerformed(ae);
    	}
	}

	// is this point within all the valid boundaries?
	
	public boolean significantPoint( double x, double y )
	{
		Rectangle r = getHighlight();
		
		return !isRestrictedGraph() || r.contains((int)x,(int)y);
	}
	
	// return the corresponding 'x' coordinate for this set of coordinates.
	
	public double calcX( double dx, double dy )
	{
		return isRestrictedGraph() ? dx : Math.sqrt(dx*dx+dy*dy);
	}

	// is this point within all the valid boundaries?
		
    /**
     * Invoked when the value of the adjustable has changed.
     */   
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
    	//debug(e);
    	triggerRedraw();
    }

	public void setMode( int mode )
	{
		this.mode = mode;
	}

	// just a reference string
	
	public String toString()
	{
		return getName()+"("+getID()+")";
	}

	static void debug( String s )
	{
		if( debug )
		{
			System.out.println("Example:: "+s);
		}
	}
}
