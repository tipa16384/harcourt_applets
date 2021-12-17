import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;

public class TheoryScreen extends DoubleBufferPanel
{
	Main main;
	GraphInfo info;
	
	final static int iCol1Form1 = 0;
	final static int iCol1Form2 = 1;
	
	static final String [] imageFiles =
		{
			"col1form2.gif",
			"col1form1.gif",
		};
	
	Image [] images = new Image[imageFiles.length];
	Graph graph;
	final Color userColor = new Color(0,204,0);
	boolean hideColorMark = false;
	Formula secondFormula = null;

	DFunction dfunct=null, dsecnt=null;
		
	public TheoryScreen( Main main, GraphInfo info )
	{
		super( new BorderLayout(5,5) );
		
		this.main = main;
		this.info = info;

		setBackground( new Color(204,204,255) );
				
		add( new Controls(), BorderLayout.EAST );
		
		graph = new Graph("Distance", "Time",
							3, 18,
							0, 0,
							1, 1);
			
		graph.setRenderBounds( 0, 18 );
		graph.addMouseListener( new MiniMousey() );
		graph.addMouseMotionListener( new MiniMotoMousey() );
		graph.setBackground( info.CONTROL_COLOR );

		add( graph, BorderLayout.CENTER );
		
		info.addPropertyChangeListener( new ButtonPressed() );
		
		add( new TheoryMessage(), BorderLayout.SOUTH );

		add( new Title("The Mean Value Theorem"), BorderLayout.NORTH );
	}
	
	class TheoryMessage extends Message
	{
		public TheoryMessage()
		{
			setString("Click here to return to The Tortoise and the Hare.");
			enableEvents( AWTEvent.MOUSE_EVENT_MASK );	
		}
	
		protected void processMouseEvent( MouseEvent e )
		{
			if( e.getID() == MouseEvent.MOUSE_CLICKED )
				info.firePropertyChange(info.part_one,2.0,16.0);
		}
	}
	
	class MiniMousey extends MouseAdapter
	{
		public void mousePressed( MouseEvent e )
		{
			updateFormula(e.getX());
			hideColorMark = true;
		}

		public void mouseReleased( MouseEvent e )
		{
			hideColorMark = false;
			updateFormula(e.getX());
		}
	}
	
	class MiniMotoMousey extends MouseMotionAdapter
	{
		public void mouseDragged( MouseEvent e )
		{
			updateFormula(e.getX());
		}
	}
	
	void updateFormula( int ix )
	{
		double x = graph.getX(ix);
		
		graph.removeFunction(2);
		
		if( dfunct != null && dsecnt != null )
		{
			Color color;
			double maindv = dfunct.derivative(x);
			double scntdv = dsecnt.derivative(x);
			
			if( !hideColorMark && Math.abs(scntdv-maindv) < 0.01 )
			{
				color = userColor;
				secondFormula.makeActive(true);
			}
			else
				color = Color.black;
			
			graph.addFunction( new UserFunc(x,dfunct.value(x),maindv), color, null );
		}
	}
	
	class ButtonPressed implements PropertyChangeListener
	{
		public void propertyChange( PropertyChangeEvent pce )
		{
			String prop = pce.getPropertyName();
			boolean newfunc = false;
			String funcLabel = "";
			
			if( prop.equals(info.func_one) )
			{
				dfunct = new HareFunc();
				dsecnt = new HareSecant();
				funcLabel = "f(t)";
				graph.setXMinMax(0,18);
				newfunc = true;
			}
			
			if( prop.equals(info.func_two) )
			{
				dfunct = new WyrdFunc();
				dsecnt = new WyrdSecant();
				funcLabel = "g(t)";
				graph.setXMinMax(0,2);
				newfunc = true;
			}

			if( newfunc )
			{
				graph.removeAllFunctions();
				graph.addFunction( dfunct, getForeground(), funcLabel );
				graph.addFunction( dsecnt, info.FIELD_COLOR, "Secant Line" );
				graph.setTicks(1,1);
			}
		}
	}
	
	interface DFunction extends Function
	{
		public double derivative( double x );
	}
	
	class UserFunc implements Function
	{
		double x0, y0, slope;
		
		public UserFunc( double x0, double y0, double slope )
		{
			this.x0 = x0;
			this.y0 = y0;
			this.slope = slope;
		}
		
		public double value( double x )
		{
			return y0 + (x-x0) * slope;
		}
	}
	
	class HareSecant implements DFunction
	{
		public double value( double x )
		{
			return x/9.0;
		}

		public double derivative( double x )
		{
			return 1.0/9.0;
		}
	}
	
	class WyrdSecant implements DFunction
	{
		public double value( double x )
		{
			return 0.0;
		}

		public double derivative( double x )
		{
			return 0.0;
		}
	}
	
	class HareFunc implements DFunction
	{
		public double value( double x )
		{
			if( x >= 0.0 && x < 3.0 )
				return Math.pow(x/3-1,3) + 1;
			else if( x >= 3.0 && x < 15.0 )
				return 1.0;
			else
				return Math.pow(x/3-5,3) + 1;
		}
		
		public double derivative( double x )
		{
			if( x >= 0.0 && x < 3.0 )
				return (x*x/9.-2./3.*x+1.);
			else if( x >= 3.0 && x < 15.0 )
				return 0.0;
			else
				return (x*x-30.*x+225.0)/9.0;
		}
	}
	
