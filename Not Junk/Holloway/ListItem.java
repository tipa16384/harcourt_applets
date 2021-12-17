
/*

		  File:	ListItem.java
	  Contains:	Hierarchical list item class
	Written by:	Steve Klingsporn <moofie@pobox.com>
	
	Copyright 1997 Steve Klingsporn.  All rights reserved.
	
*/

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ItemEvent;
import java.util.StringTokenizer;

/**
	A ListItem class that works with a ListComponent.  Construct a new
	ListItem by passing its name or its name, icon and selected icon.
	Subclass this object to draw your own types of items.
	@author Steve Klingsporn (moofie@pobox.com)
**/
public class ListItem
{
	/**
		The default name for new items without a name
	**/
	public static final String		DEFAULT_ITEM_NAME = "-";
	
	/**
		The default height for new items (manually change this for now) ** FIX **
	**/
	public static final int			DEFAULT_ITEM_HEIGHT = 20;
	
	protected ListComponent			_listComponent = null;
	protected ListItem				_parent = null;
	protected ListItem				_previous = null;
	protected ListItem				_next = null;
	protected ListItem				_firstSubItem = null;
	protected ListItem				_lastSubItem = null;
	protected String				_name = DEFAULT_ITEM_NAME;
	protected Image					_icon = null;
	protected Image					_selectedIcon = null;
	protected int					_numItems = 0;
	protected int					_height = 0;
	protected int					_top = 0;
	protected int					_level = -1;
	protected int					_nameStringWidth = -1;
	protected boolean				_isExpanded = false;
	protected boolean				_isSelected = false;
	protected boolean				_isShaded = false;

	
	/**
		Constructor
	**/
	public ListItem()
	{
		initialize();
	}
	
	
	/**
		Constructor
		@param name the name for this item
	**/
	public ListItem(String name)
	{
		_name = name;
		initialize();
	}
	

	/**
		Constructor
		@param name the name for this item
		@param icon the icon for this item
		@param selectedIcon the selected icon for this item
	**/
	public ListItem(String name, Image icon, Image selectedIcon)
	{
		_name = name;
		_icon = icon;
		_selectedIcon = selectedIcon;
		initialize();
	}
	
	
	/*
		Initializes this item by setting its default height
	*/
	protected void initialize()
	{
		_height = DEFAULT_ITEM_HEIGHT;  // set the default height initially
	}

	
	
