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
		
	// parameters
	static public final String [][] paramList = {
		{ "cash",	"int",		"amount of starting cash" },
		{ "races",	"int",		"number of races" }
		};

	static final public boolean debug = false;
	
	public static boolean isMac;

	PropertyChangeSupport pcs;

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

	static public final String time_changed = "time";
	static public final String reset_applet = "reset";
	static public final String part_one = "part1";
	static public final String part_two = "part2";
	static public final String func_one = "func1";
	static public final String func_two = "func2";
	static public final String start = "start";
	static public final String stop = "stop";
	static public final String end = "end";
	static public final String wrong = "wrong";
	static public final String right = "right";

	public void addPropertyChangeListener( PropertyChangeListener l )
	{
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener( PropertyChangeListener l )
	{
		pcs.removePropertyChangeListener(l);
	}
	
	public void firePropertyChange( String name, double oOld, double oNew )
	{
		pcs.firePropertyChange( name, new Double(oOld), new Double(oNew) );
	}
	
	public void firePropertyChange( String name, Object oOld, Object oNew )
	{
		pcs.firePropertyChange( name, oOld, oNew );
	}
}
