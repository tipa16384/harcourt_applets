import java.awt.*;

public class BalancedLayoutManager implements LayoutManager
{
	static final int space = 16;
	boolean vertical = false;
	
	public BalancedLayoutManager()
	{
		this(false);
	}
	
	public BalancedLayoutManager( boolean vertical )
	{
		this.vertical = vertical;
	}
	
    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp)
    {
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component ot be removed
     */
    public void removeLayoutComponent(Component comp)
    {
    }

    /**
     * Calculates the preferred size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     *  
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent)
    {
    	return layoutSize(parent,false);
    }

    /** 
     * Calculates the minimum size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent)
    {
    	return layoutSize(parent,true);
    }
    
    private Dimension layoutSize( Container parent, boolean min )
    {
    	if( vertical )
    		return verticalSize(parent,min);
    	else
    		return horizontalSize(parent,min);
    }
    
    private Dimension horizontalSize( Container parent, boolean min )
    {
    	Insets insets = parent.getInsets();
    	
    	int len = parent.getComponentCount();
    	int height = 0;
    	int width = insets.left+insets.right;
    	
    	if( len > 1 )
    		width += space * (len-1);
    	
    	for( int i=0; i<len; ++i )
    	{
    		Component c = parent.getComponent(i);
    		Dimension size = min ? c.getMinimumSize() : c.getPreferredSize();
    	
    		height = Math.max(height,size.height);
    		width += size.width;
    	}
    	
    	return new Dimension(width,height+insets.top+insets.bottom);
    }
    
    private Dimension verticalSize( Container parent, boolean min )
    {
    	Insets insets = parent.getInsets();
    	
    	int len = parent.getComponentCount();
    	int width = 0;
    	int height = insets.top+insets.bottom;
    	
    	if( len > 1 )
    		height += space * (len-1);
    	
    	for( int i=0; i<len; ++i )
    	{
    		Component c = parent.getComponent(i);
    		Dimension size = min ? c.getMinimumSize() : c.getPreferredSize();
    	
    		width = Math.max(width,size.width);
    		height += size.height;
    	}
    	
    	return new Dimension(width+insets.left+insets.right,height);
    }

    /** 
     * Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out 
     */
    public void layoutContainer(Container parent)
    {
    	if( vertical )
    		layoutVertically( parent );
    	else
    		layoutHorizontally( parent );
    }
    
    private void layoutHorizontally( Container parent )
    {
    	int len = parent.getComponentCount();
    	Component [] clist = parent.getComponents();
    	int i;
    	boolean min = false;
    	Dimension size = parent.getSize();
    	Insets insets = parent.getInsets();
		int width = insets.left+insets.right;
		    	
    	if( len <= 0 ) return;
    	
    	if( len == 1 )
    	{
    		Component c = clist[0];
    		c.setBounds(insets.left,insets.top,size.width-width,size.height-insets.top-insets.bottom);
			return;
    	}
    	
    	for( i = 0; i<len; ++i )
    	{
			Dimension csz = clist[i].getPreferredSize();
			width += csz.width;
    	}
    	
    	if( width > (size.width-(len-1)*space) )
    	{
    		min = true;
    		width = insets.left+insets.right;
	    	for( i = 0; i<len; ++i )
	    	{
				Dimension csz = clist[i].getMinimumSize();
				width += csz.width;
	    	}
    	}

		int pad = (size.width-width)/(len-1);
		int error = size.width - width - (len-1)*pad;
		
		int x=insets.left;
		
		for( i = 0; i < len; ++i )
		{
			if( i > 0 )
			{
				x += pad;
				if( error > 0 )
				{
					--error;
					++x;
				}
			}
			
			Component ch = clist[i];
			Dimension csz = min ? ch.getMinimumSize() : ch.getPreferredSize();
			
			ch.setBounds(x,insets.top,csz.width,csz.height);
			x += csz.width;
		}
    }
    
    private void layoutVertically( Container parent )
    {
    	int len = parent.getComponentCount();
    	Component [] clist = parent.getComponents();
    	int i;
    	boolean min = false;
    	Dimension size = parent.getSize();
    	Insets insets = parent.getInsets();
		int height = insets.top+insets.bottom;
		    	
    	if( len <= 0 ) return;
    	
    	if( len == 1 )
    	{
    		Component c = clist[0];
    		c.setBounds(insets.left,insets.top,size.width-insets.left-insets.right,size.height-height);
    		return;
    	}
    	
    	for( i = 0; i<len; ++i )
    	{
			Dimension csz = clist[i].getPreferredSize();
			height += csz.height;
    	}
    	
    	if( height > (size.height-(len-1)*space) )
    	{
    		min = true;
			height = insets.top+insets.bottom;
	    	for( i = 0; i<len; ++i )
	    	{
				Dimension csz = clist[i].getMinimumSize();
				height += csz.height;
	    	}
    	}

		int pad = (size.height-height)/(len-1);
		int error = size.height - height - (len-1)*pad;
		int cwidth = size.width - insets.left - insets.right;
		
		int y=insets.top;
		
		for( i = 0; i < len; ++i )
		{
			if( i > 0 )
			{
				y += pad;
				if( error > 0 )
				{
					--error;
					++y;
				}
			}
			
			Component ch = clist[i];
			Dimension csz = min ? ch.getMinimumSize() : ch.getPreferredSize();
			
			ch.setBounds(insets.left,y,cwidth,csz.height);
			y += csz.height;
		}
    }
}
