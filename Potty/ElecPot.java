import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ElecPot extends Panel
{
	static final boolean debug = false;

	final int kLineWidth = 2;		// width of the frame
	GraphInfo info = null;
	final boolean useItalic = false;

	public final static String STATECHANGED = "state";
	public final static String RESET = "reset";
	public final static String MODECHANGED = "mode";
	public final static String POINT = "point";
	public final static String EXAMPLE = "example";

	public final static int PLOT_MODE = 0;
	public final static int VIEW_MODE = 1;
	public final static int GRAPH_MODE = 2;

	Example [] examples;
	Example [] PE2_examples = 
		{
			new ChargedRing(),
			new PerpendicularDipole(),
			new AxisDipole(),
			new ChargedSphere(),
			new ParallelPlates(),
			new CoaxialCylinder(),
			new ChargedDisk()
		};	
	Example [] PE3_examples = 
		{
			new PerpendicularDipole(),
			new AxisDipole(),
			new ConductingSphere(),
			new PointCharge(),
			new ParallelPlates()
		};	
	
	Checkbox plotPoints = null;
	Checkbox viewEquipotential = null;
	Checkbox graphFunction = null;
	Choice exp_choice = null;
	Choice nochoice = null;
		
	ActionListener stateListeners = null;
	Example state = null;
	Main applet;
	
	static boolean firstReset = true;			//need to resetApplet() differently the first time
	
	// initializer -- start off with a BorderLayout.
	
	public ElecPot( Main applet, GraphInfo info )
	{
		super( new FixedLayoutManager() );
		
		this.applet = applet;
		
		setBackground( Color.white );

		this.info = info;
		if( info.PE2 )
			examples = PE2_examples;
		else
			examples = PE3_examples;
		
		add( new Header(), new Rectangle(0,0,590,34) );
		Panel p = new Panel( new FixedLayoutManager() );
		add( p, new Rectangle(0,34,590,277) );
		
		p.add( new ControlPanel(), new Rectangle(0,0,234,277) );	//was BorderLayout.WEST
		p.add( new GraphPanel(), new Rectangle(234,0,358,277) );	//was BorderLayout.CENTER
		
		resetApplet();
	}
	
	// change the mode
	
	public int getMode()
	{
		if( plotPoints.getState() ) return PLOT_MODE;
		else if( viewEquipotential.getState() ) return VIEW_MODE;
		else return GRAPH_MODE;
	}
	
	public void setMode( int mode )
	{
		debug("setMode( "+mode+" )");

		switch( mode )
		{
			case PLOT_MODE: if( plotPoints != null )
								plotPoints.setState(true);
							break;
			case VIEW_MODE: if( viewEquipotential != null )
								viewEquipotential.setState(true);
							break;
			case GRAPH_MODE: if( graphFunction != null )
								graphFunction.setState(true);
							break;
		}
		
		broadcast( new ActionEvent(this,mode,MODECHANGED) );
	}
	
	// change the state
	
	public void setState( Example state )
	{
		debug("setting state to "+state+" (was "+state+")");
		
		//if( this.state != state )
		{
			this.state = state;
			int id = state.getID();
			debug("setState( "+state+" id="+id+" )");
			//if( (info.Distribution < 0) || (info.Distribution >= examples.length) )	//only if we have the Distributions chooser
				exp_choice.select(state.position);
			
			broadcast( new ActionEvent(state,id,STATECHANGED) );

			setExample( 0 );
		}
	}
	
	// set the currently displayed example.
	
	public void setExample( int ex )
	{
		//if( (info.Distribution < 0) || (info.Distribution >= examples.length) )	//only if we have the Distributions chooser
			nochoice.select(ex);
		debug("setExample( "+ex+" )");
		
		broadcast( new ActionEvent(this,ex,EXAMPLE) );
		
		setMode( PLOT_MODE );	
	}
	
	// recalc everything
	
	public void recalc()
	{
	}
	
	// reset the applet
	
	public void resetApplet()
	{
		
		if( firstReset )		//the first time here, we need to init everything
		{						//  to get everything/anything to paint
			debug("Resetting the applet the first time");
			setState(examples[0]);
			firstReset = false;
		}
		else
			debug("Resetting the applet again");

		setMode(0);			//always set us in plot points mode.
		
							//set our state to the correct distribution
		debug("info.Distribution=="+info.Distribution);
		if( (info.Distribution < 0) || (info.Distribution >= examples.length) )	//only if we have the Distributions chooser
			broadcast( new ActionEvent(this,0,RESET) );
		else
		{
			setState( examples[info.Distribution] );
			broadcast( new ActionEvent(this,0,RESET) );
//			broadcast( new ActionEvent(this,info.Distribution,RESET) );
		}
	}
	
	// handle the action listener for detecting state changes.
	public void addActionListener( ActionListener l )
	{
		stateListeners = AWTEventMulticaster.add(stateListeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		stateListeners = AWTEventMulticaster.remove(stateListeners,l);
	}
	
	public void broadcast( ActionEvent e )
	{
		if( stateListeners != null )
		{
			stateListeners.actionPerformed(e);
		}
	}
	
	// graph panel
	
	class GraphPanel extends Panel
	{
		public GraphPanel()
		{
			super( new FixedLayoutManager() );	//was new BorderLayout()
			
			PicturePanel pict;
			PlotPanel plot;
			FormulaPanel form;
			
			add( pict = new PicturePanel(), new Rectangle(0,0,358,101) );
			addActionListener( pict );
			
			Panel temp = new Panel( new FixedLayoutManager() );		//was new BorderLayout()
			temp.add( plot = new PlotPanel(), new Rectangle(0,0,179,178) );
			addActionListener( plot );
			temp.add( form = new FormulaPanel(), new Rectangle(179,0,179,178) );
			addActionListener( form );
			
			add( temp, new Rectangle(0,101,358,178) );
			
			pict.addActionListener( plot );
		}
	}
	
	// header definition
	
	class Header extends PaddedPanel
	{
		public Header()
		{
			super( new FixedLayoutManager(), 0, 0, 2, 0 );			//was new BorderLayout(), 0, 0, 2, 0 );
			
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
			
			p.add( b, new Rectangle(5,5,41,22) );
			add( p, new Rectangle(539,0,51,32) );
			
			RawLabel l;
			
			if( info.PE2 )
				l = new RawLabel("Electric Potential");
			else
				l = new RawLabel("Electric Potential and Equipotential Maps");
			l.setFont( new Font("SansSerif",Font.BOLD,12) );
			l.setSize( l.getPreferredSize() );
			add( l, new Rectangle(0,0,l.getPreferredSize().width,32) );
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
			super( new FixedLayoutManager(), 0, 0, 10, 0 );	//was new BalancedLayoutManager(true)
			setBackground( info.CONTROL_COLOR );
			CheckboxGroup group = new CheckboxGroup();
			
			if( (info.Distribution < 0) || (info.Distribution >= examples.length) )	//give a popup menu of Distributions to choose from
				add( new ChooseControl(), new Rectangle(0,0,234,79) );
			else
				add( new ChooseControl(), new Rectangle(0,0,234,60) );
			add( new StepOneControl(group), new Rectangle(0,82,234,106) );
			add( new StepTwoControl(group), new Rectangle(0,189,234,83) );
		}
		
		class TitledPanel extends Panel
		{
			String title;
			FontMetrics fm;
			
			public TitledPanel( String title )
			{
				super( new GridBagLayout() );
				this.title = title;
				Font f = new Font("SansSerif",Font.PLAIN,12);
				setFont( f );
				fm = getFontMetrics(f);
			}
			
			public Insets getInsets()
			{
				return new Insets(fm.getHeight()+2,0,0,0);
			}
			
			public void paint( Graphics g )
			{
				super.paint(g);
				
				g.setFont( getFont() );
				g.setColor( Color.black );
				g.drawString( title, 0, fm.getAscent() );
				int y = fm.getHeight()-1;
				g.drawLine(0,y,getSize().width,y);
			}
		}

		class ChooseControl extends PaddedPanel
		{
			public ChooseControl()
			{
							//add some top(only) inset to keep rawlabel and choicers together
				super( new FixedLayoutManager(), 5, 1, 1, 1 );		//was new BorderLayout(2,1)
	
				//debug("initializing ChooseControl");
				
				//at first was removed because mac font becomes too big for applet to fit window
				//BEWARE the label needs to be in SansSerif, so add add it now
				//BUT the chooser is in default Serif, so create early and add late
				Font deffont = new Font("Serif",Font.PLAIN,12);
				
					//init the chooser anyway, because the are used/accessed globally
					
				exp_choice = new Choice( );
				exp_choice.addItemListener( new ItemListener(){
						public void itemStateChanged(ItemEvent e)
						{
							setState( examples[((Choice)e.getItemSelectable()).getSelectedIndex()] );
						}
					} );
				
				for( int i=0; i<examples.length; ++i )
				{
					exp_choice.addItem( examples[i].getName() );
					examples[i].position = i;
					examples[i].setApplet(applet);
				}

				nochoice = new Choice();
				nochoice.addItem("Example 1");
				nochoice.addItem("Example 2");
				nochoice.addItem("Example 3");
				nochoice.addItem("Example 4");
				
				nochoice.addItemListener( new ItemListener(){
						public void itemStateChanged(ItemEvent e)
						{
							setExample( ((Choice)e.getItemSelectable()).getSelectedIndex() );
						}
					} );

				if( (info.Distribution < 0) || (info.Distribution >= examples.length) )	//blotz give a popup menu of Distributions to choose from
				{
					RawLabel lbl = new RawLabel("Choose a Distribution and Example");
					lbl.setFont( new Font("SanSerif",Font.PLAIN,12) );
					add( lbl, new Rectangle(1,3,232,20) );
					
					// add the choice box. transmit any changes to the applet, who 
					// will tell anyone who cares about this.
					
					Panel panel = new Panel( new FixedLayoutManager() );	//was new GridLayout(0,1)
					
					panel.add( exp_choice, new Rectangle(0,0,232,27) );

					panel.add( nochoice, new Rectangle(0,27,232,27) );
				
					setFont( deffont );
					add( panel, new Rectangle(1,25,232,57) );
				}
				else		//applet was given what Distribution the prof wants
				{			// so setup that Distribution only
					RawLabel lbl0 = new RawLabel("  ");			//reserve same spacing, so use a string with spaces only
					lbl0.setFont( new Font("SanSerif",Font.PLAIN,16) );
					add( lbl0, new Rectangle(1,5,232,21) );
					
					// add 2 RawLabels the s choice box. transmit any changes to the applet, who 
					// will tell anyone who cares about this.
					
					Panel panel = new Panel( new FixedLayoutManager() );	//was new GridLayout(0,1)
					
					RawLabel lbl1 = new RawLabel( " "+examples[info.Distribution].getName() );			//reserve same spacing
					lbl1.setFont( new Font("SanSerif",Font.PLAIN,12) );
					panel.add( lbl1, new Rectangle(0,0,232,16) );

					RawLabel lbl2 = new RawLabel("    Example 1");					//reserve same spacing
					lbl2.setFont( new Font("SanSerif",Font.PLAIN,12) );
					panel.add( lbl2, new Rectangle(0,16,232,16) );

					setFont( deffont );
					add( panel, new Rectangle(1,28,232,32) );

					debug("About to setState ="+info.Distribution+" ("+examples[info.Distribution]+")" ); 

					setState( examples[info.Distribution] );
					
					debug(" all set");

					//setExample( 0 );			//always use example 1, done in setState()
				}
				
			}
		}
		
		class ModeChange implements ItemListener
		{
			int mode;
			
			public ModeChange( int mode )
			{
				this.mode = mode;
			}
			
			public void itemStateChanged(ItemEvent e)
			{
				//debug(e);
				
				if( e.getStateChange() == ItemEvent.SELECTED )
				{
					setMode(mode);
				}
			}
		}
		
		class StepOneControl extends TitledPanel
		{
			public StepOneControl( CheckboxGroup group )
			{
				super("Step One");
				
				//debug("initializing StepOneControl");
				
				setLayout( new FixedLayoutManager() );	//was new BorderLayout()
								
				String [] t1 = new String[3];
				t1[0] = "Click (20-30 times) on the 2D Diagram to";
				t1[1] = "plot the potential on the graph. A yellow area";
				t1[2] = "shows where the potential function is valid.";

				if( state != null )
				{
					int	id = state.getID();
					if(    (id == Example.INFINITECOAX)
						|| (id == Example.CONDUCTINGSPHERE)
						|| (id == Example.POINTCHARGE)
						|| (id == Example.CHARGEDSPHERE) )
					{
						t1[0] = "Click (20-30 times) on the 2D Diagram to";
						t1[1] = "plot the potential on the graph. ";
						t1[2] = "    ";
					}
				}
					
				String [] t2 = {};		

				CheckboxPanel cbp;
				
				cbp = new CheckboxPanel("Plot Points",true,t1,group);
				plotPoints = cbp.getCheckbox();
				plotPoints.addItemListener( new ModeChange(PLOT_MODE) );
				add( cbp, new Rectangle(0,17,234,64) );
				
				cbp = new CheckboxPanel("View Equipotential Lines",false,t2,group);
				viewEquipotential = cbp.getCheckbox();
				viewEquipotential.addItemListener( new ModeChange(VIEW_MODE) );
				if( !info.PE2 )
					add( cbp, new Rectangle(0,81,234,28) );
			}
		}
		
		class StepTwoControl extends TitledPanel
		{
			public StepTwoControl( CheckboxGroup group )
			{
				super("Step Two");
				
				//debug("initializing StepTwoControl");
				
				setLayout( new FixedLayoutManager() );	//was new BorderLayout()
								
				String [] t1 = {
					"Move the sliders to change the parameters",
					"of the curve. Match the curve to your data",
					"to determine the actual size of the distribution."
					};

				CheckboxPanel cbp;
				
				cbp = new CheckboxPanel("Graph Function",false,t1,group);
				graphFunction = cbp.getCheckbox();
				graphFunction.addItemListener( new ModeChange(GRAPH_MODE) );
				add( cbp, new Rectangle(0,17,234,64) );
			}
		}
	}

	static void debug( String s )
	{
		if( debug )
		{
			System.out.println("ElecPot:: "+s);
		}
	}
}
