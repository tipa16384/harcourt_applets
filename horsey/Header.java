import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;

public class Header extends Panel
{
	Main main;
	GraphInfo info;
	TextComponent betText;
	Choice myHorse;
	
	static final String anteUp = "ANTE UP!";
	static final String bet = "Bet: ";
	static final String horse = "Horse: ";
	static final String race = "RACE!";

	static final String the = "THE";
	static final String harcoll = "H A R C O L L";
	static final String derby = "THE NUMERACY DERBY";
	
	public Header( Main main, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.main = main;
		this.info = info;
		
		add( new Title(), BorderLayout.NORTH );
		add( new Cash(), BorderLayout.CENTER );
		add( new Ante(), BorderLayout.SOUTH );
	}
	
	class Title extends Panel
	{
		public Title()
		{
			setBackground( new Color(153,153,204) );
			setForeground( new Color(255,255,204) );
			
			Dimension dim = getPreferredSize();
			Insets insets = getInsets();
			dim.width -= insets.left+insets.right;
			dim.height -= insets.top+insets.bottom;
			
			add( new CalcText( derby, Font.PLAIN, dim.height )  );
		}
		
		public Insets getInsets()
		{
			return new Insets(-5,5,10,5);
		}
		
		public Dimension getPreferredSize()
		{
			return getMinimumSize();
		}
		
		public Dimension getMinimumSize()
		{
			Dimension dim = super.getMinimumSize();
			dim.height = 38;
			return dim;
		}
		
	}
	
	class Cash extends Panel
	{
		final String startingCash = "Starting Cash Amount: ";
		final String amountAvailable = "Amount Available: ";
		
		CalcText available;
		
		public Cash()
		{
			super( new BorderLayout() );
			
			setBackground( new Color(204,204,153) );
			setForeground( new Color(0,0,0) );
			
			Dimension dim = getPreferredSize();
			Insets insets = getInsets();
			dim.width -= insets.left+insets.right;
			dim.height -= insets.top+insets.bottom;

			Component c = new CalcText( startingCash+Main.formatMoney(info.getStartingCash()),
											Font.BOLD, (6*dim.height)/7, false );
			add( c, BorderLayout.WEST );
			
			available = new CalcText( amountAvailable,
											Font.BOLD, (6*dim.height)/7, false );
			add( available, BorderLayout.EAST );
			
			info.addPropertyChangeListener( new PropertyChangeListener()
				{
					public void propertyChange( PropertyChangeEvent pce )
					{
						if( pce.getPropertyName().equals(info.mo_money) )
						{
							available.setText(amountAvailable+Main.formatMoney(info.getWad()));
							Cash.this.invalidate();
							Cash.this.validate();
							//Cash.this.doLayout();
						}
					}
				} );
		}
		
		public Insets getInsets()
		{
			return new Insets(3,5,5,5);
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
	
	class Ante extends Panel
	{
		
		public Ante()
		{
			super( new BorderLayout() );
			setBackground( new Color(153,153,153) );
			setForeground( new Color(0,0,0) );
			
			Dimension dim = getPreferredSize();
			Insets insets = getInsets();
			dim.width -= insets.left+insets.right;
			dim.height -= insets.top+insets.bottom;
			
			Component c;
			Panel p1, p2;
			Horse [] horses;
			String s;
			
			c = new CalcText( anteUp, Font.PLAIN, dim.height );
			c.setForeground( Color.white );
			add( c, BorderLayout.WEST );
			
			c = new RaceButton();
			add( c, BorderLayout.EAST );

			p1 = new Panel( new FlowLayout() );
			add( p1, BorderLayout.CENTER );
			
			int small = (dim.height*7)/10;
			
			p1.add( new CalcText( bet, Font.BOLD, small, false ) );
			
			s = Integer.toString(info.getBet());
			betText = new TextField(s,6);
			p1.add( betText );
			info.setBetText(s);
			
			info.addPropertyChangeListener( new PropertyChangeListener()
				{
					public void propertyChange( PropertyChangeEvent pce )
					{
						//System.err.println("Got pce "+pce.getPropertyName());
						if( pce.getPropertyName().equals(info.what_bet) )
						{
							betText.setText(Integer.toString(info.getBet()));
						}
					}
				} );
			
			betText.addTextListener( new TextListener()
				{
					public void textValueChanged( TextEvent e )
					{
						//System.out.println("Bet text changed to "+betText.getText());
						info.setBetText( betText.getText() );
					}
				} );

			p1.add( new Spacer(10) );
			
			p1.add( new CalcText( horse, Font.BOLD, small, false ) );
			
			horses = info.getHorses();
			
			{
				myHorse = new Choice();
				
				for( int i=0; i<horses.length; ++i )
					myHorse.add( horses[i].getName() );
				
				myHorse.addItemListener( new ItemListener()
					{
						public void itemStateChanged(ItemEvent e)
						{
							info.setMyHorse( myHorse.getSelectedIndex() );
						}
					} );
				
				p1.add( myHorse );
				
				info.addPropertyChangeListener( new PropertyChangeListener()
					{
						public void propertyChange( PropertyChangeEvent pce )
						{
							//System.err.println("Got pce "+pce.getPropertyName());
							if( pce.getPropertyName().equals(info.new_horse) )
							{
								//System.out.println("Setting new horse");
								Horse h = (Horse) pce.getNewValue();
								Horse [] allHorses = info.getHorses();
								for( int i=0; i<allHorses.length; ++i )
								{
									if( allHorses[i] == h )
									{
										myHorse.select(i);
										break;
									}
								}
							}
						}
					} );
				
			}
		}
		
		public Insets getInsets()
		{
			return new Insets(3,5,5,5);
		}
		
		public Dimension getPreferredSize()
		{
			return getMinimumSize();
		}
		
		public Dimension getMinimumSize()
		{
			Dimension dim = super.getMinimumSize();
			dim.height = 35;
			return dim;
		}

		class RaceButton extends Button
		{
			public RaceButton()
			{
				super(race);
				
				addActionListener( new ActionListener()
					{
						public void actionPerformed( ActionEvent ae )
						{
							main.doRace();
						}
					} );
			}
		}
		
	}
}
