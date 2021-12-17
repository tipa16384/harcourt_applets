import java.awt.*;
import java.awt.event.*;
//import util.GenericIcon;
import java.net.URL;

public class FormulaPanel extends Panel implements ActionListener
{
	Example state = null;
	int mode = -1;
	Panel sliderPanel = null;
	
	public FormulaPanel()
	{
		super( new FixedLayoutManager() );		//was new BorderLayout()
		//try PaddedPanel super( new GridLayout(0,1), 0, 20, 0, 20 );
		//sliderPanel = new PaddedPanel( new BalancedLayoutManager(true), 2, 0, 0, 0 );
		//sliderPanel = new PaddedPanel( new BalancedLayoutManager(true), 2, 2, 2, 2 );
		sliderPanel = new PaddedPanel( new FixedLayoutManager(), 2, 2, 2, 2 );		//was new GridLayout(0,1)
		sliderPanel.setBackground( GraphInfo.CONTROL_COLOR );
		add( sliderPanel, new Rectangle(0,132,179,46) );
		checkVisibility();
	}
	
	public void paint( Graphics g )
	{
		if( mode == ElecPot.GRAPH_MODE )
			super.paint(g);
	}
	
	public void setMode( int mode )
	{
		//System.out.println("mode set to "+mode);
		this.mode = mode;
		checkVisibility();
		repaint();
	}

	private void checkVisibility()
	{
		if( mode == ElecPot.GRAPH_MODE )
		{
			if( sliderPanel != null )
			{
				sliderPanel.setVisible(true);
				validate();
			}
		}
		else if( sliderPanel != null )
		{
			sliderPanel.setVisible(false);
			sliderPanel.invalidate();
		}
	}
	
	public void setState( Example state )
	{
		//System.out.println("FormulaPanel.setState("+state+")");
		
		this.state = state;
		
		removeAll();
		
		if( state.getID() == Example.INFINITECOAX )
			add( new FormulaIcon(state.formulaImage()), new Rectangle(0,0,179,112) );
		else
			add( new FormulaIcon(state.formulaImage()), new Rectangle(0,0,179,132) );
		
		validate();

		if( sliderPanel != null )
		{
			sliderPanel.removeAll();
			state.addSliders( sliderPanel );
			checkVisibility();
			//Panel p = new PaddedPanel( new BalancedLayoutManager(true), 0, 10, 0, 10 );
			Panel p = new PaddedPanel( new FixedLayoutManager(), 0, 0, 0, 0 );		//was GridLayout(0,1)
			p.add( sliderPanel );
			//Panel p = new PaddedPanel( new BorderLayout(), 0, 0, 0, 0 );
			//p.add( sliderPanel, BorderLayout.SOUTH );
			if( state.getID() == Example.INFINITECOAX )
				add( p, new Rectangle(0,112,179,66) );
			else
				add( p, new Rectangle(0,132,179,46) );
		}
				
		validate();
	}
	
	public void actionPerformed( ActionEvent e )
	{
		//System.out.println("FormulaPanel received "+e);
		
		String s = e.getActionCommand();
		
		if( s.equals(ElecPot.STATECHANGED) )
			setState( (Example) e.getSource() );
		else if( s.equals(ElecPot.MODECHANGED) )
			setMode( e.getID() );
	}
	
	class FormulaIcon extends GenericIcon
	{
		public FormulaIcon( URL name )
		{
			super((Image)null,null,"formula");
			setIcon(getToolkit().getImage(name));
		}
		
		public FormulaIcon( Image img )
		{
			super( img, null, "formula");
			//setIcon(getToolkit().getImage(name));	done in super
			setIcon( img );
		}
		
		public Dimension getMinimumSize()
		{
			return new Dimension(179,60);
		}
		
		public Dimension getPreferredSize()
		{
			return getMinimumSize();
		}
		
		public void paint( Graphics g )
		{
			Image icon = getIcon();
			if( icon != null )
			{
				Dimension size = getSize();
				int h = icon.getHeight(this);
				int w = icon.getWidth(this);
				
				//System.out.println("size="+size+" h="+h+" w="+w);
				
				if( h != -1 && w != -1 )
				{
					g.drawImage( icon, (size.width-w)/2,
						(size.height-h)/2, w, h, this ); 
				}
			}
		}
	}
}
