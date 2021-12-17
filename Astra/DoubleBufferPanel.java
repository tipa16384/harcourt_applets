/*
 * @(#)DoubleBufferPanel.java	1.2 97/01/14 Jeff Dinkins
 *
 * Copyright (c) 1995-1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 */


package util;

import java.awt.*;

public class DoubleBufferPanel extends Panel
{
	private transient Image offscreen = null;
	private transient Graphics offscreenGraphics = null;
	private transient boolean bufferingKnown = false;
	private transient boolean overBuffered = false;

	public DoubleBufferPanel()
	{
	}
	
	public DoubleBufferPanel( LayoutManager lm )
	{
		super(lm);
	}
	
	private void killBuffers()
	{
		if ( offscreen != null )
		{
			offscreen.flush();
			offscreen = null;
		}
		
		if ( offscreenGraphics != null )
		{
			offscreenGraphics.dispose();
			offscreenGraphics = null;
		}
	}
	
	public void addNotify()
	{
		killBuffers();
		bufferingKnown = false;
		super.addNotify();
	}
	
	public void removeNotify()
	{
		killBuffers();		
		bufferingKnown = false;
		super.removeNotify();
	}
	
	public void setBounds(int x, int y, int width, int height) 
	{
	    Rectangle b = getBounds();
	    
		if ( (b.width != width) || (b.height != height) )
			killBuffers();
			
       	super.setBounds( x, y, width, height );
    }

	/**
	 * override update to *not* erase the background before painting
	 */
	public void update(Graphics g)
	{
		if( isOverBuffered() )
			super.update(g);
		else
			paint(g);
	}

	/**
	 * paint children into an offscreen buffer, then blast entire image
	 * at once.
	 */
	public synchronized void paint(Graphics g)
	{
		//System.out.println("Repainting "+getClass().getName());
		
		Rectangle bounds = getBounds();
		Rectangle clip = g.getClipRect();
		
		if( bounds.isEmpty() || clip.isEmpty() )
			return;

		//System.out.println("repaint bounds="+bounds+" clip="+clip);
		
		try
		{
			//System.out.print("1...");
			if( offscreen == null )
			{
				offscreen = createImage( bounds.width, bounds.height );
	
				if( offscreen == null )
					throw new Exception("failed to allocate offscreen image");
	
				offscreenGraphics = null;
			}
			
			//System.out.print("2...");
			if( offscreenGraphics == null )
			{
				offscreenGraphics = offscreen.getGraphics();
				if( offscreenGraphics == null )
					throw new Exception("failed to allocate offscreen graphics");
			}
			
			//System.out.print("3...");
			offscreenGraphics.setColor( getBackground() );

			try
			{
				offscreenGraphics.setClip( g.getClip() );
				//System.out.println("clipped to "+g.getClip()+" or "+g.getClipRect());
			}
			
			catch( Exception e )
			{
				//System.err.println("Couldn't transfer clip!");
				offscreenGraphics.setClip( 0, 0, bounds.width, bounds.height );
			}
			
			//System.out.print("4...");
			offscreenGraphics.fillRect(0,0,bounds.width, bounds.height);
	
			//System.out.print("5...");
			
			try
			{
				super.paint( offscreenGraphics );
			}
			
			catch( NullPointerException npe )
			{
				System.out.println("null pointer!");
				System.out.println("#### visible is "+isVisible());
				int n = getComponentCount(), i;
				System.out.println("#### n="+n);
				for( i = 0; i < n; ++i )
					System.out.println("####    "+getComponent(i).getClass().getName());
			}
			
			//System.out.print("6...");
			g.drawImage( offscreen,
				clip.x, clip.y, clip.x+clip.width, clip.y+clip.height,
				clip.x, clip.y, clip.x+clip.width, clip.y+clip.height,
				//getBackground(),
				this );
			
			//System.out.println();
		}
		
		catch( Exception e )
		{
			System.err.println("In util.DoubleBufferPanel: "+e);
		}
	}

	/**
	 * return true if this component is contained within another subclass
	 * of DoubleBufferPanel.
	 */
	
	private boolean isOverBuffered()
	{
		if( bufferingKnown )
		{
			return overBuffered;
		}
		
		else
		{
			Component c;

			for( c = getParent(); c != null && !(c instanceof DoubleBufferPanel);
								c = c.getParent() );		
			
			bufferingKnown = true;
			overBuffered = (c != null);
			
			overBuffered = false;
			
			return overBuffered;
		}
	}
}
