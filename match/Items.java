import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;
import util.RapidLabel;

public class Items extends TitledPanel implements PropertyChangeListener
{
	Main main;
	GraphInfo info;
	Component selectedItem = null;
	Vector items = new Vector();
	
	Color backgroundColor = new Color(255,204,153);
	Color selectedColor = backgroundColor.brighter();
	Color headerBackground = new Color(0x66,0x33,0x00);
	Color choiceMadeColor = new Color(255,255,204);

	public Items( Main main, GraphInfo info )
	{
		super( main.getParameter("ItemTitle"), new BalancedLayoutManager(true) );
		
		this.main = main;
		this.info = info;
		
		setHeaderBackground( headerBackground );
		setHeaderForeground( Color.white );
		setBackground( backgroundColor );
		setForeground( Color.black );
		setHeaderFont( info.fontBigBold );
		setJustification( Label.CENTER );
		
		int itemWidth = 0;
		
		String swid = main.getParameter("itemwidth");
		if( swid != null && swid.length() > 0 )
		{
			itemWidth = Integer.parseInt(swid);
		}
		
		for( int i=1; ; ++i )
		{
			String tag = "item"+i;
			String val = main.getParameter(tag);
			if( val == null || val.length() == 0 )
				break;
			Item item = new Item(val,itemWidth,i-1);
			items.addElement( item );
			add( item );
		}

		info.addPropertyChangeListener( this );
	}
	
	public void propertyChange( PropertyChangeEvent pce )
	{
		String pname = pce.getPropertyName();
		
		if( pname.equals(info.grade) )
		{
			int size = items.size();
			int numCorrect = 0;
			boolean complete = true;

			for( int i=0; i<size; ++i )
			{
				Item item = (Item) items.elementAt(i);
				
				if( !item.isAnswered() )
				{
					System.out.println("item "+i+" not answered!");
					complete = false;
					break;
				}
				
				if( item.isBingo() )
				{
					numCorrect++;
				}
			}
			
			if( !complete )
				info.firePropertyChange( info.incomplete, 0, 1 );
			else if( numCorrect == size )
				info.firePropertyChange( info.success, 0, 1 );
			else
				info.firePropertyChange( info.failure, 0, 1 );
		}
	}
		
	public Insets getInsets()
	{
		Insets insets = super.getInsets();
		insets.left = insets.right = 1;
		return insets;
	}

	class Item extends Panel implements PropertyChangeListener
	{
		final int letterWidth = 32;
		int totalWidth;
		Component rl, lb;
		int choice = -1;
		int order;
		boolean bingo = false;
		
		public Item( String s, int wid, int order )
		{
			super( new BorderLayout(5,5) );
			
			MouseListener selectMe = new SelectMe();
		
			this.order = order;
			totalWidth = wid;
			rl = new RapidLabel( s, Label.LEFT, wid-letterWidth );
			lb = new LetterBox();
			add( rl, BorderLayout.CENTER );
			add( lb, BorderLayout.EAST );
			rl.setFont( info.fontPlain );
			rl.addMouseListener( selectMe );
			lb.addMouseListener( selectMe );
			addMouseListener( selectMe );
			info.addPropertyChangeListener( Item.this );
		}
		
		public boolean isBingo()
		{
			return bingo;
		}
		
		public boolean isAnswered()
		{
			return choice >= 0;
		}
		
		public void propertyChange( PropertyChangeEvent pce )
		{
			String pname = pce.getPropertyName();
			
			if( pname.equals(info.selectItem) )
			{
				Item oldItem = (Item) pce.getOldValue();
				Item newItem = (Item) pce.getNewValue();
				
				if( oldItem == Item.this )
				{
					setBackground( null );
					Item.this.repaint();
					
					if( selectedItem == Item.this )
					{
						info.firePropertyChange(info.select,null,new Integer(-1));
						selectedItem = null;
					}
				}
				
				if( newItem == Item.this )
				{
					setBackground( selectedColor );
					selectedItem = Item.this;
					info.firePropertyChange(info.select,null,new Integer(choice));
					info.firePropertyChange(info.requestOrder,0,1);
					Item.this.repaint();
				}
			}
			
			if( pname.equals(info.sendOrder) )
			{
				int chosen = ((Integer)pce.getNewValue()).intValue();
				if( Item.this == selectedItem )
				{
					bingo = chosen == order;
					
					if( bingo )
					{
						//System.out.println("This is the correct selection");
					}
					
					info.firePropertyChange(info.grade,0,1);
				}
			}
			
			if( pname.equals(info.click_on) )
			{
				Integer fint = (Integer) pce.getNewValue();
				int oldChoice = choice;
				int newChoice = fint.intValue();

				if( selectedItem == Item.this )
				{
					choice = newChoice;
					info.firePropertyChange(info.select,new Integer(oldChoice),new Integer(choice));
					info.firePropertyChange(info.requestOrder,0,1);
					Item.this.repaint();
				}
				
				else if( newChoice == choice )
				{
					choice = -1;
					bingo = false;
					info.firePropertyChange(info.requestOrder,0,1);
					Item.this.repaint();
				}
			}
		}
			
		class SelectMe extends MouseAdapter
		{
			public void mouseClicked( MouseEvent e )
			{
				info.firePropertyChange(info.selectItem,selectedItem,Item.this);
				
				/*
				if( selectedItem != Item.this )
				{
					if( selectedItem != null )
					{
						Component ti = selectedItem;
						selectedItem = null;
						ti.setBackground( null );
						ti.repaint();
					}
					
					selectedItem = Item.this;
					setBackground( selectedColor );
					repaint();
				}
				*/
			}
		}
		
		public Insets getInsets()
		{
			return new Insets(3,5,3,5);
		}
		
		class LetterBox extends Component
		{
			public LetterBox()
			{
				setFont( new Font("SansSerif",Font.BOLD,24) );
			}
			
			public Dimension getPreferredSize()
			{
				return new Dimension(32,32);
			}
			
			public Dimension getMinimumSize()
			{
				return getPreferredSize();
			}
			
			public void paint( Graphics g )
			{
				Dimension dim = getPreferredSize();
				Dimension xdim = getSize();
				boolean choiceMade = choice >= 0;

				dim.width = Math.min(dim.width,xdim.width);
				dim.height = Math.min(dim.height,xdim.height);

				if( true || choiceMade )
				{
					g.setColor( choiceMadeColor );
					g.fillRect( 0, 0, dim.width, dim.height );
				}

				g.setColor( getForeground() );
				g.drawRect( 0, 0, dim.width-1, dim.height-1 );
				
				if( choiceMade )
				{
					Font f = getFont();
					g.setFont( f );
					FontMetrics fm = getFontMetrics(f);
					
					String s = "" + (char)(0x41+choice);
					int sw = fm.stringWidth(s);
					g.drawString( s, (dim.width-sw)/2, (dim.height+fm.getAscent())/2-5 );
				}
			}
		}
	}
}
