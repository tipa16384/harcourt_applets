import java.awt.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;

public class Fishy extends DoubleBufferPanel
				   implements PropertyChangeListener
{
	final public static String step1 = "Press the FILL TANK button";
	final public static String step2 = "Choose number of samples and press SAMPLE";
	final public static String step3 = "Enter your predictions and press PREDICT";
	final public static String winna = "Right on! Your predictions were correct! Press FILL TANK.";
	final public static String losa = "Your predictions were incorrect! Press FILL TANK.";
	
	Main main;
	GraphInfo info;
	
	Aquarium aquarium;
	BarGraph barGraph;
	SamplePanel samplePanel;
	PredictionPanel predictionPanel;
	MessageBar messageBar;
	
	Random rand = new Random();
		
	public Fishy( Main main, GraphInfo info )
	{
		super( new BorderLayout(5,5) );
		
		this.main = main;
		this.info = info;

		setBackground( new Color(204,204,255) );
		setFont( info.fontBigPlain );
		
		Panel p1 = new DoubleBufferPanel( new BorderLayout(5,5) );
		Panel p2;
		
		aquarium = new Aquarium(this,info);
		barGraph = new BarGraph(info);
		
		samplePanel = new SamplePanel( this, info );
		predictionPanel = new PredictionPanel(this,info);
		messageBar = new MessageBar();
		messageBar.setMessage( step1 );
		
		p1.add( aquarium, BorderLayout.NORTH );
		p1.add( predictionPanel, BorderLayout.CENTER );
		add( p1, BorderLayout.WEST );
		
		p1 = new BarPanel( new BorderLayout(5,5) );
		p1.setBackground( info.CONTROL_COLOR );
		
		p2 = new Panel( new BorderLayout(5,5) );
		p2.add( new BarLegend( this, info ), BorderLayout.SOUTH );
		p2.add( samplePanel.getChoice(), BorderLayout.NORTH );
		
		p1.add( p2, BorderLayout.NORTH );
		p1.add( barGraph, BorderLayout.CENTER );
		p1.add( samplePanel, BorderLayout.SOUTH );
		add( p1, BorderLayout.CENTER );
		
		add( messageBar, BorderLayout.SOUTH );
	}

	class BarPanel extends TitledPanel
	{
		public BarPanel( LayoutManager l )
		{
			super("Step 2: Sample",l);
		}
		
		public Insets getInsets()
		{
			Insets insets = super.getInsets();
			
			insets.top += 5;
			insets.left += 5;
			insets.right += 5;
			
			return insets;
		}
	}

	public void setMessage( String s )
	{
		messageBar.setMessage(s);
	}
	
	public String getMessage()
	{
		return messageBar.getMessage();
	}

	class MessageBar extends Component
	{
		String msg = "A Test";
		
		public MessageBar()
		{
			setForeground( Color.white );
			setBackground( Color.black );
			setFont( info.fontBigBold );
		}
		
		public void setMessage( String s )
		{
			msg = s;
			repaint();
		}
		
		public String getMessage()
		{
			return msg;
		}
	
		public Dimension getPreferredSize()
		{
			FontMetrics fm = getFontMetrics(getFont());
			int height = fm.getHeight();
			int width = fm.stringWidth(msg);
			return new Dimension( width, height );
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			Font f = getFont();
			FontMetrics fm = getFontMetrics(f);
			int width = fm.stringWidth(msg);
			
			g.setColor( getBackground() );
			g.fillRect( 0, 0, dim.width-1, dim.height-1 );
			g.setColor( getForeground() );
			g.setFont( f );
			
			g.drawString( msg, (dim.width-width)/2, fm.getAscent()-1 );
		}
	}

	public void removeFish()
	{
		//System.out.println("Fishy.removeFish()");
		barGraph.clear();
		predictionPanel.clear();
	}

	public void addFish()
	{
		removeFish();
	}
	
	public int [] getFishCounts()
	{
		int counts[] = new int[Fish.getNumTypes()];
		Vector fish = aquarium.getFish();
		int fishlen = fish.size();
		
		for( int i=0; i<fishlen; ++i )
		{
			Fish f = (Fish) fish.elementAt(i);
			counts[f.getType()]++;
		}

		return counts;
	}
	
	public void sample( int numSamples )
	{
		removeFish();
		
		Vector vfish = getFish();
		int len = vfish.size();
		
		if( len > 0 )
		{
			int counts[] = new int[Fish.getNumTypes()];
			int i;
			
			for( i=0; i<numSamples; ++i )
			{
				Fish fish = (Fish) vfish.elementAt(Math.abs(rand.nextInt()) % len);
				counts[fish.getType()]++;
			}
			
			barGraph.setMaxVal( numSamples );
			barGraph.setTick( (numSamples%10==0) ? numSamples/10 : numSamples/5 );
			
			for( i=0; i<counts.length; ++i )
			{
				String name = Fish.getName(i);
				Color color = Fish.getColor(i);
				int val = counts[i];
				Bar bar = new Bar( name, (double)val, color );
				barGraph.addBar( bar );
			}
		}
		
		setMessage(step3);
	}

	public Vector getFish()
	{
		return aquarium.getFish();
	}
	
	public Insets getInsets()
	{
		return new Insets(5,5,5,5);
	}
	
	public void propertyChange( PropertyChangeEvent pce )
	{
	}
}
