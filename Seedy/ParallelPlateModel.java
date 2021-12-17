import java.awt.*;
import Wired.DoubleFormat;

public class ParallelPlateModel extends Model
{
	GordySlider dslider=null,
				aslider=null,
				qslider=null;

	RawLabel label;

	final int topJoin = 5;			// separation from top border
	final int bottomJoin = 50;		// separation from bottom border
	final int rightJoin = 20;		// separation from right border
	final int solderRadius = 2;		// radius of the little solder joints
	final int scale = 10;			// scale of separation
	final int thick = 5;			// thickness of plate
	final int ascale = 14;			// scale of radius

	public ParallelPlateModel( String name )
	{
		super(name);
		
		label = new RawLabel();
		label.setFont( new Font("SanSerif", Font.PLAIN, 10 ) );
		label.setForeground( Color.black );
	}
	
	public void addSliders( Container c )
	{
		c.add( dslider = new GordySlider( "~!d", 1, 5, 3, "~mm" ) );
		c.add( aslider = new GordySlider( "~!A", 10, 100, 50, "cm~^2" ) );
		c.add( qslider = new GordySlider( "~!Q", 2, 20, 5, "nC" ) );
		
		super.addSliders( c );
	}
	
	public double value( Component sirkit, int x, int y )
	{
		return shiftPoint( sirkit, x, y ).y;
	}

	public String units()
	{
		return "~mm";
	}

	public String variable()
	{
		return "~!y~!";
	}

	public DPoint shiftPoint( Component c, int x, int y )
	{
		DPoint p = super.shiftPoint( c, x, y );
		p.x /= (double) ascale;				// scaling
		p.y /= (double) scale;				// scaling
		return p;
	}

	public Point centerPoint(Component c)
	{
		Dimension size = c.getSize();
		return new Point(size.width/2,size.height/2-bottomJoin+topJoin+10);
	}

	public Rectangle getClickBounds( Component c )
	{
		Point p0 = centerPoint(c);
				
		final int x0 = p0.x;
		final int y0 = p0.y;
		int sep = dslider.getValue() * scale;
		int sep_2 = sep/2;
		int r = (int) (Math.sqrt((double)aslider.getValue() / Math.PI)+0.5) * ascale;
		int r2 = r*2;
		
		return new Rectangle( x0-r, y0-sep_2, r2, sep );
	}

	public double getEField( Component c, double x, double y, Dielectric d )
	{
		double dk = (d == null) ? 1.0 : d.getConstant();
		return getCharge() / (GraphInfo.e0 * getArea() * dk);
	}

	public void resetValues()
	{
		dslider.setValue( dslider.getMinimum() );
		aslider.setValue( /*aslider.getMaximum()*/ 10 );
		qslider.setValue( qslider.getMinimum() );
	}

	public Point getPopupPoint( Component c )
	{
		return new Point( 3, (2*c.getSize().height)/3 );
	}