	/**
		Hook method that gets called when the item gets
		removed from a ListComponent
		@param abandoner the ListComponent that detached us
	**/
	public void detachedFromComponent(ListComponent abandoner)
	{
	}
	
	
	/**
		Returns the item's parent ListItem or this if self
		@return the parent of this item
	**/
	public final ListItem getParent()
	{
		return _parent;
	}
	
	
	/**
		Returns the parent ListComponent for this item
		@return the ListComponent this item is attached to
	**/
	public final ListComponent getListComponent()
	{
		return _listComponent;
	}
	
	
	/**
		Returns the name of this item
		@return the String name of this item
	**/
	public final String getName()
	{
		return _name;
	}
	
	
	/**
		Returns the icon for this item
		@return the Image for this item's icon
	**/
	public final Image getIcon()
	{
		return _icon;
	}
	
	
	/**
		Returns the selected icon for this item
		@return the Image for this item's selected icon
	**/
	public final Image getSelectedIcon()
	{
		return _selectedIcon;
	}
	
	
	/**
		Sets the icons for this item
		@param icon the icon for this item
		@param selectedIcon the selected icon for this item
	**/
	public final void setIcons(Image icon, Image selectedIcon)
	{
		_icon = icon;
		_selectedIcon = selectedIcon;
		
		if (_listComponent != null)
		{
			if (_listComponent.isVisible())
			{
				Graphics	graphics = _listComponent.getGraphics();
				if (graphics != null)
				{
					_listComponent.paint(graphics);
					graphics.dispose();
				}
			}
		}
	}
			
	
	/**
		Sets the name of this item and causes an event to happen
		@param name the name to set this item to
	**/
	public final void setName(String name)
	{
		if (! _name.equals(name))
		{
			_name = name;
		}
	}
	
	
	/**
		Sets if this item is selected
		@param flag whether or not the item is selected
	**/
	public void setSelected(boolean flag)
	{
		if (_isSelected != flag)
		{
			_isSelected = flag;
			
			if (_listComponent != null && _listComponent._reportItemEvents)
			{
				int state = ListComponent.ITEM_DESELECTED;
				if (flag)
					state++;
					
				ItemEvent event = new ItemEvent(_listComponent, ItemEvent.SELECTED, this, state);
				_listComponent.processEvent(event);
			}
		}			
	}
		
	
	/**
		Returns if this item is selected
		@return true if this item is selected
	**/
	public final boolean isSelected()
	{
		return _isSelected;
	}
	
	
	/**
		Sets if this item is expanded.
		@param flag whether or not the item is expanded
	**/
	public void setExpanded(boolean flag)
	{		
		if (flag != _isExpanded)
		{
			int			itemState = ListComponent.ITEM_COLLAPSED;
			
			if (flag)
				itemState++;
				
			_isExpanded = flag;
			
			if (_listComponent != null)
			{
				if (_listComponent._reportItemEvents)
				{
					ItemEvent event = new ItemEvent(_listComponent, ItemEvent.ITEM_STATE_CHANGED, this, itemState);
					_listComponent.processEvent(event);
				}

				if (_listComponent.isVisible() && (_listComponent.findVisibleItem(_top + 1) == this))	//  inline isVisible
				{
					Graphics graphics = _listComponent.getGraphics();
					_listComponent.paintItems(graphics, this, false);
					graphics.dispose();
					_listComponent._scrollbar.setMaximum(_listComponent.countPotentiallyVisibleItems());
					//System.out.println(_listComponent.getPotentiallyVisibleHeight());
				}
			}
		}
	}
	
	
	/**
		Returns if this item is expanded
		@return if this item is expanded
	**/
	public final boolean isExpanded()
	{
		return _isExpanded;
	}
	
	
	/**
		Returns this item's height
	**/
	public final int getHeight()
	{
		return _height;
	}
	
	
	/**
		Sets the height of the item (not including sub-items).  ListItems
		in a list can have variable heights, and this can be changed dynamically.
		@param height the height of this item
	**/
	public void setHeight(int height)
	{
		if (height != _height)
		{
			_height = height;

			if (_listComponent != null && (_listComponent.findVisibleItem(_top + 1) == this))	// inline isVisible()
			{
				Graphics	graphics = _listComponent.getGraphics();
				if (graphics != null)
				{
					_listComponent.paint(graphics);
					graphics.dispose();
				}
			}
		}
	}
	
	
	/*
		Returns the bottom coordinate of this item
		@return the bottom coordinate of this item
	*/
	protected int getBottomEdge()
	{
		return _top + _height;
	}
	
	
	/*
		Returns the top coordinate of this item
		@return the top coordinate of this item
	*/
	protected int getTopEdge()
	{
		return _top;
	}
	
	
	/*
		Returns the left coordinate of this item
		@return the left coordinate of this item
	*/
	protected int getLeftEdge()
	{
		return 0;
	}
	
	
	/*
		Returns the right coordinate of this item
		@return the right coordinate of this item
	*/
	protected int getRightEdge()
	{		
		if (_listComponent != null)
			return _listComponent.getSize().width;
		else
			return 0;
	}
		
	
	/**
		Returns the width of this item in its current state
		@return the width of this item in its current state
	**/
	public int getWidth()
	{
		if (_listComponent != null)
			return _listComponent.getSize().width;
		else
			return 0;
	}


