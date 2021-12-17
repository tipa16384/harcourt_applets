import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.net.URL;
import java.applet.*;
import util.*;

public class CC2 extends Panel implements ActionListener
{
	final static boolean debug = true;
		
	public static final String RESET = "reset";
	public static final String CIRCUIT = "circuit";
	public static final String RESISTOR = "resistor";
	public static final String METERS = "meters";
	public static final String PVALUE = "p-value";
	public static final String REDRAW = "redraw";
	public static final String RETRACE = "retrace";
	public static final String RECALC = "recalc";
	public static final String POPUP = "popup";
	public static final String INSTRUCTIONS = "instr";
	public static final String SWITCHON = "switchon";		//switch is toggling to charge now
	public static final String SWITCHOFF = "switchoff";		//switch is toggling to discharge now
	
	GraphInfo info = null;
	ActionListener listeners = null;

	public Circuit circuit = null;
	Resistor resistor = null;
	boolean showMeters = true;
	boolean showPValue = false;

	Choice circuitChoice = null;
	Choice resistorChoice = null;

	//public SophieChoice ohmChoice;
				//the Osciloscope have amplitude(Y) and time(X) popup with the following values
	int		defOsciAmpSelect = 0;			//starting value
	static final String [] osciAmpList =
		{
			"1x  ",
			"2x  ",
			"4x  ",
			"8x  ",
			"16x ",
			"32x "
		};
	
	int		defOsciTimeSelect = 5;			//starting value
	static final String [] osciTimeList =
		{
			"1x  ",
			"2x  ",
			"4x  ",
			"8x  ",
			"16x ",
			"32x ",
			"64x ",
			"128x"
		};
	
	
	Main applet;

	// legend
	//   [...] connect components serially (horizontally)
	//   {...} connect components in parallel (vertically)
	//     B   battery
	//     R   resistor
	
	Circuit [] circuitList =
		{
			new Circuit( "Charge/Discharge Capacitor", "{R[CS][BR]}" ),
		};
	
	Resistor [] resistorList =
		{
			new Resistor( "Resistor", "resistor.gif" ),
			new Resistor( "Light bulb", "litebulb.gif" )
		};
	
	// initializer -- start off with a BorderLayout.
	
	public CC2( Main applet, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.applet = applet;
		
		setBackground( Color.white );

		this.info = info;
		
		add( new Header(), BorderLayout.NORTH );
		
		Panel p = new Panel( new GridLayout(0,2, 10, 10) );
		
		GraphPanel gpanel;
		CircuitPanel cpanel;
		
		cpanel = new CircuitPanel();
		addActionListener( cpanel );
		p.add( cpanel );
		
		Panel cp = new Panel( new BorderLayout(10, 10) );
		gpanel = new GraphPanel();
		cp.add( gpanel, BorderLayout.CENTER );
		addActionListener( gpanel );
		
		Instructions instructs = new Instructions();
		cp.add( instructs, BorderLayout.EAST );		
		addActionListener( instructs );
		p.add( cp );
		
		add( p, BorderLayout.CENTER );
		resetApplet();
	}
	
