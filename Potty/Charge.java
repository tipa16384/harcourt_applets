import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class Charge extends Component implements GraphElement, Runnable
{
	private double charge;
	DPoint origin;
	GraphInfo info;
	boolean fixed;
	boolean test;
	private int radius;
	GraphPaper parent = null;
	
	public Charge(GraphInfo inf)
	{
		this(inf,-1.0);
	}
	
	public Charge( GraphInfo inf, double charge )
	{
		this.charge = charge;
		this.info = inf;
		radius = 1;
		origin = new DPoint(0,0);
		fixed = false;
		test = false;
		setSize(GraphInfo.CANVASSIZE,GraphInfo.CANVASSIZE);
	}

	public void setCharge( double charge )
	{
		this.charge = charge;
		info.trigger();
	}
	
	public double getCharge()
	{
		return charge;
	}
	
	public void calcForce( double scale )
	{
	}
	
	public void calcCharge( double scale )
	{
		if( !info.PE2 )
		{
			Thread t = new Thread(this,"field");
			t.start();
			//drawField();
		}
	}
	
	static public Fector calcCharge( Component [] clist, double scale, DPoint O, Charge ignore, double testCharge )
	{
		final int len = clist.length;
		Fector fec = new Fector();
		final boolean test = (ignore==null);
		Fector fecless = new Fector();
		DPoint p1 = null;
		
		for( int i=0; i<len; ++i )
		{
			Component c = clist[i];
			
			if( !(c instanceof Charge) )
				continue;
			
			Charge ch = (Charge) c;
			
			if( ignore == ch )
				continue;
				
			double factor = ch.info.scalingFactor;
			p1 = ch.getOrigin(p1);
			double forceCharge = Math.abs(testCharge) * Math.abs(ch.charge);
			
			double rx = (O.x-p1.x)*scale;
			double ry = (O.y-p1.y)*scale;
			double rz = (O.z-p1.z)*scale;
			
			double r2 = (rx*rx+ry*ry);
			
			fecless.setEndpoint(rx,ry,rz);
			double flen = fecless.getLength();
			fecless.setLength((ch.info.Klunk*flen*forceCharge*factor)/(r2*flen));

			if( (testCharge > 0.0 && ch.charge < 0.0) ||
				(testCharge < 0.0 && ch.charge > 0.0) )
			{
				fec.subtract(fecless);
			}
			
			else
			{
				fec.add(fecless);
			}
			
			//System.out.println("fecless is "+fecless);
		}

		return fec;		
	}
	
	static public double calcPotential( Component [] clist, DPoint O )
	{
		final int len = clist.length;		
		double potential = 0.0;
		DPoint p1 = null;
		
		//System.out.println("calcPotential with "+len+" components");
		for( int i=0; i<len; ++i )
		{
			Component c = clist[i];
			
			if( !(c instanceof Charge) )
			{
				System.out.println(c+" wasn't a charge!");
				continue;
			}
			
			Charge ch = (Charge) c;
			
			p1 = ch.getOrigin(p1);

			double rx = (O.x-p1.x);
			double ry = (O.y-p1.y);
			double rz = (O.z-p1.z);
			
			double r2 = rx*rx+ry*ry+rz*rz;
			if( r2 != 0 )
			{
				potential += (ch.charge)/Math.sqrt(r2);
				
				if( Double.isNaN(potential) )
				{
					System.out.println("****** Not a Number ******");
					System.out.println("index:"+i);
					System.out.println("charge:"+ch.charge);
					System.out.println("radius-Squared:"+r2);
					System.out.println("O:"+O+" p1:"+p1);
					System.out.println("rx:"+rx+" ry:"+ry+" rz:"+rz);
					break;
				}
			}
		}

		//System.out.println("Potential was "+potential);

		return potential;		
	}
	
	public void setFixed( boolean errr )
	{
		fixed = errr;
	}
	
	public boolean getFixed()
	{
		return fixed;
	}
	
	public DPoint getOrigin()
	{
		return new DPoint(origin);
	}
	
	public DPoint getOrigin( DPoint d )
	{
		if( d == null )
			d = new DPoint(origin);
		else
			d.copyFrom(origin);

		return d;
	}
	
	public void setOrigin( DPoint d )
	{
		origin = d;
	}
	
	public void setRadius( int radius )
	{
		this.radius = radius;
		info.trigger();
		//repaint();
	}

	public int getRadius()
	{
		return radius;
	}

	public Color getForeground()
	{
		Color col;
		
		if( charge < 0 )
			col = GraphInfo.NEGATIVE_COLOR;
		else if( charge == 0.0 )
			col = Color.gray;
		else
			col = GraphInfo.POSITIVE_COLOR;
		
		return col;
	}
	
	public void run()
	{
		Component [] clist = parent.getComponents();
		int ns=0, ps=0;
		
		int pcnt = 0;
		for( int i=0; i<clist.length; ++i )
		{
			Component c = clist[i];
			if( c instanceof Charge )
			{
				Charge ch = (Charge) c;
				if( ch.getCharge() > 0.0 )
					++ps;
				else
					++ns;
			}
		}
		
		if( ((ns >  ps) && (charge > 0)) ||
			((ns <= ps) && (charge < 0)) )
			return;
		
		info.addThread(Thread.currentThread());

		final double pi2 = Math.PI*2.0;
		final double curlines = 10;
		final double inc = pi2/((double)curlines);
		
		for( double theta = 0.0; theta < pi2; theta += inc )
		{
			if( ns > ps )
				drawFieldLine( new Fector(theta), clist, true );
			else
				drawFieldLine( new Fector(theta), clist, false );
		}

		info.removeThread(Thread.currentThread());
	}

	public Point getPOrigin()
	{
		Dimension size = getSize();
		double scale = 1.0;
		Component c;
		GraphPaper paper;
		
		c = parent;
		if( c != null && c instanceof GraphPaper )
		{
			paper = (GraphPaper) c;
			scale = paper.getScale();
		}
		
		Point p = getOrigin().toPoint(scale);
		p.translate( size.width/2, size.height/2 );
		
		return p;
	}
	
	public void setPOrigin( Point p )
	{
		Dimension size = getSize();
		double scale = 1.0;
		Component c;
		GraphPaper paper;
		
		c = parent;
		if( c != null && c instanceof GraphPaper )
		{
			paper = (GraphPaper) c;
			scale = paper.getScale();
		}
		
		p.translate( -size.width/2, -size.height/2 );
		setOrigin( new DPoint( p, scale ) );
	}
	
	private void drawFieldLine( Fector f, Component [] clist, boolean reverse )
	{
		DPoint o = getOrigin();
		DPoint src = new DPoint();
		double scale = 1.0;
		Component c;
		final double flen = 4.0;
		GraphPaper paper = null;
		
		c = parent;
		if( c != null && c instanceof GraphPaper )
		{
			paper = (GraphPaper) c;
			scale = paper.getScale();
		}
		
		Point p0 = getPOrigin();
		int xp=p0.x, yp=p0.y;
		
		Vector v = new Vector();
		boolean tripped = false;
		
		for( int i=0;; ++i )
		{
			f.setLength( flen );
			DPoint fe = f.getEndpoint();
			o.x += fe.x;
			o.y += fe.y;
			o.z += fe.z;
			src.x += fe.x;
			src.y += fe.y;
			src.z += fe.z;
			
			DPoint od = new DPoint(o);
			if( !tripped && (f.potential > 0) )
			{
				tripped = true;
				od.special = reverse ? 2 : 1;
			}
			v.addElement( od );
			
			int x1 = p0.x + (int)(src.x*scale+0.5);
			int y1 = p0.y + (int)(src.y*scale+0.5);
			
			xp = x1; yp = y1;
			
			f = calcCharge( clist, scale, o, null, reverse ? -1.0 : 1.0 );
			
			if( i > 500 || closeTo(o,flen,clist) ) break;
		}
		
		//System.out.println(" potential was "+f.potential);
		
		if( paper != null )
			paper.plotCurve(v, GraphInfo.FIELD_COLOR );
	}
	
	private boolean closeTo( DPoint d, double closeness, Component [] clist )
	{
		int len = clist.length;
		DPoint dog = null;
		
		for( int i=0; i<len; ++i )
		{
			Component c = clist[i];
			if( c instanceof Charge && (Charge)c != this )
			{
				Charge ch = (Charge) c;
				dog = ch.getOrigin(dog);
				if( Math.abs(d.x-dog.x) <= closeness &&
					Math.abs(d.y-dog.y) <= closeness )
					return true;
			}
		}
		
		return false;
	}
	
	public int getDrawSize()
	{
		int d = GraphInfo.DOTSIZE * getRadius();
		if( test ) d /= 2;
		return d;
	}
	
	public void paint( Graphics g )
	{
		final Point p0 = getPOrigin();
		final int x0 = p0.x;
		final int y0 = p0.y;
		final int diameter = getDrawSize();
		final int rad = diameter/2;
		
		if( info.charge == this )
		{
			int rad2 = rad+3;
			g.setColor( GraphInfo.HIGHLIGHT_COLOR );
			g.fillOval( x0-rad2, y0-rad2, rad2*2, rad2*2 );
		}

		g.setColor( getForeground() );
		g.fillOval(x0-rad,y0-rad,diameter,diameter);
		
		if( charge != 0.0 )
		{
			g.setColor( Color.white );
			
			final int ln = 2;
	
			g.fillRect(x0-rad/2,y0-ln/2,rad,ln);
			
			if( charge > 0.0 )
			{
				g.fillRect(x0-ln/2,y0-rad/2,ln,rad);
			}
		}
	}
}
