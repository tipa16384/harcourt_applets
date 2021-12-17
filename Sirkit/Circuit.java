
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

// support for describing a circuit.

public class Circuit extends Panel implements CircuitElement, ActionListener
{
	final static boolean debug = false;
		
	String code;
	
	ActionListener listeners = null;

	Color color = new Color(204,153,153);
		
	Vector parentCircuits = new Vector();	//nested circuit panels into the layoutManager
	
	int layoutmode = CircuitLayout.PARALLEL;

	Vector boxes = new Vector();
	
									//the global Elements, so we can test charge/discharge on_off state
	public Element R1, C1, PS, R2, S1;		//  and getValue() can read their values
	
	public Circuit( String name, String code )
	{
		super( new GridBagLayout() );
		//super( new CircuitLayout(CircuitLayout.PARALLEL, 20, 20) );
		debug("starting Circuit("+name+","+code+")... going to CircuitLayout()");
		setName( name );
		this.code = code;
		
		CircuitSetup( code );

		Element.setCircuit(this);
	}
	
	static double oldval = 1.0;
	
	public double getValue( int selector, double t0 )
	{
		double val = 0.0;
		double Cap = C1.getValue(CircuitElement.VOLTAGE,t0);
		
		//debug("getValue( "+selector+", "+t0+" )");

		if( ((Switcher)S1).on_off == Switcher.DISCHARGE )
		{
			val = PS.getValue(CircuitElement.VOLTAGE,t0);			//no need to *Cap / Cap;
			val *=  Math.pow( Math.E, (-t0 / (R1.getValue(selector,t0) * Cap)) );
			if( selector == CircuitElement.CURRENT )
			{
				val /= R1.getValue(selector,t0);
				val = -val;
			}
		}
		else if( ((Switcher)S1).on_off == Switcher.CHARGE )
		{
			val = PS.getValue(CircuitElement.VOLTAGE,t0) / R2.getValue(selector,t0);
			val *=  Math.pow( Math.E, (-t0 / (R2.getValue(selector,t0) * Cap)) );
			if( selector == CircuitElement.VOLTAGE )
			{
				val = PS.getValue(CircuitElement.VOLTAGE,t0) - (val * R2.getValue(selector,t0));
			}
		}
		else
			debug("getValue was given a strange selector=="+selector);
		
		if( (val != oldval) )
		{
			oldval = val;
			//debug("getValue( "+selector+", "+t0+" ) returns "+val);
		}
		
		return val;				//test with Math.sin(getPhase(t0));
	}
	
	public double getTau()
	{
		double val = 0.0;
		double Cap = C1.getValue(CircuitElement.VOLTAGE,0);

		if( ((Switcher)S1).on_off == Switcher.DISCHARGE )
		{
			val = R1.getValue(0,0) * Cap;
		}
		else
		{
			val = R2.getValue(0,0) * Cap;
		}
		return val;
	}

	public double getPhase( long t0 )
	{
		return (2000.0*Math.PI)/(double)t0;
	}

