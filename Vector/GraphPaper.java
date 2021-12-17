import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class GraphPaper extends Panel
{
	public static final int tick = 12;
	public double scale = 1.0;

	public Vector curves = null;
	public Vector curveColors = null;
	
	Image backdrop = null;
	MediaTracker media = null;
	final int mediaID = 0;
	Thread imageThread = null;
	Vector components = null;
		
	public GraphPaper()
	{
		//setBackground( Color.white );
		setLayout(null);
		media = new MediaTracker(this);
		components = new Vector();
		addComponentListener( new CompTracker() );
	}
	
	class CompTracker extends ComponentAdapter
	{
	    public void componentResized(ComponentEvent e)
	    {
	    	System.out.println(e.toString());
	    	updateBackdrop();
	    }
	    
	    public void componentShown(ComponentEvent e)
	    {
	    	System.out.println(e.toString());
	    	updateBackdrop();
	    }
	}
	
	synchronized public void updateBackdrop()
	{
		//System.out.println("Updating backdrop");

		if( imageThread != null )
		{
			//System.out.print("... killing old thread");
			if( imageThread != null ) imageThread.interrupt();
			if( imageThread != null ) imageThread.stop();
			//System.out.print("... done");
		}
		
		if( backdrop != null )
			media.removeImage( backdrop );
		backdrop = createImage( new GraphBackground(this) );
		media.addImage( backdrop, mediaID );
		//media.checkID( mediaID, true );
		//prepareImage(backdrop, this);
		repaint();
	}

	public boolean showGrid()
	{
		return true;
	}
	
	public boolean showAxes()
	{
		return true;
	}
	
	// placeholder for resetting.
	
	public void reset()
	{
		removeAll();
	}
	
	// placeholder for recalculations.
	
	public void recalc()
	{
	}
	
	public void removeAll()
	{
		removePlots();
		components = new Vector();
		//super.removeAll();
	}
	
	public Component [] getComponents()
	{
		if( components == null )
			return new Component[0];
			
		int len = components.size();
		Component [] clist = new Component[len];
		
		for( int i=0; i<len; ++i )
			clist[i] = (Component) components.elementAt(i);
		
		return clist;
	}
	
	public int getComponentCount()
	{
		return components.size();
	}
	
	public void removePlots()
	{
		//System.out.println("removePlots");
		curves = null;
		curveColors = null;
		updateBackdrop();
	}
	
	public double getScale()
	{
		return scale;
	}
	
	public void setScale( double s )
	{
		scale = s;
	}
	
	public void plotCurve( Vector pointArray )
	{
		plotCurve( pointArray, Color.black );
	}
	
	static int times = 0;
	
	public void plotCurve( Vector pointArray, Color color )
	{
//		System.out.println("plotCurve" + (++times));

		if( curves == null )
		{
			curves = new Vector(1);
			curveColors = new Vector(1);
		}
		
		curves.addElement( pointArray );
		curveColors.addElement( color );
	}
	
	public boolean showEquipotential()
	{
		return false;
	}
	
	public boolean showColor()
	{
		return false;
	}
	
	public boolean okayToPlot()
	{
		return true;
	}
	
	public void doLayout()
	{
		//System.out.println("doLayout");

		Component [] clist = getComponents();
		int len = clist.length;
		
		for( int i=0; i<len; ++i )
			arrange(clist[i]);
		
		super.doLayout();
	}

	public void arrange( Component c )
	{
		if( c instanceof GraphElement )
		{
			Dimension size = getSize();
			c.setBounds(0,0,size.width,size.height);
		}
	}	

	public void arrange()
	{
		int len = components.size();
		for( int i=0; i<len; ++i )
		{
			arrange( (Component) components.elementAt(i) );
		}
		
		updateBackdrop();
	}

	public Component add( Component c )
	{
		add( c, true );
		return c;
	}
	
	public Component add( Component c, boolean redraw )
	{
		if( redraw )
		{
			arrange(c);
			updateBackdrop();
		}
		
		components.addElement(c);
		if( c instanceof Charge )
			((Charge)c).parent = this;
			
		return c;
	}
	
	public void update( Graphics g )
	{
		paint(g);
	}
	
	public void paint( Graphics g )
	{
		//System.out.print("GraphPaper.paint");

		if( !isShowing() )
		{
			//System.out.println("...o mitenai yo!");
			return;
		}
		
		//System.out.println("...o mite dekimasu. Subarashii desu!");

		if( backdrop != null && media != null )
		{
			//System.out.print("checkID? ");
			if( media.checkID(mediaID) )
			{
				if( media.isErrorID(mediaID) )
					blueScreen(g,"Couldn't allocate memory for offscreen bitmap");
				else
					g.drawImage( backdrop, 0, 0, this );
			}
			
			else if( imageThread == null )
			{
				//System.out.println("Backdrop not ready, starting thread");
				(new ImageThread()).start();
			}
		}
		
		if( components != null )
		{
			int len = components.size();
			
		    Rectangle clip = g.getClipRect();
		    
			for( int i=0; i<len; ++i )
			{
				Component comp = (Component) components.elementAt(i);

				if (comp != null && 
				    comp.isVisible() == true)
				{
				    Rectangle cr = comp.getBounds();
				    if (( clip == null ) || cr.intersects(clip))
				    {
						Graphics cg = g.create(cr.x, cr.y, cr.width, cr.height);
						cg.setFont(comp.getFont());
						try
						{
						    comp.paint(cg);
						}
						
						finally
						{
						    cg.dispose();
					    }
				    }
				}
			}
		}
	}
	
	void blueScreen( Graphics g, String message )
	{
		g.setColor( Color.blue );
		Dimension size = getSize();
		g.fillRect( 0, 0, size.width, size.height );
		g.setColor( Color.white );
		
		FontMetrics fm = getFontMetrics(getFont());
		g.drawString( "This is the blue screen of sorrow", 10, fm.getAscent()+10 );
		g.drawString( message, 10, fm.getHeight()+fm.getAscent()+10 );
	}

	class ImageThread extends Thread
	{
		public void run()
		{
			if( media != null )
			{
				Component c = GraphPaper.this;
				Component parent;
				
				// find the ultimate parent so we can change the
				// cursor for the entire window.
				
				for(;;)
				{
					parent = c.getParent();
					if( parent == null ) break;
					c = parent;
				}
					
				try
				{
					imageThread = ImageThread.this;
					//System.out.println("waiting for media...");
					c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					media.waitForID(mediaID);
					repaint();
				}
				
				catch( Exception e )
				{
					//System.out.println("while waiting for media - "+e);
				}
				
				finally
				{
					//System.out.println("resetting imageThread");
					imageThread = null;
					c.setCursor(Cursor.getDefaultCursor());
				}
			}
		}
	}
}
