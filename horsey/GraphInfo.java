import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.*;
import java.beans.*;

public final class GraphInfo 
{
	// color for the force vectors
	static public final Color FORCE_VECTOR_COLOR = new Color(0,51,204);
	
	// color for the negative charge
	static public final Color NEGATIVE_COLOR = new Color(51,102,255);
	
	// color for the positive charge
	static public final Color POSITIVE_COLOR = new Color(255,51,51);
	
	// color for the field lines
	static public final Color FIELD_COLOR = new Color(204,0,0);
	
	// color for the control backdrop
	static public final Color CONTROL_COLOR = new Color(255,255,206);
	
	// color of the "halo" around the selected charge
	static public final Color HIGHLIGHT_COLOR = new Color(255,204,51);
	
	// color of the grid lines (the 'rules') in the graph
	static public final Color GRID_COLOR = new Color(204,255,255);
	
	// color of the axes in the graph
	static public final Color AXIS_COLOR = new Color(204,204,204);
	
	// color of the horizontal line below the graph
	static public final Color SEPARATE_COLOR = new Color(153,153,153);
	
	static public Font fontHeading;
	static public Font fontPlainSmall;
	static public Font fontPlain;
	static public Font fontBold;
	static public Font fontBigBold;
	static public Font fontBiggerBold;
	static public Font fontButton;
	
	// properties that can be changed
	static public final String mo_money = "Cash";
	static public final String num_races = "Races";
	static public final String mo_winnings = "Winnings";
	static public final String what_bet = "Bet";
	static public final String new_horse = "Horse";
	
	// parameters
	static public final String [][] paramList = {
		{ "cash",	"int",		"amount of starting cash" },
		{ "races",	"int",		"number of races" }
		};

	Horse [] horses;

	// property changer
	PropertyChangeSupport pcs;
		
	// starting cash
    private int cash = 100;
    
    // winnings
    private int winnings = 0;
    
    // bet
    private int bet = 100;
    
    // raw bet text
    private String betText = "";
    
    // current cash
    private int wad = -1;
    
    // starting number of races
    private int maxRaces = 2;
    
    // current number of races left
    private int races = -1;
    
    // which horse I like
    private Horse myHorse = null;
    
	public static Random rand = new Random();

	static final public boolean debug = false;
	
	public static boolean isMac;

	public GraphInfo( boolean crippled )
	{
		pcs = new PropertyChangeSupport(this);
		
		isMac = crippled;
		
		horses = new Horse[3];
		horses[0] = new Horse( "eDancer", 2, 0.1, 3.0/2.0 );
		horses[1] = new Horse( "Flying Fred", 2.3, 0.3, 3.33 );
		horses[2] = new Horse( "Lady B", 2.5, 0.6, 35.0 );
		
		myHorse = horses[0];

		if( crippled )
		{
			fontHeading = new Font("SansSerif",Font.BOLD,12);
			fontPlainSmall = new Font("SansSerif",Font.PLAIN,9);
			fontPlain = new Font("SansSerif",Font.PLAIN,9);
			fontBold = new Font("SansSerif",Font.BOLD,9);
			fontBigBold = new Font("SansSerif",Font.BOLD,10);
			fontBiggerBold = new Font("SansSerif",Font.BOLD,12);
			fontButton = new Font("SansSerif",Font.BOLD,14);
		}
		
		else
		{
			fontHeading = new Font("SansSerif",Font.BOLD,14);
			fontPlainSmall = new Font("SansSerif",Font.PLAIN,9);
			fontPlain = new Font("SansSerif",Font.PLAIN,10);
			fontBold = new Font("SansSerif",Font.BOLD,10);
			fontBigBold = new Font("SansSerif",Font.BOLD,12);
			fontBiggerBold = new Font("SansSerif",Font.BOLD,14);
			fontButton = new Font("SansSerif",Font.ITALIC+Font.BOLD,18);
		}
		
		calcPayoff();
	}