				//
	public void CircuitSetup( String code )
	{
		char	tok;				//current code string character being processed
		int		i;					//indexs thru the code string
		debug(" CircuitSetup("+code+")... ");
		
		Circuit curCircuit = this;			//which nested circuit is having elements inserted
		Circuit cir;
		InvisiPanel p;
		InvisiPanel p2;
		InvisiPanel p3;
		InvisiPanel p4;
		Choice choice;
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = gbc.weighty = 0.0;
		//gbc.insets = new Insets( 0, 0, 0, 0 );
		gbc.insets = new Insets( 7, 7, 7, 7 );					//0==too small, 15==too big
		
					//loop thru code string creating all objects and layouts

		R1 = new Resistor();
		if( R1 != null )
		{			//if we just created an element, add and remember it.
			boxes.addElement( R1 );
			gbc.gridx = 1;
			gbc.gridy = 0;
			curCircuit.add( R1, gbc );
		}

		C1 = new Capacitor();
		if( C1 != null )
		{			//if we just created an element, add and remember it.
			boxes.addElement( C1 );
			gbc.gridx = 2;
			gbc.gridy = 1;
			curCircuit.add( C1, gbc );
		}

		//layoutmode = CircuitLayout.SERIES;
		
		S1 = new Switcher();
		if( S1 != null )
		{			//if we just created an element, add and remember it.
			boxes.addElement( S1 );
			gbc.gridx = 0;
			gbc.gridy = 1;
			curCircuit.add( S1, gbc );
			debug("creating switcher "+S1);
			S1.addActionListener( this );
		}

		//layoutmode = CircuitLayout.PARALLEL;

		PS = new PowerSource();
		if( PS != null )
		{			//if we just created an element, add and remember it.
			boxes.addElement( PS );
			gbc.gridx = 2;
			gbc.gridy = 2;
			curCircuit.add( PS, gbc );
		}

		//layoutmode = CircuitLayout.SERIES;
		
		R2 = new Resistor();
		if( R2 != null )
		{			//if we just created an element, add and remember it.
			boxes.addElement( R2 );
			gbc.gridx = 1;
			gbc.gridy = 2;
			curCircuit.add( R2, gbc );
		}

		//layoutmode = CircuitLayout.PARALLEL;
						//hook the elements together manually in this version
// /* draws correct but... using CircuitLayout and circuit paint()
		R1.parallelOuts.addElement( C1 );
		R1.parallelOuts.addElement( PS );
		//C1.parallelOuts.addElement( R1 );
		//C1.parallelOuts.addElement( PS );
		//PS.parallelOuts.addElement( R1 );
		//PS.parallelOuts.addElement( C1 );
// */
		C1.inputs.addElement( S1 );
		//C1.outputs.addElement( PS );
		PS.inputs.addElement( R2 );
		//PS.outputs.addElement( C1 );
		//S1.outputs.addElement( C1 );
		R1.parallelIns.addElement( S1 );
		R2.parallelIn2s.addElement( S1 );


		validate();
		
/*		if( debug )
		{
			for( i=0 ; i < boxes.size(); i++ )
			{
				System.out.println("    "+i);
			}
		}
*/		
	}

