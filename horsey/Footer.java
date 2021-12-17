import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;

public class Footer extends Panel
{
	Main main;
	GraphInfo info;
	
	static final String racesLeft = "Races Left: ";
	static final String currentWinnings = "Current Winnings: ";
	static final String clear = "CLEAR";
	static final String restart1 = "Restart ";
	static final String restart2 = "-race Sequence";
	
	CalcText racesDisplay;
	CalcText winningsDisplay;
	
	public Footer( Main main, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.main = main;
		this.info = info;
		
		add( new Races(), BorderLayout.SOUTH );
	}
	
	class ResetButton extends Panel
	{
		public ResetButton()
		{
			super( new BorderLayout() );

			Component c;
			
			setBackground( new Color(153,153,153) );
			setForeground( new Color(0,0,0) );
			
			MouseListener al = new MouseAdapter()
				{
					public void mouseClicked( MouseEvent e )
					{
						//System.out.println("Reset");
						main.reset();
					}
				};
			
			addMouseListener( al );
			
			String s = restart1+info.getMaxRaces()+restart2;
			
			c = new CalcText( s, Font.ITALIC, 11, false );
			c.addMouseListener( al );
			add( c, BorderLayout.SOUTH );
			
			Panel p1 = new Panel( new BorderLayout() );
			p1.addMouseListener( al );
			add( p1, BorderLayout.CENTER );
			
			c = new CalcText( clear, Font.BOLD, 16, false );
			c.addMouseListener( al );
			p1.add( c, BorderLayout.EAST );
		}
		
		public Insets getInsets()
		{
			return new Insets(0,2,0,2);
		}
	}

	class Races extends Panel
	{
		public Races()
		{
			super( new BorderLayout() );
			setBackground( new Color(153,153,204) );
			setForeground( new Color(0,0,0) );

			Dimension dim = getPreferredSize();
			Insets insets = getInsets();
			dim.width -= insets.left+insets.right;
			dim.height -= insets.top+insets.bottom;
			
			Component c;
			Panel p1, p2;
			
			int small = (dim.height*8)/10;
			racesDisplay = new CalcText( racesLeft, Font.BOLD, small, false );
			add( racesDisplay, BorderLayout.WEST );
			
			info.addPropertyChangeListener( new PropertyChangeListener()
				{
					public void propertyChange( PropertyChangeEvent pce )
					{
						if( pce.getPropertyName().equals(info.num_races) )
						{
							racesDisplay.setText(racesLeft+info.getRaces());
							Races.this.invalidate();
							Races.this.validate();
						}
					}
				} );
			
			p1 = new Panel();
			winningsDisplay = new CalcText( currentWinnings+Main.formatMoney(info.getWinnings()), Font.BOLD, small, false );
			add( winningsDisplay, BorderLayout.CENTER );
			
			info.addPropertyChangeListener( new PropertyChangeListener()
				{
					public void propertyChange( PropertyChangeEvent pce )
					{
						if( pce.getPropertyName().equals(info.mo_winnings) )
						{
							winningsDisplay.setText(currentWinnings+Main.formatMoney(info.getWinnings()));
							Races.this.invalidate();
							Races.this.validate();
						}
					}
				} );
			
			add( new ResetButton(), BorderLayout.EAST );
		}
		
		public Insets getInsets()
		{
			return new Insets(3,5,0,5);
		}
		
		public Dimension getPreferredSize()
		{
			return getMinimumSize();
		}
		
		public Dimension getMinimumSize()
		{
			Dimension dim = super.getMinimumSize();
			dim.height = 30;
			return dim;
		}
	}
}
