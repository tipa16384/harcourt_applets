package Wired;

import java.awt.*;

public class ColumnLayout implements LayoutManager
{
	final int maxcols = 10;
	
	int hgap;
	int vgap;
	
	int [] colwid;
	int [] rowhgt;
	
	boolean tallen = false;
	
	public ColumnLayout()
	{
		this(0,0);
	}
	
	public ColumnLayout( int h, int v )
	{
		hgap = h;
		vgap = v;
	}
	
	public void setTallen( boolean tall )
	{
		tallen = tall;
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
    	return calcLayout( parent, true );
    }

    /** 
     * Calculates the minimum size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent)
    {
    	return calcLayout( parent, false );
    }
    
    Dimension calcLayout( Container parent, boolean prefer )
    {
    	int rows = 0;
    	int cols = 0;
    	Insets insets = parent.getInsets();
    	
    	rows = parent.getComponentCount();
    	
    	int i;
		    	
    	colcounter: for( i=0; i<rows; ++i )
    	{
    		try
    		{
	    		Component c = parent.getComponent(i);
	    		if( c != null )
	    		{
					if( c instanceof Container && ((Container)c).getLayout() == null )
						cols = Math.max(cols,((Container)c).getComponentCount());
					else if( cols == 0 )
						cols = 1;
				}
			}
			
			catch( ArrayIndexOutOfBoundsException ae )
			{
				rows = i-1;
				break colcounter;
			}
    	}
    	
		colwid = new int[cols];
		rowhgt = new int[rows];
		
    	for( i=0; i<rows; ++i )
    	{
    		Component c;
    		
    		try
    		{
    			c = parent.getComponent(i);
    		}
    		
    		catch( ArrayIndexOutOfBoundsException ae1 )
    		{
    			c = null;
    		}
    		
    		if( c != null )
    		{
    			if( c instanceof Container
    				&& ((Container)c).getLayout() == null )
    			{
    				Container co = (Container) c;
    				int jl = co.getComponentCount();
    				for( int j=0; j<jl; ++j )
    				{
    					Component cos;
    					
    					try
    					{
    						cos = co.getComponent(j);
    					}
    					
    					catch( ArrayIndexOutOfBoundsException ae2 )
    					{
    						cos = null;
    					}
    					
    					if( cos == null ) continue;
    					Dimension d = 
    						prefer
    							? cos.getPreferredSize()
    							: cos.getMinimumSize();
    					rowhgt[i] = Math.max(rowhgt[i],d.height);
    					colwid[j] = Math.max(colwid[j],d.width);
    				}
    			}
    			
    			else
    			{
					Dimension d = 
						prefer
							? c.getPreferredSize()
							: c.getMinimumSize();
					rowhgt[i] = Math.max(rowhgt[i],d.height);
					colwid[0] = Math.max(colwid[0],d.width);
    			}
    		}
    	}
    	
    	int width = 0;
    	int height = 0;
    	
    	for( i=0; i<rows; ++i )
    		height += rowhgt[i];
    	
    	for( i=0; i<cols; ++i )
    		width += colwid[i];
    	
    	if( rows > 1 )
    		height += (rows-1)*vgap;
    	
    	if( cols > 1 )
    		width += (cols-1)*hgap;
    	
    	return new Dimension(
    					width+insets.left+insets.right,
    					height+insets.top+insets.bottom
    					);
    }

    /** 
     * Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out 
     */
    public void layoutContainer(Container parent)
    {
		Dimension d = calcLayout( parent, true );
		Dimension size = parent.getSize();
		Insets insets = parent.getInsets();
		int rows = rowhgt.length;
		int cols = colwid.length;
		int i;
		
		//System.out.println("CF:: parent="+parent+" size="+size+" d="+d);
		//System.out.println("CF:: rows="+rows+" cols="+cols);
		
		if( rows == 0 || cols == 0 ) return;
		
		int extrawid = (size.width-d.width)/cols;
		int extrahgt = tallen ? (size.height-d.height)/rows : 0;
		int y = insets.top;
		
		//System.out.println("extrahgt="+extrahgt+"  extrawid="+extrawid);
		
		for( i=0; i<rows; ++i )
		{
			Component c;
			
			try
			{
				c = parent.getComponent(i);
				//System.out.print("   c["+i+"] is "+c);
				//if( c == null ) System.out.println();
			}
			
			catch( ArrayIndexOutOfBoundsException ae1 )
			{
				c = null;
				//System.out.println("   c["+i+"] is out of bounds.");
			}
			
			if( c != null )
			{
				//zoop(c,1);
				
				int x = insets.left;
				
				if( c instanceof Container &&
					((Container)c).getLayout() == null )
				{
					int w = size.width-insets.left-insets.right;
					int h = rowhgt[i]+extrahgt;
					Container c1 = (Container)c;
					//Insets in1 = c1.getInsets();
					int x1 = 0;
					int y1 = 0;
					
					//System.out.println(" ("+x+","+y+","+w+","+h+")");
					c1.setBounds(x,y,w,h);
					
					int jl = c1.getComponentCount();
					
					for( int j=0; j<jl; ++j )
					{
						Component co;
						
						try
						{
							co = c1.getComponent(j);
							//System.out.print("      co["+j+"] is "+co);
							//if( co == null )
							//	System.out.println();
						}
						
						catch( ArrayIndexOutOfBoundsException ae2 )
						{
							co = null;
							//System.out.println("      co is null");
						}
						
						if( co != null )
						{
							//zoop(co,2);
							w = colwid[j]+extrawid;

							//System.out.println(" ("+x1+","+y1+","+w+","+h+")");
							co.setBounds(x1,y1,w,h);
						}
						
						x1 += hgap + colwid[j] + extrawid;
					}
				}
				
				else
				{
					c.setBounds(x,y,colwid[0]+extrawid,rowhgt[i]+extrahgt);
					if( c instanceof Container )
					{
						((Container)c).doLayout();
					}
				}
			}
			
			y += vgap + rowhgt[i] + extrahgt;
		}
    }

	private void zoop( Object o, int indent )
	{
		while( indent-- > 0 )
			System.out.print("   ");
		System.out.println(o.getClass().getName());
	}
}
