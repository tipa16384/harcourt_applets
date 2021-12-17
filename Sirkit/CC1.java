import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.net.URL;
import java.applet.*;

public class Sirkit extends Panel implements ActionListener
{
	public static final String RESET = "reset";
	public static final String CIRCUIT = "circuit";
	public static final String RESISTOR = "resistor";
	public static final String METERS = "meters";
	public static final String PVALUE = "p-value";
	public static final String REDRAW = "redraw";
	
	GraphInfo info = null;
	ActionListener listeners = null;

	Circuit circuit = null;
	Resistor resistor = null;
	boolean showMeters = false;
	boolean showPValue = false;

	Choice circuitChoice = null;
	Choice resistorChoice = null;
	
	Main applet;

	final static boolean debug = true;
		
	// legend
	//   [...] connect components serially (horizontally)
	//   {...} connect components in parallel (vertically)
	//     B   battery
	//     R   resistor
	
	Circuit [] circuitList =
		{
			new Circuit( "Single Resistor", "{BR}" ),
			new Circuit( "Two Resistor Series", "{B[RR]}" ),
			new Circuit( "Two Resistors in Parallel", "{B{RR}}" ),
			new Circuit( "One Resistor Two in Parallel", "{B[{RR}R]}" )
		};
	
	Resistor [] resistorList =
		{
			new Resistor( "Resistor", "resistor.gif" ),
			new Resistor( "Light bulb", "litebulb.gif" )
		};
	
	// initializer -- start off with a BorderLayout.
	
	public Sirkit( Main applet, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.applet = applet;
		
		setBackground( Color.white );

		this.info = info;
		
		add( new Header(), BorderLayout.NORTH );
		Panel p = new Panel( new BorderLayout() );
		add( p, BorderLayout.CENTER );
		
		p.add( new ControlPanel(), BorderLayout.NORTH );		
		p.add( new CircuitPanel(), BorderLayout.CENTER );
		
		resetApplet();
	}
	
	public void actionPerformed( ActionEvent e )
	{
		String cmd = e.getActionCommand();
		
		if( cmd.equals(REDRAW) )
			broadcast( e );
	}
	
	// reset the applet
	
	public void resetApplet()
	{
		debug("Resetting the applet");

		setCircuit( null );
		setResistor( null );

		broadcast( new ActionEvent(this,0,RESET) );
	}

	public void setCircuit( Circuit circe )
	{
		if( circe == null )
			circe = circuitList[0];
		
		if( circuitChoice != null )
			circuitChoice.select( circe.getName() );
	}

	public void setResistor( Resistor isFutile )
	{
		if( isFutile == null )
			isFutile = resistorList[0];
		
		if( resistorChoice != null )
			resistorChoice.select( isFutile.getName() );
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
	
	public void broadcast( ActionEvent e )
	{
		if( listeners != null )
		{
			listeners.actionPerformed(e);
		}
	}
	
	// graph panel
	
	class CircuitPanel extends PaddedPanel
	{
		Component circuit;
		
		public CircuitPanel()
		{
			super( new BorderLayout(), 10, 10, 10, 0 );
		}


	}
	
	// header definition
	
	class Header extends PaddedPanel
	{
		public Header()
		{
			super( new BorderLayout(), 0, 0, 2, 0 );
			
			Panel p = new Panel();
			FakeButton b;
			
			b = new FakeButton("Reset");
			b.setBackground( Color.red );
			b.setForeground( Color.white );
			b.setSize(b.getPreferredSize());
			b.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						resetApplet();
					}
				} );
			
			p.add( b );
			add( p, BorderLayout.EAST );
			
			RawLabel l;
			
			l = new RawLabel( "Circuits 1" );
			l.setFont( new Font("SansSerif",Font.BOLD,14) );
			l.setSize( l.getPreferredSize() );
			add( l, BorderLayout.WEST );
		}
		
		public void paint( Graphics g )
		{
			super.paint( g );
			
			Dimension size = getSize();
			g.drawLine( 0, size.height-1, size.width, size.height-1 );
		}
	}

	// the two control panels
	
	class ControlPanel extends PaddedPanel
	{
		public ControlPanel()
		{
			super( new GridLayout(0,2), 10 );
			setBackground( info.CONTROL_COLOR );
			
			add( new LeftSide() );
			add( new RightSide() );
		}
		
		class RightSide extends Panel
		{
			public RightSide()
			{
				super( new GridLayout(0,1) );
				
				Panel p = new Panel( new GridLayout(0,1) );
				p.add( new Checkbox("Display Ammeter") );
				p.add( new RawLabel("   and Voltmeter") );
				add( p );
				
				p = new Panel( new GridLayout(0,1) );
				p.add( new Checkbox("Display P value") );
				p.add( new RawLabel("   (P = 123.4)") );
				add( p );
			}
		}
		
		class LeftSide extends Panel
		{
			public LeftSide()
			{
				super( new GridLayout(0,1) );
				
				int i;
				
				circuitChoice = new Choice();
				circuitChoice.addItemListener( new ItemListener()
					{
						public void itemStateChanged(ItemEvent e)
						{
							if( e.getStateChange() == ItemEvent.SELECTED )
							{
								setCircuit( circuitList[circuitChoice.getSelectedIndex()] );
							}
						}
					} );

				for( i=0; i<circuitList.length; ++i )
					circuitChoice.add( circuitList[i].getName() );
				
				resistorChoice = new Choice();
				resistorChoice.addItemListener( new ItemListener()
					{
						public void itemStateChanged(ItemEvent e)
						{
							if( e.getStateChange() == ItemEvent.SELECTED )
							{
								setResistor( resistorList[resistorChoice.getSelectedIndex()] );
							}
						}
					} );

				
				for( i=0; i<resistorList.length; ++i )
					resistorChoice.add( resistorList[i].getName() );
				
				Panel p = new Panel( new GridLayout(0,1) );
				p.add( new RawLabel("Choose circuit") );
				p.add( circuitChoice );
				add( p );
				
				p = new Panel( new GridLayout(0,1) );
				p.add( new RawLabel("Choose resistor") );
				p.add( resistorChoice );
				add( p );
			}
		}
	}

	static void debug( String s )
	{
		if( debug )
			System.out.println("Sirkit:: "+s);
	}
}