	public void paint( Graphics g )
	{
		int		db = 15;				//debug bitfield value to select which of 4 lines draw
		int		i;
		int		hgap = 30;

		Component [] comps = this.getComponents();
		Component	c0 = null;
		Component	c;
		Element		e0;

		super.paint(g);
		
		if( comps == null )
			return;
			
		Rectangle r = getBounds();
		//r.x = r.y = 0;

		g.setColor( getForeground() );
		
		debug("*** starting Circuit paint ****");
		
		for( i=0; i<comps.length ; ++i )
		{								//get the first element
			c0 = comps[i];
			if( c0 == null )
				continue;
			if( c0 instanceof Element )
			{
				e0 = (Element)c0;
				if( e0 instanceof Resistor )
					debug("C0 = "+c0.getName());
				else
					debug("C0=="+c0);
				Point p0 = new Point(e0.outpoint);
				p0.x += c0.getLocation().x;
				p0.y += c0.getLocation().y;

				Point p1;	
					
				for( int j=0 ; j<e0.outputs.size(); ++j )	// i is already inited
				{								//get all the other elements
					c = (Component)e0.outputs.elementAt(j);
					if( c == null )
						continue;
					if( c instanceof Element )
					{
						debug("   has out-in element "+c);
						p1 = new Point(((Element)c).inpoint);
						p1.x += c.getLocation().x;
						p1.y += c.getLocation().y;
					}
					else if( c instanceof Circuit )
					{
						debug("   has out-in Circuit "+c);
						if((db&1)==1) debug(" circuit is at location "+c.getLocation());
						p1 = new Point(c.getLocation());
						//p1.x += c.getSize().width;
						p1.y += c.getSize().height / 2;
					}
					else
					{
						debug("   ???found some other component that is not Element nor Circuit in out-in "+c);
						continue;
					}
					
					if((db&1)==1) drawConnectLines( g, p0, p1, +(hgap/2) );
				}

				for( int j=0 ; j<e0.parallelOuts.size(); ++j )	// i is already inited
				{								//get all the other elements
					c = (Component)e0.parallelOuts.elementAt(j);
					if( c == null )
						continue;
					if( c instanceof Element )
					{
						debug("   has out-par element "+c);
						p1 = new Point(((Element)c).outpoint);
						if( c.getParent() == c0.getParent() )
						{
							if((db&2)==2) debug("    element is at location "+c.getLocation());
							p1.x += c.getLocation().x;
							p1.y += c.getLocation().y;
						}
						else
						{
							if((db&2)==2) debug("    element is at parent.location "+c.getParent().getLocation());
							p1.x += c.getParent().getLocation().x;
							p1.y += c.getParent().getLocation().y;
						}
					}
					else if( c instanceof Circuit )
					{
						debug("   has out-par Circuit "+c);
						if((db&2)==2) debug("    circuit is at location "+c.getLocation());
						p1 = new Point(c.getLocation());
						p1.x += c.getSize().width;
						p1.y += c.getSize().height / 2;
					}
					else
					{
						debug("   ???found some other component that is not Element nor Circuit in out-par "+c);
						continue;
					}
					
					if((db&2)==2) drawConnectLines( g, p0, p1, (hgap/2) );
				}

				p0 = new Point(e0.inpoint);
				p0.x += c0.getLocation().x;
				p0.y += c0.getLocation().y;

				for( int j=0 ; j<e0.inputs.size(); ++j )	// i is already inited
				{								//get all the other elements
					c = (Component)e0.inputs.elementAt(j);
					if( c == null )
						continue;
					if( c instanceof Element )
					{
						debug("   has in-out element "+c);
						p1 = new Point(((Element)c).outpoint);
						p1.x += c.getLocation().x;
						p1.y += c.getLocation().y;
					}
					else if( c instanceof Circuit )
					{
						debug("   has in-out Circuit "+c);
						if((db&4)==4) debug(" circuit is at location "+c.getLocation());
						p1 = new Point(c.getLocation());
						p1.x += c.getSize().width;
						p1.y += c.getSize().height / 2;
					}
					else
					{
						debug("   ???found some other component that is not Element nor Circuit in in-out "+c);
						continue;
					}
					
					if((db&4)==4) drawConnectLines( g, p0, p1, -(hgap/2) );
				}	
				
				for( int j=0 ; j<e0.parallelIns.size(); ++j )	// i is already inited
				{								//get all the other elements
					c = (Component)e0.parallelIns.elementAt(j);
					if( c == null )
						continue;
					if( c instanceof Element )
					{
						debug("   has in-parIn element "+c);
						p1 = new Point(((Element)c).inpoint);
						p1.x += c.getLocation().x;
						p1.y += c.getLocation().y;
					}
					else if( c instanceof Circuit )
					{
						debug("   has in-par Circuit "+c);
						if((db&8)==8) debug(" circuit is at location "+c.getLocation());
						p1 = new Point(c.getLocation());
						//p1.x += c.getSize().width;
						p1.y += c.getSize().height / 2;
					}
					else
					{
						debug("   ???found some other component that is not Element nor Circuit in in-par "+c);
						continue;
					}
					
					if((db&8)==8) drawConnectLines( g, p0, p1, -(hgap/2) );
				}
				
				for( int j=0 ; j<e0.parallelIn2s.size(); ++j )	// i is already inited
				{								//get all the other elements
					c = (Component)e0.parallelIn2s.elementAt(j);
					if( c == null )
						continue;
					if( c instanceof Element )
					{
						debug("   has in-parIn element "+c);
						p1 = new Point(((Element)c).inpoint2);
						p1.x += c.getLocation().x;
						p1.y += c.getLocation().y;
					}
					else if( c instanceof Circuit )
					{
						debug("   has in-par Circuit "+c);
						if((db&8)==8) debug(" circuit is at location "+c.getLocation());
						p1 = new Point(c.getLocation());
						//p1.x += c.getSize().width;
						p1.y += c.getSize().height / 2;
					}
					else
					{
						debug("   ???found some other component that is not Element nor Circuit in in-par "+c);
						continue;
					}
					
					if((db&8)==8) drawConnectLines( g, p0, p1, -(hgap/2) );
				}
			}		
		}
		//super.paint(g);
	}
	
