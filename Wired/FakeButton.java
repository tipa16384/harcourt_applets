package Wired;

import java.awt.*;
import java.awt.event.*;

public class FakeButton extends Component
{
	ActionListener actionListener = null;
	final int textPad = 2;	// space text inset from button
	final int edgePad = 1;	// space button inset from edge
	final int pad = textPad + edgePad;
		
	public FakeButton( String name )
	{
		setName(name);
		setFont( new Font("SansSerif",Font.PLAIN,12) );
		addMouseListener( new Mousey() );
	}
	
	public Dimension getMinimumSize()
	{
		String name = getName();
		FontMetrics fm = getFontMetrics(getFont());
		int wid = 2*pad;
		int hgt = fm.getAscent()+fm.getDescent()+2*pad;
		
		if( name != null )
			wid += fm.stringWidth(name);
		
		return new Dimension(wid,hgt);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void paint( Graphics g )
	{
		Dimension size = getSize();
		
		g.setColor( getBackground() );
		g.fillRect( edgePad, edgePad, size.width-2*edgePad, size.height-2*edgePad );
		
		String name = getName();
		if( name == null ) return;
		
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		
		g.setFont(f);
		g.setColor( getForeground() );
		
		int wid = fm.stringWidth(name);
		int x = (size.width-wid)/2;
		int y = (size.height-fm.getAscent()-fm.getDescent())/2+fm.getAscent();
		
		g.drawString( name, x, y );
	}
	
	class Mousey extends MouseAdapter
	{
		Color oldColor = null;
		
		public void mousePressed( MouseEvent e )
		{
			oldColor = getBackground();
			setBackground( oldColor.darker() );
			repaint();
		}
		
		public void mouseReleased( MouseEvent e )
		{
			setBackground( oldColor );
			repaint();
		}
		
		public void mouseClicked( MouseEvent e )
		{
			if( actionListener != null )
			{
				ActionEvent ae = new ActionEvent(FakeButton.this,0,"CLICKEZ VOUS");
	            actionListener.actionPerformed(ae);
			}
		}
	}

    /**
     * Adds the specified action listener to receive action events from
     * this button. Action events occur when a user presses or releases
     * the mouse over this button.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param         l the action listener
     * @see           java.awt.event.ActionListener
     * @see           java.awt.Button#removeActionListener
     * @since         JDK1.1
     */
    public synchronized void addActionListener(ActionListener l)
    {
		if(l != null)
			actionListener = AWTEventMulticaster.add(actionListener, l);
    }

    /**
     * Removes the specified action listener so that it no longer
     * receives action events from this button. Action events occur
     * when a user presses or releases the mouse over this button.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param         	l     the action listener
     * @see           	java.awt.event.ActionListener
     * @see           	java.awt.Button#addActionListener
     * @since         	JDK1.1
     */
    public synchronized void removeActionListener(ActionListener l)
    {
    	if( l != null )
			actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

}
