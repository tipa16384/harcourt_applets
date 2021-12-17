package Wired;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.net.URL;
import java.applet.*;

import util.RapidLabel;
import util.DoubleBufferPanel;

public class Wired extends Panel implements ActionListener
{
	public static final String RESET = "reset";
	public static final String RECALC = "recalc";
	public static final String CIRCUIT = "circuit";
	public static final String RESISTOR = "resistor";
	public static final String METERS = "meters";
	public static final String PVALUE = "p-value";
	public static final String REDRAW = "redraw";
	public static final String RETRACE = "retrace";
	public static final String POPUP = "popup";
	public static final String INSTRUCTIONS = "instr";
			
	GraphInfo info = null;
	ActionListener listeners = null;
	Phasor zap = null;

	Circuit circuit = null;

	Choice circuitChoice = null;

	Oscilloscope inductanceDisplay;
	Oscilloscope voltageDisplay;
	
	Main applet;

	// legend
	//   [...] connect components serially (horizontally)
	//   {...} connect components in parallel (vertically)
	//     B   battery (or other power source)
	//     R   resistor
	//	   L   inductance coil
	//	   C   capacitor
	//	   _   null element
		
	CircuitSpecifier [] circuitList =
		{
			new CircuitSpecifier( "R",   "[B_R_]" ),
			new CircuitSpecifier( "L",   "[BL__]" ),
			new CircuitSpecifier( "C",   "[B__C]" ),
			new CircuitSpecifier( "RL",  "[BLR_]" ),
			new CircuitSpecifier( "RC",  "[B_RC]" ),
			new CircuitSpecifier( "LC",  "[BL_C]" ),
			new CircuitSpecifier( "RLC", "[BLRC]" )
		};
	
	DoubleListElement [] multList =
		{
			new DoubleListElement("1/10x",0.1),
			new DoubleListElement("1/4",0.25),
			new DoubleListElement("1/2",0.5),
			new DoubleListElement("1x",1.0),
			new DoubleListElement("2x",2.0),
			new DoubleListElement("3x",3.0),
			new DoubleListElement("4x",4.0),
			new DoubleListElement("5x",5.0),
			new DoubleListElement("10x",10.0),
			new DoubleListElement("20x",20.0),
			new DoubleListElement("50x",50.0),
			new DoubleListElement("100x",100.0),
			new DoubleListElement("200x",200.0),
			new DoubleListElement("500x",500.0),
			new DoubleListElement("750x",750.0),
			new DoubleListElement("1000x",1000.0),
			new DoubleListElement("5000x",5000.0),
		};
	
	// initializer -- start off with a BorderLayout.
	
	public Wired( Main applet, GraphInfo info )
	{
		super( new BorderLayout() );

		setFont( new Font("SansSerif",Font.PLAIN,12) );
		
		DoubleFormat.standardNotation = false;
		DoubleFormat.threshhold = 3;
		
		Utility.setApplet(applet);
		this.applet = applet;
		
		//setBackground( Color.white );
		//setCursor( Cursor.getDefaultCursor() );

		this.info = info;

		inductanceDisplay = new Oscilloscope();
		inductanceDisplay.addActionListener(this);
		
		voltageDisplay = new Oscilloscope();
		voltageDisplay.addActionListener(this);
		voltageDisplay.setYAxisLabel("V");

		inductanceDisplay.setExtraAmplitude(100.0);
		inductanceDisplay.setAmplitude(5.0);
		voltageDisplay.setAmplitude(5.0);
		
		if( info.ca2 )
		{
			zap = new Phasor();
		}

		add( new Header(), BorderLayout.NORTH );
		Panel p = new Panel( new ColumnLayout() );
		add( p, BorderLayout.CENTER );

		Panel p2 = new Panel(null);
				
		p2.add( new CircuitPanel() );
		p2.add( new PopupControlPanel(Wired.this) );
		p2.add( new ControlPanel() );
		p.add( p2 );

		debug("preferred layout size is "+p.getLayout().preferredLayoutSize(p));
		
		resetApplet();
	}
	
	public void actionPerformed( ActionEvent e )
	{
		String cmd = e.getActionCommand();
		
		debug("Wired.actionPerformed got "+cmd);
		
		if( cmd.equals(REDRAW) || cmd.equals(POPUP) || cmd.equals(INSTRUCTIONS) )
			broadcast( e );
		else if( cmd.equals(RETRACE) )
		{
			//debug("retrace");
			inductanceDisplay.repaint();
			voltageDisplay.repaint();
			broadcast(e);
		}
	}
	
	// reset the applet
	
	public void resetApplet()
	{
		debug("Resetting the applet");

		setCircuit( null );

		broadcast( new ActionEvent(this,0,RESET) );
	}

