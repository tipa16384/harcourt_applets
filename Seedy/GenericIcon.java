package Wired;

//package util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;
import	java.applet.*;
//import	Main.*;

public class GenericIcon extends Component
{
	Image icon = null;
	String label;
	String actionCommand;
	ActionListener owner;
	int totalFrames;
	int currentFrame;
	int scroll;
	
    transient ActionListener actionListener;

    GenericIcon( ActionListener owner, String command )
    {
		//setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
		this.owner = owner;
		if( owner != null )
			addActionListener( owner );
		setLabel( command );
		totalFrames = 1;
		currentFrame = 0;
		scroll = 0;
    }

//	public GenericIcon( URL imageURL, ActionListener owner, String command )
//	{
//		this(owner,command);
//		if( imageURL != null )
//			//setIcon(getToolkit().getImage(imageURL));	//doesn't work with applet security
//			setIcon(Main.globochem.getImage(imageURL));
//	}

	public GenericIcon( String imageFileName, ActionListener owner, String command )
	{
		this(owner,command);
		if( imageFileName != null )
		{
			//System.out.println("imageFileName is "+imageFileName);
			setIcon(getToolkit().getImage(imageFileName));
		}
	}

	public GenericIcon( Image image, ActionListener owner, String command )
	{
		this(owner,command);
		
		if( image != null )
			setIcon(image);
	}

	public void setTotalFrames( int n )
	{
		if( n <= 0 ) n = 1;
		if( totalFrames != n )
		{
			totalFrames = n;
			//setSize( getPreferredSize() );
			//System.out.println("totalFrames = "+n);
			currentFrame = 0;
			repaint();
		}
	}
	
	public void setScroll( int s )
	{
		scroll = s;
		repaint();
	}
	
	public int getScroll()
	{
		return scroll;
	}
	
	public void setCurrentFrame( int n )
	{
		if( n < 0 ) n = 0;
		else if( n >= totalFrames ) n = totalFrames - 1;
		if( currentFrame != n )
		{
			currentFrame = n;
			//System.out.println("currentFrame = "+n);
			repaint();
		}
	}

	public int getCurrentFrame()
	{
		return currentFrame;
	}
	
	public int getTotalFrames()
	{
		return totalFrames;
	}

    public void setIcon( Image image )
    {
    	//System.out.println("Image is "+image);
		icon = image;
		
		MediaTracker mt = new MediaTracker( this );
		mt.addImage( icon, 0 );

		try
		{
			//System.out.println("Waiting for "+icon);
			mt.waitForID(0, 2000);
			if( mt.isErrorID(0) )
				System.err.println("Error "+mt.statusID(0,true)+" while loading");
		}
		catch( Exception ie )
		{
			System.err.println("While loading image: "+ie);
		}
    }
    
    public Image getIcon()
    {
    	return icon;
    }

    /**
     * Gets the label of the button.
     * @see #setLabel
     */
    public String getLabel()
    {
		return label;
    }

    /**
     * Sets the button with the specified label.
     * @param label the label to set the button with
     * @see #getLabel
     */
    public synchronized void setLabel(String label)
    {
		this.label = label;
    }

    /**
     * Sets the command name of the action event fired by this button.
     * By default this will be set to the label of the button.
     */
    public void setActionCommand(String command)
    {
        actionCommand = command;
    }

    /**
     * Returns the command name of the action event fired by this button.
     */
    public String getActionCommand()
    {
        return (actionCommand == null? label : actionCommand);
    }

    /**
     * Adds the specified action listener to receive action events
     * from this button.
     * @param l the action listener
     */ 
    public synchronized void addActionListener(ActionListener l)
    {
		actionListener = AWTEventMulticaster.add(actionListener, l);
		if( actionListener != null )
			enableEvents( AWTEvent.MOUSE_EVENT_MASK );
    }

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this button.
     * @param l the action listener
     */
    public synchronized void removeActionListener( ActionListener l )
    {
		actionListener = AWTEventMulticaster.remove( actionListener, l ); 
		if( actionListener == null )
			disableEvents( AWTEvent.MOUSE_EVENT_MASK );
    }

	public String toString()
	{
		return getClass().getName()+"["+getActionCommand()+",bounds="+getBounds()+"]";
	}

	protected void processMouseEvent( MouseEvent e )
	{
		if( e.getID() == MouseEvent.MOUSE_PRESSED && actionListener != null )
		{
			ActionEvent ae = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,getActionCommand());
			actionListener.actionPerformed( ae );
		}
		
		super.processMouseEvent( e );
	}

	public Dimension getMinimumSize()
	{
		if( icon == null )
			return new Dimension(16,16);

		int iwidth = icon.getWidth( this );
		int iheight = icon.getHeight( this );

		if( iwidth == -1 || iheight == -1 )
		{
			// I want image loading to be centralized, these individual media trackers should
			// never be invoked
			MediaTracker mt = new MediaTracker( this );
			mt.addImage( icon, 0 );

			try
			{
				mt.waitForAll(1000);
				iwidth = icon.getWidth( this );
				iheight = icon.getHeight( this );
			}

			catch( InterruptedException ie )
			{                                    
				iwidth = iheight = 16;
				System.err.println("The image didn't load in time.");
			}
		}

		Dimension size = new Dimension(iwidth,iheight/totalFrames);

		return size;
	}

    public Dimension getPreferredSize(){ return getMinimumSize(); }

	public void paintAt( Graphics g, int x, int y )
	{
		if( icon != null )
		{
			//System.out.println("Painting "+this+" at ("+x+","+y+")");

			Dimension size = getSize();
			Dimension isiz = getMinimumSize();

			g.drawImage( icon,
						x, y, x+size.width, y+size.height,
						0, currentFrame*isiz.height,
						isiz.width, (currentFrame+1)*isiz.height,
						this );
		}
	}
	
	public void update( Graphics g )
	{
		paint(g);
	}

	public void paint( Graphics g )
	{
		if( icon != null )
		{
			//System.out.println("Painting "+this);

			Dimension size = getSize();
			Dimension isiz = getMinimumSize();

			//System.out.println("size="+size+" isiz="+isiz);

			g.drawImage( icon,
						0, 0, size.width, size.height,
						0, currentFrame*isiz.height+scroll,
						isiz.width, (currentFrame+1)*isiz.height+scroll,
						this );
		}
	}
	
	public void flush()
	{
		if ( icon != null )
		{
			icon.flush();
		}
	}
	
    protected void finalize() throws Throwable
    {
    	if( icon != null )
    	{
			//System.out.println("GenericIcon flushed an image!");
    		icon.flush();
    		icon = null;
    	}

    	super.finalize();
    }
}