	class WyrdFunc implements DFunction
	{
		public double value( double x )
		{
			return 1.0 - Math.sqrt(Math.abs(x-1.0));
		}
		
		public double derivative( double x )
		{
			final double dx = 0.0001;
			return (value(x+dx)-value(x))/dx;
		}
	}

	public Insets getInsets()
	{
		return new Insets(5,5,0,5);
	}
	
	class Formula extends Panel
	{
		String name;
		FakeButton fakir=null;
		int img1idx, img2idx;
		
		public Formula( String name, String action, String inaction, int img1idx )
		{
			super( new BorderLayout(5,5) );
			
			setBackground( info.CONTROL_COLOR );

			this.name = name;
			this.img1idx = img1idx;
			this.img2idx = img2idx;

			if( name != null )
			{
				fakir = new FakeButton(name,action,inaction);
				add( fakir, BorderLayout.NORTH );
			}

			{
				Panel p1 = new Panel( new BorderLayout() );	
				p1.add( new Imager(images[img1idx]), BorderLayout.NORTH );
				
				add( p1, BorderLayout.CENTER );
			}
		}

/*		public Insets getInsets()
		{
			return new Insets(5,5,5,5);
 		}
 */
		public void makeActive( boolean activate )
		{
			if( fakir != null )
			{
				fakir.highlight(activate);
				//this.setVisible(activate);
				//TheoryScreen.this.invalidate();
				//TheoryScreen.this.validate();
				repaint();
			}
		}
	}
	
	class FakeButton extends Component
					 implements PropertyChangeListener
	{
		final int gap = 5;
		
		String action;
		String inaction;
		boolean active = false;
		boolean grayed = false;
		
		final Color offColor = new Color(204,153,104);
		final Color onColor = new Color(255,204,153);
		
		public FakeButton( String name, String action, String inaction )
		{
			setName( name );
			this.action = action;
			this.inaction = inaction;
			
			setFont( info.fontBiggerBold );
			setForeground( Color.black );
			setBackground( offColor );
			
			if( action != null )
			{
				enableEvents( AWTEvent.MOUSE_EVENT_MASK );
				info.addPropertyChangeListener( FakeButton.this );
			}
		}
		
		public void highlight( boolean ungrayed )
		{
			this.grayed = !ungrayed;
			repaint();
		}
		
	    protected void processMouseEvent(MouseEvent e)
	    {
	    	if( e.getID() == MouseEvent.MOUSE_CLICKED )
	    	{
	    		if( !grayed )
	    		{
		    		//System.out.println(this+" sent "+action);
		    		info.firePropertyChange(action,-1.0,1.0);
				}
	    	}
	    }
		
		public void propertyChange( PropertyChangeEvent pce )
		{
			String prop = pce.getPropertyName();
			
			//System.out.println(this+" received "+prop);
			
			if( prop.equals(action) )
			{
				//System.out.println(this+" activated.");
				active = true;
				repaint();
			}
			
			if( prop.equals(inaction) )
			{
				//System.out.println(this+" deactivated.");
				active = false;
				repaint();
			}
		}
		
		public Dimension getPreferredSize()
		{
			FontMetrics fm = getFontMetrics(getFont());
			
			return new Dimension(fm.stringWidth(getName())+2*gap,
								 fm.getHeight()+2*gap);
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			Dimension sdim = getPreferredSize();
			
			Color color = getBackground();
			Color fgcolor = getForeground();
			
			if( false && grayed )
			{
				//fgcolor = Color.white;
				color = info.AXIS_COLOR;
			}
			
			else if( active )
			{
				color = onColor;
				//fgcolor = Color.white;
			}
				
			g.setColor( color );
			
			int x0 = (dim.width-sdim.width)/2;
			int y0 = (dim.height-sdim.height)/2;
			
			FontMetrics fm = getFontMetrics(getFont());
			g.setFont(getFont());
			
			g.fillRect( 0, 0, dim.width, dim.height );
			
			g.setColor( fgcolor );
			g.drawString( getName(), x0+gap,y0+gap+fm.getAscent() );
		}
	}
	
	class Imager extends Component
	{
		Image image;
		
		public Imager( Image image )
		{
			this.image = image;
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(image.getWidth(this)+10,image.getHeight(this)+10);
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			int w = image.getWidth(this);
			g.drawImage(image,(dim.width-w)/2,0,this);
		}
	}
	
	class Controls extends Panel
	{
		public Controls()
		{
			super( new BorderLayout(5,5) );
			
			//setBackground( info.CONTROL_COLOR );
			
			readImages();
			
			//add( new Formula(null,null,null,iCol1Title,iCol2Title) );
			add( new Formula("First Function, f(t)",info.func_one,info.func_two,iCol1Form1), BorderLayout.CENTER );
			secondFormula = new Formula("Second Function, g(t)",info.func_two,info.func_one,iCol1Form2);
			add( secondFormula, BorderLayout.SOUTH );
			secondFormula.makeActive(false);
		}
		
		void readImages()
		{
			try
			{
				MediaTracker mt = new MediaTracker(this);
				
				for( int i=0; i<imageFiles.length; ++i )
				{
					Image image = Utility.getImage(this,imageFiles[i]);
					images[i] = image;
					mt.addImage(image,0);
				}
				
				mt.waitForAll();
			}
			
			catch( Exception e )
			{
				System.err.println("An error occurred while loading images - "+e);
			}
		}
	}
}
