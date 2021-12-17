import java.util.*;
import java.io.*;
import java.awt.*;

public class Flipbook extends Picture
					  implements FlipbookController
{
	public String frames = null;

	Component control;
	
	transient int frame = 0;
	transient int nframes = 1;
	
	public Flipbook()
	{
		setLayout(null);
		control = new StandardControl(this);
		add( control );
	}
	
	void checkImage()
	{
		super.checkImage();
		
		if( frames != null )
		{
			try
			{
				nframes = Integer.parseInt(frames);
				if( nframes < 1 ) nframes = 1;
			}
			
			catch( Exception e )
			{
				System.err.println("while finding frame count from "+frames+": "+e);
				nframes = 1;
			}
		}
		
		else
		{
			nframes = 1;
		}
	}
	
	public void doLayout()
	{
		Dimension controlSize = control.getSize();
		Dimension size = getSize();
		
		int y = size.height - controlSize.height;
		int x;
		
		if( "center".equals(align) )
		{
			x = (size.width-controlSize.width)/2;
		}
		
		else if( "right".equals(align) )
		{
			x = (size.width-controlSize.width);
		}
		
		else x = 0;
		
		control.setLocation(x,y);
	}
	
	
	public Dimension getMinimumSize()
	{
		Dimension dim = super.getMinimumSize();
		//System.out.println(nframes+" frames");
		dim.height /= nframes;
		dim.height += control.getSize().height;
		return dim;
	}
	
	public int getCurrentStep()
	{
		checkImage();
		return frame+1;
	}
	
	public void setTotalSteps( int ts )
	{
	}
	
	public int getTotalSteps()
	{
		checkImage();
		return nframes;
	}

	public void stepForward()
	{
		gotoStep( getCurrentStep()+1 );
	}
	
	public void stepBackward()
	{
		gotoStep( getCurrentStep()-1 );
	}
	
	public void gotoStep( int step )
	{
		if( step > 0 && step <= nframes )
		{
			frame = step-1;
			repaint();
		}
	}
	
	void drawPicture( Graphics g )
	{
		checkImage();

		if( image != null )
		{
			Dimension size = getSize();
			int x = 0;
			
			int pw = image.getWidth(this);
			int ph = image.getHeight(this)/nframes;
			
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
			
			g.drawImage(image,
						x, 0, x+pw, ph,
						0, frame*ph, pw, (frame+1)*ph,
						this);

		}
	}

	public String toString()
	{
		return getClass().getName()+"(align="+align+",frames="+frames+",src="+src+")";
	}
}
