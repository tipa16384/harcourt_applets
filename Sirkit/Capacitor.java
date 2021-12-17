import java.awt.*;
import java.awt.event.*;


// support for describing a resistor.

public class Capacitor extends Element implements ActionListener
{
	final static boolean debug = false;
	
	int thickness = 1;

	Color capColor = new Color(102,102,102);
	Color faradBackground = Color.lightGray;
	int	choiceSize = 30;					//width of farad chooser box

	public	int	wSize = 56;			//overall width of element (voltmeter+resistor+setterbox)
	public	int hSize = 70;			//height of Capacitor art, will have sophieChoice height added


	double farad = 5.0 * 1E-3;
	int		defselect = 1;			//make sure that default choice selected equals default ohms
	
	// in millifarads - mult is 1E-3
	public SophieChoice faradChoice;
	static final String [] faradList =
		{
			"1 mF",
			"5 mF",
			"7.5mF",
			"10 mF",
			"25 mF",
			"30 mF"
		};
	RawLabel faradLabel;

	public Capacitor()
	{
		super( new FixedLayoutManager() );

		inpoint = new Point( 0, hSize/2 );

		faradChoice = new SophieChoice();

		for( int i=0; i<faradList.length; ++i )
			faradChoice.add( faradList[i] );
		faradChoice.select( defselect );
		
		faradChoice.addItemListener( new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if( e.getStateChange() == ItemEvent.SELECTED )
					{
						double d = Utility.parseDouble(faradList[faradChoice.getSelectedIndex()] );
						setFarad( d * 1E-3 );
					}
				}
			} );

		Dimension size = faradChoice.getMinimumSize();
		if( wSize < size.width )					//there's a strange side effect in that
			wSize = size.width;						//the circuit paintlines will be off by this increase
		outpoint = new Point( wSize, hSize/2 );

		add( faradChoice, new Rectangle(0,hSize-size.height,wSize,size.height) );		//, BorderLayout.SOUTH
		faradChoice.setShowLabel(false);
		faradChoice.setLabelOnTop(false);
	}
	
	public void paint( Graphics g )
	{
		Rectangle r;
		int	pGap = 3;

		//if( ampmeter.isVisible() )
		//	ampmeter.paint( g );
		
		g.setColor( capColor );
		if( inpoint.y != outpoint.y )
			debug("inpoint.y ("+inpoint.y+") doesn't match outpoint.y ("+outpoint.y);

		int xpoints[] = new int[4];
		int ypoints[] = new int[4];

		xpoints[0] = outpoint.x;
		ypoints[0] = outpoint.y;
		xpoints[1] = (wSize / 2) + pGap;
		ypoints[1] = outpoint.y;
		xpoints[2] = (wSize / 2) + pGap;
		ypoints[2] = outpoint.y - 10;
		xpoints[3] = (wSize / 2) + pGap;
		ypoints[3] = outpoint.y + 12;
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
		ypoints[2] = inpoint.y - 10;
		xpoints[3] = (wSize / 2) - pGap;
		ypoints[3] = inpoint.y + 12;
		for( int i=0; i < thickness ; i++ )
		{							//make element lines thicker
			g.drawPolyline( xpoints, ypoints, 4 );
			xpoints[1] += 1;
			xpoints[2] += 1;
			xpoints[3] += 1;
			ypoints[0] += 1;
			ypoints[1] += 1;
		}

		Dimension size = faradChoice.getMinimumSize();
		faradChoice.paint( g, 0, hSize - size.height );
/*		int yy = hSize;
		g.setColor( faradBackground );
		g.fillRoundRect( (outpoint.x - choiceSize -4), (yy - 13), (choiceSize +2), 13, 4, 4 );
		g.setColor( Color.black );
		g.drawRoundRect( (outpoint.x - choiceSize -4), (yy - 13), (choiceSize +2), 13, 4, 4 );

		Font oldfon = g.getFont();				//save old font
		Font fon = new Font("Serif",Font.PLAIN,9);
		g.setFont( fon );
		g.drawString( faradList[1], (outpoint.x - choiceSize), yy-2 );
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
		return "capacitor.gif";
	}
	
	public Color getColor()
	{
		return capColor;
	}

	public String getCheckboxName()
	{
		return "C";
	}
	
	public double reactance( double angle )
	{
		return 1/(angle*farad);
	}
	
	public void setFarad( double f )
	{
		farad = f;
		//System.out.println("setFarad("+f+")");
		//retrace();
	}
	
	public double getFarad()
	{
		return farad;
	}

	public double getValue( int sel, double t0 )
	{
		//debug("Capacitor: getValue()");
		
/*		if( sel == CircuitElement.VOLTAGE )
		{
			if( circuit != null )
			{
				double t = toSeconds(t0);
				double I0 = 1.0; //circuit.getI0(t0);
				double w = 1.0; //circuit.getAngle();
				double phi = 1.0; //circuit.getPhi();
				
				double V = reactance(w)*I0*Math.sin(w*t-phi-Math.PI/2.0);
				
				//double V0 = circuit.getECM(t0);
				
				//System.out.println("V="+DoubleFormat.format(V)+
				//				"   V0="+DoubleFormat.format(V0)+
				//				"   dV="+DoubleFormat.format(Math.abs(V-V0)));
				
				return V;
			}
		}
*/		
		return farad;
	}

	public double getPhase( long tau )
	{
		return 0.0;
	}

	public void actionPerformed( ActionEvent e )
	{
		String cmd = e.getActionCommand();
		
		debug(" Got actionPerformed("+e+") ");
		//if( cmd.equals(REDRAW) )
		//	broadcast( e );
	}
	
	static void debug( String s )
	{
		if( debug )
			System.out.println("Capacitor:: "+s);
	}
}