	public void setCircuit( Circuit circe )
	{
		if( circuit != null )
			circuit.removeActionListener(this);
			
		if( circe == null )
			circe = new Circuit(circuitList[0]);
		
		if( circuitChoice != null )
			circuitChoice.select( circe.getName() );
		
		circuit = circe;
		
		if( circuit != null )
			circuit.addActionListener(this);
		
		broadcast( new ActionEvent(circe,0,CIRCUIT) );
		
		doLayout();
		repaint();
	}

	void retrace()
	{
		actionPerformed( new ActionEvent(this,0,RETRACE) );
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
	
	class CircuitPanel extends PaddedPanel implements ActionListener
	{
		Component circuit;
		Component icon = null;
		
		public CircuitPanel()
		{
			super( new BorderLayout(), 10 );
			setBackground( Color.white );
			Wired.this.addActionListener( CircuitPanel.this );
			
			Panel p, p1;
			
			p = new Panel( new BorderLayout() );
			p.add( new RawLabel("Choose a Circuit"), BorderLayout.NORTH );

			int i;
			
			circuitChoice = new Choice();
			circuitChoice.setForeground( applet.isCrippled() ? Color.black : GraphInfo.VARIABLE_COLOR );
			
			circuitChoice.addItemListener( new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						if( e.getStateChange() == ItemEvent.SELECTED )
						{
							setCircuit( new Circuit(circuitList[circuitChoice.getSelectedIndex()]) );
						}
					}
				} );

			int listlen = info.ca2 ? circuitList.length : 3;
			
			for( i=0; i<listlen; ++i )
				circuitChoice.add( circuitList[i].getName() );

			p.add( circuitChoice, BorderLayout.SOUTH );
			
			p1 = new PaddedPanel( new BorderLayout(), 0, 0, 10, 0 );
			p1.add( p, BorderLayout.WEST );
			add( p1, BorderLayout.NORTH );
		}

