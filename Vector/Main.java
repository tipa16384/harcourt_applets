/*
	Load Coulomb's Law applet.
*/

import java.awt.*;
import java.applet.Applet;

public class Main extends Applet
{
	boolean crippleware = false;
	
	public void init()
	{
		// the Mac implementation of the JVM is flawed; take note.
		
		String s = System.getProperty("os.name");
		System.out.println("os.name = \""+s+"\"");
		crippleware = s.startsWith("M");
		
		GraphInfo info = new GraphInfo();
		info.setParameters( this );
		
		Coulomb1 coul = new Coulomb1(this,info);
		
		setLayout( new BorderLayout() );
		add( coul, BorderLayout.CENTER );
		
		validate();
		repaint();
	}

    public String[][] getParameterInfo()
    {
		return GraphInfo.paramList;
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

	public String getAppletInfo()
	{
		return( "Coulomb's Law [5.26.1999] by Archipelago Productions" );
	}
}