	void calcPayoff()
	{
		final int nraces = 2000;
		final int len = horses.length;
		
		int [] wins = new int[len];
		int [] losses = new int[len];
		int j;
		
		for( int i=0; i<nraces; ++i )
		{
			int best = -1;
			double bestTime = 10000.0;
			
			for( j=0; j<len; ++j )
			{
				double time = horses[j].raceTime(false);
				if( time < bestTime )
				{
					best = j;
					bestTime = time;
				}
			}
			
			for( j=0; j<len; ++j )
			{
				if( j == best )
					++wins[j];
				else
					++losses[j];
			}
		}
		
		for( j=0; j<len; ++j )
		{
			if( wins[j] == 0 )
				wins[j] = 1;
			
			double ratio = (double)losses[j] / (double)wins[j];
			ratio = Math.rint(ratio * 100.0)/100.0;
			if( ratio == 0.0 ) ratio = 0.01;
			
			horses[j].setPayoff(ratio);
		}
	}

	public void setMyHorse( int index )
	{
		Horse old = myHorse;
		myHorse = horses[index];
		
		//if( old != myHorse )
			pcs.firePropertyChange(new_horse,old,myHorse);
	}
	
	public Horse getMyHorse()
	{
		return myHorse;
	}
	
	public Horse [] getHorses()
	{
		return horses;
	}

	public void setBetText( String text )
	{
		//System.err.println("Bet text set to "+text);
		betText = text;
	}
	
	public String getBetText()
	{
		return betText;
	}

	public int getBet()
	{
		return bet;
	}
	
	public void setBet( int bet )
	{
		//System.err.println("Bet set to "+bet);
		int t = this.bet;
		this.bet = bet;
		pcs.firePropertyChange(what_bet,new Integer(t),new Integer(bet));
	}

	public int getWinnings()
	{
		return winnings;
	}
	
	public void setWinnings( int win )
	{
		int t = winnings;
		winnings = win;
		pcs.firePropertyChange(mo_winnings,new Integer(t),new Integer(winnings));
	}

	public int getStartingCash()
	{
		return cash;
	}

	public int getWad()
	{
		return wad;
	}
	
	public void setWad( int amt )
	{
		int t = wad;
		wad = amt;
		pcs.firePropertyChange(mo_money,new Integer(t),new Integer(wad));
	}

	public int getMaxRaces()
	{
		return maxRaces;
	}

	public int getRaces()
	{
		return races;
	}
	
	public void setRaces( int amt )
	{
		int t = races;
		races = amt;
		pcs.firePropertyChange(num_races,new Integer(t),new Integer(races));
	}

	public void setParameters( Applet apl )
	{
		System.out.println("setting parameters for the applet");

		cash = parseInt(apl,paramList[0][0],cash);
		maxRaces = parseInt(apl,paramList[1][0],maxRaces);
	}
	
	private String param( Applet apl, String name )
	{
		String s = apl.getParameter(name);
		if( s != null && s.length() == 0 )
			s = null;
		
		System.out.println("Value of "+name+" is "+s);
		
		return s;
	}
	
	private boolean parseBoolean( Applet apl, String name, boolean old )
	{
		String val;
		
		System.out.println("Looking for parameter "+name);
		
		val = param( apl, name );
		
		if( val == null )
			return old;
		else if( val.equalsIgnoreCase("on") )
			return true;
		else if( val.equalsIgnoreCase("off") )
			return false;
		else
			return old;
	}
	
	private String parseString( Applet apl, String name, String old )
	{
		String val = param( apl, name );
		
		return (val == null) ? old : val;
	}

	private int parseInt( Applet apl, String name, int old )
	{
		String val;
		int res = old;
		
		val = param( apl, name );
		
		if( val != null )
		{
			try
			{
				res = Integer.parseInt(val);
			}
			
			catch( Exception e )
			{
			}
		}

		return res;
	}	
	
	private double parseDouble( Applet apl, String name, double old )
	{
		String val;
		double res = old;
		
		val = param( apl, name );
		
		if( val != null )
		{
			try
			{
				Double d = new Double(val);
				res = d.doubleValue();
			}
			
			catch( Exception e )
			{
			}
		}

		return res;
	}	


    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     *
     * @param listener  The PropertyChangeListener to be added
     */

    public synchronized void addPropertyChangeListener(
				PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  The PropertyChangeListener to be removed
     */

    public synchronized void removePropertyChangeListener(
				PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }
}