		public void actionPerformed( ActionEvent e )
		{
			//System.out.println("CircuitPanel received "+e);
			
			String cmd = e.getActionCommand();
			
			if( cmd.equals(CIRCUIT) )
			{
				if( icon != null )
					remove( icon );
				
				icon = (Component) e.getSource();
				
				add( icon, BorderLayout.CENTER );
				validate();
			}
			
			else if( cmd.equals(RETRACE) )
			{
				debug("retrace");
				inductanceDisplay.repaint();
				voltageDisplay.repaint();
			}
		}
	}
	
	// header definition
	
	class Header extends PaddedPanel
	{
		public Header()
		{
			super( new BorderLayout(), 0, 0, 2, 0 );
			
			setBackground( Color.white );
			
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
			String s = info.ca2 ? "AC Circuits and Phasors" : "AC Circuits";
			if( Utility.debug )
				s += " ["+info.dateInfo+"]";
			
			l = new RawLabel( s );
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
			super( new BorderLayout(), 10, 10, 10, 10 );
			
			Panel p = new PaddedPanel( new BorderLayout(), 0, 0, 0, 10 );
			p.add( new Popups(), BorderLayout.SOUTH );
			p.add( new GraphPanel(), BorderLayout.CENTER );
			add( p, BorderLayout.CENTER );
			add( new Instructions(), BorderLayout.EAST );
		}
	}

	class GraphPanel extends DoubleBufferPanel
					 implements ActionListener
	{
		public GraphPanel()
		{
			super( new ColumnLayout(0,5) );
			add( inductanceDisplay );
			add( voltageDisplay );
			setBackground( Color.white );
			Wired.this.addActionListener( GraphPanel.this );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			String cmd = e.getActionCommand();
			
			if( cmd.equals(CIRCUIT) )
			{
				voltageDisplay.removeAllTraces();
				inductanceDisplay.removeAllTraces();
				
				Circuit c = (Circuit)e.getSource();
				
				inductanceDisplay.addTrace(c);
				
				Vector v = c.getElements();
				int len = v.size();
				int i;
				
				for( i=0; i<len; ++i )
				{
					voltageDisplay.addTrace( (CircuitElement)v.elementAt(i) );
				}

				if( zap != null )
				{
					zap.removeAllTraces();
					zap.add( inductanceDisplay, c );
					for( i=0; i<len; ++i )
					{
						zap.add( voltageDisplay, (CircuitElement)v.elementAt(i) );
					}
				}
				
				retrace();
			}
		}
	}
	
	class Popups extends Panel
	{
		public Popups()
		{
			super( new GridLayout(0,1) );
			
			Choice choice;
			
			choice = jamChoice(multList,inductanceDisplay.getAmplitude());
			choice.addItemListener( new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						debug(e.toString());
						if( e.getStateChange() == ItemEvent.SELECTED )
						{
							int index = ((Choice)e.getSource()).getSelectedIndex();
							double val = multList[index].getValue();
							inductanceDisplay.setAmplitude( val );
						}
					}	
				} );
			add( new PoopUp(choice,null,"Zoom ~!I~!",null) );
			
			choice = jamChoice(multList,voltageDisplay.getAmplitude());
			choice.addItemListener( new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						debug(e.toString());
						if( e.getStateChange() == ItemEvent.SELECTED )
						{
							int index = ((Choice)e.getSource()).getSelectedIndex();
							double val = multList[index].getValue();
							voltageDisplay.setAmplitude( val );
						}
					}	
				} );
			add( new PoopUp(choice,null,"Zoom ~!V~!",null) );

			choice = jamChoice(multList,inductanceDisplay.getFrequency());
			choice.addItemListener( new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						debug(e.toString());
						if( e.getStateChange() == ItemEvent.SELECTED )
						{
							int index = ((Choice)e.getSource()).getSelectedIndex();
							double val = multList[index].getValue();
							voltageDisplay.setFrequency( val );
							inductanceDisplay.setFrequency( val );
						}
					}	
				} );
			add( new PoopUp(choice,null,"Zoom ~!t~!",null) );
		}		
	}

	private Choice jamChoice( DoubleListElement [] list, double value )
	{
		Choice choice;
		int selected = -1;
		
		debug("jamChoice - value is "+value);
		
		choice = new Choice();
		for( int i=0; i<list.length; ++i )
		{
			choice.add( list[i].getName() );
			if( list[i].getValue() == value )
			{
				debug("Select "+i);
				selected = i;
			}
		}
		
		if( selected >= 0 )
		{
			debug("Setting selection to "+selected);
			choice.select( selected );
		}
		
		return choice;
	}

	class BorderPanel extends DoubleBufferPanel
	{
		Insets insets;
		
		public BorderPanel( LayoutManager layout, int pad )
		{
			super(layout);
			this.insets = new Insets(pad,pad,pad,pad);
		}
				
		public Insets getInsets()
		{
			return insets;
		}
	
		public void paint( Graphics g )
		{
			super.paint(g);

			Insets insets = getInsets();
			int border = insets.left-4;
						
			Dimension size = getSize();
			g.setColor( getForeground() );
			
			for( int i=0; i<border; ++i )
			{
				g.drawRect( i, i, size.width-2*i-1, size.height-2*i-1 );
			}
		}
	}

	class Popup extends Panel
				implements ActionListener
	{
		Vector meters = new Vector();
		Panel meterPanel;
				
		public Popup()
		{
			super( new ColumnLayout(0,5) );

			CircuitElement clown;
			
			TrackClicks tc = new TrackClicks();
			
			addContainerListener( tc );
			addMouseListener( tc );

			Wired.this.addActionListener( Popup.this );

			Component c;			
			Font f = new Font( "Serif", Font.PLAIN, applet.isCrippled()?10:11 );
			setFont(f);
			setForeground( Color.black );
			setBackground( null );
			
			if( info.ca2 )
			{
				add( zap );
			}
	
			meterPanel = new BorderPanel( new ColumnLayout(), 5 );

			clown = new TimeElement();
			c = new Meter(voltageDisplay,clown);
			c.setFont( f );
			meterPanel.add( c );
			
			clown = new InductanceElement();
			c = new Meter(inductanceDisplay,clown);
			c.setFont( f );
			meterPanel.add( c );

			add( meterPanel );

			meters.addElement( new Meter(voltageDisplay,Circuit.power) );
			meters.addElement( new Meter(voltageDisplay,Circuit.resistor) );
			meters.addElement( new Meter(voltageDisplay,Circuit.coil) );
			meters.addElement( new Meter(voltageDisplay,Circuit.capacitor) );
		}

		public void actionPerformed( ActionEvent e )
		{
			swirl( Popup.this, e );
		}
		
		void swirl( Container container, ActionEvent e )
		{
			Component [] comps = container.getComponents();
			int len = comps.length;
			int i;
			
			try
			{
				for( i=0; i<len; ++i )
				{
					Component c = comps[i];
					if( c != null )
					{
						if( c instanceof ActionListener )
						{
							((ActionListener)c).actionPerformed(e);
						}
						
						if( c instanceof Container )
						{
							swirl( (Container)c, e );
						}
					}
				}
			}
			
			catch( ArrayIndexOutOfBoundsException aioobe )
			{
			}
		}

		class TrackClicks extends MouseAdapter
						  implements ContainerListener
		{
			public void componentAdded(ContainerEvent e)
			{
				//debug("added "+e.getChild()+" to "+e.getContainer());

				Component c = e.getChild();
				
				if( c instanceof Container )
				{
					((Container)c).addContainerListener(this);
				}
				
				c.addMouseListener(this);
			}
			
			public void componentRemoved(ContainerEvent e)
			{
				//debug("removed "+e.getChild()+" from "+e.getContainer());

				Component c = e.getChild();
				
				if( c instanceof Container )
				{
					((Container)c).removeContainerListener(this);
				}
				
				c.removeMouseListener(this);
			}
			
			public void mouseClicked( MouseEvent e )
			{
				//debug("click on "+e.getSource());
				Wired.this.broadcast( new ActionEvent(this,0,INSTRUCTIONS) );
			}
		}

		public void repop()
		{
			Font f = getFont();
			int len;
			int i;
			
			len = meters.size();
			for( i=0; i<len; ++i )
			{
				Meter m = (Meter)meters.elementAt(i);
				CircuitElement target = m.getTarget();
				boolean visible = (target instanceof Element) ? ((Element)target).isVisible() : true;
				
				meterPanel.remove(m);
				
				if( meterPanel.isAncestorOf(m) )
				{
					if( !visible )
						meterPanel.remove(m);
				}
				
				else if( visible )
				{
					m.setFont(f);
					meterPanel.add( m );
				}
			}

			invalidate();
			validate();
//			meterPanel.validate();
//			meterPanel.repaint();
		}
	}
	
	class InductanceElement implements CircuitElement
	{
		public boolean showTrace()
		{
			return (circuit==null) ? false : circuit.showTrace();
		}
	
		public Color getColor()
		{
			return (circuit==null) ? Color.black : circuit.getColor();
		}
		
		public String getMeterLabel()
		{
			return (circuit==null) ? "?" : circuit.getMeterLabel();
		}
		
		public String getMeterUnits()
		{
			return (circuit==null) ? "!" : circuit.getMeterUnits();
		}
		
		public double getValue( long t0 )
		{
			return (circuit==null) ? 0.0 : circuit.getValue(t0);
		}
		
		public double getPhase( long t0 )
		{
			return (circuit==null) ? 0.0 : circuit.getPhase(t0);
		}
	}
	
	class TimeElement implements CircuitElement
	{
		public boolean showTrace()
		{
			return true;
		}
	
		public Color getColor()
		{
			return Color.black;
		}
		
		public String getMeterLabel()
		{
			return "~!t~!";
		}
		
		public String getMeterUnits()
		{
			return "ms";
		}
		
		public double getPhase( long t0 )
		{
			return 0;
		}
		
		public double getValue( long t0 )
		{
			return Element.toSeconds(t0) * 1000.0;
		}
	}
	
	class Instructions extends Panel
					   implements ActionListener
	{
		Component text;
		Popup popup;
		boolean popupActive = false;
				
		public Instructions()
		{
			super(null);
			
			text = new RapidLabel(
				info.ca2 ? "Click on the graph to view phasors and readout information."
						 : "Click on the graph to view readout information.",
				Label.LEFT, 100);
			text.setFont( new Font("SansSerif", Font.PLAIN, 11) );
			text.setForeground( GraphInfo.SEPARATE_COLOR );
			
			popup = new Popup();
			
			add( text );
			add( popup );

			adjust();

			Wired.this.addActionListener( Instructions.this );
		}
	
		public void actionPerformed( ActionEvent e )
		{
			String cmd = e.getActionCommand();
			
			if( cmd.equals(POPUP) )
			{
				debug("showing popup");
				showPopup();
			}
			
			if( cmd.equals(INSTRUCTIONS) )
			{
				debug("showing instructions");
				showInstructions();
			}

			if( cmd.equals(CIRCUIT) )
			{
				debug("changing circuit");
				popup.repop();
			}
		}
					
		void adjust()
		{
			popup.setVisible(popupActive);
			
			if( !popupActive )
			{
				inductanceDisplay.setSliderPosition(-1);
			}
			
			text.setVisible(!popupActive);
		}

		public void showPopup()
		{
			if( !popupActive )
			{
				popupActive = true;
				adjust();
				repaint();
			}
		}
		
		public void showInstructions()
		{
			if( popupActive )
			{
				popupActive = false;
				adjust();
				repaint();
			}
		}
		
		public void doLayout()
		{
			debug("Instructions.doLayout()");
			
			Dimension size = getSize();
			text.setBounds(0,0,size.width,size.height);
			popup.setBounds(0,0,size.width,size.height);
		}
		
		public Dimension getMinimumSize()
		{
			return new Dimension(100,100);
		}

		public Dimension getPreferredSize()
		{
			return getMinimumSize();
		}
	}

	static void debug( String s )
	{
		if( Utility.debug )
			System.out.println("Wired:: "+s);
	}
}
