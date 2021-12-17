import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class Link extends Paragraph
{
	public String url = null;
	
	public void addNotify()
	{
		super.addNotify();
		
		debug("Link.addNotify called");
		debug("parent is "+getParent());
		debug("url is "+url);
		debug("heading is "+heading);
		debug("text is "+characters);
		debug("texter is "+rl);
		
		if( rl != null )
		{
			rl.setForeground( Color.blue );
			rl.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
			if( characters == null || characters.length() == 0 )
				rl.setText(url);
			rl.addMouseListener( new Mousey() );
			rl.lull();
		}
	}
	
	public void removeNotify()
	{
		super.removeNotify();
		
		debug("Link.removeNotify called");
	}

	class Mousey extends MouseAdapter
	{
		public void mouseClicked( MouseEvent me )
		{
			System.out.println("Let's go to "+url);
			
			String state = "";
			
			try
			{
				state = "forming URL from "+url;
				
				URL purl = new URL(applet.getDocumentBase(),url);
				
				state = "sending URL to applet";
				
				applet.getAppletContext().showDocument(purl,"_leslet");
			}
			
			catch( Exception e )
			{
				System.err.println("Link.Mousey crashed while "+state+" with "+e);
			}
		}
	}
}
