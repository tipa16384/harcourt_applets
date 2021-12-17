/*
	Load Capacitance applet.
*/
import java.awt.*;
import java.applet.*;

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
		
		System.out.println("Main - setting parameters");
		info.setParameters( this );
		
		Kapasitenz coul = new Kapasitenz(this,info);
		
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
		return( "Capacitance [4.26.1999] by Archipelago Productions" );
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

