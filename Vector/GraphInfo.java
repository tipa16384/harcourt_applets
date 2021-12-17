import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.Vector;

public class GraphInfo 
{
	// applet mode for FC3
	static public final int FORCE = 0;
	static public final int FIELD = 1;
	static public final int POTENTIAL = 2;
	static public final int COLOR = 3;
	static private final int LEN = COLOR+1;
	
	// size of the charge circle, in pixels
	static public final int DOTSIZE = 10;
	
	// actual size of the charge component, in pixels
	static public final int CANVASSIZE = 1024;
	
	// color for the force vectors
	static public final Color FORCE_VECTOR_COLOR = new Color(0,51,204);
	
	// color for the negative charge
	static public final Color NEGATIVE_COLOR = new Color(51,102,255);
	
	// color for the positive charge
	static public final Color POSITIVE_COLOR = new Color(255,51,51);
	
	// color for the field lines
	static public final Color FIELD_COLOR = new Color(204,0,0);
	
	// color for the control backdrop
	static public final Color CONTROL_COLOR = new Color(255,255,206);
	
	// color of the "halo" around the selected charge
	static public final Color HIGHLIGHT_COLOR = new Color(255,204,51);
	
	// color of the grid lines (the 'rules') in the graph
	static public final Color GRID_COLOR = new Color(204,255,255);
	
	// color of the axes in the graph
	static public final Color AXIS_COLOR = new Color(204,204,204);
	
	// color of the horizontal line below the graph
	static public final Color SEPARATE_COLOR = new Color(153,153,153);
	
	static public String [][] paramList = {
		{ "grid",	"boolean",		"show grid" },
		{ "force",	"boolean",		"show force mode in FC3" },
		{ "field",	"boolean",		"show field mode in FC3" },
		{ "potential", "boolean",	"show equipotential in FC3" },
		{ "fc2",	"boolean",		"this is FC2 (not FC3)" },
		{ "scale",	"double",		"scaling factor" },
		{ "limit",	"boolean",		"limit the number of charges" },
		{ "highwater", "int",		"max. number of charges" },
		{ "color",	"boolean",		"use color equipotential" },
		{ "testcharge", "boolean",	"FC2 uses immutable test charge" }
		};
		
	boolean [] props;

	Charge charge = null;
	static public GraphInfo info = null;
		
    transient ActionListener actionListener;
    
    public boolean grid = true;
    public boolean field = true;
    public boolean potential = false;
    public boolean force = true;
    public boolean fc2 = false;
    public boolean limit = true;
    public int highwater = 10;
    public boolean color = false;
    public boolean testcharge = false;
	    
    public double scalingFactor = 7E15;
    
    public Vector plotThreads = null;

	static public String REDRAW = "REDRAW";
	static public String SELECTION = "SELECTION";
	static public String CHARGE = "CHARGE";

	// Coulomb's constant
	public static final double Ke = 8.9875E9;
	
	// charge on an electron, in Coulombs.
	public static final double C = -1.60217733E-19;
	
	// convert pixel to distance
	public final double mscale = 5.0E-8/GraphPaper.tick;
	
	// precalc constant part of Coulomb's Law equation.
	public final double Klunk = Ke*C*C/(mscale*mscale);

	public GraphInfo()
	{
		info = this;
		props = new boolean[LEN];
		
		plotThreads = new Vector();
		
		setShowForce( true );
		//setShowField( true );
	}

	public synchronized void killThreads()
	{
		int len = plotThreads.size();
		int i;
		
		if( len > 0 )
			System.out.println("killing "+len+" active thread"+((len>1)?"s":""));
		
		while( plotThreads.size() > 0 )
		{
			Thread t = (Thread) plotThreads.elementAt(0);

			try
			{
				t.interrupt();
				t.stop();
			}
			
			catch( Exception e )
			{
			}
			
			removeThread(t);
		}
	}
	
	public synchronized void removeThread( Thread t )
	{
		plotThreads.removeElement(t);
		
		//System.out.println("removeThread - now have "+plotThreads.size());

		if( plotThreads.size() == 0 )
		{
	        if( actionListener != null )
	        {
	        	//System.out.println("--> triggering listeners");
				ActionEvent e = new ActionEvent(this,0,REDRAW);
	            actionListener.actionPerformed(e);
	        }
		}
	}
	
	public synchronized void addThread( Thread t )
	{
		plotThreads.addElement(t);
		//System.out.println("addThread - now have "+plotThreads.size());
	}

