package Wired;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

// support for describing a circuit.

public class Circuit extends PaddedPanel implements CircuitElement
{
	String code;
	
	Vector boxes = new Vector();
	
	ActionListener listeners = null;

	static PowerSource power = new PowerSource();
	static Coil coil = new Coil();
	static Resistor resistor = new Resistor();
	static Capacitor capacitor = new Capacitor();

	Color color = new Color(204,153,153);

	boolean hasPower, hasCoil, hasResistor, hasCapacitor;
	
	public Circuit( CircuitSpecifier spec )
	{
		this( spec.getName(), spec.getLayout() );
	}
	
	public Circuit( String name, String code )
	{
		super( null, 32, 0, 32, 0 );
		setLayout( new CircuitLayout() );
		setName( name );
		this.code = code;
		
		Element ce;
		
		hasPower = code.indexOf('B') >= 0;
		hasCoil = code.indexOf('L') >= 0;
		hasResistor = code.indexOf('R') >= 0;
		hasCapacitor = code.indexOf('C') >= 0;
		
		power.setVisible(hasPower);
		coil.setVisible(hasCoil);
		resistor.setVisible(hasResistor);
		capacitor.setVisible(hasCapacitor);
		
		Element.setCircuit(this);
		
		if( hasPower )
		{
			boxes.addElement( power );
			add( power, BorderLayout.WEST );
		}
		if( hasCoil )
		{
			boxes.addElement( coil );
			add( coil, BorderLayout.NORTH );
		}
		if( hasResistor )
		{
			boxes.addElement( resistor );
			add( resistor, BorderLayout.EAST );
		}
		if( hasCapacitor )
		{
			boxes.addElement( capacitor );
			add( capacitor, BorderLayout.SOUTH );
		}
	}

	public String getMeterLabel()
	{
		return "~!I~!";
	}
	
	public String getMeterUnits()
	{
		return "A";
	}

	public double getAngle()
	{
		return power.getFrequency() * Math.PI * 2.0;
	}

	public double getPhi()
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

	public double getECM( long tau )
	{
		return power.getValue(tau);
	}

	public double getI0( long tau )
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
				System.out.println("High is "+hiI0+" Low is "+loI0);
			}
			
			if( I0 < loI0 )
			{
				loI0 = I0;
				System.out.println("High is "+hiI0+" Low is "+loI0);
			}
		}

		return I0;
	}

	double hiI0 = -1E10;
	double loI0 = 1E10;

	public double getValue( long tau )
	{
		try
		{
			double I;
			
			I = getI0(tau) * Math.sin(getAngle()*Element.toSeconds(tau)-getPhi());
			
			return I;
		}
		
		catch( Exception e )
		{
		}
		
		return 0.0;
	}

	public double getPhase( long tau )
	{
		try
		{
			double I;
			
			I = getI0(tau) * Math.cos(getAngle()*Element.toSeconds(tau)-getPhi());
			
			return I;
		}
		
		catch( Exception e )
		{
		}
		
		return 0.0;
	}

	public boolean showTrace()
	{
		return true;
	}

	public Color getColor()
	{
		return color;
	}

	public Vector getElements()
	{
		return boxes;
	}
	
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
		