	void drawConnectLines( Graphics g,Point pstart, Point pend, int gap )
	{
		Point p0 = new Point(pstart);					//use local var (so callers var is not incremented)
		Point p1 = new Point(pend);
		int xgap = gap;
				
		if( (p0.x != p1.x) || (p0.y != p1.y) )			//if there is any line to draw
		{
			debug("drawing lines @ "+p0.x+","+p0.y+" to "+p1.x+","+p1.y+" gap="+xgap);
			for( int i=0 ; i < 1 ; i++ )			//draw thicker lines
			{
				if( p0.y == p1.y )			//if this is one straight horizonal line
					g.drawLine( p0.x, p0.y, p1.x, p1.y );
				else if( p0.x == p1.x )			//if this is one straight vertical line
				{
					g.drawLine( p0.x, p0.y, (p0.x + xgap), p0.y );
					g.drawLine( (p0.x + xgap), p0.y, (p1.x + xgap), p1.y );
					g.drawLine( (p1.x + xgap), p1.y, p1.x, p1.y );
				}
				else
				{
					g.drawLine( p0.x, p0.y, (p1.x + xgap), p0.y );	//do across x dir first
					g.drawLine( (p1.x + xgap), p0.y, (p1.x + xgap), p1.y );
					g.drawLine( (p1.x + xgap), p1.y, p1.x, p1.y );
				}
				
				p0.y += 1;		//make lines thicker
				p1.y += 1;
				xgap += 1;
			}
		}
	}
	
	public Color getColor()
	{
		return color;
	}

						//special call to our switch to ask it's current color
	public Color getSwitchColor()
	{
		return ((Switcher)S1).getStateColor();
	}

						//special call to our switch to ask it's current state
	public boolean isCharging()
	{
		return( ((Switcher)this.S1).on_off == Switcher.CHARGE );
	}
	

							//Here is a silly (but needed) routine
							// to toggle S1.on_off to the opposite value
	public void toggleSwitch()
	{
		//debug("toggleSwitch "+((Switcher)S1).on_off+" "+((Switcher)S1).numOfToggles);
		if( ((Switcher)S1).numOfToggles <= 1 )		//after reset we show not traces
			return;									//first toggle only shows active, so no toggle allowed
									//toggle the S1.on_off
		if( ((Switcher)S1).on_off == Switcher.DISCHARGE )
			((Switcher)S1).on_off = Switcher.CHARGE;
		else
			((Switcher)S1).on_off = Switcher.DISCHARGE ;
	}
	
	public Vector getElements()
	{
		return boxes;
	}

