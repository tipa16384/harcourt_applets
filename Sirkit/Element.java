import java.awt.event.*;
import java.awt.*;
import java.util.Vector;

public class Element extends Panel 					//was GenericIcon
					 implements CircuitElement
{
	final static boolean debug = true;
		
	ActionListener listeners = null;
	Checkbox checkbox = null;
	static Circuit circuit = null;
	
	public Point	outpoint = new Point(0,0);			//point that graphically connects to next element
	public Point	inpoint = new Point(0,0);			//point that graphically connects to next element
	public Point	inpoint2 = new Point(0,0);			//point that graphically connects to next another element (Switcher)
	
	public Vector inputs = new Vector();				//list of the other elements I connect from
	public Vector outputs = new Vector();				//list of the other elements I connect to
	public Vector parallelIns = new Vector();			//list of the other elements I am in parallel operation input with
	public Vector parallelOuts = new Vector();			//list of the other elements I am in parallel operation output with
	public Vector parallelIn2s = new Vector();			//another list of the other elements I am in parallel operation input2 with
	
	double phiMult = 1.0;
	
	boolean visible;
	
	Panel elementPanel = null;
	
	public Element()
	{
		super();
		//super( (Image)null, null, "element" );
		//setIcon( Utility.getImage(this,getImageName()) );
	}


	public Element( LayoutManager lm )
	{
		super( lm );
	}
	
/*	public Panel getPanel()
	{
		Panel myPanel = null;
		
		return( myPanel );
	}
*/
	
	public void setVisible( boolean v )
	{
		visible = v;
	}

	public double R( double angle )
	{
		return visible ? reactance(angle) : 0.0;
	}
	
	public double reactance( double angle )
	{
		return 0.0;
	}

	public void retrace()
	{
		if( circuit != null )
		{
			ActionEvent e = new ActionEvent(this,0,"retrace");
			circuit.broadcast(e);
		}
		else
			debug("retrace called but circuit is null");
	}

	String getImageName()
	{
		return "";		//"power.gif";
	}

	static double toSeconds( double t0 )
	{
		return t0 / 1E3;
	}

	public double getAngle()
	{
		if( circuit == null )
			return 0.0;
		else
			return 1.0; //circuit.getAngle();
	}

	public double getValue( int sel, double t0 )
	{
		debug("getValue("+sel+", "+t0+")");
		return 0.0;
	}
	
	public double getTau()
	{
		return 10.0;
	}
	
	public double getPhase( long tau )
	{
		return 0.0;
	}

	public static void setCircuit( Circuit c )
	{
		//System.out.println("Element.circuit set to "+c);
		circuit = c;
	}
	
	public String getCheckboxName()
	{
		return "?";
	}

	public Checkbox getCheckbox()
	{
		if( checkbox == null )
		{
			checkbox = new Checkbox(getCheckboxName(),true);
			checkbox.addItemListener( new ItemListener()
				{
					public void itemStateChanged( ItemEvent e )
					{
						retrace();
					}
				} );
		}
		
		return checkbox;
	}

	public boolean showTrace( int sel )
	{
		switch( sel )
		{
			case CircuitElement.VOLTAGE:
				// this returns the EMF value
				return getCheckbox().getState();
		}
		
		return false;
	}

	public void setBounds( int x, int y, int w, int h )
	{
		Dimension size = getPreferredSize();
		
		super.setBounds( x+(w-size.width)/2,
						 y+(h-size.height)/2,
						 size.width, size.height );
		//System.out.println("Element::setBounds("+(x+(w-size.width)/2)+","
		//				 						+(y+(h-size.height)/2)+" "
		//				 						+size.width+"x"+size.height+") with size="+size);
	}

	// handle the action listener for detecting state changes.
	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}
	
	public String getMeterLabel()
	{
		return "";
	}
		
	public String getMeterUnits()
	{
		return "";
	}
		
	public Color getColor()
	{
		return GraphInfo.SEPARATE_COLOR;
	}
	
	static void debug( String s )
	{
		if( debug )
			System.out.println("Element::"+s);
	}
}