	public void drawCircuit( Graphics g, Component c, Dielectric d )
	{
		//System.out.println("g="+g+" c="+c+" d="+d);
		
		final Dimension size = c.getSize();
		int i;
		Point p0 = centerPoint(c);
				
		final int x0 = p0.x;
		final int y0 = p0.y;

		// draw wire down the middle
		
		g.setColor( Color.black );
		g.drawLine( x0, 0, x0, size.height );
		
		// now the test wire
		
		g.drawLine( x0, topJoin, size.width-rightJoin, topJoin );
		g.drawLine( size.width-rightJoin, topJoin, size.width-rightJoin, size.height-bottomJoin );
		g.drawLine( size.width-rightJoin, size.height-bottomJoin, x0, size.height-bottomJoin );
		g.fillOval( x0-solderRadius, topJoin-solderRadius, 2*solderRadius+1, 2*solderRadius+1 );
		g.fillOval( x0-solderRadius, size.height-bottomJoin-solderRadius, 2*solderRadius+1, 2*solderRadius+1 );
		
		int sep = dslider.getValue() * scale;
		int sep_2 = sep/2;
		int r = (int) (Math.sqrt((double)aslider.getValue() / Math.PI) * (double)ascale + 0.5);
		int r2 = r*2;
		
		g.setColor( d.getColor() );
		g.fillRect( x0-r, y0-sep_2, r2, sep );
		
		g.setColor( GraphInfo.POSITIVE_COLOR );
		g.fillRect( x0-r, y0-sep_2-thick, r2, thick );
		
		g.setColor( GraphInfo.NEGATIVE_COLOR );
		g.fillRect( x0-r, y0+sep_2, r2, thick );
		
		// now the field lines
				
		g.setColor( GraphInfo.FIELD_COLOR );

		int fieldsep = getFieldSeparation(d);		// field line separation
		int nlines = (r2-5) / fieldsep;
		int [] xs = new int[4];
		int [] ys = new int[4];	
		int ysize = 8;
		int xsize = 8;

		for( i=0; i<=nlines; ++i )
		{
			int xv = x0 - nlines*fieldsep/2 + i*fieldsep;
			
			g.drawLine( xv, y0-sep_2, xv, y0+sep_2 );
			
			xs[0] = xs[3] = xv;				ys[0] = ys[3] = y0+ysize/2+1;
			xs[1] = xv-xsize/2;				ys[1] = ys[0]-ysize;
			xs[2] = xv+xsize/2;				ys[2] = ys[0]-ysize;
			
			g.fillPolygon( xs, ys, 4 );
		}
		
		// now for the voltmeter
		
		int qv = qslider.getValue();
		int av = aslider.getValue();
		int dv = dslider.getValue();
		double k = d.getConstant();
		
		double V = getV(k);
		
		//System.out.println("Parallel: min="+minV+"V cur="+V+"V max="+maxV+"V");
		
		label.setText( "~!V~! = "+DoubleFormat.format(V) + " V" );

		Dimension lsize = label.getMinimumSize();

		int mw = lsize.width + 10;
		int mh = lsize.height + 4;
		int mx = x0 + 10;
		int my = size.height - bottomJoin - 10;
		
		g.setColor( Color.white );
		g.fillRect( mx, my, mw, mh );
		g.setColor( Color.black );
		g.drawRect( mx, my, mw, mh );
		
		label.paint( g, mx+5, my+2, false );
		
		label.setText("Voltmeter");
		label.paint( g, mx+5, my-mh+3, false );
		
		my += 20 + 2;
		double C = getCapacitance(k);
		label.setText( "~!C~! = "+DoubleFormat.format(C)+" F" );
		label.paint( g, mx+5, my+2, false );
		
		double U = getStoredEnergy(k);
		
		my += lsize.height;
		label.setText( "~!U~! = "+DoubleFormat.format(U)+" J" );
		label.paint( g, mx+5+macBugNudge(), my+2, false );
		
		super.drawCircuit(g,c,d);
	}
	
	// get the separation between field lines, in pixels
	
	private int getFieldSeparation( Dielectric d )
	{
		final double minsep = 50.0;
		final double maxsep = 3.0;
		final double minE = Math.log(2.26E4)/Math.log(2.0);
		final double maxE = Math.log(2.26E6)/Math.log(2.0);
		
		double x = getEField(null,0,0,d);
		
		x = Math.log(x)/Math.log(2.0);
		
		x = ((x-minE)/(maxE-minE)) * (maxsep-minsep) + minsep;
		
		return (int)(x+0.5);
	}
	
	// get charge
	
	public double getCharge()
	{
		return (double)qslider.getValue() * 1E-9;				// nano C to C
	}
	
	// get area
	
	public double getArea()
	{
		return (double)aslider.getValue() / (100.0*100.0);		// cm^2 to m^2
	}
	
	// get separation
	
	public double getSeparation()
	{
		return (double)dslider.getValue() / 1E6;					// micrometers to meters
	}
	
	// get capacitance
	
	public double getCapacitance( double K )
	{
		return (GraphInfo.e0 * getArea() * K) / getSeparation();
	}
	
	// get volts
	
	public double getV( double K )
	{
		double V = (getCharge() * getSeparation()) / (GraphInfo.e0 * getArea() * K);
		
		return V;
	}

	// get energy
	
	public double getStoredEnergy( double K )
	{
		return (getCharge()*getCharge()*getSeparation()) / (2.0*GraphInfo.e0*getArea()*K);
	}
}
