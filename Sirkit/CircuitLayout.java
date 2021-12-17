import java.awt.*;

public class CircuitLayout implements LayoutManager
{
	//final int maxcols = 10;

	public final static int X_AXIS = 0;
	public final static int Y_AXIS = 1;
	public final static int SERIES = 0;
	public final static int PARALLEL = 1;
	
	int axis = Y_AXIS;		//default to vertical alignments
	int hgap;
	int vgap;
	
	int [] colwid;
	int [] rowhgt;

	Component[][]	rowcolComps;	//storage for components in their row,col array
	
	public CircuitLayout()
	{
		this(Y_AXIS,0,0);
	}
	
	public CircuitLayout( int axi )
	{
		this(axi,3,3);
	}
	
	public CircuitLayout( int h, int v )
	{
		this( Y_AXIS, h, v);
	}

	public CircuitLayout( int axi, int h, int v )
	{
		axis = axi;
		hgap = h;
		vgap = v;
		//if( axis == X_AXIS )
			//System.out.println(" created a series layout");
	}
	
    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp)
    {
    	//System.out.println("CircuitLayout::addLayoutComponent adding "+name);
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
    	int	comps = 0;
    	int rows = 0;
    	int cols = 0;
    	Insets insets = parent.getInsets();

						//count the components to find out number of rows and cols

   		comps = parent.getComponentCount();
    	
    	int i;
		    	
    	colcounter: for( i=0; i<comps; ++i )
    	{
    		try
    		{
	    		Component c = parent.getComponent(i);
	    		if( c != null )
	    		{
					if( c instanceof Container
	    				&& ((Container)c).getLayout() == null )
					{
						if( axis == X_AXIS )				//maybe switch the orientation
						{
							cols = Math.max(cols,((Container)c).getComponentCount());
							//System.out.println("   X_AXIS container cols became "+cols);
						}
						else
							rows = Math.max(rows,((Container)c).getComponentCount());
					}
					else
					{
						if( axis == X_AXIS )	//add to proper orientation
						{
							++cols;
							//System.out.println("   X_AXIS cols became "+cols);
						}
						else
							++rows;
										//first component would make this 1r,1c
						if( rows == 0 )
							rows = 1;
						if( cols == 0 )
							cols = 1;
					}
				}
			}
			
			catch( ArrayIndexOutOfBoundsException ae )
			{
				comps = i-1;
				break colcounter;
			}
    	}
    	System.out.println("CalcLayout:: found "+rows+" rows and "+cols+" cols");

    				//build an array of sizes of the cols,rows
		colwid = new int[cols];
		rowhgt = new int[rows];
		rowcolComps = new Component[rows][cols];	//storage for components in their row,col array
					

   		int		x = 0;			//temp col counter
   		int		y = 0;			//temp row counter

					//redo the above loop, adding dim into the sizes arrays
					//  and storing each component into an array by row,col
    	for( i=0; i<comps; ++i )
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
    					rowhgt[y] = Math.max(rowhgt[y],d.height);
    					colwid[x] = Math.max(colwid[x],d.width);

						rowcolComps[y][x] = c;	//store this component in it's row,col array
						//System.out.println("c["+i+"]"+c+" into rc["+y+"]["+x+"]");
					
						if( axis == X_AXIS )
							++x;			//advance cell ptr to next
						else
							++y;
    				}
    			}
    			else
    			{
    				if( c instanceof Container
    					 && ((Container)c).getLayout() != null )
	    			{
	    				LayoutManager	lay = ((Container)c).getLayout();
	    				//System.out.println("c is a Container with layout="+lay );
	    				Dimension d = 
	    					prefer
	    						? lay.preferredLayoutSize((Container)c)
	    						: lay.minimumLayoutSize((Container)c);
	    				rowhgt[y] = Math.max(rowhgt[y],d.height);
	    				colwid[x] = Math.max(colwid[x],d.width);
	    			}
		
	    			else		//just a plain component
	    			{
						Dimension d = 
							prefer
								? c.getPreferredSize()
								: c.getMinimumSize();
						rowhgt[y] = Math.max(rowhgt[y],d.height);
						colwid[x] = Math.max(colwid[x],d.width);
	    			}

					rowcolComps[y][x] = c;	//store this component in it's row,col array
					//System.out.println("c["+i+"]"+c+" into rc["+y+"]["+x+"]");

					if( axis == X_AXIS )
						++x;			//advance cell ptr to next
					else
						++y;
    			}
    		}
    	}

    					//finally, calc the total width, height
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
		int row,col,nc;
		
		//System.out.println("CF:: parent="+parent+" size="+size+" d="+d);
		//System.out.println("CF:: rows="+rows+" cols="+cols);
		
		if( rows == 0 || cols == 0 ) return;
		
		int y = insets.top;
		int extrahi = (size.height-d.height)/rows;
		
		for( row=0; row<rows; ++row )
		{
			int x = insets.left;
			nc = 0;
			for( col=0; col<cols; ++col )
			{
				try				//count the component in this row
				{
					if( rowcolComps[row][col] != null )
						++nc;
				}
				catch( ArrayIndexOutOfBoundsException ae1 )
				{
					System.out.println("   c["+row+"]["+col+"] is out of bounds.");
				}
			}
			
			Component c;
			for( col=0; col<cols; ++col )
			{
				c = rowcolComps[row][col];

				if( c != null )
				{
					//System.out.print("  c["+row+"]["+col+"] is "+c);
	
					int extrawid = (size.width-d.width)/(nc+1);
					x += extrawid;
					
					if( c instanceof Container &&
						((Container)c).getLayout() == null )
					{
						int w = size.width-insets.left-insets.right;
						int h = rowhgt[row];
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
								System.out.print("      co["+j+"] is "+co);
								//if( co == null )
								//	System.out.println();
							}
							
							catch( ArrayIndexOutOfBoundsException ae2 )
							{
								co = null;
								System.out.println("      co is null");
							}
							
							if( co != null )
							{
								w = colwid[j]+extrawid;
	
								//System.out.println(" ("+x1+","+y1+","+w+","+h+")");
								co.setBounds(x1,y1,w,h);
							}
							
							x1 += hgap + colwid[j] + (extrawid / jl);
						}
					}
					
					else
					{
						c.setBounds(x,y,colwid[col],rowhgt[row]);
						//System.out.println(" ("+x+","+y+","+(colwid[col]+extrawid)+","+rowhgt[row]+")");
						if( c instanceof Container )
						{
							((Container)c).doLayout();
						}
					}
					
					x += hgap + colwid[col];			//advance to next x col
				}
			}
						
			y += vgap + rowhgt[row] + extrahi;
		}
    }
}