	public Object[] getSelectedObjects()
	{
		return null;
	}
	
	public void setParameters( Applet apl )
	{
		grid = parseBoolean(apl,paramList[0][0],grid);
		force = parseBoolean(apl,paramList[1][0],force);
		field = parseBoolean(apl,paramList[2][0],field);
		potential = parseBoolean(apl,paramList[3][0],potential);
		fc2 = parseBoolean(apl,paramList[4][0],fc2);
		scalingFactor = parseDouble(apl,paramList[5][0],scalingFactor);
		limit = parseBoolean(apl,paramList[6][0],limit);
		highwater = parseInt(apl,paramList[7][0],highwater);
		color = parseBoolean(apl,paramList[8][0],color);
		testcharge = parseBoolean(apl,paramList[9][0],testcharge);
	}
	
	private String param( Applet apl, String name )
	{
		String s = apl.getParameter(name);
		if( s != null && s.length() == 0 )
			s = null;
		
		//System.out.println("Value of "+name+" is "+s);
		
		return s;
	}
	
	private boolean parseBoolean( Applet apl, String name, boolean old )
	{
		String val;
		
		val = param( apl, name );
		
		if( val == null )
			return old;
		else if( val.equalsIgnoreCase("on") )
			return true;
		else if( val.equalsIgnoreCase("off") )
			return false;
		else
			return old;
	}
	
	private String parseString( Applet apl, String name, String old )
	{
		String val = param( apl, name );
		
		return (val == null) ? old : val;
	}

	private int parseInt( Applet apl, String name, int old )
	{
		String val;
		int res = old;
		
		val = param( apl, name );
		
		if( val != null )
		{
			try
			{
				res = Integer.parseInt(val);
			}
			
			catch( Exception e )
			{
			}
		}

		return res;
	}	
	
	private double parseDouble( Applet apl, String name, double old )
	{
		String val;
		double res = old;
		
		val = param( apl, name );
		
		if( val != null )
		{
			try
			{
				Double d = new Double(val);
				res = d.doubleValue();
			}
			
			catch( Exception e )
			{
			}
		}

		return res;
	}	
	
    /**
     * Adds the specified action listener to receive action events from
     * this button. Action events occur when a user presses or releases
     * the mouse over this button.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param         l the action listener
     * @see           java.awt.event.ActionListener
     * @see           java.awt.Button#removeActionListener
     * @since         JDK1.1
     */
    public synchronized void addActionListener(ActionListener l)
    {
		if(l != null)
			actionListener = AWTEventMulticaster.add(actionListener, l);
    }

    /**
     * Removes the specified action listener so that it no longer
     * receives action events from this button. Action events occur
     * when a user presses or releases the mouse over this button.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param         	l     the action listener
     * @see           	java.awt.event.ActionListener
     * @see           	java.awt.Button#addActionListener
     * @since         	JDK1.1
     */
    public synchronized void removeActionListener(ActionListener l)
    {
    	if( l != null )
			actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

	public void setShowForce( boolean torf )
	{
		setProperty( FORCE, torf );
	}
	
	public boolean getShowForce()
	{
		return getProperty( FORCE );
	}

	public void setShowField( boolean torf )
	{
		setProperty( FIELD, torf );
	}
	
	public boolean getShowField()
	{
		return getProperty( FIELD );
	}

	public void setShowPotential( boolean torf )
	{
		setProperty( POTENTIAL, torf );
	}
	
	public boolean getShowPotential()
	{
		return getProperty( POTENTIAL );
	}
	
	public void setProperty( int prop, boolean torf )
	{
		//System.out.println("setProperty("+prop+","+torf+")");

//		for( int i=0; i<LEN; ++i )
//			props[i] = (i == prop) ? torf : false;
		props[prop] = torf;
				
        if( actionListener != null )
        {
			ActionEvent e = new ActionEvent(this,prop,SELECTION);
            actionListener.actionPerformed(e);
        }
	}
	
	public void trigger()
	{
        if( actionListener != null )
        {
			ActionEvent e = new ActionEvent(this,0,REDRAW);
            actionListener.actionPerformed(e);
        }
	}
	
	public boolean getProperty( int prop )
	{
		return props[prop];
	}
	
	public void selectCharge( Charge c )
	{
		//if( c != charge )
		{
			//System.out.println("Selected "+c);
			charge = c;
					
	        if( actionListener != null )
	        {
				ActionEvent e = new ActionEvent(this,0,CHARGE);
	            actionListener.actionPerformed(e);
	        }
		}
	}
}
