
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

// support for describing a circuit.

public class Circuit extends Panel
{
	final static boolean debug = true;
		
	String code;
	
	ActionListener listeners = null;

	Color color = new Color(204,153,153);
		
	Vector parentCircuits = new Vector();	//nested circuit panels into the layoutManager
	
	int layoutmode = CircuitLayout.PARALLEL;

	Vector boxes = new Vector();
	
	public Circuit( CircuitSpecifier spec )
	{
		this( spec.getName(), spec.getLayout() );
	}

	public Circuit( String name, String code )
	{
		super( new CircuitLayout(CircuitLayout.PARALLEL) );
		debug("starting Circuit("+name+","+code+")... going to CircuitLayout()");
		setName( name );
		this.code = code;
		
		ParseCircuit( code );

		Element.setCircuit(this);
	}
	
	public Circuit( LayoutManager layout )
	{
		super( layout );
		debug("new circuit with "+layout);
	}

				//Parse the given a code String  (e.g. {B[RC]} ).
				// build(add to layout) a complete circuit of elements
				//where the syntax of code string is:
				//		{} == parallel connection to the circuit
				//		[] == series connection
				//
	public void ParseCircuit( String code )
	{
		char	tok;				//current code string character being processed
		int		i;					//indexs thru the code string
		debug(" ParseCircuit("+code+")... ");
		
		Element ce;

		Circuit curCircuit = this;			//which nested circuit is having elements inserted
		Circuit cir;
		InvisiPanel p;
		InvisiPanel p2;
		InvisiPanel p3;
		InvisiPanel p4;
		Choice choice;
		
					//loop thru code string creating all objects and layouts

		int pc = 0;								//index into parentcuruits[]
		parentCircuits.addElement( curCircuit );	//always add first default curcuit (parallel)
		for( i=0 ; i < code.length() ; i++ )
		{
			tok = code.toUpperCase().charAt(i);
			ce = null;
			switch( tok )
			{
				case 'B': ce = new PowerSource();
						  break;
				case 'R': ce = new Resistor();
						  break;
				case 'C': ce = new Capacitor();
						  break;
				case 'L': ce = new Coil();
						  break;
						  		//nested create a new parallel circuit
				case '{': if( i == 0 )
							continue;	//don't create new parallel on first character
						  cir = new Circuit(new CircuitLayout(CircuitLayout.PARALLEL));
						  curCircuit.add( cir );							//add new circuit into parent layout
						  curCircuit = cir; 						//switch current work will be on this circuit now
						  parentCircuits.addElement( curCircuit );			//store this nested create
						  pc++;
						  break;
				case '}': //validate();
						  if( pc >= 1 )
						  {
						  	  --pc;
							  curCircuit = (Circuit)parentCircuits.elementAt(pc);
						  }
						  break;
				case '[': cir = new Circuit(new CircuitLayout(CircuitLayout.SERIES));
						  layoutmode = CircuitLayout.SERIES;
						  curCircuit.add( cir );							//add new circuit into parent layout
						  curCircuit = cir; 						//switch current work will be on this circuit now
						  parentCircuits.addElement( curCircuit );
						  pc++;
						  break;
				case ']': --pc;
						  curCircuit = (Circuit)parentCircuits.elementAt(pc);
						  break;
				default: break;
			}
			if( ce != null )
			{			//if we just created an element, add and remember it.
				boxes.addElement( ce );
				curCircuit.add( ce );
			}
		}
		
				//loop thru code string connecting each element with it's neighbors

		int b = -1;						//current boxes index
		ce = null;
		int	curmode = CircuitLayout.PARALLEL;		//remember which curcuit mode we currently are connecting
		pc = 0;
		curCircuit = (Circuit)parentCircuits.elementAt(pc);
		for( i=0 ; i < code.length() ; i++ )
		{
			tok = code.toUpperCase().charAt(i);
			switch( tok )
			{
				case 'B': 
				case 'R': 
				case 'C': 
				case 'L': 
						  ce = (Element)boxes.elementAt(++b);		//increment to next element
						  if( curmode == CircuitLayout.SERIES )
						  {
					  		if( Character.isLetter(code.toUpperCase().charAt(i-1)) )
					  		{		//blotz elaborate on this for only BRCL
					  			ce.inputs.addElement(boxes.elementAt(b-1));
					  			((Element)boxes.elementAt(b-1)).outputs.addElement(ce);
					  		}
					  		else		//blotz elaborate on this for only {}[]
					  		{			//hook into previous circuit
					  			if( pc > 0 )
						  			ce.inputs.addElement(parentCircuits.elementAt(pc-1));
					  		}
						  }
						  else				//Parallel
						  {
						  	for( int j=i-1 ; j >= 0 ; --j )
						  	{						//while there are letters before ours
						  		if( Character.isLetter(code.toUpperCase().charAt(j)) )
						  		{		//blotz elaborate on this for only BRCL (when/if other letters that don't do this)
						  			if( (b-(i-j)) > 0 )
						  			{
						  				ce.parallels.addElement(boxes.elementAt(b-(i-j)));
						  				((Element)boxes.elementAt(b-(i-j))).parallels.addElement(ce);
						  			}
						  		}
						  		else		//blotz elaborate on this for only {}[]  (when/if other letters that don't do this)
						  		{			//hook into previous circuit
						  			if( pc > 0 )
						  				ce.inputs.addElement(parentCircuits.elementAt(pc-1));
						  			break;
						  		}
						  	}
						  }
						  break;
						  		//switch to connecting parallel circuit
				case '{': if( ce != null )				//if not on first char is parallel
						  {
						  	curCircuit = (Circuit)parentCircuits.elementAt(++pc);
					  		ce.outputs.addElement(curCircuit);
					  		if( curmode == CircuitLayout.PARALLEL )		//if was parallel then connect inputs too
					  			ce.inputs.addElement(curCircuit);
						  }
						  curmode = CircuitLayout.PARALLEL;
						  break;
				case '}': if( pc > 0 )
						  {
							curCircuit = (Circuit)parentCircuits.elementAt(--pc);
							curmode = curCircuit.layoutmode;
					  		ce.outputs.addElement(curCircuit);
						  }
						  break;
				case '[': curmode = CircuitLayout.SERIES;
						  curCircuit = (Circuit)parentCircuits.elementAt(++pc);
					  	  ce.outputs.addElement(curCircuit);
						  break;
				case ']': if( pc > 0 )
						  {
							curCircuit = (Circuit)parentCircuits.elementAt(--pc);
							curmode = curCircuit.layoutmode;
					  		ce.outputs.addElement(curCircuit);
						  }
						  break;
				default: break;
			}
		}

		validate();
		
		if( debug )
		{
			for( i=0 ; i < boxes.size(); i++ )
			{
				System.out.println("    "+i);
			}
		}
		
	}

