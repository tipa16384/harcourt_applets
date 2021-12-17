
/*

		  File:	ListComponent.java
	  Contains:	Hierarchical list view component AWT class
		
	  Copyright 1997 Steve Klingsporn <moofie@pobox.com>		
*/

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

/**
	A nifty little list view component written in Java.
**/
public class ListComponent extends Panel
implements MouseListener, AdjustmentListener, ItemSelectable
{
	/**
		Default shaded item background color
	**/
	public static final Color			DEFAULT_SHADED_ITEM_COLOR	= new Color(217, 217, 217);
		
	/**
		The event code that is returned when an item is added to the list
	**/
	public static final int				ITEM_ADDED 			= 0;
	
	/**
		The event code that is returned when an item is removed from the list
	**/
	public static final int				ITEM_REMOVED 		= 1;
	
	/**
		The event code that is returned when all items are removed from the list
	**/
	public static final int				ALL_ITEMS_REMOVED 	= 2;
	
	/**
		The event code that is returned when an item in the list is collapsed
	**/
	public static final int				ITEM_COLLAPSED 		= 3;
	
	/**
		The event code that is returned when an item in the list is expanded
	**/
	public static final int				ITEM_EXPANDED 		= 4;
	
	/**
		The event code that is returned when an item in the list is deselected
	**/
	public static final int				ITEM_DESELECTED 	= 5;
	
	/**
		The event code that is returned when an item in the list is selected
	**/
	public static final int				ITEM_SELECTED 		= 6;

	protected int						_numItems = 0;
	protected Scrollbar					_scrollbar = null;
	protected Image						_expanderExpandedIcon = null;
	protected Image						_expanderCollapsedIcon = null;
    protected Image						_expanderMovingIcon = null;
    protected Color						_shadedItemColor = DEFAULT_SHADED_ITEM_COLOR;
    protected boolean					_shadeAlternateItems = false;
	protected boolean					_shadeFlag = false;
	protected FontMetrics				_fontMetrics = null;
	protected int						_fontAscent = 0;
	protected int						_fontDescent = 0;
	protected ListItem					_selectedItem = null;
	protected boolean					_reportItemEvents = true;
	protected ItemListener				_itemListener = null;
	protected boolean					_ignoreMouseEvents = false;
	protected boolean					_indentedExpanders = true;
	protected int						_numDrawnItems = 0;
	protected boolean					_canSelectItems = true;
	protected boolean					_expanderAnimation = true;
	protected int						_lastScrollbarValue = 0;
	protected ListItem					_firstItem = null;
	protected ListItem					_lastItem = null;
	protected ListItem					_topItem = null;
	
	
	/**
		Constructor
	**/
	public ListComponent()
	{
		super();
		initialize();
	}
	
	
	/**
		Constructor that allows you to set the width and height.
		@param width the width to set this component to
		@param height the height to set this component to
	**/
	public ListComponent(int width, int height)
	{
		super();
		setSize(width, height);
		initialize();
	}
	
	
	/**
		Constructor that's best used when adding to a component that
		has a null LayoutManager.  Allows you to specify absolute shape.
		@param left the left coordinate for this component
		@param top the top coordinate for this component
		@param right the right coordinate for this component
		@param bottom the bottom coordinate for this component
	*/
	public ListComponent(int left, int top, int width, int height)
	{
		super();
		setBounds(left, top, width, height);
		initialize();
	}
	
	
	/*
		Initializes the component by setting its LayoutManager, adding
		the scrollbar, initializing the items Vector, resetting the items
		counter, and adding ourself as a MouseListener.  We're registered as
		an AdjustmentListener so we can get word back from the scrollbar when
		it has changed.
	*/
	protected void initialize()
	{
		setBackground(Color.white);
		setLayout(new BorderLayout(0, 0));
		_scrollbar = new Scrollbar(Scrollbar.VERTICAL);
		_scrollbar.setMinimum(0);
		_scrollbar.setVisibleAmount(1);
		_scrollbar.setValue(0);
		_scrollbar.addAdjustmentListener(this);
		add("East", _scrollbar);
		addMouseListener(this);
	}
		

	/**
		Sets the expander icons (do this before you go on screen)
		@param collapsedImage the image for a collapsed node
		@param expandedImage the image for an expanded node
		@param movingImage the image for a node in transition
	**/
	public final void setExpanderIcons(Image collapsedImage, Image expandedImage, Image movingImage)
	{
		_expanderCollapsedIcon = collapsedImage;
		_expanderExpandedIcon = expandedImage;
		_expanderMovingIcon = movingImage;

		if (_numItems > 0 && isVisible())
		{
			Graphics	graphics = getGraphics(); // inline repaint
			if (graphics != null)
			{
				paint(graphics);
				graphics.dispose();
			}
		}
	}
	
	
	/**
		Returns whether or not alternately displayed ListItems have
		shaded backgrounds
		@return true if background shading is enabled
	**/
	public final boolean getShadeAlternateItems()
	{
		return _shadeAlternateItems;
	}
	
	
	/**
		Sets whether or not alternately displayed ListItems should
		have a shaded background.
		@param flag true to enable background shading
	**/
	public final void setShadeAlternateItems(boolean flag)
	{
		if (_shadeAlternateItems != flag)
		{
			_shadeAlternateItems = flag;
			
			if (isVisible() && _numItems > 0)	// inline repaint
			{
				Graphics	graphics = getGraphics();
				if (graphics != null)
				{
					paint(graphics);
					graphics.dispose();
				}
			}
		}
	}
			

	/**
		Returns the shaded item background color
		@return the shaded item background color
	**/
	public final Color getShadedItemColor()
	{
		return _shadedItemColor;
	}
	
	
	/**
		Sets the shaded item background color
		@param color the new color
	**/
	public final void setShadedItemColor(Color color)
	{
		if (color != null)
			_shadedItemColor = color;
			
		if (_shadeAlternateItems && isVisible())	// inline repaint
		{
				Graphics	graphics = getGraphics();
				if (graphics != null)
				{
					paint(graphics);
					graphics.dispose();
				}
		}
	}
	
	
	/**
		Convenience method to determine if we are empty
		@returns true if we have no items
	**/
	public final boolean isEmpty()
	{
		if (_numItems > 0)
			return false;
		else
			return true;
	}


	/**
		Returns whether or not items can be selected
		@return true if items in this list can be selected
	**/
	public final boolean getCanSelectItems()
	{
		return _canSelectItems;
	}
	
	
	/**
		Sets whether or not items can be selected
		#param flag whether or not items can be selected in this list
	**/
	public final void setCanSelectItems(boolean flag)
	{
		if (flag != _canSelectItems)
		{
			_canSelectItems = flag;

			if (! flag)
			{
				_selectedItem = null;
			}
			else
			{
				_selectedItem = null;
			}
		}
	}
	
	
	/**
		Returns whether or not we animate the expanders
		@return flag whether or not expanders are animated when they change
	**/
	public final boolean getExpanderAnimation()
	{
		return _expanderAnimation;
	}
	
	
	/**
		Sets whether or not we animate the expanders
		@param flag whether or not expanders should be animated when they change
	**/
	public final void setExpanderAnimation(boolean flag)
	{
		if (flag != _expanderAnimation)
			_expanderAnimation = flag;
	}
	
	
	/**
		Adds an item to the list
		@param item the item to add
	**/
	public final void addItem(ListItem item)
	{
		addItem(item, false);
	}
	
	
	/**
		Adds an item to the list, at the beginning if desired
		@param item the ListItem to add
		@param atBeginning whether nor not to add at the beginning
	**/
	public final void addItem(ListItem item, boolean atBeginning)
	{
		if (item != null)
		{
			if (_firstItem != null)
			{			
				if (atBeginning)
				{
					item._next = _firstItem;
					_firstItem._previous = item;
					_firstItem = item;
				}
				else
				{
					_lastItem._next = item;
					item._previous = _lastItem;
					_lastItem = item;
				}
			}
			else
				_firstItem = _lastItem = _topItem = item;
			
			item._listComponent = this;
			item._parent = null;
			item._level = 0;
			_numItems++;
			
			if (_reportItemEvents)
			{
				ItemEvent event = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, item, ListComponent.ITEM_ADDED);
				processEvent(event);
			}
		}
	}


	/**
		Removes an ListItem from the list.
		@param item the ListItem to remove
	**/
	public final void removeItem(ListItem item)
	{
		//  BROKEN BROKEN BROKEN BROKEN BROKEN BROKEN
		int		itemIndex;
		
		if (item != null && item._parent == item)
		{
			boolean		itemWasVisible = item.isVisible();
			
			//_items.removeElement(item);
			_numItems--;
			item._listComponent = null;
			item._parent = item;
			item._level = -1;
			item.detachedFromComponent(this);

			if (_reportItemEvents)
			{
				//  The item will probably be GC'd after this call
				ItemEvent event = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, item, ITEM_REMOVED);
				processEvent(event);
			}
			
			if (isVisible() && itemWasVisible)
			{
				Graphics	graphics = getGraphics();	// inline repaint
				if (graphics != null)
				{
					paint(graphics);
					graphics.dispose();
				}
			}
		}
	}
	
	
	/**
		removeAllItems
		Removes all items from the list view.
	**/
	public final void removeAllItems()
	{
		//  BROKEN BROKEN BROKEN BROKEN BROKEN BROKEN
	}
	

	/*
		Finds a visble item, given a vertical test point
		@param y the vertical point to test
		@return the ListItem that contains the vertical point given or null
	*/
	protected final ListItem findVisibleItem(int y)
	{
		if (_numItems > 0)
		{			
			Dimension	size = getSize();
			ListItem 	item = _topItem;
			
			do
			{
				if (y >= item._top && y <= (item._top + item._height))
					return item;
				else
					item = item.forward();
			} while (item != null && item._top <= size.height);
		}
		return null;
	}
				

	/**
		Called when the mouse button is pressed.  Finds the visible item
		at the mouse point (if any, there generally would be one), and passes
		the mousePressed call on to the item if one is found to handle the event.
		@param event the event that was received
	**/
	public final void mousePressed(MouseEvent event)
	{
		int			y;
		ListItem	item;
		
		if (_ignoreMouseEvents)
			return;
			
		y = event.getY();
		item = findVisibleItem(y);
		if (item == null)
			return;

		Graphics graphics = this.getGraphics();
		item.mousePressed(event, event.getX(), y, graphics);
		graphics.dispose();
	}


	/*
		A little magic genie that knows how to draw
		@param graphics the graphics context to draw into
		@param item the ListItem to start drawing with
		@param startAtTop start drawing at the top of the view?
	*/
	protected final void paintItems(Graphics graphics, ListItem item, boolean startAtTop)
	{
		int			y = item._top;
		Dimension	size = getSize();
		
		//  Ignore all mouse events while drawing
		_ignoreMouseEvents = true;
				
		if (startAtTop)
		{
			y = 0;
			_shadeFlag = false;
		}
		else
			_shadeFlag = item._isShaded;
		
		do
		{
			item._top = y;
			item._isShaded = _shadeFlag;
				
			if (_shadeAlternateItems && _shadeFlag)
				graphics.setColor(_shadedItemColor);
			else
				graphics.setColor(getBackground());
			graphics.fillRect(0, item._top, size.width, item._height);
			
			item.paint(graphics);
			
			_shadeFlag = !_shadeFlag;
			y += item._height;
			item = item.forward();
		} while (y < size.height && item != null);
		
		_ignoreMouseEvents = false;	// after if need be...
		
		if (y < size.height)
		{
			graphics.setColor(getBackground());
			graphics.fillRect(0, y, size.width, (size.height - y));
		}
	}
	
	
	/**
		Patches update so we don't flicker
		@param graphics the Graphics context being updated into
	**/
	public final void update(Graphics graphics)
	{	
		paint(graphics);
	}
			
	
	/**
		Paints the component
		@param graphics the graphics context to paint into
	**/
	public final void paint(Graphics graphics)
	{		
		if (_numItems > 0)
		{	
			if (_fontMetrics == null)
				calculateFontInfo(graphics.getFont());
				
			if (_topItem == null)
				_topItem = _firstItem;
				
			paintItems(graphics, _topItem, true);
		}
		else
		{
			Dimension size = getSize();
			graphics.setColor(getBackground());
			graphics.fillRect(0, 0, size.width, size.height);
		}
	}


	/**
		Returns whether or not we have indented expanders
		@return whether or not expanders are drawn indented
	**/
	public final boolean getIndentedExpanders()
	{
		return _indentedExpanders;
	}
	
	
	/**
		Sets whether or not we have indented expanders
		@param flag whether or not expanders are drawn indexed
	**/
	public final void setIndentedExpanders(boolean flag)
	{
		if (_indentedExpanders != flag)
		{
			_indentedExpanders = flag;
			if (isVisible())
			{
				Graphics	graphics = getGraphics();
				if (graphics != null)
				{
					paint(graphics);
					graphics.dispose();
				}
			}
		}
	}
	
		
	/**
		Patch to setFont to process the font being set
		and call the inherited setFont method.
		@param font the font to set this component to
	**/
	public synchronized final void setFont(Font font)
	{
		calculateFontInfo(font);
		super.setFont(font);
	}
	
	
	/*
		Calculates interesting cached font information
	*/
	protected final void calculateFontInfo(Font font)
	{
		if (font != null)
		{
			_fontMetrics = getFontMetrics(font);
			_fontAscent = _fontMetrics.getAscent();
			_fontDescent = _fontMetrics.getDescent();
		}
	}
		

	/**
		Returns the ListItems in the list and its sub-items
		which are selected.
		@return an array of selected ListItem objects
	**/
	public final ListItem[] getSelectedItems()
	{
		return null;
	}
	
	
	/**
		Returns an array of the selected items
		@return an array of selected ListItem objects
	**/
	public final Object[] getSelectedObjects()
	{
		return getSelectedItems();
	}
	
	
	/**
		Adds an ItemListener to listen for item selections
		@param listener the listener to add
	**/
	public synchronized final void addItemListener(ItemListener listener)
	{
		_itemListener = AWTEventMulticaster.add(_itemListener, listener);
	}
	
	
	/**
		Removes an ItemListener to listen for item selections
		@param listener the listener to add
	**/
	public final void removeItemListener(ItemListener listener)
	{
		_itemListener = AWTEventMulticaster.remove(_itemListener, listener);
	}
	
	
	/**
		Called when the mouse enters this component.  Override to
		do something nifty.
		@param event the event received
	**/
	public final void mouseEntered(MouseEvent unused)
	{
	}
	
	
	/**
		Called when the component is hidden.  Override to
		do something nifty.
		@param event the event received
	**/
	public final void mouseExited(MouseEvent unused)
	{
	}
	
	
	/**
		Called when the component is hidden.  Override to
		do something nifty.
		@param event the event received
	**/
	public final void mouseClicked(MouseEvent event)
	{
	}
	
	
	/**
		Called when the mouse is released in this component.  Override it in a
		subclass to do something nifty.
		@param event the event received
	**/
	public void mouseReleased(MouseEvent event)
	{
	}
		
	
	/**
		Processes events
		@param even the AWTEvent received
	**/
	protected final void processEvent(AWTEvent event) 
	{
        if (event instanceof ItemEvent)
        {
            processItemEvent((ItemEvent)event);
        }
		super.processEvent(event);
    }
    
    
    /**
    	Processes item events
    	@param event the ItemEvent received
    **/
    protected final void processItemEvent(ItemEvent event)
    {
    	if (_itemListener != null)
    		_itemListener.itemStateChanged(event);
    }
    
    
    /**
    	Turns item event reporting on or off
    	@param flag whether or not item events should be sent to ItemListeners
    **/
    public final void setReportItemEvents(boolean flag)
    {
    	_reportItemEvents = flag;
    }
    
    
    /**
    	Returns whether or not item events are being reported
    	@return whether or not item events are being reported
    **/
    public final boolean getReportItemEvents()
    {
    	return _reportItemEvents;
    }
    
    
    /**
    	Called when the scrollbar value has changed
    	@param event the AdjustmentEvent from the scrollbar
    **/
    public synchronized final void adjustmentValueChanged(AdjustmentEvent event)
    {
       	int value = event.getValue();

    	if (value != _lastScrollbarValue)
    	{
    		_topItem = findNthPotentiallyVisibleItem(value);
    		repaint(1);	// don't inline repaint() here because it fucks everything up
    	}
    	_lastScrollbarValue = value;
    }
    
    
    /*
    	Called to count the total number of visible items
    	@return the number of items in the entire list that could be visible
    	if they were scrolled to.
    */
    protected final int countPotentiallyVisibleItems()
    {
    	int		itemCount = 0;
    	
    	if (_numItems > 0)
    	{
    		ListItem	item = _firstItem;
    		while (item != null)
    		{
    			item = item.forward();
    		    itemCount++;
    		}
    	}
   		return itemCount;
  	}
    
    
    /*
    	Returns the potentially visible height
    */    
    protected final int getPotentiallyVisibleHeight()
    {
    	int		height = 0;
    	
    	if (_numItems > 0)
    	{
    		ListItem	item = _firstItem;
    		while (item != null)
    		{
    			height += item._height;
    			item = item.forward();
    		}
    	}
   		return height;
  	}


    /*
    	Returns the nth potentially visible item in the tree
    	from the first item in the list.
    	@param n the nth potentially visible item to find
    	@return the nth potentially visible item or null
    */
    protected final ListItem findNthPotentiallyVisibleItem(int n)
    {
    	ListItem	item = _firstItem;
    	
    	if ( n > 0 && _numItems > 0)
    		return item.forward(n);
    	else
    		return item;
  	}
  	
  	
  	/**
  		Finds an item in the tree, with a name
  	**/
  	public final ListItem findItemByName(String name, boolean deep)
    {
    	ListItem	foundItem = null;
    	
    	if (_numItems > 0)
    	{
    		ListItem	item = _firstItem;
    		
    		while (item != null)
    		{
    			if (item._name.equals(name))
    				return item;
    				
    			if (item._numItems > 0 && deep)
    			{
    				foundItem = item.findItemByName(name, true);
    				if (foundItem != null)
    					return foundItem;
    			}
    	    	item = item._next;
    	    }
    	}
    	return null;
    }


  	/**
    	Finds an item, given a path from the root of the tree.  The
    	path delimiter is the forward slash ("/").
    	@param path the path to the item, by name
    	@return the item found, or null
    **/
    public final ListItem findItemByPath(String path)
    {
    	if (_numItems > 0 && path != null)
    	{
    		StringTokenizer		toker = new StringTokenizer(path, "/", false);
    		
    		if (toker.countTokens() > 0)
    		{
    			ListItem	item = findItemByName(toker.nextToken(), false);
    			
    			if (item != null)
    			{
    				do
    				{
    					item = item.findItemByName(toker.nextToken(), false);
    					if (item == null)
    						return null;
    				} while (toker.hasMoreTokens());
    				return item;
    			}
    			else
    				return null;
    		}
    		else
    			return null;
    	}
    	else
    		return null;
    }
      
}