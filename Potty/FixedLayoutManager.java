import java.awt.*;
import java.util.Vector;

public class FixedLayoutManager implements LayoutManager2
{
	Vector componentList = new Vector();
	Vector rectangleList = new Vector();
	
    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     * @param comp the component to be added
     * @param constraints  where/how the component is added to the layout.
     */
    public void addLayoutComponent(Component comp, Object constraints)
    {
    	Rectangle r;
    	int i;
    	
    	if( constraints != null && constraints instanceof Rectangle )
    	{
			i = componentList.indexOf(comp);
			if( i < 0 )
			{
				componentList.addElement(comp);
				rectangleList.addElement(constraints);
			}
			
			else
			{
				rectangleList.setElementAt(constraints,i);
			}
    	}
    	
    	else removeLayoutComponent(comp);
    }

    /** 
     * Returns the maximum size of this component.
     * @see java.awt.Component#getMinimumSize()
     * @see java.awt.Component#getPreferredSize()
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(Container target)
    {
    	int width, height;
    	int len = rectangleList.size();
    	int i;
    	Insets insets = target.getInsets();
    	
    	width = insets.left;
    	height = insets.top;
    	
    	for( i=0; i<len; ++i )
    	{
    		Rectangle r = (Rectangle) rectangleList.elementAt(i);
    		width = Math.max(r.width+r.x,width);
    		height = Math.max(r.height+r.y,height);
    	}
    	
    	return new Dimension(width+insets.right,height+insets.bottom);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container target)
    {
    	return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container target)
    {
    	return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target)
    {
    }

    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp)
    {
    	addLayoutComponent(comp,name);
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component ot be removed
     */
    public void removeLayoutComponent(Component comp)
    {
    	int i = componentList.indexOf(comp);
    	if( i >= 0 )
    	{
    		componentList.removeElementAt(i);
    		rectangleList.removeElementAt(i);
    	}
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
    	return maximumLayoutSize(parent);
    }

    /** 
     * Calculates the minimum size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent)
    {
    	return maximumLayoutSize(parent);
    }

    /** 
     * Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out 
     */
    public void layoutContainer(Container parent)
    {
    	int len = parent.getComponentCount();
    	int i;
    	Insets insets = parent.getInsets();
    	Dimension size = parent.getSize();
    	Rectangle dflt = new Rectangle(insets.left,insets.top,
    		size.width-insets.left-insets.right,
    		size.height-insets.top-insets.bottom);
    	
    	try
    	{
    		for( i=0; i<len; ++i )
    		{
    			Component c = parent.getComponent(i);
    			int pos = componentList.indexOf(c);
    			Rectangle r = null;
    			
    			if( pos >= 0 )
    				r = (Rectangle) rectangleList.elementAt(pos);
    			
    			if( r == null )
					r = dflt;
				
				c.setBounds(r);
    		}
    	}
    	
    	catch( ArrayIndexOutOfBoundsException aoobe )
    	{
    	}
    }
}
