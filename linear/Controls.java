import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.beans.*;
import util.DoubleBufferPanel;

public class Controls extends DoubleBufferPanel implements PropertyChangeListener
{
	GraphInfo info;
	TextComponent x0Text, y0Text, mText;
	Component equation;
		
//	Color controlsBackground = new Color(204,204,204);
//	Color tableBackground = new Color(204,204,153);
//	Color titleBackground = new Color(153,153,104);
//	Color titleForeground = Color.black;
//	Color controlsForeground = Color.black;
//	Color dataForeground = Color.black;
//	Color dataBackground = Color.white;
	
	static final Color controlsBackground = new Color(204,204,204);
	static final Color tableBackground = new Color(240,240,240);
	static final Color titleBackground = new Color(104,104,104);
	static final Color titleForeground = Color.white;
	static final Color controlsForeground = Color.black;
	static final Color dataForeground = Color.black;
	static final Color dataBackground = Color.white;
	
	static final String formulaTitleFile = "formhdr.gif";
	Image formulaTitleImage;
	
	static final String x1File = "x1.gif";
	Image x1Image;
	
	static final String x01File = "x01.gif";
	Image x01Image;
	
	static final String x001File = "x001.gif";
	Image x001Image;
	
	static final int columnX = 0;
	static final int columnYours = 1;
	static final int columnMine = 2;
	static final int columnActual = 3;
	static final int columnCloser = 4;
	
	public Controls( GraphInfo info )
	{
		super( new BorderLayout(5,5) );
		
		Component c;
		Panel p, p2;
		
		this.info = info;
		setBackground( controlsBackground );
		setForeground( controlsForeground );
		setFont( info.fontBiggerBold );
		
		formulaTitleImage = Utility.getImage( this, formulaTitleFile );
		x1Image = Utility.getImage( this, x1File );
		x01Image = Utility.getImage( this, x01File );
		x001Image = Utility.getImage( this, x001File );
		
		{
			try
			{
				MediaTracker mt = new MediaTracker(this);
				mt.addImage( formulaTitleImage, 0 );
				mt.addImage( x1Image, 3 );
				mt.addImage( x01Image, 1 );
				mt.addImage( x001Image, 4 );
				mt.waitForAll();
			}
			
			catch( Exception e )
			{
				formulaTitleImage = null;
				x1Image = x01Image = x001Image = null;
			}
		}
		
		mText = new TextField(5);
		mText.addKeyListener( new UserEntry(info) );
		
		p2 = new Panel( new BorderLayout() );
		
		p = new Panel( new BorderLayout() );
		p.add( new Label("Enter the slope (m): ",Label.RIGHT), BorderLayout.WEST );
		p.add( mText, BorderLayout.CENTER );
		Panel p1 = new Panel();
		p1.add( p );
		p2.add( p1, BorderLayout.NORTH );
		
		equation = new Equation();
		p2.add( equation, BorderLayout.SOUTH );
		add( p2, BorderLayout.NORTH );
		
		y0Text = new TextField("1.0");
		y0Text.setEditable(false);
		
		x0Text = new TextField("2.0");
		x0Text.setEditable(false);
		
		add( new ThatTable(), BorderLayout.CENTER );
		
		updateTextValue();
		info.addPropertyChangeListener( this );
	}
	
	public Insets getInsets()
	{
		return new Insets(0,5,5,5);
	}
	
	class Equation extends Component
					implements PropertyChangeListener
	{
		Font f;
		Font littlef;
		FontMetrics fm;
		FontMetrics littlefm;
		
		public Equation()
		{
			f = info.fontBiggerBold;
			fm = getFontMetrics(f);

			littlef = new Font(f.getName(),f.getStyle()|Font.ITALIC,f.getSize()-2);
			littlefm = getFontMetrics(littlef);
			
			setFont(f);
			setForeground( Color.black );
			setBackground( tableBackground );
			
			info.addPropertyChangeListener(this);
		}
		
		public void propertyChange( PropertyChangeEvent e )
		{
			repaint();
		}
		
		public Dimension getMinimumSize()
		{
			Dimension dim = super.getMinimumSize();
			dim.height = fm.getAscent()+littlefm.getHeight();
			return dim;
		}
		
		public Dimension getPreferredSize()
		{
			Dimension dim = super.getPreferredSize();
			dim.height = fm.getAscent()+littlefm.getHeight();
			return dim;
		}
		
		public void paint( Graphics g )
		{
			super.paint(g);
			
			Dimension dim = getSize();
			
			g.setColor( getBackground() );
			g.fillRect( 0, 0, dim.width, dim.height );
			g.setColor( getForeground() );
			
			String s1 = "y - ";
			String s2 = y0Text.getText();
			String s3 = " = "+(info.userSlopeVisible() ? Double.toString(info.getUserSlope()) : "m") + " (";
			String s4 = "x - ";
			String s5 = x0Text.getText();
			String s6 = ")";
			String s = s1+s2+s3+s4+s5+s6;

			int wid = fm.stringWidth(s);
			int left = (dim.width-wid)/2;
			
			g.setFont(f);
			//System.out.println("("+left+","+fm.getAscent()+") -> "+s);
			g.drawString( s, left, fm.getAscent()-2 );
			
			String sy1 = "y";
			String sy2 = "0";
			String sy = sy1+sy2;
			int t1 = fm.stringWidth(s1);
			int t2 = fm.stringWidth(s2);
			int t3 = littlefm.stringWidth(sy);
			t1 += (t2-t3)/2;
			g.setFont( littlef );
			g.drawString( sy1, left+t1, fm.getAscent()+littlefm.getAscent()-2 );
			t1 += littlefm.stringWidth(sy1);
			g.drawString( sy2, left+t1, fm.getAscent()+littlefm.getAscent()+4-2 );
			
			if( info.userSlopeVisible() )
			{
				String sm = "m";
				t1 = fm.stringWidth(s1+s2);
				t2 = fm.stringWidth(s3);
				t3 = littlefm.stringWidth(sm);
				t1 += (t2-t3)/2;
				g.setFont( littlef );
				g.drawString( sm, left+t1, fm.getAscent()+littlefm.getAscent()-2 );
			}
			
			String sx1 = "x";
			String sx2 = "0";
			String sx = sx1+sx2;
			t1 = fm.stringWidth(s1+s2+s3+s4);
			t2 = fm.stringWidth(s5);
			t3 = littlefm.stringWidth(sx);
			t1 += (t2-t3)/2;
			g.setFont( littlef );
			g.drawString( sx1, left+t1, fm.getAscent()+littlefm.getAscent()-2 );
			t1 += littlefm.stringWidth(sx1);
			g.drawString( sx2, left+t1, fm.getAscent()+littlefm.getAscent()+4-2 );
		}
	}
	
