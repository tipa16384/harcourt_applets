import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import util.DoubleBufferPanel;

public class Content extends Panel implements Runnable
{
	Main main;
	GraphInfo info;
	Vector flipbook = new Vector();
	Vector buttons = new Vector();
	Vector bets = new Vector();
	int currentScreen = -1;
	
	static final Color butSelBG = Color.red;
	static final Color butSelFG = Color.white;
	static final Color butUnselBG = GraphInfo.AXIS_COLOR;
	static final Color butUnselFG = Color.red;

	RaceScreen raceScreen;
	
	public Content( Main main, GraphInfo info )
	{
		super( new BorderLayout() );
		
		Component c;
		Dimension dim;
//		Panel buttonPanel = new Panel( new TwoDLayout(5,10) );
		Panel buttonPanel = new Panel( new GridLayout(0,1,0,5) );

		this.main = main;
		this.info = info;
		
		buttonPanel.setFont( info.fontButton );
		add( buttonPanel, BorderLayout.WEST );
		
		setBackground( new Color(255,255,204) );
		setForeground( new Color(0,0,0) );
	
		Horse [] horses = info.getHorses();
		
		raceScreen = new RaceScreen(horses);
		flipbook.addElement( raceScreen );
		
		c = new NavLabel("Show Track",0);
		buttonPanel.add( c );
		buttons.addElement( c );
		
		for( int i=0; i<horses.length; ++i )
		{
			NavLabel navLabel = new NavLabel(horses[i].getName(),i+1);
			flipbook.addElement( new DataScreen(horses[i]) );
			buttonPanel.add( navLabel );
			buttons.addElement( navLabel );
		}
		
		clearBets();
		loadScreen(0);
	}
	
	// run the race
	public void doRace()
	{
		debug("doRace");
		
		loadScreen(0);
		raceScreen.reset();

		try
		{
			debug("doRace - getBet");
			
			String text = info.getBetText();
			
			int tbet = Integer.parseInt(text);
			info.setBet(tbet);
			
			debug("doRace - create thread");
			
			Thread t = new Thread(this);
			t.start();
		}
		
		catch( NumberFormatException e )
		{
			debug("doRace - format exception");
			
			info.setBet(0);
			return;
		}
	}

	public void run()
	{
		double time = 0;
		
		if( info.getRaces() <= 0 )
			return;
		
		int x = info.getBet();
		
		info.setBet( Math.max(0,Math.min(x,info.getWad())) );
		info.setWad( info.getWad()-info.getBet() );
		info.setRaces( info.getRaces()-1 );

		try
		{
		
			while( !raceScreen.isRaceOver() )
			{
				time += 0.04;
				raceScreen.setTime(time);
				Thread.yield();
				Thread.sleep(100);
			}
		}
		
		catch( Exception e )
		{
		}

		// after the race is finished, reset all bets
		clearBets();
		
		Horse winner = raceScreen.whoWon();
		
		//System.out.println(winner.getName()+" won");
		//System.out.println("User bet on "+info.getMyHorse().getName());
		
		if( winner == info.getMyHorse() )
		{
			double po = winner.getPayoff();
			
			if( po >= 1.0 )
				po = Math.rint(po);
			else
				po = 1.0/Math.rint(1.0/po);
				
			int winnings = (int) Math.round(info.getBet() * po);
			info.setWinnings( info.getWinnings()+winnings );
			info.setWad( info.getWad()+info.getBet()+winnings );
		}
		
		else
		{
			info.setWinnings( info.getWinnings()-info.getBet() );
		}
		
		info.setBet(Math.max(0,Math.min(100,info.getWad())));
	}
	
	void clearBets()
	{
		bets.removeAllElements();

		Bet b;
		Horse [] horses = info.getHorses();
		
		bets.addElement( new Bet(info,horses[1],100,Bet.WIN) );
		bets.addElement( new Bet(info,horses[0],1000,Bet.PLACE) );
		bets.addElement( new Bet(info,horses[2],216,Bet.SHOW) );
		bets.addElement( new Bet(info,horses[1],1024,Bet.PLACE) );
	}
	
	public void loadScreen( int index )
	{
		//System.out.println("Loading screen "+index);
		
		Component [] components = getComponents();
		Component newScreen = (Component)flipbook.elementAt(index);
		
		boolean addnew = true;
		
		for( int i=0; i<components.length; ++i )
		{
			Component c = getComponent(i);
			
			if( c == null ) continue;
			
			if( c instanceof RaceScreen ||
				c instanceof DataScreen )
			{
				if( c != newScreen )
					remove(c);
				else
					addnew = false;
				
				break;
			}
		}
		
		if( addnew )
		{
			if( currentScreen >= 0 )
			{
				Component c;
				
				c = (Component) buttons.elementAt( currentScreen );
				c.setForeground( butUnselFG );
				c.setBackground( butUnselBG );
				
				if( GraphInfo.isMac )
					c.repaint();
			}
			
			currentScreen = index;
			
			{
				Component c;
				
				c = (Component) buttons.elementAt( currentScreen );
				c.setForeground( butSelFG );
				c.setBackground( butSelBG );

				if( GraphInfo.isMac )
					c.repaint();
			}

			add( newScreen, BorderLayout.CENTER );
			invalidate();
			validate();
		}
	}
	