	public void actionPerformed( ActionEvent e )
	{
		//debug("actionPerformed "+e);
		
		String cmd = e.getActionCommand();
		
		if( cmd.equals(REDRAW)
		 || cmd.equals(RETRACE)
		 || cmd.equals(RESET)
		 || cmd.equals(INSTRUCTIONS)
		 || cmd.equals(POPUP))
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
		//debug("setCircuit set to "+circe);

		if( circuit != null )
			circuit.removeActionListener(this);
			
		if( circe == null )
			circe = circuitList[0];
		
		if( circuitChoice != null )
			circuitChoice.select( circe.getName() );

		circuit = circe;
		
		if( circuit != null )
			circuit.addActionListener(this);
		
		broadcast( new ActionEvent(circe,0,CIRCUIT) );
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
		debug("Adding listener "+l);
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
			//System.out.println("CC2::broadcasting "+e);
			listeners.actionPerformed(e);
		}
	}
	
	public double getTau()
	{
		return (circuit==null) ? 10.0 : circuit.getTau();
	}
	
	// Circuit panel
	
	class CircuitPanel extends PaddedPanel implements ActionListener
	{
		Circuit circuit;
		Component icon = null;
		
		public CircuitPanel()
		{
			super( new BorderLayout(), 10, 10, 10, 10 );
			setBackground( info.CONTROL_COLOR );
			//CC2.this.addActionListener( CircuitPanel.this );

			//circuit = new Circuit( "Charge/Discharge Capacitor", "{R[CS][BR]}" );
			//setCircuit( circuit );
			setCircuit( null );
			circuit = circuitList[0];
			circuit.addActionListener( CircuitPanel.this );
			add( circuit, BorderLayout.CENTER );
			validate();
		}

		public void actionPerformed( ActionEvent e )
		{
			//debug("CircuitPanel received "+e);
			
			String cmd = e.getActionCommand();
			
			if( cmd.equals(CIRCUIT) )
			{
				if( icon != null )
					remove( icon );
				
				icon = (Component) e.getSource();
				
				add( icon, BorderLayout.CENTER );
				validate();
			}
			else if( cmd.equals(REDRAW) )
				broadcast( e );
			else if( cmd.equals(RESET) )
			{
				//debug("actionPerformed got a RESET");
				if( circuit != null )
					circuit.actionPerformed(e);
			}
			//else if( cmd.equals(RETRACE) )	causes endless loop
			//	broadcast( e );
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
			
			l = new RawLabel( "Circuits 2" );
			l.setFont( new Font("Sansserif",Font.BOLD,14) );
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

	// the two Graph panels
	
	Oscilloscope currDisplay;
	Oscilloscope voltDisplay;
	SophieChoice IMChoice;				//and their little range setters
	SophieChoice IAChoice;
	SophieChoice VMChoice;
	SophieChoice VAChoice;
	
	class GraphPanel extends DoubleBufferPanel implements ActionListener
	{
		public GraphPanel()
		{
			super( new GridLayout(0,1,20,20) );	//, 5 );
			
			currDisplay = new Oscilloscope();
			currDisplay.addActionListener( CC2.this );
			currDisplay.setYAxisLabel("I");
			currDisplay.setDispTypeSel(CircuitElement.CURRENT);
			add( currDisplay );

			voltDisplay = new Oscilloscope();
			voltDisplay.addActionListener( CC2.this );
			voltDisplay.setYAxisLabel("V");
			voltDisplay.setDispTypeSel(CircuitElement.VOLTAGE);
			add( voltDisplay );
			
			//validate();
			//Dimension size = getSize();
			//currDisplay.setBounds( 0, 20, size.width, size.height - 20 );
			//invalidate();
			//validate();
		}
		
		public void actionPerformed( ActionEvent e )
		{
			String cmd = e.getActionCommand();
			
			//debug("actionPerformed "+e);
			if( cmd.equals(CIRCUIT) )
			{
				Circuit c = (Circuit)e.getSource();
				
						//make this trace only repaint (leave the other trace on)
				voltDisplay.removeTrace((CircuitElement)c);
				currDisplay.removeTrace((CircuitElement)c);
				
				currDisplay.addTrace((CircuitElement)c);
				voltDisplay.addTrace((CircuitElement)c);
				
						//no need to add all these traces
				//Vector v = c.getElements();
				//int len = v.size();
				//int i;
				
				//for( i=0; i<len; ++i )
				//{
				//	voltDisplay.addTrace( (CircuitElement)v.elementAt(i) );
				//}

						//I don't know what zap is/does
				//if( zap != null )
				//{
				//	zap.removeAllTraces();
				//	zap.add( inductanceDisplay, c );
				//	for( i=0; i<len; ++i )
				//	{
				//		zap.add( voltageDisplay, (CircuitElement)v.elementAt(i) );
				//	}
				//}
				
				currDisplay.retrace();
				voltDisplay.retrace();
			}
			else if( cmd.equals(RETRACE) )
			{
				//debug("GraphPanel action about to call currDisplay.retrace()");
				currDisplay.retrace();
				//debug("GraphPanel action about to call voltDisplay.retrace()");
				voltDisplay.retrace();
			}
		}
	}

	
	class Instructions extends Panel
					   implements ActionListener
	{
		Component text1, text2, text3;
		Popup popup;
		boolean popupActive = false;
				
		public Instructions()
		{
			super(null);
			setLayout(new ColumnLayout());
			
			double d;
			
			text1 = new RapidLabel("Click on the graph",Label.LEFT, 18);
			text1.setFont( new Font("Serif", Font.PLAIN, 11) );
			text1.setForeground( GraphInfo.SEPARATE_COLOR );
			add( text1 );

			text2 = new RapidLabel("to obtain the values",Label.LEFT, 20);
			text2.setFont( new Font("Serif", Font.PLAIN, 11) );
			text2.setForeground( GraphInfo.SEPARATE_COLOR );
			add( text2 );

			text3 = new RapidLabel("for t, I, V and \u03C4.",Label.LEFT, 17);
			text3.setFont( new Font("Serif", Font.PLAIN, 11) );
			text3.setForeground( GraphInfo.SEPARATE_COLOR );
			add( text3 );
			
			popup = new Popup();
			
			add( popup );

			adjust();

			CC2.this.addActionListener( Instructions.this );

							//add a Current Amplitude choicer
			IMChoice = new SophieChoice("Zoom ~!I~!");
	
			for( int i=0; i<osciAmpList.length; ++i )
				IMChoice.add( osciAmpList[i] );
			IMChoice.select( defOsciAmpSelect );
			currDisplay.maxAmplitude = 0.025;					//set max Current expected.
			//currDisplay.setExtraAmplitude(200.0);					//boost the little current up.
			d = Utility.parseDouble(osciAmpList[IMChoice.getSelectedIndex()] );
			//currDisplay.setAmplitude( currDisplay.maxAmplitude / d );
			currDisplay.setAmplitude( d );
			
			IMChoice.addItemListener( new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						if( e.getStateChange() == ItemEvent.SELECTED )
						{
							double dd = Utility.parseDouble(osciAmpList[IMChoice.getSelectedIndex()] );
							//currDisplay.setAmplitude( currDisplay.maxAmplitude / dd );
							currDisplay.setAmplitude( dd );
							currDisplay.retrace();
						}
					}
				} );
	
			add( IMChoice );
			IMChoice.setShowLabel(true);
			IMChoice.setLabelOnTop(true);

							//add the voltage magnitude choicer
			VMChoice = new SophieChoice("Zoom ~!V~!");
	
			for( int i=0; i<(osciAmpList.length -2); ++i )
				VMChoice.add( osciAmpList[i] );
			VMChoice.select( defOsciAmpSelect );
			voltDisplay.maxAmplitude = 12.0;					//set max voltage expected.
			//voltDisplay.setExtraAmplitude(200.0);					//boost the little current up.
			d = Utility.parseDouble(osciAmpList[VMChoice.getSelectedIndex()] );
			//voltDisplay.setAmplitude( voltDisplay.maxAmplitude / d );
			voltDisplay.setAmplitude( d );
			
			VMChoice.addItemListener( new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						if( e.getStateChange() == ItemEvent.SELECTED )
						{
							double dd = Utility.parseDouble(osciAmpList[VMChoice.getSelectedIndex()] );
							//voltDisplay.setAmplitude( voltDisplay.maxAmplitude / dd );
							voltDisplay.setAmplitude( dd );
							voltDisplay.retrace();
						}
					}
				} );
	
			add( VMChoice );
			VMChoice.setShowLabel(true);
			VMChoice.setLabelOnTop(true);

							//add a frequency/time choicer for both oscilloscopes
			IAChoice = new SophieChoice("Zoom ~!t~!");
	
			for( int i=0; i<osciTimeList.length; ++i )
				IAChoice.add( osciTimeList[i] );
			IAChoice.select( defOsciTimeSelect );
			d = Utility.parseDouble(osciTimeList[IAChoice.getSelectedIndex()] );
			currDisplay.setFrequency( 2.0 * d );
			voltDisplay.setFrequency( 2.0 * d );
			
			IAChoice.addItemListener( new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						if( e.getStateChange() == ItemEvent.SELECTED )
						{
							double oldfreq = currDisplay.getFrequency();
							double dd = Utility.parseDouble(osciTimeList[IAChoice.getSelectedIndex()] );
							currDisplay.setFrequency( 2.0 * dd );
							voltDisplay.setFrequency( 2.0 * dd );
							int oldslid = currDisplay.getSliderPosition();
							if( oldslid > 0 )
							{
								currDisplay.setSliderPosition( (int)(oldslid * ((2.0 * dd) / oldfreq)) );
								voltDisplay.setSliderPosition( (int)(oldslid * ((2.0 * dd) / oldfreq)) );
								debug("setSliderPosition from "+oldslid+" to "+(int)(oldslid * ((2.0 * dd) / oldfreq)) );
							}
							//currDisplay.retrace();
							//voltDisplay.retrace();
							broadcast( new ActionEvent(this,0,CC2.RETRACE) );
						}
					}
				} );
	
			add( IAChoice );
			IAChoice.setShowLabel(true);
			IAChoice.setLabelOnTop(true);
			currDisplay.retrace();
			voltDisplay.retrace();

		}
	
		public void actionPerformed( ActionEvent e )
		{
			String cmd = e.getActionCommand();
			
			//System.out.println("Hey! "+this.getClass().getName()+" got "+e);
			
			if( cmd.equals(POPUP) )
			{
				//debug("Instructions showing popup");
				showPopup();
				popup.repop();
			}
			else if( cmd.equals(INSTRUCTIONS) )
			{
				//debug("Instructions showing instructions");
				showInstructions();
			}
			else if( cmd.equals(CIRCUIT) )
			{
				//debug("Instructions changing circuit");
				popup.repop();
			}
			//else
				//debug("Instructions got other event "+e);
		}
					
		void adjust()
		{
			popup.setVisible(popupActive);
			
			if( !popupActive )
			{
				currDisplay.setSliderPosition(-1);
				voltDisplay.setSliderPosition(-1);
			}
			
			text1.setVisible(!popupActive);
			text2.setVisible(!popupActive);
			text3.setVisible(!popupActive);
		}

		public void showPopup()
		{
			if( !popupActive )
			{
				popupActive = true;
				adjust();
				repaint();
			}
			//debug("showPopup called and popupActive now=="+popupActive);
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
			text1.setBounds(0,0,size.width,size.height);
			text2.setBounds(0,16,size.width,size.height);
			text3.setBounds(0,32,size.width,size.height);

			int half = size.height / 2;
			popup.setBounds(0,0,size.width,half-28);
			
			IMChoice.setBounds(0,half-20,size.width,32);
			VMChoice.setBounds(0,half+30,size.width,32);
			IAChoice.setBounds(0,size.height-60,size.width,32);
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

	class TimeElement implements CircuitElement
	{
		public boolean showTrace( int sel )
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
			return "s";
		}
		
		public double getTau()
		{
			return (circuit==null) ? 10.0 : circuit.getTau();
		}
	
		public double getPhase( long t0 )
		{
			return 0;
		}
		
		public double getValue( int selector, double t0 )
		{
			//debug("TimeElement::getValue("+selector+", "+t0+")");
			return Element.toSeconds(t0) * 1000.0;
			//return (circuit==null) ? Element.toSeconds(t0) * 1000.0 : circuit.getValue( selector, t0 );
		}
	}
	
	class CurrentElement implements CircuitElement
	{
		public boolean showTrace( int sel)
		{
			return (circuit==null) ? false : circuit.showTrace( sel );
		}
	
		public Color getColor()
		{
			return (circuit==null) ? Color.black : circuit.getColor();
		}
		
		public String getMeterLabel()
		{
			return (circuit==null) ? "?" : "~!I~!";
		}
		
		public String getMeterUnits()
		{
			return (circuit==null) ? "!" : "A";
		}
		
		public double getValue( int selector, double t0 )
		{
			//debug("CurrentElement::getValue("+selector+", "+t0+")");
			return (circuit==null) ? 0.0 : circuit.getValue( selector, t0 );
		}
		
		public double getTau()
		{
			return (circuit==null) ? 10.0 : circuit.getTau();
		}
	
		public double getPhase( long t0 )
		{
			return (circuit==null) ? 0.0 : circuit.getPhase(t0);
		}
	}
	
	class VoltageElement implements CircuitElement
	{
		public boolean showTrace( int sel)
		{
			return (circuit==null) ? false : circuit.showTrace( sel );
		}
	
		public Color getColor()
		{
			return (circuit==null) ? Color.black : circuit.getColor();
		}
		
		public String getMeterLabel()
		{
			return (circuit==null) ? "?" : "~!V~!";
		}
		
		public String getMeterUnits()
		{
			return (circuit==null) ? "!" : "v";
		}
		
		public double getValue( int selector, double t0 )
		{
			//debug("VoltageElement::getValue("+selector+", "+t0+")");
			return (circuit==null) ? 0.0 : circuit.getValue( selector, t0 );
		}
		
		public double getTau()
		{
			return (circuit==null) ? 10.0 : circuit.getTau();
		}
	
		public double getPhase( long t0 )
		{
			return (circuit==null) ? 0.0 : circuit.getPhase(t0);
		}
	}
	
	class TauElement implements CircuitElement
	{
		public boolean showTrace( int sel)
		{
			return (circuit==null) ? false : circuit.showTrace( sel );
		}
	
		public Color getColor()
		{
			return (circuit==null) ? Color.black : circuit.getColor();
		}
		
		public String getMeterLabel()
		{
			String subChar = "d";
			if( ((Switcher)CC2.this.circuit.S1).on_off == Switcher.CHARGE )
				subChar = "c";
			return (circuit==null) ? "?" : "~!~t~v"+subChar+"~0";
		}
		
		public String getMeterUnits()
		{
			return (circuit==null) ? "!" : "s";	//???what units tau
		}
		
		public double getValue( int selector, double t0 )
		{
			//debug("TauElement::getValue("+selector+", "+t0+")");
			return (circuit==null) ? 0.0 : circuit.getTau();
		}
		
		public double getTau()
		{
			return (circuit==null) ? 10.0 : circuit.getTau();
		}
	
		public double getPhase( long t0 )
		{
			return (circuit==null) ? 0.0 : circuit.getPhase(t0);
		}
	}
	
	class Popup extends Panel
				implements ActionListener
	{
		Vector meters = new Vector();
		Panel meterPanel;
		RapidLabel text0;
				
		public Popup()
		{
			super( new ColumnLayout(0,5) );

			CircuitElement clown;
			
			TrackClicks tc = new TrackClicks();
			
			addContainerListener( tc );
			addMouseListener( tc );

			CC2.this.addActionListener( Popup.this );
			//CC2.this.circuit.addActionListener( Popup.this );

			Component c;			
			Font f = new Font( "Serif", Font.PLAIN, applet.isCrippled()?10:11 );
			setFont(f);
			setForeground( Color.black );
			setBackground( null );
			
			//if( info.ca2 )
			//{
			//	add( zap );
			//}
	
			meterPanel = new BorderPanel( new ColumnLayout(), 5 );

			text0 = new RapidLabel("Discharging Cycle",Label.LEFT, 18);
			text0.setFont( new Font("Serif", Font.PLAIN, 11) );
			text0.setForeground( GraphInfo.SEPARATE_COLOR );
			meterPanel.add( text0 );

			clown = new TimeElement();
			c = new Meter(currDisplay,clown);
			c.setFont( f );
			meterPanel.add( c );
			
			clown = new CurrentElement();
			c = new Meter(currDisplay,clown);
			c.setFont( f );
			meterPanel.add( c );

			clown = new VoltageElement();
			c = new Meter(voltDisplay,clown);
			c.setFont( f );
			meterPanel.add( c );

			clown = new TauElement();
			c = new Meter(currDisplay,clown);
			c.setFont( f );
			meterPanel.add( c );

			add( meterPanel );

					//these meters are for static values (which I don't need, yet)
			//meters.addElement( new Meter(voltDisplay,circuit) );
			//meters.addElement( new Meter(currDisplay,circuit) );
					//old ca meters
			//meters.addElement( new Meter(voltDisplay,Circuit.power) );
			//meters.addElement( new Meter(voltDisplay,Circuit.resistor) );
			//meters.addElement( new Meter(voltDisplay,Circuit.coil) );
			//meters.addElement( new Meter(voltDisplay,Circuit.capacitor) );

		}

		public void actionPerformed( ActionEvent e )
		{
			if( ((Switcher)CC2.this.circuit.S1).on_off == Switcher.CHARGE )
			{
				text0.setText("Charging Cycle");
				//debug("text0 should be switched to Charging Cycle");
			}
			else
				text0.setText("Discharging Cycle");

			//debug("Popup actionPerformed "+e);
			swirl( Popup.this, e );
			//repaint();
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
				CC2.this.broadcast( new ActionEvent(this,0,INSTRUCTIONS) );
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
		}
	}
	
	static void debug( String s )
	{
		if( debug )
			System.out.println("CC2:: "+s);
	}
}
