/*
	Trivial applet that displays a string - 4/96 PNL
*/

import java.awt.*;
import java.applet.Applet;

public class TrivialApplet extends Applet
{
	public void init() {
		repaint();
		
		Gauss gauss = new Gauss( 5.0, 10.0 );
		for( int i=0; i<20; ++i )
			System.out.println( gauss.getValue() );
	}
	
	public void paint( Graphics g ) {
		g.drawString( "Hello World!", 30, 30 );
	}

}