	class NavLabel extends Label
	{
		int index;
		
		public NavLabel( String s, int index )
		{
			super(s);
			this.index = index;
			setBackground( butUnselBG );
			setForeground( butUnselFG );
			setAlignment( Label.CENTER );
			setFont( info.fontButton );
			enableEvents( AWTEvent.MOUSE_EVENT_MASK );
		}
		
		public void setBounds( int x, int y, int w, int h )
		{
			if( GraphInfo.isMac )
			{
				y += 5;
				h -= 10;
			}

			else
			{			
				x += 5; y += 5;
				w -= 10; h -= 10;
			}
						
			super.setBounds(x,y,w,h);
		}
		
		protected void processMouseEvent( MouseEvent e )
		{
			if( e.getID() == MouseEvent.MOUSE_CLICKED )
			{
				loadScreen(index);
			}
			
			super.processMouseEvent(e);
		}
	}
	
	class Timer extends Component
				implements Resettable
	{
		double time;
		
		public Timer()
		{
			reset();
		}
		
		public void reset()
		{
			setTime(0.0);
		}

		public void setTime( double time )
		{
			if( this.time != time )
			{
				this.time = time;
				repaint();
			}
		}
		
		public boolean isRaceOver()
		{
			return true;
		}
	}
	
	class RaceScreen extends DoubleBufferPanel
					 implements Resettable
	{
		public RaceScreen( Horse [] horses )
		{
			super( new GridLayout(0,1,0,5) );
			
			add( new Timer() );
			
			for( int i=0; i<horses.length; ++i )
				add( new RaceTrack( horses[i] ) );
			
			//p.add( new BetDisplay(info,bets), BorderLayout.CENTER );
			//p.add( new BetEntry(info), BorderLayout.SOUTH );
			//add( p, BorderLayout.EAST );
		}
		
		public Insets getInsets()
		{
			return new Insets(0,10,0,10);
		}
		
		public void reset()
		{
			Component [] comps = getComponents();
			
			for( int i=0; i<comps.length; ++i )
			{
				Component c = getComponent(i);
				
				if( c instanceof Resettable )
					((Resettable)c).reset();
			}
		}
		
		public void setTime( double time )
		{
			Component [] comps = getComponents();
			
			for( int i=0; i<comps.length; ++i )
			{
				Component c = getComponent(i);
				
				if( c instanceof Resettable )
					((Resettable)c).setTime( time );
			}
		}
		
		public boolean isRaceOver()
		{
			Component [] comps = getComponents();
			
			boolean raceOver = true;
			
			for( int i=0; raceOver && (i<comps.length); ++i )
			{
				Component c = getComponent(i);
				
				if( c instanceof Resettable )
					raceOver &= ((Resettable)c).isRaceOver();
			}
			
			return raceOver;
		}
		
		public Horse whoWon()
		{
			Component [] comps = getComponents();
			
			double mintime = 1000.0;
			Horse minHorse = null;
			
			for( int i=0; i<comps.length; ++i )
			{
				Component c = getComponent(i);
				
				if( c instanceof RaceTrack )
				{
					RaceTrack rt = (RaceTrack)c;
					double time = rt.getFinishTime();
					
					if( time < mintime )
					{
						mintime = time;
						minHorse = rt.getHorse();
					}
				}
			}

			return minHorse;
		}
	}
	
	class DataScreen extends Panel
	{
		Horse horse;
		
		public DataScreen( Horse h )
		{
			super( new BorderLayout() );
			
			horse = h;

			Panel p;
			Component c;
			Dimension dim;
			Point origin = new Point(10,10);
			
			p = new Panel(null);
			
			c = h.getBellCurve();
			p.add( c );
			dim = c.getPreferredSize();
			c.setBounds( origin.x, origin.y, dim.width, dim.height );
			origin.x += dim.width+10;
			
			c = new RaceHistory( info, h );
			p.add( c );
			dim = c.getPreferredSize();
			c.setBounds( origin.x, origin.y, dim.width, dim.height );
			origin.x += dim.width+10;

			add( p, BorderLayout.CENTER );
		}
	}

	private static void debug( String s )
	{
		if( GraphInfo.debug )
			System.out.println("Utility:: "+s);
	}
}