	public void paint( Graphics g )
	{
		int		db = 15;
		int		i;
		int		hgap = 15;

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
					
					if((db&1)==1) drawConnectLines( g, p0, p1, -(hgap/2) );
				}

				for( int j=0 ; j<e0.parallels.size(); ++j )	// i is already inited
				{								//get all the other elements
					c = (Component)e0.parallels.elementAt(j);
					if( c == null )
						continue;
					if( c instanceof Element )
					{
						debug("   has out-par element "+c);
						p1 = new Point(((Element)c).outpoint);
						if((db&2)==2) debug(" element is at parent.location "+c.getParent().getLocation());
						if((db&2)==2) debug(" circuit is at location "+c.getLocation());
						if( c.getParent() == c0.getParent() )
						{
							p1.x += c.getLocation().x;
							p1.y += c.getLocation().y;
						}
						else
						{
							p1.x += c.getParent().getLocation().x;
							p1.y += c.getParent().getLocation().y;
						}
					}
					else if( c instanceof Circuit )
					{
						debug("   has out-par Circuit "+c);
						if((db&2)==2) debug(" circuit is at location "+c.getLocation());
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
					
					if((db&4)==4) drawConnectLines( g, p0, p1, (hgap/2) );
				}	
				
				for( int j=0 ; j<e0.parallels.size(); ++j )	// i is already inited
				{								//get all the other elements
					c = (Component)e0.parallels.elementAt(j);
					if( c == null )
						continue;
					if( c instanceof Element )
					{
						debug("   has in-par element "+c);
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
			}		
		}
		//super.paint(g);
	}
	
