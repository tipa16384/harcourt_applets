package Wired;

import java.awt.*;
import java.awt.event.*;

public class RawCheckbox extends RawLabel
						 implements ItemSelectable
{
	final static int boxSize = 14;
	final static int gap = 4;
	
	ItemListener itemListener = null;
	
	boolean state;
	boolean pressed = false;
	boolean inside = false;
	
	int calign;
	int spacing = 0;
			
	public RawCheckbox()
	{
		this("",Label.LEFT,false);
	}
	
	public RawCheckbox( boolean state )
	{
		this("",Label.LEFT,state);
	}
	
	public RawCheckbox( String text )
	{
		this(text,Label.LEFT,false);
	}
	
	public RawCheckbox( String text, boolean state )
	{
		this(text,Label.LEFT,state);
	}
	
	public RawCheckbox( String text, int aln, boolean state )
	{
		super(text);
		this.calign = aln;
		this.state = state;
		addMouseListener( new Mouser(this) );
	}
	
	public Dimension getMinimumSize()
	{
		Dimension d = super.getMinimumSize();
		//Dimension d1 = new Dimension(d);
		d.width += boxSize+gap /*+spacing*/;
		d.height = Math.max(d.height,boxSize);
		//System.out.println("min size for "+getText()+" is "+d+" (inherited "+d1+")");
		return d;
	}

	public void setSpacing( int i )
	{
		spacing = i;
		repaint();
	}

	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();
		d.width += boxSize+gap/*+spacing*/;
		d.height = Math.max(d.height,boxSize);
		return d;
	}

	public Object[] getSelectedObjects()
	{
		if( state )
		{
			Object [] o = new Object[1];
			o[0] = this;
			return o;
		}
		else
			return null;
	}

    public synchronized void addItemListener(ItemListener l)
    {
		if (l == null)
	    	return;
		
        itemListener = AWTEventMulticaster.add(itemListener, l);
    }

    public synchronized void removeItemListener(ItemListener l)
    {
		if (l == null)
		    return;

        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

	public boolean getState()
	{
		return state;
	}
	
	public void setState( boolean ns )
	{
		if( ns != state )
		{
			state = ns;
			
			if( itemListener != null )
			{
				ItemEvent e;
				
				e = new ItemEvent(this,
							ItemEvent.ITEM_STATE_CHANGED,
							this,
							(state?ItemEvent.SELECTED:ItemEvent.DESELECTED)
							);
							
				itemListener.itemStateChanged(e);
			}
			
			repaint();
		}
	}
	
	public void paint( Graphics g )
	{
		g.setColor( getForeground() );
		
		Dimension size = getSize();
		Dimension psize = getMinimumSize();
		
		int y0;
		int x0;
		
		switch( valign )
		{
			default:
				y0 = 0;
				break;
			
			case Label.CENTER:
				y0 = (size.height-psize.height)/2;
				break;
			
			case Label.RIGHT:
				y0 = size.height - psize.height;
				break;
		}
		
		switch( calign )
		{
			default:
				x0 = spacing;
				break;
			
			case Label.CENTER:
				x0 = (size.width-psize.width)/2;
				break;
			
			case Label.RIGHT:
				x0 = (size.width-psize.width);
				break;
		}
		
		//System.out.println("x0="+x0+" align="+calign);

		g.drawRect( x0, y0, boxSize-1, boxSize-1 );
		
		if( pressed && inside )
		{
			g.drawRect( x0+1, y0+1, boxSize-3, boxSize-3 );
		}
		
		if( state )
		{
			g.drawLine( x0, y0, x0+boxSize-1, y0+boxSize-1 );
			g.drawLine( x0, y0+boxSize-1, x0+boxSize-1, y0 );
		}
		
		paint( g, x0+boxSize+gap, 0, false );
	}

	class Mouser extends MouseAdapter
	{
		RawCheckbox check;
		
		public Mouser( RawCheckbox check )
		{
			this.check = check;
		}
		
		public void mouseEntered( MouseEvent e )
		{
			check.inside = true;
			if( check.pressed )
				check.repaint();
		}
		
		public void mouseExited( MouseEvent e )
		{
			check.inside = false;
			if( check.pressed )
				check.repaint();
		}
		
		public void mousePressed( MouseEvent e )
		{
			check.pressed = true;
			if( check.inside )
				check.repaint();
		}
		
		public void mouseReleased( MouseEvent e )
		{
			check.pressed = false;
			if( check.inside )
			{
				check.setState( !check.getState() );
			}
		}
	}
	
}
