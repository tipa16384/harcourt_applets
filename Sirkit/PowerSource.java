import java.awt.event.*;
import java.awt.*;

public class PowerSource extends Element
{
	final static boolean debug = false;
	
	public	int	wSize = 56;		//overall width of element (voltmeter+resistor+setterbox)
	public	int hSize = 60;			//overall height of element (voltmeter+resistor+setterbox)
	int	rSize = 30;					//width of resistor drawlines

	//final InvisiPanel myPanel;
		
	double current = 50.0;	// amps?
	
	int thickness = 1;
	Color battColor = new Color(0,102,255);
	Color voltsBackground = Color.lightGray;

	double voltage = 9.0;	// volts
	int		defselect = 3;			//make sure that default choice selected equals default ohms
	// in Volts
	public SophieChoice powerChoice;
	static final String [] powerList =
		{
			"1.5V",
			"3 V ",
			"6 V ",
			"9 V ",
			"12 V"
		};
	
	public PowerSource()
	{
		super( new FixedLayoutManager() );

		inpoint = new Point( 0, hSize/2 );

		powerChoice = new SophieChoice();

		for( int i=0; i<powerList.length; ++i )
			powerChoice.add( powerList[i] );
		powerChoice.select( defselect );
		
		powerChoice.addItemListener( new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if( e.getStateChange() == ItemEvent.SELECTED )
					{
						double d = Utility.parseDouble(powerList[powerChoice.getSelectedIndex()] );
						setVoltage( d );
					}
				}
			} );

		Dimension size = powerChoice.getMinimumSize();
		if( wSize < size.width )
			wSize = size.width;
		outpoint = new Point( wSize, hSize/2 );

		add( powerChoice, new Rectangle(0,hSize-size.height,wSize,size.height) );		//, BorderLayout.SOUTH
		powerChoice.setShowLabel(false);
		powerChoice.setLabelOnTop(false);
	}
	
	public void paint( Graphics g )
	{
		Rectangle r;
		int	pGap = 3;

		//if( ampmeter.isVisible() )
		//	ampmeter.paint( g );
		
		g.setColor( battColor );
		//g.setColor( Color.blue );
		if( inpoint.y != outpoint.y )
			debug("inpoint.y ("+inpoint.y+") doesn't match outpoint.y ("+outpoint.y);

		int xpoints[] = new int[4];
		int ypoints[] = new int[4];

		xpoints[0] = outpoint.x;
		ypoints[0] = outpoint.y;
		xpoints[1] = (wSize / 2) + pGap;
		ypoints[1] = outpoint.y;
		xpoints[2] = (wSize / 2) + pGap;
		ypoints[2] = outpoint.y - 8;
		xpoints[3] = (wSize / 2) + pGap;
		ypoints[3] = outpoint.y + 10;
		for( int i=0; i < thickness ; i++ )
		{							//make element lines thicker
			g.drawPolyline( xpoints, ypoints, 4 );
			debug("drew "+xpoints[0]+","+ypoints[0]+" "+xpoints[1]+","+ypoints[1] );
			xpoints[1] += 1;
			xpoints[2] += 1;
			xpoints[3] += 1;
			ypoints[0] += 1;
			ypoints[1] += 1;
		}

		xpoints[0] = inpoint.x;
		ypoints[0] = inpoint.y;
		xpoints[1] = (wSize / 2) - pGap;
		ypoints[1] = inpoint.y;
		xpoints[2] = (wSize / 2) - pGap;
		ypoints[2] = inpoint.y - 4;
		xpoints[3] = (wSize / 2) - pGap;
		ypoints[3] = inpoint.y + 6;
		for( int i=0; i < thickness ; i++ )
		{							//make element lines thicker
			g.drawPolyline( xpoints, ypoints, 4 );
			xpoints[1] += 1;
			xpoints[2] += 1;
			xpoints[3] += 1;
			ypoints[0] += 1;
			ypoints[1] += 1;
		}

					//put a little plus sign on the battery
		Font oldfon = g.getFont();				//save old font
		Font fon = new Font("Serif",Font.PLAIN,10);
		g.setFont( fon );
		g.drawString( "+", ((wSize / 3) * 2), (inpoint.y - 6) );
		g.setFont( oldfon );						//restore old font
		
		Dimension size = powerChoice.getMinimumSize();
		powerChoice.paint( g, 0, hSize - size.height );

/*		g.setColor( voltsBackground );
		int yy = hSize;
		g.fillRoundRect( (outpoint.x - rSize -4), (yy - 13), (rSize +2), 13, 4, 4 );
		g.setColor( Color.black );
		g.drawRoundRect( (outpoint.x - rSize -4), (yy - 13), (rSize +2), 13, 4, 4 );

		oldfon = g.getFont();				//save old font
		fon = new Font("Serif",Font.PLAIN,9);
		g.setFont( fon );
		g.drawString( Double.toString(voltage), (outpoint.x - rSize), yy-2 );
		g.setFont( oldfon );						//restore old font
*/
		//super.paint( g );

		debug("paint() done inpoint.y ("+inpoint.y+") to outpoint.y ("+outpoint.y);
	}

	public Dimension getMinimumSize()
	{
		return new Dimension( wSize, hSize );
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}

	String getImageName()
	{
		return "";		//"power.gif";
	}

	//public Panel getPanel()
	//{
	//	return( myPanel );
	//}
	
	public double getVoltage()
	{
		return voltage;
	}
	
	public void setVoltage( double v )
	{
		voltage = v;
		//retrace();
	}
	
	public double getPhase( long tau )
	{
		return 0.0;
	}

	public String getCheckboxName()
	{
		return "EMF";
	}

	public double getValue( int sel, double t0 )
	{
		double secs = toSeconds(t0);
		
		debug("getValue("+sel+", "+t0+")");
		switch( sel )
		{
			case CircuitElement.VOLTAGE:
				// this returns the EMF value
				return voltage;		// * Math.sin(getAngle()*secs);
			
			case CircuitElement.CURRENT:
				// this returns the EMF value
				return current;
			
			case CircuitElement.INDUCTANCE:
				// no such thing? meaningless?
				return 0.0;
		}
		
		return 0.0;
	}
	
	public double getCurrent()
	{
		return current;
	}
	
	public void setCurrent( double I )
	{
		//System.out.println("PowerSource.setcurrent("+I+")");
		current = I;
		retrace();
	}

	public Color getColor()
	{
		return battColor;
	}

	public void setColor( Color c )
	{
		battColor = c;
	}

	static void debug( String s )
	{
		if( debug )
			System.out.println("PowerSource:: "+s);
	}
}
