import java.util.*;
import java.io.*;
import java.awt.*;
import java.net.*;

public class Picture extends ParsedClass
{
	public String align = "none";
	public String caption = null;
	public String src = null;
	
	transient Image image = null;
	transient MediaTracker mt = null;
	
	public Picture()
	{
		mt = new MediaTracker(this);
	}
		
	public String toString()
	{
		return getClass().getName()+"(align="+align+",src="+src+")";
	}

	void checkImage()
	{
		if( mt == null || src == null )
			return;
			
		try
		{
			URL url = new URL(applet.getDocumentBase(),src);
			image = getToolkit().getImage(url);
			mt.addImage( image, 0 );
			mt.waitForAll();
		}
		
		catch( Exception e )
		{
			System.err.println("While trying to get "+src+" at "+applet.getDocumentBase()+": "+e);
		}

		finally
		{
			mt = null;
		}
	}
	
	public Dimension getMinimumSize()
	{
		Dimension dim;
		int w=0, h=0;
		
		checkImage();
		if( image != null )
		{
			w = image.getWidth(this);
			h = image.getHeight(this);
		}
			
		w = (w <= 0) ? 72 : w;
		h = (h <= 0) ? 72 : h;

		return new Dimension(w,h);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		drawPicture(g);
	}
	
	void drawPicture( Graphics g )
	{
		checkImage();

		if( image != null )
		{
			Dimension size = getSize();
			int x = 0;
			
			int pw = image.getWidth(this);
			if( size.width > pw )
			{
				if( "center".equals(align) )
				{
					x = (size.width-pw)/2;
				}
				
				else if( "right".equals(align) )
				{
					x = size.width - pw;
				}
			}
			
			g.drawImage(image,x,0,this);

		}
	}
}
