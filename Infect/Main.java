/*
	Load Graphing applet.
*/
import java.awt.*;
import java.applet.*;

public class Main extends Applet
{
	boolean crippleware = false;
	Infect coul;
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
		
		coul = new Infect(this,info);
		
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
		return( "Squares [12.19.2000] by Brenda Holloway for Harcourt e-Learning" );
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
	
	// reset the applet
	public void reset()
	{
	}
}