	/**
		Toggles the item between expanded and collapsed
	**/
	public final void toggleExpanded()
	{
		setExpanded(! _isExpanded);
	}
			
	
	/**
		Toggles if the item is selected and not
	**/
	public final void toggleSelected()
	{
		setSelected(! _isSelected);
	}
	
	
	/**
		Paints this ListItem.  Override this method to draw your own
		items in a subclass.  The default painting behaviour supports
		an icon, a selected icon, a title that is hilited, caching
		of the string width for faster animation, and selection.
		This code is a tad hard-coded right now, so be gentle and
		tweak accordingly.
		@param graphics the Graphics context to paint into.  
	**/
	public void paint(Graphics graphics)
	{		
		int x = 2;
		int y = _top + 3;
						
		if (_listComponent._indentedExpanders)
			x += _level * 15;

		if (_numItems > 0)
		{				
			if (_isExpanded == false)
			{
				if (_listComponent._expanderCollapsedIcon != null)
					graphics.drawImage(_listComponent._expanderCollapsedIcon, x, y, null);
			}
			else
			{
				if (_listComponent._expanderExpandedIcon != null)
					graphics.drawImage(_listComponent._expanderExpandedIcon, x, y, null);
			}
		}
		
		x += 18;

		if (! _listComponent._indentedExpanders)
		{
			x += _level * 15;
		}
				
		if (_isSelected)
		{
			if (_selectedIcon != null)
				graphics.drawImage(_selectedIcon, x, y, null);
		}
		else
		{
			if (_icon != null)
				graphics.drawImage(_icon, x, y, null);
		}
		
		x += 18;
		
		if (_nameStringWidth == -1)
			_nameStringWidth = _listComponent._fontMetrics.stringWidth(_name);
				
		if (_isSelected)
		{
			graphics.setColor(Color.black);
			graphics.fillRect(x, _top + 13 - _listComponent._fontAscent,
					 		  _nameStringWidth + 4, _listComponent._fontAscent + _listComponent._fontDescent + 1);
			graphics.setColor(Color.white);
		}
		else
		{
			if (_listComponent._shadeAlternateItems && _isShaded)
				graphics.setColor(_listComponent._shadedItemColor);
			else
				graphics.setColor(_listComponent.getBackground());

			graphics.fillRect(x, _top + 13 - _listComponent._fontAscent,
					 		  _nameStringWidth + 4, _listComponent._fontAscent + _listComponent._fontDescent + 1);
			graphics.setColor(Color.black);
		}
		x += 2;
		graphics.drawString(_name, x, _top + 13);
	}
			
		
	/**
		Returns the indentation level of this item.  A value of 0 (and a parent
		of self) denotes a top-level child item in the ListComponent.
		@return the level of indentation for this item
	**/
	public final int getLevel()
	{
		return _level;
	}
	
	
	/**
		Returns the index of this item in its parent
		@return the index of this item in its parent
	**/
	public final int getIndex()
	{
		return -1; // unimplemented
	}
		
				
	/**
		Adds an item to this item
		@param item the ListItem to add
		@return the ListItem that was added
	**/
	public final ListItem addItem(ListItem item)
	{
		return addItem(item, false);
	}
	
	
	/**
		Adds a ListItem to this ListItem.  Pass true for
		atBeginning to add the item at the beginning of the
		item list.
		@param item the ListItem to add to this item
		@param atBeginning whether or not to add the item at the beginning
		@return the ListItem that was added
	**/
	public final ListItem addItem(ListItem item, boolean atBeginning)
	{
		if (item != null)
		{
			if (_firstSubItem != null)
			{			
				if (atBeginning)
				{
					item._next = _firstSubItem;
					_firstSubItem._previous = item;
					_firstSubItem = item;
				}
				else
				{
					_lastSubItem._next = item;
					item._previous = _lastSubItem;
					_lastSubItem = item;
				}
			}
			else
				_firstSubItem = _lastSubItem = item;
			
			item._listComponent = _listComponent;
			item._parent = this;
			item._level = _level + 1;
			_numItems++;
			
			if (_listComponent._reportItemEvents)
			{
				ItemEvent event = new ItemEvent(_listComponent, ItemEvent.ITEM_STATE_CHANGED, item, ListComponent.ITEM_ADDED);
				_listComponent.processEvent(event);
			}
			
			//  add code to repaint later, maybe
		}
		return item;
	}
	
	
	/**
		removes an item from this item
		@param item the ListItem to remove from this item
		@return the ListItem that was removed
	**/
	public ListItem removeItem(ListItem item)
	{
		//  UNIMPLEMENTED
		return null;
	}
		
	
	/**
		Swap this item's location with another item's
		(can be used for sorting the list)
		@param anotherItem the other item to swap with
	**/
	public void swapWith(ListItem anotherItem)
	{
		//  UNIMPLEMENTED
	}
			