	//public double getAngle()
	//{
	//	return power.getFrequency() * Math.PI * 2.0;
	//}

/*	public double getPhi()
	{
		double w = getAngle();
		double rr = resistor.R(w);
		double lr = coil.R(w);
		double cr = capacitor.R(w);
		double num = lr-cr;
		double phi;
		
		if( rr == 0.0 )
		{
			if( lr > cr )
				phi = Math.PI/2.0;
			else if( lr < cr )
				phi = -Math.PI/2.0;
			else // if lr == cr
				phi = Math.PI/4;
		}
		
		else
		{
			phi = Math.atan((lr-cr)/rr);
		}
		
		return phi;
	}
*/

/*	public double getECM( long tau )
	{
		return power.getValue(CircuitElement.VOLTAGE,tau);
	}
*/

/*	public double getI0( long tau )
	{
		double V0 = power.getAmplitude();
		double dtau = Element.toSeconds(tau);
		double w = getAngle();
		double rr = resistor.R(w);
		double rl = coil.R(w);
		double rc = capacitor.R(w);
		double rlc = rl-rc;
		
		double I0 = V0 / Math.sqrt(rr*rr+rlc*rlc);
		
		if( false )
		{
			if( I0 > hiI0 )
			{
				hiI0 = I0;
				debug("High is "+hiI0+" Low is "+loI0);
			}
			
			if( I0 < loI0 )
			{
				loI0 = I0;
				debug("High is "+hiI0+" Low is "+loI0);
			}
		}

		return I0;
	}
*/
	double hiI0 = -1E10;
	double loI0 = 1E10;

/*	public double getValue( int selector, double tau )
	{
		try
		{
			switch( selector )
			{
				case CircuitElement.VOLTAGE:
					return power.getValue(selector,tau);
				
				case CircuitElement.INDUCTANCE:
					{
									
						double I;
						
						I = getI0(tau) * Math.sin(getAngle()*Element.toSeconds(tau)-getPhi());
						
						return I * 500.0;
					}
			}
		}
		
		catch( Exception e )
		{
		}
		
		return 0.0;
	}
*/

	public boolean showTrace( int sel )
	{
		if( ((Switcher)S1).numOfToggles == 0 )		//after reset we show not traces
			return( false );
		else
			return( (sel==CircuitElement.CURRENT) || (sel==CircuitElement.VOLTAGE) );
	}

	public String getMeterLabel()
	{
		return "~!V~!";
	}
		
	public String getMeterUnits()
	{
		return "V";
	}
		

	private Component search( Container con, Class cl, Point offset )
	{
		if( con == null )
			return null;
		
		Component [] comps = con.getComponents();
		
		if( comps == null )
			return null;
			
		int i;
		
		for( i=0; i<comps.length; ++i )
		{
			Component c = comps[i];
			
			if( c == null )
				continue;
			
			if( cl.isInstance(c) )
			{
				Point p2 = c.getLocation();
				offset.x += p2.x;
				offset.y += p2.y;
				return c;
			}
			
			if( c instanceof Container )
			{
				Point p1 = c.getLocation();
				offset.x += p1.x;
				offset.y += p1.y;
				Component cc = search( (Container)c, cl, offset );
				if( cc != null )
				{
					return cc;
				}
				offset.x -= p1.x;
				offset.y -= p1.y;
			}
		}
		
		return null;
	}


	public void actionPerformed( ActionEvent e )
	{
		String cmd = e.getActionCommand();
		
		debug("actionPerformed "+e);

		if( cmd.equals(CC2.SWITCHON) )			//when switching to charge cycle
		{										// deactive the charge elements
			((Resistor)R1).ohmChoice.setActive( true );
			((Resistor)R2).ohmChoice.setActive( false );
			((PowerSource)PS).powerChoice.setActive( false );
			((Capacitor)C1).faradChoice.setActive( false );
			repaint();
		}
		else if( cmd.equals(CC2.SWITCHOFF) )
		{
			((Resistor)R1).ohmChoice.setActive( false );
			((Resistor)R2).ohmChoice.setActive( true );
			((PowerSource)PS).powerChoice.setActive( true );
			((Capacitor)C1).faradChoice.setActive( false );
			repaint();
		}

		if( cmd.equals(CC2.RESET) )
		{
			if( S1 != null )
			{
				((Switcher)this.S1).on_off = Switcher.DISCHARGE ;
				((Switcher)this.S1).numOfToggles = 0;
				debug("RESET action "+((Switcher)S1).on_off+" "+((Switcher)S1).numOfToggles);
				((Capacitor)C1).faradChoice.setActive( true );
				((Resistor)R1).ohmChoice.setActive( false );			//at reset all elements are active
				((Resistor)R2).ohmChoice.setActive( true );
				((PowerSource)PS).powerChoice.setActive( true );
			}
		}
		else
			broadcast( e );
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

	static void debug( String s )
	{
		if( debug )
			System.out.println("Circuit:: "+s);
	}
}