	void drawConnectLines( Graphics g,Point p0, Point p1, int gap )
	{
		if( (p0.x != p1.x) || (p0.y != p1.y) )			//if there is any line to draw
		{
			debug("drawing input parallels lines @ "+p0.x+","+p0.y+" to "+p1.x+","+p1.y);
			if( p0.y == p1.y )			//if this is one straight horizonal line
				g.drawLine( p0.x, p0.y, p1.x, p1.y );
			else if( p0.x == p1.x )			//if this is one straight vertical line
			{
				g.drawLine( p0.x, p0.y, (p0.x + gap), p0.y );
				g.drawLine( (p0.x + gap), p0.y, (p1.x + gap), p1.y );
				g.drawLine( (p1.x + gap), p1.y, p1.x, p1.y );
			}
			else
			{
				g.drawLine( p0.x, p0.y, (p1.x + gap), p0.y );	//do across x dir first
				g.drawLine( (p1.x + gap), p0.y, (p1.x + gap), p1.y );
				g.drawLine( (p1.x + gap), p1.y, p1.x, p1.y );
			}
		}
	}
	
		
	public Color getColor()
	{
		return color;
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

/*	public double getValue( int selector, long tau )
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
		return (sel==CircuitElement.INDUCTANCE);
	}

/* //old paint method
	public void paint( Graphics g )
	{
		Component ppower=null,
				  pcoil=null,
				  presistor=null,
				  pcapacitor=null;
		
		Point powerPoint, coilPoint, resistorPoint, capacitorPoint;
		
		powerPoint = new Point(0,0);
		ppower = search( this, PowerSource.class, powerPoint );
		coilPoint = new Point(0,0);
		pcoil = search( this, Coil.class, coilPoint );
		resistorPoint = new Point(0,0);
		presistor = search( this, Resistor.class, resistorPoint );
		capacitorPoint = new Point(0,0);
		pcapacitor = search( this, Capacitor.class, capacitorPoint );
		
//		debug("powerPoint is "+powerPoint);
//		debug("coilPoint is "+coilPoint);
//		debug("resistorPoint is "+resistorPoint);
//		debug("capacitorPoint is "+capacitorPoint);
		
		Rectangle r = getBounds();
		r.x = r.y = 0;
		
		if( ppower != null )
		{
			//debug("power found");
			Dimension dim = ppower.getSize();
			//debug("power size is "+dim);
			r.y = powerPoint.y+dim.height/2;
			r.height -= r.y;
			//debug("r now is "+r);
		}
		
		if( pcoil != null )
		{
			Dimension dim = pcoil.getSize();
			r.y = coilPoint.y+dim.height/2;
			r.height -= r.y;
		}
		
		if( presistor != null )
		{
			Dimension dim = presistor.getSize();
			int y = resistorPoint.y + dim.height/2;
			r.height = y-r.y;
		}
		
		if( pcapacitor != null )
		{
			Dimension dim = pcapacitor.getSize();
			int y = capacitorPoint.y + dim.height/2;
			r.height = y-r.y;
		}
		
		g.setColor( getForeground() );
		debug("drawing lines @ "+r);
		g.drawRect( r.x, r.y, r.width-1, r.height-1 );
		
		super.paint(g);
	}
*/  //old paint method

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
