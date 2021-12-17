import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class SophieChoice extends Component
						  implements ItemSelectable, ActionListener
{
    /**
     * The items for the Choice.
     */
    Vector pItems;

    /** 
     * The index of the current choice for this Choice.
     */
    int selectedIndex = -1;
    
    int halign = Label.CENTER;
    int valign = Label.CENTER;
    
    boolean showLabel = false;
    boolean labelOnTop = true;

	final static int hgap = 3;
	final static int vgap = 1;
	final static int hextra = 12;
	final static int vextra = 0;

    transient ItemListener itemListener = null;

    private static final String base = "sophiechoice";
    private static int nameCounter = 0;

	PopupMenu menu = null;
	RawLabel label = null;

	public SophieChoice()
	{
		this(constructComponentName());
	}
	
	public SophieChoice( String name )
	{
		setName(name);
		pItems = new Vector();
		setBackground( new Color(240,240,240) );
		setForeground( Color.red );
		Font f = new Font("SansSerif",Font.PLAIN,10);
		setFont( f );
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	public String toString()
	{
		return getClass().getName()+"["+getName()+",select="+getSelectedIndex()+"]";
	}

	public void paint( Graphics g )
	{
		int x, y;
		int x0, y0;
		int len;
		
		Dimension size = getSize();
		
		x0 = y0 = 0;
		
		if( showLabel && label != null )
		{
			int rx, ry;
			
			if( labelOnTop )
			{
				rx = x0;
				ry = y0;
				y0 += label.getMinimumSize().height;
			}
			
			else
			{
				rx = x0;
				ry = y0;
				x0 += label.getMinimumSize().width+hgap;
			}
			
			label.paint( g, rx, ry, false );
		}
		
		g.setColor( getBackground() );
		g.fillRoundRect( x0, y0, size.width-1-x0, size.height-1-y0, 6, 6 );
		g.setColor( Color.black );
		g.drawRoundRect( x0, y0, size.width-1-x0, size.height-1-y0, 6, 6 );

		x = size.width-hextra+1;
		g.drawLine( x, y0, x, size.height-1 );
		
		x += 2;
		y = y0+vgap+5;
		
		for( len=4; len>0; --len, ++x, ++y )
		{
			g.drawLine( x, y, x+(2*(len-1)), y );
		}
		
		Font f = getFont();
		String s = getSelectedItem();
		g.setColor( getForeground() );
		if( s != null )
		{
			FontMetrics fm = getFontMetrics( f );
			g.setFont( f );
			
			g.drawString( s, x0+hgap, y0+fm.getAscent()-1+vgap );
		}
	}

	public RawLabel getLabel()
	{
		if( label == null )
		{
			label = new RawLabel(getName(),Label.LEFT,Label.LEFT);
			label.setFont(getFont());
		}

		return label;
	}

	public void actionPerformed( ActionEvent e )
	{
		select(e.getActionCommand());
		
		if( itemListener != null )
		{
			ItemEvent ie = new ItemEvent( this, ItemEvent.ITEM_STATE_CHANGED,
								getSelectedItem(), ItemEvent.SELECTED );
			itemListener.itemStateChanged(ie);
		}
	}
	
	protected void processMouseEvent( MouseEvent e )
	{
		switch( e.getID() )
		{
			case MouseEvent.MOUSE_PRESSED:
				showMenu();
				break;
		}
		
		super.processMouseEvent( e );
	}

	private void clearMenu()
	{
		if( menu != null )
		{
			remove(menu);
			menu = null;
		}
	}
	
	private void showMenu()
	{
		if( menu == null )
		{
			if( pItems != null )
			{
				int len = pItems.size();
				if( len > 0 )
				{
					menu = new PopupMenu();
					add( menu );
		
					MenuItem mi;
					
					for( int i=0; i<len; ++i )
					{
						mi = new MenuItem((String)pItems.elementAt(i));
						menu.add( mi );
						mi.addActionListener(this);
					}
				}
			}
		}
		
		if( menu != null )
		{
			menu.show(this,0,0);
		}
	}
	
	public int getHAlign()
	{
		return halign;
	}
	
	public void setHAlign( int a )
	{
		halign = a;
		invalidate();
	}
	
	public int getVAlign()
	{
		return valign;
	}
	
	public void setVAlign( int a )
	{
		valign = a;
		invalidate();
	}
	
	public void setShowLabel( boolean showit )
	{
		showLabel = showit;
		invalidate();
	}
	
	public boolean getShowLabel()
	{
		return showLabel;
	}
	
	public void setLabelOnTop( boolean ontop )
	{
		labelOnTop = ontop;
		invalidate();
	}
	
	public boolean getLabelOnTop()
	{
		return labelOnTop;
	}
	
	public Dimension getMinimumSize()
	{
		int len = pItems.size();
		int i;
		int w = 0;
		
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		
		for( i=0; i<len; ++i )
		{
			String s = getItem(i);
			if( s != null )
			{
				w = Math.max(w,fm.stringWidth(s));
			}
		}
		
		Dimension dim = new Dimension(w+2*hgap+hextra,
					fm.getAscent()+fm.getDescent()+2*vgap+vextra);
		
		if( showLabel )
		{
			RawLabel xlabel = getLabel();

			Dimension rdim = xlabel.getMinimumSize();
			
			if( labelOnTop )
			{
				dim.height += rdim.height;
				dim.width = Math.max(dim.width,rdim.width);
			}
			
			else
			{
				dim.height = Math.max(dim.height,rdim.height);
				dim.width += rdim.width + hgap;
			}
		}
		
		return dim;
	}

	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void setBounds( int x, int y, int w, int h )
	{
		Dimension dim = getMinimumSize();
		
		switch( halign )
		{
			default:
				break;
			case Label.RIGHT:
				x += w-dim.width;
				break;
			case Label.CENTER:
				x += (w-dim.width)/2;
				break;
		}
		
		switch( valign )
		{
			default:
				break;
			case Label.RIGHT:
				y += h-dim.height;
				break;
			case Label.CENTER:
				y += (h-dim.height)/2;
				break;
		}
		
		super.setBounds( x, y, dim.width, dim.height );
	}

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    static String constructComponentName()
    {
        return base + nameCounter++;
    }

    /**
     * Returns the number of items in this <code>Choice</code> menu.
     * @see     java.awt.Choice#getItem
     * @since   JDK1.1
     */
    public int getItemCount()
    {
		return pItems.size();
    }

    /**
     * Gets the string at the specified index in this 
     * <code>Choice</code> menu.
     * @param      index the index at which to begin.
     * @see        java.awt.Choice#getItemCount
     * @since      JDK1.0
     */
    public String getItem(int index)
    {
		return (String)pItems.elementAt(index);
    }

    /**
     * Adds an item to this <code>Choice</code> menu.
     * @param      item    the item to be added
     * @exception  NullPointerException   if the item's value is <code>null</code>.
     * @since      JDK1.1
     */
    public void add(String item)
    {
		addItem(item);
    }
    
     /**
     * Adds an item to this Choice.
     * @param item the item to be added
     * @exception NullPointerException If the item's value is equal to null.
     */
    public synchronized void addItem(String item)
    {
		if(item == null)
		{
	    	throw new NullPointerException("cannot add null item to SophieChoice");
		}
		
		pItems.addElement(item);
		
		if( selectedIndex < 0 )
			select( 0 );
		
		invalidate();
		clearMenu();
	}

    /**
     * Inserts the item into this choice at the specified position.
     * @param item the item to be inserted
     * @param index the position at which the item should be inserted
     * @exception IllegalArgumentException if index is less than 0.
     */

    public synchronized void insert(String item, int index)
    {
		if (index < 0)
		{
		    throw new IllegalArgumentException("index less than zero.");
		}

        int nitems = getItemCount();
		Vector tempItems = new Vector();

		/* Remove the item at index, nitems-index times 
		   storing them in a temporary vector in the
		   order they appear on the choice menu.
		   */
		for (int i = index ; i < nitems; i++)
		{
		    tempItems.addElement(getItem(index));
		    remove(index);
		}
	
		add(item);

		/* Add the removed items back to the choice menu, they 
		   are already in the correct order in the temp vector.
		   */
		for (int i = 0; i < tempItems.size()  ; i++)
		{
		    add((String)tempItems.elementAt(i));
		}
    }

    /**
     * Remove the first occurrence of <code>item</code> 
     * from the <code>Choice</code> menu.
     * @param      item  the item to remove from this <code>Choice</code> menu.
     * @exception  IllegalArgumentException  if the item doesn't 
     *                     exist in the choice menu.
     * @since      JDK1.1
     */
    public synchronized void remove(String item)
    {
    	int index = pItems.indexOf(item);
    	if (index < 0)
    	{
		    throw new IllegalArgumentException("item " + item +
						       " not found in choice");
		}
	
		else
		{
		    remove(index);
		}
    }

    /**
     * Removes an item from the choice menu 
     * at the specified position.
     * @param      position the position of the item.
     * @since      JDK1.1
     */
    public synchronized void remove(int position)
    {
    	pItems.removeElementAt(position);
		clearMenu();

    	/* Adjust selectedIndex if selected item was removed. */
    	if (pItems.size() == 0)
    	{
		    selectedIndex = -1;
		}
		else if (selectedIndex == position)
		{
	    	select(0);
		}
		else if (selectedIndex > position)
		{
	    	select(selectedIndex-1);
		}
    }

    /**
     * Removes all items from the choice menu.
     * @see       java.awt.Choice#remove
     * @since     JDK1.1
     */
    public synchronized void removeAll()
    {
        int nitems = getItemCount();
		for (int i = 0 ; i < nitems ; i++)
		{
	    	remove(0);
		}
		repaint();
    }

    /**
     * Gets a representation of the current choice as a string.
     * @return    a string representation of the currently 
     *                     selected item in this choice menu.
     * @see       java.awt.Choice#getSelectedIndex
     * @since     JDK1.0
     */
    public synchronized String getSelectedItem()
    {
		return (selectedIndex >= 0) ? getItem(selectedIndex) : null;
    }

    /**
     * Returns an array (length 1) containing the currently selected
     * item.  If this choice has no items, returns null.
     * @see ItemSelectable
     */
    public synchronized Object[] getSelectedObjects()
    {
		if (selectedIndex >= 0)
		{
            Object[] items = new Object[1];
            items[0] = getItem(selectedIndex);
            return items;
        }
        return null;
    }

    /**
     * Returns the index of the currently selected item.
     * @see #getSelectedItem
     */
    public int getSelectedIndex()
    {
		return selectedIndex;
    }

    /**
     * Sets the selected item in this <code>Choice</code> menu to be the 
     * item at the specified position. 
     * @param      pos      the positon of the selected item.
     * @exception  IllegalArgumentException if the specified
     *                            position is invalid.
     * @see        java.awt.Choice#getSelectedItem
     * @see        java.awt.Choice#getSelectedIndex
     * @since      JDK1.0
     */
    public void select(int pos)
    {
		if (pos >= pItems.size())
		{
	    	throw new IllegalArgumentException("illegal Choice item position: " + pos);
		}
	
		if (pItems.size() > 0)
		{
	    	selectedIndex = pos;
			repaint();
		}
    }

    /**
     * Sets the selected item in this <code>Choice</code> menu 
     * to be the item whose name is equal to the specified string. 
     * If more than one item matches (is equal to) the specified string, 
     * the one with the smallest index is selected. 
     * @param       str     the specified string
     * @see         java.awt.Choice#getSelectedItem
     * @see         java.awt.Choice#getSelectedIndex
     * @since       JDK1.0
     */
    public synchronized void select(String str)
    {
		int index = pItems.indexOf(str);
		if (index >= 0)
		{
		    select(index);
		}
    }

    /**
     * Adds the specified item listener to receive item events from
     * this <code>Choice</code> menu.
     * @param         l    the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.Choice#removeItemListener
     * @since         JDK1.1
     */ 
    public synchronized void addItemListener(ItemListener l)
    {
        itemListener = AWTEventMulticaster.add(itemListener, l);
    }

    /**
     * Removes the specified item listener so that it no longer receives 
     * item events from this <code>Choice</code> menu. 
     * @param         l    the item listener.
     * @see           java.awt.event.ItemEvent
     * @see           java.awt.event.ItemListener
     * @see           java.awt.Choice#addItemListener
     * @since         JDK1.1
     */ 
    public synchronized void removeItemListener(ItemListener l)
    {
        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /**
     * Processes events on this choice. If the event is an 
     * instance of <code>ItemEvent</code>, it invokes the 
     * <code>processItemEvent</code> method. Otherwise, it calls its
     * superclass's <code>processEvent</code> method.
     * @param      e the event.
     * @see        java.awt.event.ItemEvent
     * @see        java.awt.Choice#processItemEvent
     * @since      JDK1.1
     */
    protected void processEvent(AWTEvent e)
    {
        if (e instanceof ItemEvent)
        {
            processItemEvent((ItemEvent)e);
            return;
        }
        
		super.processEvent(e);
    }

    /** 
     * Processes item events occurring on this <code>Choice</code> 
     * menu by dispatching them to any registered 
     * <code>ItemListener</code> objects. 
     * <p>
     * This method is not called unless item events are 
     * enabled for this component. Item events are enabled 
     * when one of the following occurs:
     * <p><ul>
     * <li>An <code>ItemListener</code> object is registered 
     * via <code>addItemListener</code>.
     * <li>Item events are enabled via <code>enableEvents</code>.
     * </ul>
     * @param       e the item event.
     * @see         java.awt.event.ItemEvent
     * @see         java.awt.event.ItemListener
     * @see         java.awt.Choice#addItemListener
     * @see         java.awt.Component#enableEvents
     * @since       JDK1.1
     */  
    protected void processItemEvent(ItemEvent e)
    {
        if (itemListener != null)
        {
            itemListener.itemStateChanged(e);
        }
    }

    /**
     * Returns the parameter string representing the state of this 
     * choice menu. This string is useful for debugging. 
     * @return    the parameter string of this <code>Choice</code> menu.
     * @since     JDK1.0
     */
    protected String paramString()
    {
		return super.paramString() + ",current=" + getSelectedItem();
    }


}
