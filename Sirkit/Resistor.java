import java.awt.*;
import java.awt.event.*;

// support for describing a resistor.

public class Resistor extends Element
{
	final static boolean debug = true;
	
	//final Panel myPanel;
	
	static int instancenum = 1;
		
	String name;
	String imageFileName = "resistor.gif";
	
	int thickness = 1;
	Color resistorColor = Color.red;
	Color ohmsBackground = Color.lightGray;

	public	int	wSize = 56;			//overall width of element (voltmeter+resistor+setterbox)
	public	int hSize = 60;			//overall height of element (voltmeter+resistor+setterbox)
	int	rSize = 30;					//width of resistor drawlines
	
	Voltmeter voltmeter;
	
	double 	ohm = 1000.0;
	int		defselect = 2;			//make sure that default choice selected equals default ohms
	// in ohms
	public SophieChoice ohmChoice;
	static final String [] ohmList =
		{
			"500 \u03A9",
			"750 \u03A9",
			"1000 \u03A9",
			"1200 \u03A9",
			"1500 \u03A9",
			"2000 \u03A9"
		};							//or 8486 or 215
	
	public Resistor( String name, String imgfile )
	{
		super( new FixedLayoutManager() );

		setName( name );
		imageFileName = imgfile;

		voltmeter = new Voltmeter( this );
		voltmeter.setVisible( false );

		inpoint = new Point( 0, hSize/2 );

		ohmChoice = new SophieChoice();

		for( int i=0; i<ohmList.length; ++i )
			ohmChoice.add( ohmList[i] );
		ohmChoice.select( defselect );
		
		ohmChoice.addItemListener( new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if( e.getStateChange() == ItemEvent.SELECTED )
					{
						double d = Utility.parseDouble(ohmList[ohmChoice.getSelectedIndex()] );
						setOhm( d );
					}
				}
			} );

		Dimension size = ohmChoice.getMinimumSize();
		if( wSize < size.width )
			wSize = size.width;
		outpoint = new Point( wSize, hSize/2 );

		add( ohmChoice, new Rectangle(0,hSize-size.height,wSize,size.height) );		//, BorderLayout.SOUTH
		ohmChoice.setShowLabel(false);
		ohmChoice.setLabelOnTop(false);

		//phiMult = 0.0;
	}
	
	public Resistor()
	{
		this( "Resistor"+instancenum, "resistor.gif" );
		++instancenum;
	}

	public Dimension getMinimumSize()
	{
		return new Dimension( wSize, hSize );
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}

	

	public void paint( Graphics g )
	{
		Rectangle r;

		if( voltmeter.isVisible() )
			voltmeter.paint( g );
		
		g.setColor( resistorColor );
		if( inpoint.y != outpoint.y )
			debug("inpoint.y ("+inpoint.y+") doesn't match outpoint.y ("+outpoint.y);
		int xpoints[] = new int[10];
		int ypoints[] = new int[10];
		xpoints[0] = outpoint.x;
		ypoints[0] = outpoint.y;
		xpoints[1] = rSize + (wSize-rSize)/2;
		ypoints[1] = outpoint.y;
		int dx = 5;
		int dy = 4;
		for( int i=2; i < 8 ; i++ )
		{
			xpoints[i] = (rSize + (wSize-rSize)/2 + 3) - ((i-1) * dx);
			if( (i % 2) == 0 )			//even?
				ypoints[i] = outpoint.y + dy;
			else
				ypoints[i] = outpoint.y - dy;
		}
		xpoints[8] = (wSize-rSize)/2;
		ypoints[8] = inpoint.y;
		xpoints[9] = inpoint.x;
		ypoints[9] = inpoint.y;
		g.drawPolyline( xpoints, ypoints, 10 );
		for( int i=1 ; i < thickness ; i++ )
		{
			for( int j=0; i < 10 ; i++ )
				ypoints[j] += 1;		//make element lines thicker
			g.drawPolyline( xpoints, ypoints, 10 );
		}

		Dimension size = ohmChoice.getMinimumSize();
		ohmChoice.paint( g, 0, hSize - size.height );

/*		g.setColor( ohmsBackground );
		int yy = hSize -4;
		g.fillRoundRect( (outpoint.x - rSize -4), (yy - 13), (rSize +2), 13, 4, 4 );
		g.setColor( Color.black );
		g.drawRoundRect( (outpoint.x - rSize -4), (yy - 13), (rSize +2), 13, 4, 4 );

		Font oldfon = g.getFont();				//save old font
		Font fon = new Font("Serif",Font.PLAIN,9);
		g.setFont( fon );
		g.drawString( Double.toString(ohm), (outpoint.x - rSize), yy-2 );
		g.setFont( oldfon );						//restore old font
*/
		//super.paint( g );
	}


	//public Panel getPanel()
	//{
	//	return( myPanel );
	//}
	
	public double reactance( double angle )
	{
		return ohm;
	}
	
	public double getValue( int sel, double t0 )
	{
		//debug("getValue("+sel+", "+t0+")");
/*		if( sel == CircuitElement.VOLTAGE )
		{
			if( circuit != null )
			{
				double t = toSeconds(t0);
				double I0 = 1.0; //circuit.getI0(t0);
				double w = 1.0; //circuit.getAngle();
				double phi = 1.0; //circuit.getPhi();
				
				return reactance(w)*I0*Math.sin(w*t-phi);
			}
		}
*/		
		return ohm;
	}
	
	public double getPhase( long tau )
	{
		return 0.0;
	}

	public void setName( String name )
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getImageName()
	{
		if( imageFileName == null )
			imageFileName = "resistor.gif";
		//debug("getImageName() will return: "+imageFileName);
		return( imageFileName );
	}
	
	public void setImageName( String imgfile )
	{
		imageFileName = imgfile;
	}
	
	public Color getColor()
	{
		return resistorColor;
	}

	public String getCheckboxName()
	{
		return "R";
	}
	
	public double getOhm()
	{
		return ohm;
	}
	
	public void setOhm( double ohm )
	{
		this.ohm = ohm;
		//retrace();
	}

	static void debug( String s )
	{
		if( debug )
			System.out.println("Resistor:: "+s);
	}
}
