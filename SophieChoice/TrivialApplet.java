/*
	Trivial applet that displays a string - 4/96 PNL
*/

import java.awt.*;
import java.applet.Applet;

public class TrivialApplet extends Applet
{
	public void init() {
		repaint();
	}
	
	public void start()
	{
		removeAll();
		setLayout( new BorderLayout() );
		add( new TestPanel(), BorderLayout.CENTER );
	}
}