//		System.out.println("powerPoint is "+powerPoint);
//		System.out.println("coilPoint is "+coilPoint);
//		System.out.println("resistorPoint is "+resistorPoint);
//		System.out.println("capacitorPoint is "+capacitorPoint);
		
		Rectangle r = getBounds();
		Insets insets = getInsets();
		
		int x1 = insets.left;
		int y1 = insets.top;
		int x2 = r.width - insets.right;
		int y2 = r.height - insets.bottom;
		
		if( ppower != null )
		{
			//System.out.println("power found");
			Dimension dim = ppower.getSize();
			//System.out.println("power size is "+dim);
			x1 = powerPoint.x+dim.width/2;
		}
		
		if( pcoil != null )
		{
			Dimension dim = pcoil.getSize();
			y1 = coilPoint.y+dim.height/2;
		}
		
		if( presistor != null )
		{
			Dimension dim = presistor.getSize();
			x2 = resistorPoint.x + dim.width/2;
		}
		
		if( pcapacitor != null )
		{
			Dimension dim = pcapacitor.getSize();
			y2 = capacitorPoint.y + dim.height/2;
		}

		r.x = x1;
		r.y = y1;
		r.width = x2-x1;
		r.height = y2-y1;
				
		g.setColor( getForeground() );
		//System.out.println("drawing lines @ "+r);
		g.drawRect( r.x, r.y, r.width-1, r.height-1 );
		
		super.paint(g);
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

	class CircuitLayout implements LayoutManager
	{
		Component [] comps = new Component[5];
		
		final int C = 0;
		final int N = 1;
		final int E = 2;
		final int W = 3;
		final int S = 4;
		
    	public void addLayoutComponent(String name, Component comp)
    	{
    		int index;
    		
    		if( name == null || name.equals(BorderLayout.CENTER) )
    			index = C;
    		else if( name.equals(BorderLayout.NORTH) )
    			index = N;
    		else if( name.equals(BorderLayout.WEST) )
    			index = W;
    		else if( name.equals(BorderLayout.EAST) )
    			index = E;
    		else if( name.equals(BorderLayout.SOUTH) )
    			index = S;
			else
			    throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + name);

			comps[index] = comp;
    	}
    	
    	public void removeLayoutComponent(Component comp)
    	{
    		for( int i=0; i<comps.length; ++i )
    			if( comps[i] == comp )
    				comps[i] = null;
    	}
    	
    	public Dimension minimumLayoutSize(Container parent)
    	{
    		Dimension d, size, centerSize;
    		Component c;
    		
    		size = new Dimension(0,0);
    		centerSize = new Dimension(0,0);
    		
    		if( (c=comps[C]) != null )
    		{
    			centerSize = c.getMinimumSize();
    		}
    		
    		if( (c=comps[N]) != null )
    		{
    			d = c.getMinimumSize();
    			centerSize.width = Math.max(d.width,centerSize.width);
    			size.height += d.height;
    		}
    		
    		if( (c=comps[S]) != null )
    		{
    			d = c.getMinimumSize();
    			centerSize.width = Math.max(d.width,centerSize.width);
    			size.height += d.height;
    		}
    		
    		if( (c=comps[E]) != null )
    		{
    			d = c.getMinimumSize();
    			size.width += d.width;
    			centerSize.height = Math.max(d.height,centerSize.height);
    		}
    		
    		if( (c=comps[W]) != null )
    		{
    			d = c.getMinimumSize();
    			size.width += d.width;
    			centerSize.height = Math.max(d.height,centerSize.height);
    		}
    		
    		Insets insets = parent.getInsets();
    		
    		size.width += centerSize.width + insets.left + insets.right;
    		size.height += centerSize.height + insets.top + insets.bottom;
    		
    		return size;
    	}
    	
    	public Dimension preferredLayoutSize(Container parent)
    	{
    		Dimension d, size, centerSize;
    		Component c;
    		
    		size = new Dimension(0,0);
    		centerSize = new Dimension(0,0);
    		
    		if( (c=comps[C]) != null )
    		{
    			centerSize = c.getPreferredSize();
    		}
    		
    		if( (c=comps[N]) != null )
    		{
    			d = c.getPreferredSize();
    			centerSize.width = Math.max(d.width,centerSize.width);
    			size.height += d.height;
    		}
    		
    		if( (c=comps[S]) != null )
    		{
    			d = c.getPreferredSize();
    			centerSize.width = Math.max(d.width,centerSize.width);
    			size.height += d.height;
    		}
    		
    		if( (c=comps[E]) != null )
    		{
    			d = c.getPreferredSize();
    			size.width += d.width;
    			centerSize.height = Math.max(d.height,centerSize.height);
    		}
    		
    		if( (c=comps[W]) != null )
    		{
    			d = c.getPreferredSize();
    			size.width += d.width;
    			centerSize.height = Math.max(d.height,centerSize.height);
    		}
    		
    		Insets insets = parent.getInsets();
    		
    		size.width += centerSize.width + insets.left + insets.right;
    		size.height += centerSize.height + insets.top + insets.bottom;
    		
    		return size;
    	}
	
		public void layoutContainer(Container parent)
		{
			Insets insets = parent.getInsets();
			Dimension size = parent.getSize();
			Component c;
			
			int top = insets.top;
			int bottom = size.height - insets.bottom;
			int left = insets.left;
			int right = size.width - insets.right;
			
			Insets cinsets = new Insets(0,0,0,0);
			
			if( (c=comps[N]) != null )
			{
				Dimension csz = c.getPreferredSize();
				csz.setSize( right-left, csz.height );
				cinsets.top = c.getPreferredSize().height;
				//System.out.println("N size is "+csz.getSize());
			}
			if( (c=comps[S]) != null )
			{
				Dimension csz = c.getPreferredSize();
				csz.setSize( right-left, csz.height );
				cinsets.bottom = c.getPreferredSize().height;
			}
			if( (c=comps[E]) != null )
			{
				Dimension csz = c.getPreferredSize();
				csz.setSize( csz.width, bottom-top );
				cinsets.right = c.getPreferredSize().width;
			}
			if( (c=comps[W]) != null )
			{
				Dimension csz = c.getPreferredSize();
				csz.setSize( csz.width, bottom-top );
				cinsets.left = c.getPreferredSize().width;
			}
			
			int centerWidth = right-left-cinsets.left-cinsets.right;
			int centerHeight = bottom-top-cinsets.bottom-cinsets.top;
			
			if( (c=comps[C]) != null )
			{
				c.setBounds(left+cinsets.left,
							top+cinsets.top,
							centerWidth,
							centerHeight);
				//System.out.println("resized "+c+" to "+c.getBounds());
			}
			
			if( (c=comps[N]) != null )
			{
				Dimension d = c.getPreferredSize();
				int extra = 0;
				int c2 = centerWidth + (cinsets.left+cinsets.right)/2;
				if( d.width < c2 )
					extra = (centerWidth-d.width)/2;
				else
					d.width = centerWidth;
				c.setBounds(left+cinsets.left+extra,top,d.width,cinsets.top);
				//System.out.println("resized "+c+" to "+c.getBounds());
			}
			
			if( (c=comps[S]) != null )
			{
				Dimension d = c.getPreferredSize();
				int extra = 0;
				if( d.width < centerWidth )
					extra = (centerWidth-d.width)/2;
				else
					d.width = centerWidth;
				c.setBounds(left+cinsets.left+extra,bottom-cinsets.bottom,d.width,cinsets.bottom);
				//System.out.println("resized "+c+" to "+c.getBounds());
			}
			
			if( (c=comps[W]) != null )
			{
				Dimension d = c.getPreferredSize();
				int extra = 0;
				if( d.height < centerHeight )
					extra = (centerHeight-d.height)/2;
				else
					d.height = centerHeight;
				c.setBounds(left,top+cinsets.top+extra,
							d.width,cinsets.left);
				//System.out.println("resized "+c+" to "+c.getBounds());
			}
			
			if( (c=comps[E]) != null )
			{
				Dimension d = c.getPreferredSize();
				int extra = 0;
				if( d.height < centerHeight )
					extra = (centerHeight-d.height)/2;
				else
					d.height = centerHeight;
				c.setBounds(right-cinsets.right,
							top+cinsets.top+extra,
							cinsets.right,
							d.height);
				//System.out.println("resized "+c+" to "+c.getBounds());
			}
		}
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
}
