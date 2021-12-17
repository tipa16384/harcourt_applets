import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.*;
import java.beans.*;

public final class GraphInfo 
{
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
	
	static public Font fontHeading;
	static public Font fontPlainSmall;
	static public Font fontPlain;
	static public Font fontBold;
	static public Font fontBigBold;
	static public Font fontBiggerBold;
	static public Font fontButton;
		
	// properties that can be changed
	static public final String new_function = "Function";
	static public final String new_scale = "Scale";
	static public final String new_slope = "Slope";
	static public final String repaint = "Repaint";
	static public final String select = "Selection";
	
	// parameters
	static public final String [][] paramList = {
		{ "cash",	"int",		"amount of starting cash" },
		{ "races",	"int",		"number of races" }
		};

	// property changer
	PropertyChangeSupport pcs;
		
	static final public boolean debug = false;
	
	public static boolean isMac;

	Function currentFunction = null;
	int scale = 1;
	int level = 1;
	
	// stuff for linearization
	boolean showUserSlope = false;
	double userSlope = 1.0;
	
	boolean showComputerSlope = false;
	double computerSlope = -1.0;
	
	// selected point
	double P = 0.0;
	
	public GraphInfo( boolean crippled )
	{
		pcs = new PropertyChangeSupport(this);
		
		isMac = crippled;
		
		if( crippled )
		{
			fontHeading = new Font("SansSerif",Font.BOLD,12);
			fontPlainSmall = new Font("SansSerif",Font.PLAIN,9);
			fontPlain = new Font("SansSerif",Font.PLAIN,9);
			fontBold = new Font("SansSerif",Font.BOLD,9);
			fontBigBold = new Font("SansSerif",Font.BOLD,10);
			fontBiggerBold = new Font("SansSerif",Font.BOLD,12);
			fontButton = new Font("SansSerif",Font.BOLD,14);
		}
		
		else
		{
			fontHeading = new Font("SansSerif",Font.BOLD,14);
			fontPlainSmall = new Font("SansSerif",Font.PLAIN,9);
			fontPlain = new Font("SansSerif",Font.PLAIN,10);
			fontBold = new Font("SansSerif",Font.BOLD,10);
			fontBigBold = new Font("SansSerif",Font.BOLD,12);
			fontBiggerBold = new Font("SansSerif",Font.BOLD,14);
			fontButton = new Font("SansSerif",Font.ITALIC+Font.BOLD,18);
		}
	}
	
	public double getP()
	{
		return P;
	}
	
	public double getPy()
	{
		return getCurrentFunction().value(P);
	}
	
	public void setP( double nP )
	{
		double oP = P;
		P = nP;
		pcs.firePropertyChange(select,new Double(oP),new Double(P));
	}

	public double getUserSlope()
	{
		return userSlope;
	}
	
	public void setUserSlope( double ns )
	{
		userSlope = ns;
		showUserSlope = true;
		pcs.firePropertyChange(new_slope,new Double(-1232.5),new Double(userSlope));
	}
	
	public double getComputerSlope()
	{
		return computerSlope;
	}

	public void setComputerSlope( double ns )
	{
		computerSlope = ns;
		showComputerSlope = true;
		pcs.firePropertyChange(repaint,new Double(-1232.5),new Double(computerSlope));
	}

	public boolean userSlopeVisible()
	{
		return showUserSlope;
	}

	public boolean computerSlopeVisible()
	{
		return showComputerSlope;
	}
	public void reset()
	{
		showUserSlope = showComputerSlope = false;
		pcs.firePropertyChange(repaint,new Double(-1232.5),new Double(0.0));
	}

	public int getLevel()
	{
		return level;
	}
	
	public void setLevel( int lev )
	{
		level = level;
	}

	public int getScale()
	{
		return scale;
	}

	public void setScale( int nscale )
	{
		int os = scale;
		scale = nscale;
		pcs.firePropertyChange(new_scale,new Integer(os),new Integer(scale));
	}

	public Function getCurrentFunction()
	{
		return currentFunction;
	}

	public void setCurrentFunction( Function f )
	{
		Function of = currentFunction;
		currentFunction = f;
		pcs.firePropertyChange(new_function,of,currentFunction);
	}

	public void setParameters( Applet apl )
	{
		System.out.println("setting parameters for the applet");
	}
	
	private String param( Applet apl, String name )
	{
		String s = apl.getParameter(name);
		if( s != null && s.length() == 0 )
			s = null;
		
		System.out.println("Value of "+name+" is "+s);
		
		return s;
	}
	
	private boolean parseBoolean( Applet apl, String name, boolean old )
	{
		String val;
		
		System.out.println("Looking for parameter "+name);
		
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
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     *
     * @param listener  The PropertyChangeListener to be added
     */

    public synchronized void addPropertyChangeListener(
				PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  The PropertyChangeListener to be removed
     */

    public synchronized void removePropertyChangeListener(
				PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }
}
