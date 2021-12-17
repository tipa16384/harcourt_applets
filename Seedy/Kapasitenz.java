import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.net.URL;
import java.applet.*;
import Wired.*;

public class Kapasitenz extends Panel implements ActionListener
{
	public static final String RESET = "reset";
	public static final String DIELECTRIC = "dielectric";
	public static final String MODEL = "model";
	public static final String REDRAW = "redraw";
	
	GraphInfo info = null;
		
	ActionListener listeners = null;
	Dielectric die = null;
	Model model = null;
	Choice dieChoice = null;
	Choice modelChoice = null;
	Main applet;
		
	Dielectric [] kList = {
		new Dielectric( "Vacuum ( \u03BA = 1.00000 )", 1.0, Color.white ),
		new Dielectric( "Dry Air ( \u03BA = 1.00059 )", 1.00059, new Color(255,255,204) ),
		new Dielectric( "Teflon ( \u03BA = 2.1 )", 2.1, new Color(153,153,153) ),
		new Dielectric( "Silicone Oil ( \u03BA = 2.5 )", 2.5, new Color(204,204,153) ),
		new Dielectric( "Polystyrene ( \u03BA = 2.56 )", 2.56, new Color(204,204,255) ),
		new Dielectric( "Nylon ( \u03BA = 3.4 )", 3.4, new Color(204,204,102) ),
		new Dielectric( "Paper ( \u03BA = 3.7 )", 3.7, new Color(204,153,204) ),
		new Dielectric( "Fused Quartz ( \u03BA = 3.78 )", 3.78, new Color(255,204,153) ),
		new Dielectric( "Bakelite ( \u03BA = 4.9 )", 4.9, new Color(204,153,255) ),
		new Dielectric( "Pyrex Glass ( \u03BA = 5.6 )", 5.6, new Color(204,204,204) ),
		new Dielectric( "Neoprene Rubber ( \u03BA = 6.7 )", 6.7, new Color(153,204,153) ),
		new Dielectric( "Water ( \u03BA = 80.0 )", 80.0, new Color(255,204,204) ),
		new Dielectric( "Strontium Titanate ( \u03BA = 233.0 )", 233.0, new Color(153,204,204) )
	};		
	
	Model [] mList = {
		new ParallelPlateModel("Parallel Plates"),
		new CoaxModel("Coaxial Cylinders")
	};
	
	// initializer -- start off with a BorderLayout.
	
	public Kapasitenz( Main applet, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.applet = applet;
		
		setBackground( Color.white );

		this.info = info;
		
		add( new Header(), BorderLayout.NORTH );
		Panel p = new Panel( new BorderLayout() );
		add( p, BorderLayout.CENTER );
		
		p.add( new ControlPanel(), BorderLayout.SOUTH );		
		p.add( new GraphPanel(), BorderLayout.CENTER );
		
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
		//System.out.println("Resetting the applet");

		setModel( model );
		
		if( model != null )
			model.resetValues();
			
		setDielectric( null );
		broadcast( new ActionEvent(this,0,RESET) );
	}

	public void setDielectric( Dielectric dying )
	{
		if( dying == null )
			dying = kList[0];
			
		if( dieChoice != null )
		{
			dieChoice.select( dying.getName() );
		}
	
		//if( die != dying )
		{
			die = dying;
	
			//System.out.println("Selecting "+dying);
	
			broadcast( new ActionEvent(dying,0,DIELECTRIC) );
		}
	}
	
