import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;
import util.RapidLabel;

public class Facts extends TitledPanel
{
	Main main;
	GraphInfo info;
	Component selectedItem = null;
	Vector facts = new Vector();
	Vector order = new Vector();
	static final int boxSize = 48;
	static final int boxGap = 10;
	Selection cursel = null;
	FactSheet factSheet;
	Message message;
	
	Color backgroundColor = new Color(0xFF,0xFF,0x99);
	Color headerBackground = new Color(0x66,0x33,0x00);
	
	Color selectedBGColor = new Color(204,204,153);
	Color selectedFGColor = Color.black;
	
	Color flyBGColor = new Color(0xFF,0xFF,0x33);
	Color flyFGColor = Color.black;
	
	Color unselectedBGColor = new Color(102,102,51);
	Color unselectedFGColor = new Color(255,255,204);

	public Facts( Main main, GraphInfo info )
	{
		super( main.getParameter("FactTitle"), new BorderLayout(10,10) );
		
		this.main = main;
		this.info = info;
		
		setHeaderBackground( headerBackground );
		setHeaderForeground( Color.white );
		setBackground( null );
		setForeground( Color.black );
		setHeaderFont( info.fontBigBold );
		setJustification( Label.CENTER );
		setStyle( TitledPanel.INVISIBLE );
		
		factSheet = new FactSheet();
		Panel panel = new BorderPanel();
		panel.add( factSheet, BorderLayout.CENTER );
		
		add( new ButtonBox(), BorderLayout.NORTH );
		add( panel, BorderLayout.CENTER );
		
		message = new StatusMessage();
		add( message, BorderLayout.SOUTH );
	}
	
	public Insets getInsets()
	{
		Insets ins = super.getInsets();
		
		ins.top += 10;
		ins.bottom += 10;
		ins.left += 10;
		ins.right += 10;
		
		return ins;
	}
	
	void showFact( int which )
	{
		factSheet.showFact(which);
	}
		
	class ButtonBox extends DoubleBufferPanel
	{
		int numfacts;
		
		public ButtonBox()
		{
			super( new GridLayout(0,4,boxGap,boxGap) );
			
			int i;
			
			for( i=1; ; ++i )
			{
				String s = main.getParameter("fact"+i);
				if( s != null && s.length() > 0 )
				{
					facts.addElement(s);
					order.addElement( new Integer(i-1) );
				}
				else
					break;
			}
			
			numfacts = facts.size();
			
			Random rand = new Random();
			
			for( i=0; i<numfacts; ++i )
			{
				int j = Math.abs(rand.nextInt()) % numfacts;
				Object to;
				
				to = facts.elementAt(i);
				facts.setElementAt(facts.elementAt(j),i);
				facts.setElementAt(to,j);
				
				to = order.elementAt(i);
				order.setElementAt(order.elementAt(j),i);
				order.setElementAt(to,j);
			}
			
			for( i=0; i<numfacts; ++i )
			{
				add( new Selection(i) );
			}
		}

		public Insets getInsets()
		{
			Dimension dim = getSize();
			Insets ins = super.getInsets();
			
			int wid = 4*boxSize+3*boxGap;
			int avail = dim.width-ins.left-ins.right;
			int gap = (avail-wid)/2;
			
			return new Insets(0,ins.left+gap,0,ins.right+gap);
		}
	}
		
	class Selection extends Component implements PropertyChangeListener
	{
		int selection;
		boolean flyover = false;
		boolean selected = false;
		
		public Selection( int selection )
		{
			this.selection = selection;
			setFont( new Font("SansSerif",Font.BOLD,32) );
			enableEvents( AWTEvent.MOUSE_EVENT_MASK );
			info.addPropertyChangeListener( Selection.this );
		}
		
		public void propertyChange( PropertyChangeEvent pce )
		{
			String pname = pce.getPropertyName();

			if( pname.equals(info.select) )
			{
				Integer iobj = (Integer) pce.getNewValue();
				int tsel = iobj.intValue();
				boolean newsel = selection == tsel;
				if( selected != newsel )
				{
					selected = newsel;
					repaint();
				}
			}
			
			if( pname.equals(info.requestOrder) )
			{
				if( selected )
				{
					info.firePropertyChange(info.sendOrder,null,order.elementAt(selection));
				}
			}
		}
		
