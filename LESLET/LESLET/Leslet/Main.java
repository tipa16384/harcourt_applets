/* 
	Main.java

	Title:			Leslet
	Author:			Brenda Holloway
	Description:	A mini lesson player to be included within an HTML page.
*/

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class Main extends Applet 
{

// BEGIN GENERATED CODE
	// member declarations
// END GENERATED CODE

	public static Lesson lesson = null;
	boolean isStandalone = false;

	// Variables to hold applet parameters
	String xmlSource;
	int width;
	int height;

	public Main() 
	{
		ParsedClass.applet = this;
	}

	// Retrieve the value of an applet parameter
	public String getParameter(String key, String def) 
	{
		return isStandalone ? System.getProperty(key, def) :
			(getParameter(key) != null ? getParameter(key) : def);
	}

	// Get info on the applet parameters
	public String[][] getParameterInfo() 
	{
		String info[][] =
		{
			{"src", "String", "XML source file URL"},
			{"width", "int", "Width of the applet"},
			{"height", "int", "Height of the applet"}
		};

		return info;
	}

	// Get applet information
	public String getAppletInfo() 
	{
		return "An applet to provide a mini-browsing environment for Lesson-Player style layouts.";
	}

	// Initialize the applet
	public void init() 
	{
		try { xmlSource = this.getParameter("src",""); } catch (Exception e) { e.printStackTrace(); }
		try { width = Integer.parseInt(this.getParameter("width","400")); } catch (Exception e) { e.printStackTrace(); }
		try { height = Integer.parseInt(this.getParameter("height","500")); } catch (Exception e) { e.printStackTrace(); }
		initComponents();
	}

	public void initComponents()
	{
// BEGIN GENERATED CODE
		// the following code sets the frame's initial state
		
		String state = "";
		
		try
		{
			state = "setSize";
			setSize( width, height );
			state = "setLayout";
			setLayout(new BorderLayout());
			state = "setLocation";
			setLocation(new java.awt.Point(0, 0));
			state = "setBackground";
			setBackground(Color.white);
			state = "setFont";
			setFont(new Font("Serif",Font.PLAIN,14));
		}
		
		catch( Exception e )
		{
			System.err.println("Main: in "+state+", got "+e);
		}

// END GENERATED CODE
	}
	

	// Standard method to start the applet
	public void start() 
	{
		Convert convert = new Convert();
		convert.start(getCodeBase(),xmlSource);
		if( lesson != null )
			add( lesson, BorderLayout.CENTER );
		//System.out.println("lesson is "+lesson);
	}

	// Standard method to stop the applet
	public void stop() 
	{
	}

	// Standard method to destroy the applet
	public void destroy() 
	{
	}

	// Main entry point when running standalone
	public static void main(String[] args) 
	{
		Main applet = new Main();
		applet.isStandalone = true;
		Frame frame = new Frame();
		frame.setTitle("Applet Frame");
		frame.add( applet, BorderLayout.CENTER );
		applet.init();
		applet.start();
		frame.setSize( 400, 500 );
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation( (d.width - frame.getSize().width) / 2,
			(d.height - frame.getSize().height) / 2);
		frame.setVisible( true );
	}

}