	public void setModel( Model kateMoss )
	{
		if( model != null )
			model.removeActionListener(this);
			
		model = kateMoss;

		if( model == null )
			model = mList[0];

		model.addActionListener(this);

		if( modelChoice != null )
		{
			modelChoice.select( model.getName() );
		}

//		System.out.println("Selecting "+model);

		broadcast( new ActionEvent(model,0,MODEL) );
		setDielectric( die );
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
	
	class GraphPanel extends Panel
	{
		public GraphPanel()
		{
			super( new BorderLayout() );
			add( new PicturePanel(), BorderLayout.EAST );
			add( new CircuitPanel(), BorderLayout.CENTER );
		}
	}
	
	class CircuitPanel extends PaddedPanel
	{
		Component circuit;
		
		public CircuitPanel()
		{
			super( null, 10, 10, 10, 0 );
			circuit = new Circuit(); 
			add( circuit );
		}
		
		public void doLayout()
		{
			//System.out.println("CircuitPanel.doLayout");
			Insets insets = getInsets();
			Dimension size = getSize();
			circuit.setBounds( insets.left, insets.top,
				size.width - insets.right - insets.left,
				size.height - insets.top - insets.bottom );
		}
	}
	
	class Circuit extends Component implements ActionListener
	{
		public Circuit()
		{
			Kapasitenz.this.addActionListener( this );
			Circuit.this.addMouseListener( new PopupWatcher(Circuit.this) );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			//System.out.println("Circuit: "+e);
			repaint();
		}
		
		public void paint( Graphics g )
		{
			if( model != null )
				model.drawCircuit( g, Circuit.this, die );
		}
	}
	
	// class to display all that intrusive information
	
	class AnalProbe extends Canvas
	{
		final int borderSize = 1;
		RawLabel text;
		int x, y;
		int theight;
		Component sirkit;
				
		public AnalProbe( Component sirkit )
		{
			text = new RawLabel("M=MMMMM.MMMMM");
			text.setFont( new Font("SansSerif",Font.PLAIN,10) );
			text.setForeground( Color.black );
			Dimension size = text.getPreferredSize();
			theight = size.height;
			setSize( size.width+borderSize, 3*theight+borderSize+8 );
			this.sirkit = sirkit;
			setBackground( Color.white );
			setForeground( Color.black );
		}
		
		public void setTarget( int x, int y )
		{
			this.x = x;
			this.y = y;
		}

		public void setVisible( boolean showIt )
		{
			if( model != null )
			{
				if( showIt )
				{
					Rectangle r = getBounds();
					model.showLine( sirkit, new Point(x,y), new Point(r.x+r.width/2,r.y+r.height/2) );
				}
				
				else model.hideLine( sirkit );
			}
			
			super.setVisible( showIt );
		}

		public void paint( Graphics g )
		{
			Dimension size = getSize();
			
			//g.setColor( Color.black );
			//g.fillRect( borderSize, borderSize, size.width-borderSize, size.height-borderSize );

			//g.setColor( Color.white );
			//g.fillRect( 0, 0, size.width-borderSize, size.height-borderSize );
			g.setColor( GraphInfo.SEPARATE_COLOR );
			g.drawRect( 0, 0, size.width-borderSize, size.height-borderSize );
			g.drawRect( 1, 1, size.width-borderSize-2, size.height-borderSize-2 );
			
			if( model == null ) return;
			
			DPoint p = model.shiftPoint(sirkit,x,y);
			
			int x0 = 8;
			int y0 = 4;
			double ddub;
			
			ddub = model.getEField(sirkit,p.x,p.y,die);
			text.setText("~!E~! = "+DoubleFormat.format(ddub)+" V/m");
			text.paint( g, x0, y0, false );
			y0 += theight;

			ddub = model.getEnergy(sirkit,p.x,p.y,die);
			text.setText("~!~m~! = "+DoubleFormat.format(ddub)+" J/m~^3");
			text.paint( g, x0, y0, false );
			y0 += theight;

			text.setText(model.variable()+" = "+DoubleFormat.format(model.value(sirkit,x,y))+" "+model.units());
			text.paint( g, x0, y0, false );
			y0 += theight;
		}
	}
	
	// class to handle clicks on the circuit panel
	
	class PopupWatcher extends MouseAdapter
	{
		AnalProbe zippo = null;
		Component sirkit = null;
		
		public PopupWatcher( Component sirkit )
		{
			this.sirkit = sirkit;
		}
		
		public void mousePressed( MouseEvent e )
		{
			//System.out.println("PopupWatcher: mouse pressed");
			
			Container parent = sirkit.getParent();
			
			if( zippo == null )
			{
				zippo = new AnalProbe(sirkit);
				zippo.setVisible(false);
				parent.add( zippo, 0 );
			}
			
			if( model != null && model.inDielectric( sirkit, e.getX(), e.getY() ) )
			{
				Dimension size = zippo.getSize();
				Dimension psize = parent.getSize();
				int x, y;
				
				Point p = model.getPopupPoint( sirkit );
				zippo.setLocation( p.x, p.y );
				zippo.setTarget( e.getX(), e.getY() );
				zippo.setVisible(true);
			}
		}

		public void mouseReleased( MouseEvent e )
		{
			//System.out.println("PopupWatcher: mouse released");
			zippo.setVisible(false);
		}
	}
	
	
	// panel holding the model picture
	
	class PicturePanel extends PaddedPanel implements ActionListener
	{
		public PicturePanel()
		{
			super( new BorderLayout(), 10, 10, 0, 10 );
			Kapasitenz.this.addActionListener( PicturePanel.this );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			if( e.getActionCommand().equals(MODEL) )
			{
				removeAll();
				
				Image image = model.getImage();
				
				if( image != null )
				{
					Component gi = new GenericIcon(image,null,"3d");
					add( gi, BorderLayout.NORTH );
				}

				validate();
				repaint();
			}
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
			
			l = new RawLabel( info.cd2 ? "Capacitors and Dielectrics" : "Capacitance" );
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
		
		class RightSide extends PaddedPanel implements ActionListener
		{
			public RightSide()
			{
				super( new GridLayout(0,1), 0, 20, 0, 20 );
				Kapasitenz.this.addActionListener( RightSide.this );
			}
			
			public void actionPerformed( ActionEvent e )
			{
				if( e.getActionCommand().equals(MODEL) )
				{
					//System.out.println("RightSide setting model");
					removeAll();
					Model baroque = (Model) e.getSource();
					baroque.addSliders(this);
					validate();
					repaint();
				}
			}
		}
		
		class LeftSide extends Panel
		{
			public Dimension getPreferredSize()
			{
				return new Dimension(100,80);
			}
			
			public LeftSide()
			{
				super( new GridLayout(0,1) );

				int i;
				
				Panel p2 = new Panel( new BorderLayout() );
				
				add( p2 );
				
				p2.add( new RawLabel("Choose a Capacitor"), BorderLayout.NORTH );
				modelChoice = new Choice();
				modelChoice.setFont( new Font("Serif",Font.PLAIN,12) );
				
				for( i=0; i<mList.length; ++i )
				{
					modelChoice.add( mList[i].getName() );
					mList[i].setApplet(applet);
				}
				
				p2.add( modelChoice, BorderLayout.CENTER );
				modelChoice.addItemListener( new ItemListener()
					{
						public void itemStateChanged(ItemEvent e)
						{
							if( e.getStateChange() == ItemEvent.SELECTED )
							{
								setModel( mList[modelChoice.getSelectedIndex()] );
							}
						}
					} );

				if( info.cd2 )
				{
					Panel p1 = new Panel( new BorderLayout() );
					
					add( p1 );
					
					p1.add( new RawLabel("Choose a Dielectric"), BorderLayout.NORTH );

					dieChoice = new Choice();
					dieChoice.setFont( new Font("Serif",Font.PLAIN,12) );
					
					for( i=0; i<kList.length; ++i )
					{
						Dielectric d = kList[i];
						int color = 255 - (int)((double)i * 102.0/(double)kList.length + 0.5);
						//d.setColor(new Color(color,color,color));
						
						String s = d.getName();
						
						if( applet.isCrippled() )
						{
							s = s.replace('\u03BA','K');
						}
						
						dieChoice.add( s );
					}
					
					p1.add( dieChoice, BorderLayout.CENTER );
					dieChoice.addItemListener( new ItemListener()
						{
							public void itemStateChanged(ItemEvent e)
							{
								if( e.getStateChange() == ItemEvent.SELECTED )
								{
									setDielectric( kList[dieChoice.getSelectedIndex()] );
								}
							}
						} );
				}
			}
		}
	}
}
