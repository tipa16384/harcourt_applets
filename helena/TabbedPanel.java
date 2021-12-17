import java.awt.*;
import java.awt.event.*;
import java.util.*;
import util.DoubleBufferPanel;

public class TabbedPanel extends DoubleBufferPanel
{
	Vector cardNames = new Vector();
	Vector widths = new Vector();
	
	CardLayout layout;
	Font font;
	FontMetrics fm;
	
	static final int lineWidth=2;
	
	public TabbedPanel()
	{
		super( new CardLayout() );
		layout = (CardLayout) getLayout();
		
		setFont( font = GraphInfo.fontBiggerBold );
		fm = getFontMetrics(font);
		
		addMouseListener( new TabController() );
	}
	
	class TabController extends MouseAdapter
	{
		public void mouseClicked( MouseEvent e )
		{
			Insets insets = getInsets();
			
			if( e.getY() < insets.top )
			{
				int len = widths.size();
				int i;
				int pos = e.getX();
				
				for( i=0; i<len; ++i )
				{
					int w = ((Integer)widths.elementAt(i)).intValue();
					pos -= w;
					if( pos <= 0 )
					{
						String s = (String) cardNames.elementAt(i);
						layout.show(TabbedPanel.this,s);
						repaint();
						break;
					}
				}
			}
		}
	}
	
	public void add( Component c, String name )
	{
		super.add( c, name );
		cardNames.addElement(name);
		//System.out.println("Adding card "+name);
	}
	
	public Insets getInsets()
	{
		return new Insets(fm.getHeight()+lineWidth,lineWidth,lineWidth,lineWidth);
	}

	private void calcWidths( int w )
	{
		int total = 0;
		int used = 0;
		int i;
		int len = cardNames.size();
		
		for( i=0; i<len; ++i )
		{
			String s = (String) cardNames.elementAt(i);
			total += fm.stringWidth(s);
		}
			
		widths.removeAllElements();
		
		for( i=0; i<len; ++i )
		{
			String s = (String) cardNames.elementAt(i);
			int portion;
			
			if( i == (len-1) )
				portion = w-used;
			else
				portion = (fm.stringWidth(s)*w)/total;
				
			used += portion;

			widths.addElement( new Integer( portion ) );
		}
	}
	
	private void drawLabels( Graphics g, Dimension dim, Insets insets )
	{
		calcWidths(dim.width);
		
		int len = cardNames.size();
		int i;
		int left = 0;
		
		g.setFont( font );
		
		g.setColor( getParent().getBackground() );
		g.fillRect( 0, 0, dim.width, insets.top );

		Color unselectedColor = getBackground().darker();
		
		for( i=0; i<len; ++i )
		{
			int lw = ((Integer)widths.elementAt(i)).intValue();
			Component c = getComponent(i);
			Color col, textCol;
			boolean selected;
			
			selected = ( c != null && c.isVisible() );
			
			col = selected ? getBackground() : unselectedColor;
			textCol = selected ? getForeground() : new Color(204,204,204);
			
			g.setColor( getForeground() );
			g.fillRoundRect(left,0,lw,2*insets.top,16,16);
			g.setColor( col );
			g.fillRoundRect(left+lineWidth,lineWidth,lw-2*lineWidth,2*(insets.top-lineWidth),16,16);
			
			g.setColor( textCol );
			String s = (String) cardNames.elementAt(i);
			int sw = fm.stringWidth(s);
			g.drawString( s, left+(lw-sw)/2, fm.getAscent() );
			
			if( !selected )
			{
				g.setColor( getForeground() );
				g.fillRect( left, insets.top-lineWidth, lw, lineWidth );
			}
			
			left += lw;
		}
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		Insets insets = getInsets();
		Dimension dim = getSize();
		
		g.setColor( getForeground() );
		
		g.fillRect(0,insets.top,insets.left,dim.height-insets.top);
		g.fillRect(dim.width-insets.right,insets.top,insets.right,dim.height-insets.top);
		g.fillRect(0,dim.height-insets.bottom,dim.width,insets.bottom);

		drawLabels(g,dim,insets);
	}
}