	class ThatTable extends Component
					implements PropertyChangeListener
	{
		String [] titles =
			{
				"x",
				"Your%guess",
				"Computer%guess",
				"f(x)=x^3/2",
				"Who's%closer?"
			};
		
		String [] col1captions =
			{
				"x'",
				"x'+0.1",
				"x'+0.01",
				"x'+0.001"
			};
		
		Dimension [] cellDims;
		
		Font font;
		FontMetrics fm;

		Font captionFont;
		FontMetrics captionfm;
		
		int titleHeight = 0;
		int titleWidth = 0;
		
		public ThatTable()
		{
			font = info.fontBold;
			fm = getFontMetrics(font);
			
			captionFont = info.fontPlainSmall;
			captionfm = getFontMetrics(captionFont);
			
			setBackground( tableBackground );
			setFont(font);
			
			// find column widths
			
			{
				cellDims = new Dimension[titles.length];
				for( int i=0; i<titles.length; ++i )
				{
					cellDims[i] = figgerDimension(titles[i]);
					titleHeight = Math.max(cellDims[i].height,titleHeight);
					titleWidth += cellDims[i].width;
				}
			}
			
			cellDims[0].width = Math.max(cellDims[0].width,fm.stringWidth("9.999"));
			
			info.addPropertyChangeListener(this);
		}
		
		public void propertyChange( PropertyChangeEvent e )
		{
			repaint();
		}

		public void paint( Graphics g )
		{
			super.paint(g);
			
			Dimension dim = getSize();
			titleHeight = dim.height/4;
			
			int slop = (dim.width-titleWidth)/(titles.length+1);
			
			g.setColor( getBackground() );
			g.fillRect( 0, 0, dim.width, dim.height );
			g.setColor( getForeground() );
			
			//System.out.println("width="+dim.width+" titlewidth="+titleWidth+" slop="+slop);
			
			// write the column headings
			{
				// color in the title bar
				g.setColor( titleBackground );
				g.fillRect( 0, 0, dim.width, titleHeight );
				
				int left = 0;
				for( int i=0; i<titles.length; ++i )
				{
					if( i == columnActual && formulaTitleImage != null )
					{
						int iw = formulaTitleImage.getWidth(this);
						int ih = formulaTitleImage.getHeight(this);
						int cw = cellDims[i].width+slop;
						g.drawImage( formulaTitleImage, left + (cw-iw)/2, (titleHeight-ih)/2, this );
					}
					
					else
					{
						drawTitle(g,titles[i],left,0,cellDims[i].width+slop,cellDims[i].height);
					}
					
					left += cellDims[i].width+slop;
				}
			}

			// write the data
			{
				boolean user = info.userSlopeVisible();
				boolean cpu = info.computerSlopeVisible();
				
				g.setColor( dataForeground );
				for( int i=1; i<4; ++i )
				{
					double xVal;
					double linPlay;
					double linCPU;
					double linActual;
					String close;
					double x0 = info.getP();
					double y0 = info.getPy();
					double dx;
					
					switch( i )
					{
						default: dx = 0.0; break;
						case 1: dx = 0.1; break;
						case 2: dx = 0.01; break;
						case 3: dx = 0.001; break;
					}
					
					linPlay = y0+info.getUserSlope()*dx;
					linCPU = y0+info.getComputerSlope()*dx;
					linActual = info.getCurrentFunction().value(x0+dx);
					
					double playDiff = Math.abs(linActual-linPlay);
					double cpuDiff = Math.abs(linActual-linCPU);
					
					if( playDiff < cpuDiff )
						close = "You";
					else if( cpuDiff < playDiff )
						close = "Computer";
					else
						close = "Neither";
					
					int left = 0;
					for( int j=0; j<titles.length; ++j )
					{
						String s;
						
						switch( j )
						{
							case 0: s = frmt(x0+dx); break;
							case 1: s = user ? frmt(linPlay) : ""; break;
							case 2: s = cpu ? frmt(linCPU) : ""; break;
							case 3: s = (user && cpu) ? frmt(linActual) : ""; break;
							case 4: s = (user && cpu) ? close : ""; break;
							default: s = "?"; break;
						}
						
						int cwidth = cellDims[j].width+slop;
						int sw = fm.stringWidth(s);
						
						int x = left+2;
						int w = cwidth-4;
						int y = i*titleHeight+2;
						int h = ((j==0) ? fm.getHeight() : titleHeight-8);
						int blah = 0;
						
						g.setColor( dataBackground );
						g.fillRect( x, y, w, h );
						g.setColor( dataForeground );
						g.drawRect( x, y, w, h );
						
						g.setFont( font );
						g.setColor( dataForeground );
						
						blah = (j == 0) ? 2 : h/4;
							
						g.drawString(s,left+(cwidth-sw)/2,i*titleHeight+fm.getAscent()+blah);
						
						if( j == 0 )
						{
							Image image;
							
							switch( i )
							{
								default: image = x1Image; break;
								case 2: image = x01Image; break;
								case 3: image = x001Image; break;
							}
							
							int iw = image.getWidth(this);
							int ih = image.getHeight(this);

							g.drawImage( image, left+(cwidth-iw)/2, i*titleHeight+fm.getHeight()+5, this );
						}
						
						left += cwidth;
					}
				}
			}
		}
		
		String frmt( double d )
		{
			return Double.toString( Math.rint(d*1000.0)/1000.0 );
		}
		
		void drawTitle( Graphics g, String s, int x, int y, int w, int h )
		{
			g.setColor( titleForeground );
			g.setFont( font );
			
			int width = 0;
			int height = 0;
			int div;
			
			if( (div=s.indexOf('%')) >= 0 )
			{
				height = 2*fm.getHeight();
				String s1 = s.substring(0,div);
				String s2 = s.substring(div+1);
				int w1 = fm.stringWidth(s1);
				int w2 = fm.stringWidth(s2);

				g.drawString(s1,x+(w-w1)/2,y+fm.getAscent());
				g.drawString(s2,x+(w-w2)/2,y+fm.getHeight()+fm.getAscent());
			}
			
			else
			{
				height = fm.getHeight();
				width = fm.stringWidth(s);
				g.drawString(s,x+(w-width)/2,y+(titleHeight+fm.getAscent())/2);
			}
		}

		public Dimension getPreferredSize()
		{
			return new Dimension(titleWidth,titleHeight*4);
		}
		
		public Dimension getMinimumSize()
		{
			return new Dimension(titleWidth,titleHeight*4);
		}
		
		Dimension figgerDimension( String s )
		{
			int width = 0;
			int height = 0;
			int div;
			
			if( (div=s.indexOf('%')) >= 0 )
			{
				height = 2*fm.getHeight();
				String s1 = s.substring(0,div);
				String s2 = s.substring(div+1);
				width = Math.max(fm.stringWidth(s1),fm.stringWidth(s2));
			}
			
			else
			{
				height = fm.getHeight();
				width = fm.stringWidth(s);
			}
			
			return new Dimension(width,height);
		}
	}
	
