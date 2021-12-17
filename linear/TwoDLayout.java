import java.awt.*;

public class TwoDLayout implements LayoutManager
{
	int hpadding, vpadding;
    boolean vertical;
    
    final static int defaultHPad = 5;
    final static int defaultVPad = 5;
    final static boolean defaultOrientation = true;
    
	public TwoDLayout(){ this( defaultOrientation ); }

	public TwoDLayout( boolean vertical )
	{
		this( vertical, defaultHPad, defaultVPad );
	}
	
	public TwoDLayout( int hpad, int vpad )
	{
		this( defaultOrientation, hpad, vpad );
	}

	public TwoDLayout( boolean vertical, int hpad, int vpad ){
		this.vertical = vertical;
    	hpadding = (hpad >= 0) ? hpad : defaultHPad;
    	vpadding = (vpad >= 0) ? vpad : defaultVPad;
	}

    public String toString()
    {
    	return getClass().getName()+"[padding=("+hpadding+","+vpadding+
    			"),vertical="+vertical+"]";
    }

    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp){}

    /**
     * Removes the specified component from the layout.
     * @param comp the component ot be removed
     */
    public void removeLayoutComponent(Component comp){}

    /**
     * Calculates the preferred size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     *
     * @see #minimumLayoutSize
     */
   public  Dimension preferredLayoutSize(Container parent)
    {
		int height = 0, maxHeight = 0;
		int width = 0, maxWidth = 0;
		Component[] ca = parent.getComponents();
		int i, count;
		Dimension dim;
		
		count = ca.length;
		if( count == 0 ) return new Dimension(100,100);
		
		for( i = 0; i < count; ++i )
		{
			Dimension d = ca[i].getPreferredSize();
			
			height += d.height;
			maxHeight = Math.max(maxHeight,d.height);
			width += d.width;
			maxWidth = Math.max(maxWidth,d.width);
		}                                         

		if( vertical )
			dim = new Dimension(maxWidth+2*hpadding,
								height+(count+1)*vpadding);
        else
        	dim = new Dimension(width+(count+1)*hpadding,
        						maxHeight+2*vpadding);

		Insets insets = parent.getInsets();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;

        //System.out.println("preferred layout size for "+parent+" is "+dim);

		return dim;
    }

    /**
     * Calculates the minimum size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent)
    {
		int height = 0, maxHeight = 0;
		int width = 0, maxWidth = 0;
		Component[] ca = parent.getComponents();
		int i, count;
		Dimension dim;
		
		count = ca.length;
		if( count == 0 ) return new Dimension(100,100);
		
		for( i = 0; i < count; ++i )
		{
			Dimension d = ca[i].getMinimumSize();
			
			height += d.height;
			maxHeight = Math.max(maxHeight,d.height);
			width += d.width;
			maxWidth = Math.max(maxWidth,d.width);
		}

		if( vertical )
			dim = new Dimension(maxWidth+2*hpadding+1,
								height+(count+1)*vpadding);
        else
        	dim = new Dimension(width+(count+1)*hpadding,
        						maxHeight+2*vpadding+1);
        
		Insets insets = parent.getInsets();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;

       // System.out.println("minimum layout size for "+parent+" is "+dim);

		return dim;
    }

    /**
     * Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out
     */
    public void layoutContainer(Container parent)
    {
    	Dimension size = preferredLayoutSize(parent);
    	Dimension parentSize = parent.getSize();
    	boolean minimum = false;
   		
   		if( false && (size.width > parentSize.width ||
   			size.height > parentSize.height) )
   		{
   			size = minimumLayoutSize(parent);
   			minimum = true;
   		}
   		
   		else
   			size = parentSize;
        
        if( vertical )
        	layoutVertically(parent,size,minimum);
        else
        	layoutHorizontally(parent,size,minimum);
     }

     private void layoutVertically( Container parent, Dimension size, boolean minimum )
     {
     	//System.out.println("layoutVertically(parent,"+size+","+minimum+")");
     	Insets insets = parent.getInsets();
     	int x, y, width, height;
     	Component[] ca = parent.getComponents();
     	int count, i;
        
        y=vpadding+insets.top;
        count = ca.length;
	    
	layoutloop:
		for(;;)
		{
			float bestAlign = 2f;
			int bestIndex = -1;
			Component c;

			for( i=0; i<count; ++i )
			{                    
				c = ca[i];
				if( c == null ) continue;
				float calign = c.getAlignmentY();
				if( calign < bestAlign )
				{
					bestIndex = i;   
					bestAlign = calign;
				}
			}    
			
			if( bestIndex < 0 )
				break layoutloop;
			
			c = ca[bestIndex];
			ca[bestIndex] = null;
			
     		Dimension csize = minimum ? c.getMinimumSize() : c.getPreferredSize();
     		
     		x = hpadding+insets.left;
     		width = size.width - insets.left - insets.right - 2*hpadding;
     		height = csize.height;
     		c.setBounds(x,y,width,height);
     		y += height+vpadding;
		}
     }
     
     private void layoutHorizontally( Container parent, Dimension size, boolean minimum )
     {
     	//System.out.println("layoutHorizontally(parent,"+size+","+minimum+")");
     	Insets insets = parent.getInsets();
     	int x, y, width, height;  
     	Component[] ca = parent.getComponents();
     	int count, i;
        
        x = hpadding+insets.left;
        count = ca.length;

     	for( i = 0; i < count; ++i )
     	{                               
     		Component c = ca[i];
     		Dimension csize = minimum ? c.getMinimumSize() : c.getPreferredSize();
     		
     		y = vpadding+insets.top;
     		width = csize.width;
     		height = size.height - 2*vpadding - insets.top - insets.bottom;
     		c.setBounds(x,y,width,height);
     		x += width+hpadding;
     	}
     }
}
