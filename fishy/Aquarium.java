import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Aquarium extends TitledPanel
{
	GraphInfo info;
	Fishy fishy;
	Tank tank;
	Button fillTank;
	Label roundLabel;
	Random rand;

	final int numFish = 10;
		
	final double vperspective = 0.1;
	final double hperspective = 0.1;
	
	final double tankWidth = 1000;
	final double tankHeight = 600;
	final double tankDepth = 600;
	
	double level = 0.0;		// level/1.0 is full
	double fillGap = 0.8;	// how full is full?
	
	Vector fish = new Vector();
	
	int roundNum = 0;
	
	public Aquarium( Fishy fishy, GraphInfo info )
	{
		super( "Step 1: Fill Tank", new BorderLayout() );
		
		setBackground( info.CONTROL_COLOR );
		
		this.info = info;
		this.fishy = fishy;
		
		rand = new Random();

		fillTank = new Button("Fill Tank");
		roundLabel = new Label("Round 1",Label.RIGHT);
		tank = new Tank();
		
		fillTank.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					fillTank();
				}
			} );
				
		add( tank, BorderLayout.CENTER );
		
		Panel p = new Panel();
		p.add( fillTank );
		p.add( roundLabel );
		add( p, BorderLayout.NORTH );
	}
	
	public Insets getInsets()
	{
		Insets insets = super.getInsets();
		
		insets.top += 5;
		insets.left += 5;
		insets.right += 5;
		insets.bottom += 5;
		
		return insets;
	}

	public Vector getFish()
	{
		Vector v = new Vector();

		if( fish != null )
		{
			int len = fish.size();
			
			for( int i=0; i<len; ++i )
				v.addElement( ((SwimmingFish)fish.elementAt(i)).getFish() );
		}
				
		return v;
	}
	
	class SwimmingFish
	{
		Fish fishy;

		double x1, y1, z1;
		double x2, y2, z2;
		long circuitTime;
		long startTime;
		
		public SwimmingFish()
		{
			fishy = new Fish();
			x1 = rand.nextDouble()*tankWidth;
			y1 = rand.nextDouble()*tankDepth;
			z1 = rand.nextDouble()*tankHeight*fillGap;
			x2 = rand.nextDouble()*tankWidth;
			y2 = rand.nextDouble()*tankDepth;
			z2 = rand.nextDouble()*tankHeight*fillGap;
			circuitTime = 10000L + Math.abs(rand.nextLong()) % 5000L;
			startTime = System.currentTimeMillis();
		}
		
		public Point getLoc()
		{
			long ctime = (System.currentTimeMillis()-startTime) % (circuitTime*2);
			double complete;
			
			complete = (Math.sin(Math.PI*(double)ctime/(double)circuitTime)+1.0)/2.0;
			
			return flatten( x1+complete*(x2-x1),
							y1+complete*(y2-y1),
							z1+complete*(z2-z1) );
		}
		
		public Fish getFish()
		{
			return fishy;
		}
	}
	
	void addFish()
	{
		fish.removeAllElements();
		
		for( int i=0; i<numFish; ++i )
		{
			fish.addElement( new SwimmingFish() );
		}
		
		fishy.addFish();
		
		tank.repaint();
	}
	
	Point flatten( double x, double y, double z )
	{
		Dimension dim = tank.getSize();
		
		double dw = (double) (dim.width-1);
		double dh = (double) (dim.height-1);
		double dhp, dwp;
		double dht, dwt;
		
		dwt = dw*hperspective;
		dwp = dw-dwt;
		dht = dh*vperspective;
		dhp = dh-dht;
		
		double xx = (dwp*x)/tankWidth;
		double yy = (dhp*(tankHeight-z))/tankHeight;
		
		xx += ((tankDepth-y)*dwt)/tankDepth;
		yy += ((tankDepth-y)*dht)/tankDepth;
		
		return new Point((int)xx,(int)yy);
	}
	
	void addPoint( Polygon poly, double x, double y, double z )
	{
		Point p = flatten(x,y,z);
		poly.addPoint( p.x, p.y );
	}
	
	void fillTank()
	{
		if( true || (getLevel() == 0.0) )
		{
			roundLabel.setText("Round "+(++roundNum));
			Container par = roundLabel.getParent();
			par.invalidate();
			par.validate();
			Thread t = new FillErUp();
			t.start();
		}
	}
	
	class FillErUp extends Thread
	{
		public void run()
		{
			System.out.println("Filling the tank...");
			
			fishy.setMessage("Glub glub glub...");
			
			setLevel( 0.0 );
			
			fish.removeAllElements();
			fishy.removeFish();
			
			try
			{
				final int totalTime = 1000;
				final int finalVal = 1000;
				final int delay = 50;
				
				long btime = System.currentTimeMillis();
				long elapsedTime;
				
				do
				{
					elapsedTime = (System.currentTimeMillis()-btime);
					elapsedTime = Math.min(elapsedTime,totalTime);
					
					setLevel( ((double)elapsedTime)/((double)totalTime) );
					Thread.sleep( delay );
					Thread.yield();
				} while( elapsedTime < totalTime );
			}
			
			catch( Exception e )
			{
			}
			
			setLevel(1.0);
			
			System.out.println("Tank filled");
			
			addFish();

			fishy.setMessage(Fishy.step2);
		}
	}

	double getLevel()
	{
		return level;
	}
	
	void setLevel( double l )
	{
		level = fillGap*l;
		tank.repaint();
	}
	
	class Tank extends Component
			   implements Runnable
	{
		final Color tankEdge = Color.black;
		final Color tankFloor = Color.black;
		final Color tankWater = new Color(0x66,0x99,0xCC);
		
		public Tank()
		{
			Thread t = new Thread(this);
			t.start();
		}
		
		public void run()
		{
			final long delay = 100;
			
			try
			{
				for(;;)
				{
					Thread.sleep(delay);
					Thread.yield();
					Tank.this.repaint();
				}
			}
			
			catch( Exception e )
			{
			}
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			
			--dim.width;
			--dim.height;
			
			int tvper = (int)((double)dim.height * vperspective);
			int theight = dim.height-tvper;
			
			int thper = (int)((double)dim.width * hperspective);
			int twidth = dim.width-thper;

			double wh = level * tankHeight;
						
			if( level > 0.0 )
			{
				g.setColor( tankWater );

				Polygon poly = new Polygon();
				
				addPoint( poly, 0, 0, 0 );
				addPoint( poly, 0, tankDepth, 0 );
				addPoint( poly, 0, tankDepth, wh );
				addPoint( poly, tankWidth, tankDepth, wh );
				addPoint( poly, tankWidth, 0, wh );
				addPoint( poly, tankWidth, 0, 0 );
				
				g.fillPolygon( poly );
			}
			
			{
				Polygon poly = new Polygon();
				
				addPoint( poly, 0, 0, 0 );
				addPoint( poly, 0, tankDepth, 0 );
				addPoint( poly, tankWidth, tankDepth, 0 );
				addPoint( poly, tankWidth, 0, 0 );
				
				g.setColor( tankFloor );
				g.fillPolygon( poly );
			}
			
			{
				int len = fish.size();
				final int fishSize = 5;
				
				for( int i=0; i<len; ++i )
				{
					SwimmingFish f = (SwimmingFish) fish.elementAt(i);
					
					if( fishy.getMessage().equals(Fishy.winna) ||
						fishy.getMessage().equals(Fishy.losa) )
						g.setColor( f.getFish().getBGColor() );
					else
						g.setColor( Color.white );
					
					Point p = f.getLoc();
					
					g.fillOval( p.x-fishSize/2,
								p.y-fishSize/2,
								fishSize, fishSize );
				}
			}
			
			if( level > 0.0 )
			{
				g.setColor( tankEdge );
				
				Polygon poly = new Polygon();

				addPoint( poly, 0, 0, wh );
				addPoint( poly, 0, tankDepth, wh );
				addPoint( poly, tankWidth, tankDepth, wh );
				addPoint( poly, tankWidth, 0, wh );
				
				g.drawPolygon( poly );
			}
			
			g.setColor( tankEdge );
			
			{
				Point p1, p2;
				Point p3, p4;
				
				p1 = flatten( 0, tankDepth, tankHeight );
				p2 = flatten( tankWidth, tankDepth, 0 );
				
				p3 = flatten( 0, 0, tankHeight );
				p4 = flatten( tankWidth, 0, 0 );
				
				g.drawRect( p1.x, p1.y, p2.x-p1.x, p2.y-p1.y );
				g.drawRect( p3.x, p3.y, p4.x-p3.x, p4.y-p3.y );
				
				g.drawLine( p1.x, p1.y, p3.x, p3.y );
				g.drawLine( p2.x, p2.y, p4.x, p4.y );
				g.drawLine( p1.x, p2.y, p3.x, p4.y );
				g.drawLine( p2.x, p1.y, p4.x, p3.y );
			}
		}
		
		public Dimension getPreferredSize()
		{
			final double wid = 150;
			return new Dimension((int)wid,(int)(wid*tankHeight/tankWidth));
		}
	}
}
