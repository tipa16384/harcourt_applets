/*
	Load Electric Potential applet.
*/
import java.awt.*;
import java.applet.Applet;

public class Main extends Applet
{
	boolean crippleware = false;
	
	public void start()
	{
		// the Mac implementation of the JVM is flawed; take note.
		
		String s = System.getProperty("os.name");
		System.out.println("os.name = \""+s+"\"");
		crippleware = s.startsWith("M");
		
		GraphInfo info = new GraphInfo();
		info.setParameters( this );
		
		ElecPot coul = new ElecPot(this,info);
		
		setLayout( new FixedLayoutManager() );
		add( coul, new Rectangle( 0, 0, 590, 310 ) );
		
		validate();
		repaint();
	}

    public String[][] getParameterInfo()
    {
		return GraphInfo.paramList;
    }

	public String getAppletInfo()
	{
		return( "Electric Potential [4.13.99] by Archipelago Productions" );
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
}

