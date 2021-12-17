import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;

public class Controls extends Panel
{
	GraphInfo info;
	
	final static int gap = 10;

	final static String cubicFile = "cubic.gif";
	final static String absFile = "abs.gif";
	final static String blancFile = "blanc.gif";

	final static String zoomInString = "zoom in 2x";
	final static String zoomOutString = "zoom out";
	final static String curvesTitle = "Functions:";
	final static String viewTitle = "View:";
	
	public Controls( GraphInfo info )
	{
		super( new GridLayout(1,2) );
		
		this.info = info;
		setBackground( new Color(153,153,153) );
		
		add( new CurvePanel() );
		add( new ViewPanel() );
	}
	
	public Dimension getPreferredSize()
	{
		Dimension dim = super.getPreferredSize();
		
		dim.height = Math.max(dim.height,72);
		return dim;
	}
	
	class NamedPanel extends Panel
	{
		String title;
		Font font = GraphInfo.fontBigBold;
		FontMetrics fm;
		Vector buttons;
		
		public NamedPanel( String title )
		{
			super( new GridLayout(0,1,0,2) );
			setFont(font);
			fm = getFontMetrics( font );
			this.title = title;
			buttons = new Vector();
		}
		
		public void setActive( FakeButton fb )
		{
			//System.out.println("Activate "+fb);
			
			int len = buttons.size();
			
			for( int i=0; i<len; ++i )
			{
				FakeButton ifb = (FakeButton) buttons.elementAt(i);
				ifb.setActive( fb == ifb );
			}
		}
		
		public Component add( Component c )
		{
			if( c instanceof FakeButton )
			{
				FakeButton fb = (FakeButton)c;
				
				boolean first = buttons.isEmpty();
				
				fb.setActive( first );
				buttons.addElement(fb);
			}

			return super.add( c );
		}
		
		public Insets getInsets()
		{
			return new Insets(fm.getHeight(),2,3,2);
		}
		
		public void paint( Graphics g )
		{
			super.paint(g);
			
			Dimension dim = getSize();
			g.setColor(Color.white);
			int sw = fm.stringWidth(title);
			g.drawString(title,(dim.width-sw)/2,fm.getAscent());
		}
	}
	
	class CurvePanel extends NamedPanel
	{
		public CurvePanel()
		{
			super(curvesTitle);
			add( new CubicButton(this) );
			add( new AbsButton(this) );
			add( new BlancButton(this) );
		}
	}
	
	class ViewPanel extends NamedPanel
	{
		public ViewPanel()
		{
			super(viewTitle);
			add( new ScaleButton(this,zoomOutString,1) );
			add( new ScaleButton(this,zoomInString,2) );
		}
	}
	
	class FakeButton extends Canvas
	{
		NamedPanel np;
		boolean active;
		
		public FakeButton( NamedPanel parent )
		{
			np = parent;
			addMouseListener( new MouseAdapter()
				{
					public void mouseClicked( MouseEvent e )
					{
						np.setActive(FakeButton.this);
					}
				} );
		}

		public void setActive( boolean active )
		{
			this.active = active;
			
			if( active )
				setBackground( Color.white );
			else
				setBackground( new Color(204,204,204) );
			
			repaint();
		}
	}

	class ScaleButton extends TextButton
	{
		int scale;
		
		public ScaleButton( NamedPanel parent, String label, int scale )
		{
			super( parent, label );
			this.scale = scale;
		}
		
		public void setActive( boolean active )
		{
			super.setActive( active );
			if( active )
			{
				if( scale == 1 )
				{
					info.setLevel(9);
					info.setScale(1);
				}
				else
				{
					info.setLevel(info.getLevel()+1);
					info.setScale(Math.min(16384,info.getScale()*scale));
				}
			}
		}
	}
	
	class TextButton extends FakeButton
	{
		String text;
		
		public TextButton( NamedPanel parent, String text )
		{
			super(parent);
			
			this.text = text;
			
			setForeground( Color.black );
		}
		
		public void paint( Graphics g )
		{
			Font font = GraphInfo.fontBiggerBold;
			FontMetrics fm = getFontMetrics(font);
			
			g.setFont(font);
			g.setColor( getForeground() );
			
			int width = fm.stringWidth(text);
			Dimension dim = getSize();
			
			g.drawString(text,(dim.width-width)/2,(dim.height-fm.getHeight())/2+fm.getAscent());
		}
	}

	class BlancButton extends FunctionButton
	{
		//int level = 7;
		int displacement = 2;
		double scale = 2.0;
		
		public BlancButton( NamedPanel parent )
		{
			super( parent, blancFile );
		}
		
		double g( double x )
		{
			x /= scale;
			return 0.5 - Math.abs((x%1.0)-0.5);
		}
		
		public double value( double x )
		{
			int end = 2*(20);
			double val = 0.0;
			
			for( int i=0; i<=end; ++i )
			{
				val += Math.pow((double)displacement,(double)(-i)) *
					   g(Math.pow(2.0,(double)i)*Math.abs(x));
			}
		
			return val * scale;
		}
	}

	class AbsButton extends FunctionButton
	{
		public AbsButton( NamedPanel parent )
		{
			super( parent, absFile );
		}
		
		public double value( double x )
		{
//			return Math.abs(2*x*x-4*x+1) + 1;
//			return Math.abs(2*x*x-4*x+1);
			return Math.abs(x*x-4*x+3);
		}
	}

	class CubicButton extends FunctionButton
	{
		public CubicButton( NamedPanel parent )
		{
			super( parent, cubicFile );
		}
		
		public double value( double x )
		{
			return x*x*x-x*x+2*x-1;
		}
	}

	abstract class FunctionButton extends ImageButton
						 implements Function
	{
		public FunctionButton( NamedPanel parent, String fileName )
		{
			super( parent, fileName );
		}
		
		public void setActive( boolean active )
		{
			super.setActive( active );
			
			if( active )
				info.setCurrentFunction( this );
		}
	}

	class ImageButton extends FakeButton
	{
		Image image;
		String fileName;
		
		public ImageButton( NamedPanel parent, String fileName )
		{
			super(parent);
			
			this.fileName = fileName;
			
			try
			{
				MediaTracker mt = new MediaTracker(this);
				image = Utility.getImage(this,fileName);
				mt.addImage( image, 0 );
				mt.waitForAll();
			}
			
			catch( Exception e )
			{
				System.err.println("FakeButton: while reading "+fileName+": "+e);
			}
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			
			int width = image.getWidth(this);
			int height = image.getHeight(this);
			
			//System.out.println("Width,height for "+fileName+" is "+width+","+height);
			
			g.drawImage( image, (dim.width-width)/2,
								(dim.height-height)/2,
								width, height, this );
		}
	}
}