	/**
		Called when the mouse is down
		@param event the MouseEvent passed to this component
		@param x the x coordinate of the event
		@param y the y coordinate of the event
		@param graphics the graphics context to draw updates into
	**/
	public void mousePressed(MouseEvent event, int x, int y, Graphics graphics)
	{
		if (_numItems > 0)
		{
			if (_listComponent._indentedExpanders)
			{
				if (x <= 20 + (_level * 15))
				{
					if (_listComponent._expanderAnimation && _listComponent._expanderMovingIcon != null)
						graphics.drawImage(_listComponent._expanderMovingIcon, (_level * 15) + 2, _top + 3, null);
					setExpanded(! _isExpanded);
					return;
				}
			}
			else
			{
				if (x <= 18)
				{
					if (_listComponent._expanderAnimation && _listComponent._expanderMovingIcon != null)
						graphics.drawImage(_listComponent._expanderMovingIcon, 2, _top + 3, null);
					setExpanded(! _isExpanded);
					return;
				}
			}
		}
		
		if (_listComponent._canSelectItems)
		{
			toggleSelected();
			paint(graphics);
		}
	}
	
	
	/**
		Returns whether or not this item is currently visible
		@return whether or not this item is currently visible
	**/
	public final boolean isVisible()
	{
		if (_listComponent.findVisibleItem(_top + 1) == this) // inline isVisible
			return true;
		else
			return false;
	}			
	
	
	/**
		Returns the "base item" this item descends from
		@return the base item up this item's inheritence chain
	**/
	public final ListItem getBaseItem()
	{
		ListItem	item = this;
		
		while (item._parent != null)
			item = item._parent;
		return item;
	}
				
  	   
  	/*
  		Iteration method that walks the tree structure
  		backward by one item.  This method is used heavily
  		by the scrolling mechanism.
  		@return the previous ListItem in the tree structure or null
  	*/
  	protected final ListItem backward()
  	{
  		ListItem	item = this;
  		
  		if (item._previous != null)
  		{
  			item = item._previous;
  			while (item._isExpanded && item._numItems > 0)
  				item = item._lastSubItem;
  			return item;
  		}
   		if (item._parent != null)
			item = item._parent;
		return item;
  	}
  	
  	
  	/*
  		Iteration method that walks the tree structure
  		forward by one item.  This method is used heavily
  		by the item drawing, hit testing, and scrolling
  		mechanisms in ListComponent.
  		@return the next ListItem in the tree structure or null
  	*/
    protected final ListItem forward()
    {
    	ListItem	item = this;
    	
        if (_isExpanded && _numItems > 0)
        	return _firstSubItem;
        if (_next != null)
        	return _next;
        
        while (item._parent != null)
        {
        	item = item._parent;
        	if (item._next != null)
        		return item._next;
		}        		
        return null;
    }

 
    /*
    	Iterates forward n items in the tree structure and returns
    	the item it lands on.  Pins to the last item if we
    	try to walk off the edge of the list.
    	@param n the number of items forward in the tree to move
    	@return the ListItem n items forward in the tree
    */
    protected final ListItem forward(int n)
    {
    	int			counter = 0;
    	ListItem	item = this;
    	ListItem	next = null;
    	
    	do
    	{
    		next = item.forward();
    		if (next == null)
    			return item;
    		else
    			item = next;
    		counter++;
    	} while (counter < n);
    	
    	return item;
    }
    
    
    /*
    	Iterates backward n items in the tree and returns
    	the item it lands on.  Pins to the first item if
    	we try to back up too far.
    	@param n the number of items backward in the tree to move
    	@return the ListItem n items backward in the tree
    */
    protected final ListItem backward(int n)
    {
    	int			counter = 0;
    	ListItem	item = this;
    	ListItem	prev = null;
    	
    	do
    	{
    		prev = item.backward();
    		if (prev == null)
    			return item;
    		else
    			item = prev;
    		counter++;
    	} while (counter < n);
    	
    	return item;
    }
    
    
    /**
    	Returns an item, if found, under the current item or in the
    	rest of the list, with the given name.  Returns null if nothing
    	is found.
    **/
    public final ListItem findItemByName(String name, boolean deep)
    {
    	ListItem	foundItem = null;
    	
    	if (_numItems > 0)
    	{
    		ListItem	item = _firstSubItem;
    		
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
    	Finds an item, given a path from this item.  The
    	path delimiter is the forward slash ("/").
    	@param path the path to the item, by name
    	@return the item found, or null
    **/
    public final ListItem findItemByPath(String path)
    {
    	if (_numItems > 0 && path != null)
    	{
    		StringTokenizer toker = new StringTokenizer(path, "/", false);
    		
    		if (toker.countTokens() > 0)
    		{
    			ListItem item = findItemByName(toker.nextToken(), false);
    			
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