import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import Wired.*;

public class Coulomb1 extends Panel
{
	final int kLineWidth = 2;		// width of the frame
	Checkbox placeCharges = null;
	Checkbox placePositive = null;
	Checkbox moveCharges = null;
	Checkbox forceVectors = null;
	Checkbox electricFields = null;
	Checkbox equipotentials = null;
	Checkbox defaultFixedState = null;
	Checkbox defaultTestState = null;
	TestPaper graph = null;
	GraphInfo info = null;
	ChargeTextField chargeField=null, radiusField=null, charge2Field = null;
	transient Main applet;

	final double SCALE = 1.0;
	final boolean useItalic = false;
	
	private Charge fixedCharge = null;
	final double initFixedCharge = 1.0;
	public Charge testCharge = null;
	final double initTestCharge = -1.0;
	
	// initializer -- start off with a BorderLayout.
	
	public Coulomb1( Main applet, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.applet = applet;
		this.info = info;
		
		info.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					if( graph != null )
					{
						//System.out.println("--> repainting because of change in GraphInfo");
						graph.updateBackdrop();
						graph.repaint();
					}
				}
			} );
		
		add( new Header(), BorderLayout.NORTH );
		Panel p = new PaddedPanel( new BorderLayout(), 10 );
		add( p, BorderLayout.CENTER );
		
		if( info.fc2 )
		{
			p.add( new ChargePanel(), BorderLayout.EAST );
		}
		
		else
		{
			p.add( new ControlPanel(), BorderLayout.EAST );
		}
		
		p.add( new GraphPanel(), BorderLayout.CENTER );
		
		resetApplet();
	}
	
	// recalc everything
	
	public void recalc()
	{
		if( graph != null )
			graph.recalc();
	}
	
	// reset the applet
	
	public void resetApplet()
	{
		//System.out.println("Resetting the applet");
		
		if( !info.fc2 )
		{
			placeCharges.setEnabled(true);
			placePositive.setEnabled(true);
			placeCharges.setState(true);
			forceVectors.setState(true);
			equipotentials.setState(false);
			electricFields.setState(false);
			info.setShowForce(true);
			info.setShowField(false);
			info.setShowPotential(false);
			info.selectCharge(null);
		}
		
		else
		{
			info.trigger();
		}
		
		graph.reset();
	}
	
	// graph panel
	
	class GraphPanel extends Panel
	{
		public GraphPanel()
		{
			super( new BorderLayout() );
			Panel p = new PaddedPanel( new BorderLayout(), 0, 0, 0, 20 );
			add( p, BorderLayout.CENTER );
			
			graph = new TestPaper();
			
			p.add( graph, BorderLayout.CENTER );
				
			p.add( new ValuePanel(), BorderLayout.SOUTH );
		}
	}
	
	class Propertybox extends Checkbox
	{
		int index;
		
		public Propertybox( String label, int prop, CheckboxGroup group )
		{
			super( label, false, null );
			setState(info.getProperty(prop));
			index = prop;
			addItemListener( new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						//System.out.println(e);
						info.setProperty( index, e.getStateChange() == ItemEvent.SELECTED );
						graph.recalc();
					}
				});
		}
	}

	class TestPaper extends GraphPaper
	{
		MouseListener mouse = new PaperMouse();

		public TestPaper()
		{
			scale = SCALE;
			reset();
			
			addMouseListener( mouse );
		}
		
		public boolean showColor()
		{
			return info.color;
		}
		
		public void removePlots()
		{
			super.removePlots();
			info.killThreads();
		}
		
		public boolean showGrid()
		{
			return info.grid /*&& (info.fc2 || !equipotentials.getState()) */;
		}
		
		public boolean showEquipotential()
		{
			return !info.fc2 && equipotentials.getState();
		}

		Charge getCharge( Point where )
		{
			Charge charge = null;
			
			Component [] clist = getComponents();
			int r;
						
			for( int i=0; charge == null && i<clist.length; ++i )
			{
				Component c = clist[i];
				
				if( c instanceof Charge )
				{
					Charge ch = (Charge) c;
					Point o = ch.getPOrigin();
					r = ch.getDrawSize()/2;
										
					if( where.x >= (o.x-r) && where.x <= (o.x+r) ||
						where.y >= (o.y-r) && where.y <= (o.y+r) )
					{
						int dx = o.x-where.x;
						int dy = o.y-where.y;
						
						if( (dx*dx+dy*dy) <= (r*r) )
							charge = ch;
					}
				}
			}
			
			return charge;
		}
		
		class PaperMouse extends MouseAdapter
		{
			public Point promote( MouseEvent e )
			{
				return e.getPoint();
			}

			public void mousePressed( MouseEvent e )
			{
				Point p = promote( e );
				
				if( info.fc2 || moveCharges.getState() )
				{
					Charge c = getCharge( p );
					if( c != null && !c.getFixed() )
					{
						info.selectCharge(c);
					}
				}
			}
			
			public void mouseReleased( MouseEvent e )
			{
				Point p = promote( e );
				
				if( !info.fc2 && (!info.limit || (TestPaper.this.getComponentCount()<info.highwater)) && 
									(placePositive.getState() || placeCharges.getState()) )
				{
					if( TestPaper.this.contains(p) )
					{
						Dimension size = getSize();
						p.translate(-size.width/2,-size.height/2);
						
						double charge = placePositive.getState() ? 1.0 : -1.0;
						
						Charge c = new Charge(info,charge);
						c.setOrigin( new DPoint(p,scale) );
						c.addMouseListener( this );
						add( c );
						
						info.selectCharge( c );
	
						recalc();
						
						if( info.limit && (TestPaper.this.getComponentCount() >= info.highwater) )
						{
							placePositive.setEnabled(false);
							placeCharges.setEnabled(false);
							moveCharges.setState(true);
						}
					}
				}
				
				if( (info.fc2 || moveCharges.getState()) && info.charge != null )
				{
					if( TestPaper.this.contains(p) )
					{
						Charge c = getCharge( p );
						
						if( c == null )
						{
							c = info.charge;
	
							Dimension size = TestPaper.this.getSize();
							p.translate(-size.width/2,-size.height/2);
							
							DPoint nO = new DPoint(p,scale);
	
							//System.out.println("size of "+TestPaper.this);
							//System.out.println("transform click "+p+" to "+nO);
							
							c.setOrigin( nO );
							
							info.selectCharge( c );
							
							doLayout();
							recalc();						
						}
					}
				}
			}
		}
		
		public boolean okayToPlot()
		{
			if( info.fc2 ) return false;
			
			return electricFields.getState();
		}
		
		// recalc any changes based upon new charge.
		
		public void recalc()
		{
			removePlots();
			
			fixOverlap();
			
			Component [] clist = getComponents();
			int len = clist.length;
			
			for( int i=0; i<len; ++i )
			{
				Component c = clist[i];
				if( c instanceof Charge )
				{
					Charge ch = (Charge)c;
					ch.calcForce(scale);
					if( !info.fc2 )
					{
						if( electricFields.getState()  )
							ch.calcCharge(scale);
					}
				}
			}
			
			//System.out.println("repainting because of recalc");
			
			repaint();
		}
	
		// ensure test and fixed charges don't overlap
	
		void fixOverlap()
		{
			if( info.fc2 )
			{
				Charge tc = getTestCharge();
				Charge fc = getFixedCharge();
				
				int fcr = fc.getDrawSize()/2;
				int tcr = tc.getDrawSize()/2;
				
				Point fc0 = fc.getPOrigin();
				Point tc0 = tc.getPOrigin();
				
				Fector f = new Fector();
				f.setEndpoint( (double)(tc0.x-fc0.x), (double)(tc0.y-fc0.y) );
				
				if( f.getLength() < (double)(fcr+tcr) )
				{
					//System.out.println("they overlap!");
					f.setLength( (double)(fcr+tcr) );
					DPoint d = f.getEndpoint();
					
					tc0.x = fc0.x + (int)d.x;
					tc0.y = fc0.y + (int)d.y;

					tc.setPOrigin(tc0);
					
					repaint();
				}
			}
		}
	
		public void reset()
		{
			removeAll();
			
			if( info.fc2 )
			{
				fixedCharge = null;
				Charge fc;
				
				fc = getFixedCharge();
				fc.setOrigin( new DPoint(new Point(0,0),scale) );
				fc.setRadius(1);
				fc.setCharge(initFixedCharge);
				fc.removeMouseListener(mouse);
				fc.addMouseListener(mouse);
				chargeField.setValue( (int)Math.abs(fc.getCharge()) );
				radiusField.setValue( fc.getRadius() );
				add( fc );
				
				fc = getTestCharge();
				fc.setOrigin( new DPoint(new Point(60,60),scale) );
				fc.setCharge(initTestCharge);
				fc.removeMouseListener(mouse);
				fc.addMouseListener(mouse);
				charge2Field.setValue( (int)Math.abs(fc.getCharge()) );
				info.charge = testCharge;
				add( testCharge );
				
				if( defaultFixedState != null && defaultTestState != null )
				{
					defaultFixedState.setState(true);
					defaultTestState.setState(true);
				}
				
				recalc();
				
				info.trigger();
			}
			
			//System.out.println("repainting because of reset");
			repaint();
		}
	}
	
	public Charge getFixedCharge()
	{
		if( fixedCharge == null )
		{
			fixedCharge = new Charge(info,initFixedCharge);
		}
		
		return fixedCharge;
	}
	
	public Charge getTestCharge()
	{
		if( testCharge == null )
		{
			testCharge = new Charge(info,initTestCharge);
			testCharge.test = info.testcharge;
		}
		
		return testCharge;
	}
	
	
	class ValuePanel extends Panel
	{
		public ValuePanel()
		{
			super( new BorderLayout() );
			
			Panel p = new Panel( new BalancedLayoutManager() );
			
			p.add( new ValueLabel(0) );
			p.add( new ValueLabel(1) );
			p.add( new ValueLabel(2) );
			p.add( new ValueLabel(3) );
			
			add( p, BorderLayout.SOUTH );
			
			RawLabel rl = new RawLabel("Data for highlighted charge");
			rl.setFont( new Font("Serif",Font.PLAIN,12) );
			rl.setUnderline(true);
			add( rl, BorderLayout.NORTH );
		}
	}

	class ValueLabel extends RawLabel implements ActionListener
	{
		int which;
		
		public ValueLabel( int which )
		{
			this.which = which;
			setFont( new Font("SansSerif",Font.PLAIN,applet.isCrippled()?10:12) );
			setForeground( Color.black );
			updateText();
			info.addActionListener( this );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			//System.out.println("ValueLabel["+which+"] needs updating");
			updateText();
		}
		
		private void updateText()
		{
			String pre="", post="";
			double val = 0.0;
			DPoint d;
			
			if( info.charge == null )
			{
				//System.out.println("no charge!");
				setText("");
				return;
			}
			
			switch( which )
			{
				case 0:
					pre = "~!x~! = ";
					post = "m";
					d = info.charge.getOrigin();
					val = d.x*SCALE*5.0E-8/GraphPaper.tick;
					break;
				
				case 1:
					pre = "~!y~! = ";
					post = "m";
					d = info.charge.getOrigin();
					if( d.y == 0 )
						val = d.y;
					else
						val = (-d.y)*SCALE*5.0E-8/GraphPaper.tick;
					break;
				
				case 2:
					pre = useItalic ? "~i~!F~!~i = " : "~!F~! = ";
					info.charge.calcForce(SCALE);
					val = forcedEntry();
					post = "N";
					break;
				
				case 3:
					pre = useItalic ? "~i~!~a~!~i = " : "~!~a~! = ";
					post = "~o";
					if( applet.isCrippled() ) post += "   ";
					info.charge.calcForce(SCALE);
					val = info.charge.getArrow().getFector().getAzimuth();
					val = Fector.fixAngle( -val );
					val = (double) Fector.toDeg(val);
					break;
			}

			if( which != 3 )			
				setText( pre + DoubleFormat.format(val) + post );
			else
			{
				setText( pre + (int)val + post );
			}
		}

		private double forcedEntry()
		{
			Charge ch = info.charge;
			
			if( ch == null )
				return 0.0;
			
			GraphPaper parent = ch.parent;
			if( parent == null )
				return 0.0;
				
			Component [] clist = parent.getComponents();
			if( clist == null )
				return 0.0;
				
			DPoint dh = ch.getOrigin();
			double xcomp=0;
			double ycomp=0;
			
			for( int i=0; i<clist.length; ++i )
			{
				Charge ci = (Charge) clist[i];
				
				if( ci != ch )
				{
					DPoint di = ci.getOrigin();
					double dx = di.x-dh.x;
					double dy = di.y-dh.y;
					double r2 = dx*dx+dy*dy;
					
					//System.out.println("separation - "+di+" / "+dh);
					
					if( r2 != 0 )
					{
						double r3 = r2*Math.sqrt(r2);
						double magnitude = (info.Klunk*ch.getCharge()*ci.getCharge());
						xcomp += (dx * magnitude)/r3;
						ycomp += (dy * magnitude)/r3;
					}
				}
			}
			
			return Math.sqrt(xcomp*xcomp+ycomp*ycomp);
		}
	}
	
	// header definition
	
	class Header extends PaddedPanel
	{
		public Header()
		{
			super( new BorderLayout(), 0, 0, 3, 0 );
			
			FakeButton b = new FakeButton("Reset");
			b.setBackground( Color.red );
			b.setForeground( Color.white );
			b.setSize(50,20);
			b.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						resetApplet();
					}
				} );
			add( b, BorderLayout.EAST );
			
			Label l;
			
			if( info.fc2 )
				l = new Label("Coulomb's Law");
			else
				l = new Label("Electric Force and Field Lines Due to Point Charges");
				
			l.setFont( new Font("SansSerif",Font.BOLD,14) );
			add( l, BorderLayout.WEST );
		}
		
		public void paint( Graphics g )
		{
			super.paint( g );
			
			Dimension size = getSize();
			g.drawLine( 0, size.height-1, size.width, size.height-1 );
		}
	}

	class ChargePanel extends PaddedPanel
	{
		public ChargePanel()
		{
			super( new GridBagLayout(), 0, 10, 0, 10 );

			setBackground( info.CONTROL_COLOR );

			GridBagConstraints gbc;
			Component c;
			Font f = new Font("SansSerif",Font.PLAIN,12);
			setFont(f);
			CheckboxGroup group1 = new CheckboxGroup();
			CheckboxGroup group2 = new CheckboxGroup();
									
			gbc = new GridBagConstraints();
			gbc.gridx = gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			
			Panel p, p1;
			
			p = new Panel( new BorderLayout() );
			c = new Label("Radius of Charge One");
			p.add( c, BorderLayout.NORTH );
			p1 = new Panel( new TwoDLayout( false, 0, 0 ) );
			p1.add( new RawLabel(useItalic?"~i~!r~!~i~v1~v: ":"~!r~!~v1~v: ") );
			p1.add( radiusField = new ChargeTextField(Coulomb1.this,true) );
			p1.add( new IncrementTool(radiusField) );
			p1.add( new RawLabel(" ~+10~^-8~^ m") );
			p.add( p1, BorderLayout.WEST );
			
			add( p, gbc );
			
			gbc.weightx = 1.0;
			gbc.gridy = GridBagConstraints.RELATIVE;
			
			p = new Panel( new GridLayout(0,1) );
			c = new Label("Magnitude of Charges");
			//c.setFont(f);
			p.add( c );
			p1 = new Panel( new TwoDLayout( false, 0, 0 ) );
			p1.add( new RawLabel(useItalic?"~i~!q~!~i~v1~v: ":"~!q~!~v1~v: ") );
			p1.add( chargeField = new ChargeTextField(Coulomb1.this,false) );
			p1.add( new IncrementTool(chargeField) );
			p1.add( new RawLabel(" ~+1.602~+10~^-19~^ C") );
			p.add( p1 );
			
			p1 = new Panel( new TwoDLayout( false, 0, 0 ) );
			p1.add( new RawLabel(useItalic?"~i~!q~!~i~v2~v: ":"~!q~!~v2~v: ") );
			p1.add( charge2Field = new ChargeTextField(Coulomb1.this,false,false) );
			p1.add( new IncrementTool(info.testcharge ? null : charge2Field) );
			p1.add( new RawLabel(" ~+1.602~+10~^-19~^ C") );
			p.add( p1 );
			
			add( p, gbc );
			
			p = new Panel( new GridLayout(0,1) );
			c = new Label("Sign of Charge One");
			//c.setFont(f);
			p.add( c );
			
			Checkbox ch;
			defaultFixedState = new Checkbox("+",(initFixedCharge > 0),group1);
			defaultFixedState.addItemListener( new ChangeCharge(true,1.0) );
			defaultFixedState.setFont(f);
			p.add( defaultFixedState );
			
			ch = new Checkbox("\u2212",(initFixedCharge < 0),group1);
			ch.addItemListener( new ChangeCharge(true,-1.0) );
			ch.setFont(f);
			p.add( ch );
			
			add( p, gbc );
			
			p = new Panel( new GridLayout(0,1) );
			c = new Label("Sign of Charge Two");
			c.setFont(f);
			p.add( c );
			
			ch = new Checkbox("+",(initTestCharge > 0),group2);
			ch.addItemListener( new ChangeCharge(false,1.0) );
			ch.setFont(f);
			p.add( ch );
			
			defaultTestState = new Checkbox("\u2212",(initTestCharge < 0),group2);
			defaultTestState.addItemListener( new ChangeCharge(false,-1.0) );
			defaultTestState.setFont(f);
			p.add( defaultTestState );
			
			add( p, gbc );
			
		}
	}

	class ChangeCharge implements ItemListener
	{
		boolean changeFixed;
		double newCharge;
		
		public ChangeCharge( boolean changeFixed, double newCharge )
		{
			this.changeFixed = changeFixed;
			this.newCharge = newCharge;
		}
		
		public void itemStateChanged(ItemEvent e)
		{
			if( e.getStateChange() == ItemEvent.SELECTED )
			{
				Charge charge = changeFixed ? getFixedCharge() : getTestCharge();
				
				if( newCharge > 0 )
				{
					if( charge.getCharge() < 0 )
						charge.setCharge( -charge.getCharge() );
				}
				
				else
				{
					if( charge.getCharge() > 0 )
						charge.setCharge( -charge.getCharge() );
				}
				
				graph.recalc();
			}
		}
		
	}

	// the two control panels
	
	class ControlPanel extends PaddedPanel
	{
		public ControlPanel()
		{
			super( new BorderLayout(), 0, 10, 0, 10 );
			setBackground( info.CONTROL_COLOR );
			add( new ModeControl(), BorderLayout.CENTER );
			Panel p = new Panel( new BorderLayout() );
			p.add( new DisplayControl(), BorderLayout.NORTH );
			add( p, BorderLayout.SOUTH );
		}
		
		class TitledPanel extends Panel
		{
			String title;
			FontMetrics fm;
			CheckboxGroup group;
			
			public TitledPanel( String title )
			{
				super( new GridBagLayout() );
				this.title = title;
				Font f = new Font("SansSerif",Font.PLAIN,12);
				setFont( f );
				fm = getFontMetrics(f);
				group = new CheckboxGroup();
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
		
		class ModeControl extends TitledPanel
		{
			public ModeControl()
			{
				super("Mode");
				
				String [] t2 = {
					"Click to select, then click in new",
					"position to move charge, or click and",
					"drag to new position."
					};		

				GridBagConstraints gbc;
				CheckboxPanel cbp;
				
				gbc = new GridBagConstraints();
				gbc.gridx = gbc.gridy = 0;
				gbc.anchor = GridBagConstraints.NORTHWEST;
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				
				Panel p3 = new Panel( new BorderLayout() );
				
				placeCharges = new Checkbox("Place Negative (\u2212) Charges",true,group);
				p3.add( placeCharges, BorderLayout.NORTH );
				
				String [] t1a = new String[2];
				
				if( info.limit )
				{
					t1a[0] = "Click inside the box to place up to";
					t1a[1] = info.highwater+" charges.";
				}
				else
				{
					t1a[0] = "Click inside the box to place a charge.";
					t1a[1] = "";
				}

				cbp = new CheckboxPanel("Place Positive (+) Charges",false,t1a);
				placePositive = cbp.getCheckbox();
				p3.add( cbp, BorderLayout.SOUTH );
				add( p3, gbc );

				gbc.gridy = GridBagConstraints.RELATIVE;

				cbp = new CheckboxPanel("Move Charges",false,t2);
				moveCharges = cbp.getCheckbox();
				add( cbp, gbc );
			}
			
			class CheckboxPanel extends Panel
			{
				Checkbox cb;
				
				public CheckboxPanel(String label,boolean state,
									String [] text)
				{
					super( new BorderLayout() );
					
					cb = new Checkbox(label,state,group);
					add( cb, BorderLayout.NORTH );
					add( new TextPanel(text), BorderLayout.CENTER );
				}
				
				public Checkbox getCheckbox()
				{
					return cb;
				}
			}
			
			class TextPanel extends Component
			{
				String [] text;
				FontMetrics fm;
				final int xOffset = 22;
				
				public TextPanel( String [] text )
				{
					this.text = text;
					Font f = new Font("SansSerif",Font.PLAIN,9);
					setFont( f );
					fm = getFontMetrics( f );
				}
				
				public Dimension getMinimumSize()
				{
					int h = text.length * fm.getHeight();
					int w = 0;
					
					for( int i=0; i<text.length; ++i )
					{
						w = Math.max(w,fm.stringWidth(text[i]));
					}
					
					return new Dimension(w+xOffset,h);
				}
				
				public Dimension getPreferredSize()
				{
					return getMinimumSize();
				}
				
				public void paint( Graphics g )
				{
					g.setColor( Color.black );
					g.setFont( getFont() );
					for( int i=0; i<text.length; ++i )
					{
						int y = i*fm.getHeight()+fm.getAscent()-1;
						g.drawString(text[i],xOffset,y);
					}
				}
			}
		}
		
		class DisplayControl extends TitledPanel
		{
			public DisplayControl()
			{
				super("Display");
		
				GridBagConstraints gbc;
				
				gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.anchor = GridBagConstraints.NORTHWEST;
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				forceVectors = new Propertybox("Force Vectors",GraphInfo.FORCE,group);
				electricFields = new Propertybox("Electric Field Lines",GraphInfo.FIELD,group);
				equipotentials = new Propertybox("Equipotentials",GraphInfo.POTENTIAL,group);

				if( info.force ) add( forceVectors , gbc );
				if( info.field ) add( electricFields, gbc );
				if( info.potential )
				{
					add( equipotentials, gbc );
				}
			}
		}
	}
}
