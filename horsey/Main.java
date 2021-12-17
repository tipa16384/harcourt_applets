/*
	Load Graphing applet.
*/
import java.awt.*;
import java.applet.*;

public class Main extends Applet
{
	boolean crippleware = false;
	Horsey coul;
	GraphInfo info;
	
	public void start()
	{
		// the Mac implementation of the JVM is flawed; take note.
		
		String s = System.getProperty("os.name");
		System.out.println("os.name = \""+s+"\"");
		crippleware = s.startsWith("M");
		
		Utility.setApplet(this);
		
		info = new GraphInfo(crippleware);
		
		//System.out.println("Main - setting parameters");
		info.setParameters( this );
		
		coul = new Horsey(this,info);
		
		setLayout( new BorderLayout() );
		add( coul, BorderLayout.CENTER );
	
		this.setBackground( Color.white );
		
		validate();
		repaint();

	
    	//System.out.println("Applet Main.setStub("+stub+")");
	}

    public String[][] getParameterInfo()
    {
		return GraphInfo.paramList;
    }

	public String getAppletInfo()
	{
		return( "Horsey [7.20.2000] by Archipelago Productions" );
	}
	
	// must we run with crippled functionality -- on the Macintosh?
	
	public boolean isCrippled()
	{
		return crippleware;
	}
	
	// find the applet that contains this component.
	
	public static Main getApplet( Component c )
	{
		while( c != null && !(c instanceof Applet) )
		{
			c = c.getParent();
		}
		
		return (Main) c;
	}
	
	// run the race
	public void doRace()
	{
		coul.doRace();
	}

	// reset the applet
	public void reset()
	{
		// set current money to starting money
		info.setWad( info.getStartingCash() );
		info.setRaces( info.getMaxRaces() );
		info.setWinnings( 0 );
		info.setMyHorse( 0 );
	}

	// utility function to format money nicely
	static String formatMoney( int money )
	{
		String s = "$";
		
		if( money >= 1000 )
		{
			s += (money/1000) + ",";
			money %= 1000;
			if( money < 10 ) s += "00"+money;
			else if( money < 100 ) s += "0"+money;
			else s += money;
		}
		
		else
		{
			s += money;
		}
		
		return s;
	}
}

