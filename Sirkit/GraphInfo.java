import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.Vector;

public final class GraphInfo 
{
	// applet mode for FC3
	static public final int FORCE = 0;
	static public final int FIELD = 1;
	static public final int POTENTIAL = 2;
	static public final int COLOR = 3;
	static private final int LEN = COLOR+1;
	
	// size of the charge circle, in pixels
	static public final int DOTSIZE = 16;
	
	// actual size of the charge component, in pixels
	static public final int CANVASSIZE = 1024;
	
	// color for the force vectors
	static public final Color FORCE_VECTOR_COLOR = new Color(0,51,204);
	
	// color for the negative charge
	static public final Color NEGATIVE_COLOR = new Color(51,102,255);
	
	// color for the positive charge
	static public final Color POSITIVE_COLOR = new Color(255,51,51);
	
	// color for variables
	static public final Color VARIABLE_COLOR = new Color(153,51,0);
	
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
	
	static public final String [][] paramList = {
		{ "cc2",	"boolean",		"this is CC2 (not CC1)" }
		};
		
    public boolean cc2 = false;
    
	static final public String REDRAW = "REDRAW";
	static final public String SELECTION = "SELECTION";
	static final public String CHARGE = "CHARGE";

	// Coulomb's constant
	public static final double Ke = 8.9875E9;
	
	// charge on an electron, in Coulombs.
	public static final double C = -1.60217733E-19;
	
	// space permittivity constant (epsilon-0)
	public static final double e0 = 8.854187817E-12;
	
	public GraphInfo()
	{
	}

	public void setParameters( Applet apl )
	{
		System.out.println("setting parameters for the applet");

		cc2 = parseBoolean(apl,paramList[0][0],cc2);
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
}