		protected void processMouseEvent( MouseEvent e )
		{
			switch( e.getID() )
			{
				case MouseEvent.MOUSE_ENTERED:
					flyover = true;
					showFact(selection);
					repaint();
					break;

				case MouseEvent.MOUSE_EXITED:
					flyover = false;
					showFact(-1);
					repaint();
					break;
				
				case MouseEvent.MOUSE_CLICKED:
				
					info.firePropertyChange(info.click_on,null,new Integer(selection));
					
				/*
					if( cursel != null )
					{
						Component c = cursel;
						cursel = null;
						c.repaint();
					}
					
					cursel = Selection.this;
					Selection.this.repaint();
				*/
					break;
			}
			
			super.processMouseEvent( e );
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			Dimension rdim = getPreferredSize();
			int xoffs = (dim.width-rdim.width)/2;
			int yoffs = (dim.height-rdim.height)/2;
			
			Color fg, bg;
			
			//boolean selected = cursel == this;
			
			if( selected )
			{
				fg = selectedFGColor;
				bg = selectedBGColor;
			}
			
			else if( flyover )
			{
				fg = flyFGColor;
				bg = flyBGColor;
			}
			
			else
			{
				fg = unselectedFGColor;
				bg = unselectedBGColor;
			}
			
			g.setColor( bg );
			g.fillRect( xoffs, yoffs, rdim.width, rdim.height );
			
			g.setColor( getForeground() );
			g.drawRect( xoffs, yoffs, rdim.width-1, rdim.height-1 );
			
			if( selected )
				g.drawRect( xoffs+1, yoffs+1, rdim.width-3, rdim.height-3 );
			
			g.setColor( fg );
			g.setFont( getFont() );
			String s = ""+(char)('A'+(char)selection);
			FontMetrics fm = getFontMetrics(getFont());
			int sw = fm.stringWidth(s);
			int x = xoffs + (rdim.width-sw)/2;
			int y = yoffs + (rdim.height+fm.getAscent())/2 - 3;
			g.drawString( s, x, y );
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(boxSize,boxSize);
		}
	}

	class BorderPanel extends DoubleBufferPanel
	{
		public BorderPanel()
		{
			super( new BorderLayout() );
			setBackground( null );
			setForeground( Color.black );
		}
		
		public Insets getInsets()
		{
			return new Insets(6,6,6,6);
		}
		
		public void paint( Graphics g )
		{
			super.paint( g );
			
			Dimension dim = getSize();
			g.setColor( getForeground() );
			g.drawRect( 0, 0, dim.width-1, dim.height-1 );
		}
	}
	
	class FactSheet extends RapidLabel
	{
//		Font normalFont = new Font("Serif",Font.PLAIN,12);
		Font normalFont = info.fontBigPlain;
		Font bigFont = new Font("SansSerif",Font.BOLD,36);
		int shownFact = -1;
		
		public FactSheet()
		{
			setText("");
			setFont( normalFont );
		}
		
		public void setBounds( int x, int y, int w, int h )
		{
			super.setBounds(x,y,w,h);
		}
		
		public void showFact( int which )
		{
			shownFact = which;
			
			if( shownFact < 0 )
				setText("");
			else
			{
				//System.out.println("showing "+which);
				String s = (String) facts.elementAt(shownFact);
				//System.out.println("text is "+s);
				setText( s );
			}

			getParent().doLayout();
			repaint();
		}
	}

	class StatusMessage extends Message
						implements PropertyChangeListener
	{
		public StatusMessage()
		{
			setText("");
			setBackground( null );
			setForeground( Color.red );
			setFont( info.fontBiggerBold );
			info.addPropertyChangeListener( this );
		}

		public void propertyChange( PropertyChangeEvent pce )
		{
			String pname = pce.getPropertyName();

			if( pname.equals(info.success) )
			{
				setBackground( Color.green );
				setForeground( Color.white );
				setText("All Correct!");
			}
			
			else if( pname.equals(info.failure) )
			{
				setBackground( Color.red );
				setForeground( Color.white );
				setText("Some of your matches are incorrect");
			}
			
			if( pname.equals(info.incomplete) )
			{
				setBackground( null );
				setText("");
			}
		}
	}
	
}