	public void propertyChange( PropertyChangeEvent e )
	{
		if( GraphInfo.select.equals(e.getPropertyName()) )
		{
			updateTextValue();
		}
	}

	void updateTextValue()
	{
		double P = info.getP();
		x0Text.setText( Double.toString(P) );
		double Py = info.getCurrentFunction().value(P);
		Py = Math.rint(Py*1000.0)/1000.0;
		y0Text.setText( Double.toString(Py) );
	}

	class UserEntry extends KeyAdapter
	{
		GraphInfo info;
		
		public UserEntry( GraphInfo info )
		{
			this.info = info;
		}
		
		public void keyPressed(KeyEvent e)
		{
			TextComponent tc = (TextComponent)e.getSource();
			int code = e.getKeyCode();
			
			//System.out.println("Key typed is "+e.getKeyText(code));
		
			if( code == KeyEvent.VK_ENTER )
			{
				String s = tc.getText();
				//System.out.println("Slope text is "+s);
				
				tc.selectAll();
				
				try
				{
					Double dbl = Double.valueOf(s);
					double slope = dbl.doubleValue();
					//System.out.println("Slope value is "+slope);
					info.setUserSlope(slope);
				}
				
				catch( Exception ex )
				{
					System.err.println("Couldn't parse "+s+" - "+ex);
				}
			}
		}
	}
		
	public Dimension getPreferredSize()
	{
		Dimension dim = super.getPreferredSize();
		
		dim.height = Math.max(dim.height,72);
		return dim;
	}
}
