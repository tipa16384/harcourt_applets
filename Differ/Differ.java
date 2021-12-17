import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.net.URL;
import java.applet.*;
import java.beans.*;
import util.RapidLabel;
import util.DoubleBufferPanel;
import Wired.*;

public class Differ extends Panel implements ActionListener, PropertyChangeListener
{
	public static final String RESET = "reset";
	public static final String POPUP = "popup";
	public static final String INSTRUCTIONS = "instr";

	public static final int WAVELENGTH = 0x0001;	// wavelength of light
	public static final int SLITSPACING = 0x0002;	// space between slits
	public static final int NUMSLITS = 0x0004;		// number of slits
	public static final int DISTANCE = 0x0008;		// distance of a screen
	public static final int SLITWIDTH = 0x0010;		// width of a slit
	public static final int LINESPCM = 0x0020;		// lines per cm
	public static final int SLIDERS = 0x00FF;
		
	public static final int FAKEWAVES = 0x0100;		// fake number of waves from slits
	public static final int FAKESLITS = 0x0200;		// fake number of slits
	public static final int FAKESPACE = 0x0400;		// fake slit spacing
	public static final int FAKENOTES = 0x0800;		// fake special conditions
	
	static final int version1Rows = 3;
	
	Configuration [] configurations =
		{
			new Configuration( "Double-slit Interference", WAVELENGTH+DISTANCE+SLITSPACING,
					0, 0, 2, 0, GraphInfo.MIN_WIDTH ),
			new Configuration( "Single-slit Diffraction", WAVELENGTH+DISTANCE+SLITWIDTH,
					0, 0, 1, 0, 0 ),
			new Configuration( "Diffraction Grating",
					DISTANCE+LINESPCM+WAVELENGTH+FAKEWAVES+FAKESLITS,
					0, 0, GraphInfo.MAX_SLITS,
						  0,
						  GraphInfo.MIN_WIDTH ),
			new Configuration( "Single-slit Derivation", WAVELENGTH+NUMSLITS+FAKENOTES+FAKEWAVES+FAKESPACE,
					0, 0, 2, GraphInfo.MIN_DISTANCE, GraphInfo.MIN_WIDTH ),
			new Configuration( "Interference and Diffraction",
					DISTANCE+SLITSPACING+WAVELENGTH+NUMSLITS+SLITWIDTH+FAKEWAVES,
					0, 0, 5,
						  0,
						  0 )
		};
	
	public static final int I_CLICK = 0;
	public static final int I_NOTE = 1;
	public static final int I_NM = 2;
	public static final int I_A = 3;
	public static final int I_CM = 4;
	public static final int I_D = 5;
	public static final int I_L = 6;
	public static final int I_LAMBDA = 7;
	public static final int I_LNCM = 8;
	public static final int I_MICRO = 9;
	public static final int I_N = 10;
	public static final int I_1OVERD = 11;
	
	public static final int I_ARRAY_LEN = I_1OVERD+1;
	
	static String [] imageFiles =
		{
			"click.gif",
			"note.gif",
			"nanometer.gif",
			"a.gif",
			"centimeter.gif",
			"d.gif",
			"L.gif",
			"lambda.gif",
			"lines-cm.gif",
			"micrometer.gif",
			"N.gif",
			"1overd.gif"
		};
	
	static Image [] images = new Image[I_ARRAY_LEN];
	
	GraphInfo info = null;
	ActionListener listeners = null;
	Panel sliderPanel = null;
	Choice configPicker = null;
	Panel popupPanel;
	Component instructionComponent;
	Component popupComponent;
	Component noteIcon;
	Graph graph;
				
	Main applet;
	
	// initializer -- start off with a BorderLayout.
	
	public Differ( Main applet, GraphInfo info )
	{
		super( new BorderLayout() );

		setFont( new Font("SansSerif",Font.PLAIN,12) );
		
		Utility.setApplet(applet);
		this.applet = applet;
		
		loadImages();
		
		DoubleFormat.threshhold = 3;
		
		//setBackground( Color.white );
		//setCursor( Cursor.getDefaultCursor() );

		this.info = info;

		info.addPropertyChangeListener(this);

		Header head = new Header("Interference and Diffraction");
		head.addActionListener(this);
		add( head, BorderLayout.NORTH );
		
		Component overhead = new Overhead(info);
		Component projection = new Projection(info);
		graph = new Graph(info);
		
		{
			LayoutManager lm = new GridLayout(version1Rows,2);
									
			sliderPanel = new FixedPanel( lm, 250, 0 );
			sliderPanel.setBackground( GraphInfo.CONTROL_COLOR );
		}
		
		ColumnLayout cl = new ColumnLayout(5,5);
		cl.setTallen(true);
		
		Panel content = new PaddedPanel( cl, 10 );
		Panel p1, p2, p3;
		
		p1 = new Panel(new BorderLayout());
		p2 = new Panel(null);
		
		configPicker = new Choice();

		for( int i=0; i<configurations.length; ++i )
		{
			//System.out.println("info["+i+"] is "+info.selection[i]);
			
			if( info.selection[i] )
				configPicker.add( configurations[i].getName() );
		}

		configPicker.addItemListener( new ConfigurationChanger() );
		
		try
		{
			configPicker.select(info.model);
		}
		
		catch( IllegalArgumentException iae )
		{
			configPicker.select(0);
		}
		
		if( info.getVersion() == 1 )
			p1.add( new RawLabel("Choose a Configuration:"), BorderLayout.NORTH );
		p1.add(configPicker, BorderLayout.WEST);
		p2.add(p1);
		p3 = new Panel(new BorderLayout());
		p3.add(new RawLabel("Screen"),BorderLayout.SOUTH);
		p2.add(p3);
		content.add( p2 );
		
		p1 = new DoubleBufferPanel(null);
		p1.setName("overhead");
		p2 = new DoubleBufferPanel( new GridLayout(0,1,0,5) );
		p2.setName("graph");
		p2.add( projection );
		p2.add( graph );
		p1.add( overhead );
		p1.add( p2 );
		content.add( p1 );
		
		p1 = new Panel(null);
		p2 = new Panel( new BorderLayout() );
		p2.add( sliderPanel, BorderLayout.WEST );
		p1.add( p2 );
		
		p3 = new PaddedPanel( new BorderLayout(), 10, 0, 0, 0 );
		popupPanel = new FixedPanel( new BorderLayout(), 150, 100, true, false );
		
//		Component c = new GenericIcon(Utility.getImage(this,"click.gif"),null,"click here!");
		Component c = new RawLabel(Differ.imageString(I_CLICK));
		
		System.out.println("click.gif component is "+c);
		
		Dimension cdim = c.getMinimumSize();
		
		System.out.println("minimum size of that thing is "+cdim);
		
		instructionComponent = new FixedPanel(new BorderLayout(),cdim.width,cdim.height,true,true);
		((Panel)instructionComponent).add( c, BorderLayout.NORTH );

		c = new GenericIcon(Utility.getImage(this,"note.gif"),null,"read this!");
		cdim = c.getMinimumSize();
		noteIcon = new FixedPanel(new BorderLayout(),cdim.width,cdim.height,true,true);
		((Panel)noteIcon).add( c, BorderLayout.SOUTH );

		popupComponent = new Popup();
		popupPanel.add( instructionComponent, BorderLayout.NORTH );
		//popupPanel.add( noteIcon, BorderLayout.SOUTH );
		p3.add( popupPanel, BorderLayout.CENTER );
		p3.add( noteIcon, BorderLayout.SOUTH );
		noteIcon.setVisible(false);
		p1.add( p3 );
		
		content.add( p1 );
		add( content, BorderLayout.CENTER );
		resetApplet();
	}
	
	static public Image getImage( int n )
	{
		return images[n];
	}
	
	void loadImages()
	{
		MediaTracker mt = new MediaTracker(this);
		final int id = 0;
		int len = imageFiles.length;
		int i;
		
		for( i=0; i<len; ++i )
		{
			Image image = Utility.getImage(this,imageFiles[i]);
			images[i] = image;
			if( image != null )
			{
				applet.showStatus("loading "+imageFiles[i]);
				mt.addImage( image, id );
			}
		}
		
		applet.showStatus("waiting for images...");
		
		try
		{
			mt.waitForID(id);
			applet.showStatus("image loading succeeded.");
		}
		
		catch( Exception exc )
		{
			applet.showStatus("image loading failed :- "+exc);
		}
	}
	
	static public String imageString( int which )
	{
		char [] seed = new char[3];
		seed[0] = '~';
		seed[1] = '@';
		seed[2] = (char) which;
		return new String(seed);
	}
	
	public void propertyChange( PropertyChangeEvent evt )
	{
		String prop = evt.getPropertyName();
		boolean updatePopup = false;
		
		if( prop.equals("slider") )
		{
			int newval = ((Integer)evt.getNewValue()).intValue();
			int oldval = ((Integer)evt.getOldValue()).intValue();
			
			if( newval >= 0 )
			{
				if( oldval < 0 )
				{
					// show popup
					//System.out.println("show popup");
					popupPanel.removeAll();
					popupPanel.add( popupComponent, BorderLayout.CENTER );
					popupPanel.validate();
					popupPanel.repaint();
				}
				
				else
				{
					// update popup
//					System.out.println("update popup");
					updatePopup = true;
				}
			}
			
			else if( oldval >= 0 )
			{
				// hide popup
				//System.out.println("hide popup");
				popupPanel.removeAll();
				popupPanel.add( instructionComponent, BorderLayout.NORTH );
				popupPanel.validate();
				popupPanel.repaint();
			}
		}
		
		else if( info.getSliderPosition() >= 0 )
		{
			updatePopup = true;
		}

		if( updatePopup )
		{
			//System.out.println("update popup");
			popupComponent.repaint(info.RESPONSIVENESS);
		}
	}
	
	class FixedPanel extends Panel
	{
		int width;
		int height;
		boolean hHold, vHold;
		
		public FixedPanel( LayoutManager lm, int width )
		{
			this(lm,width,width);
		}
		
		public FixedPanel( LayoutManager lm, int width, int height )
		{
			this(lm,width,height,false,false);
		}
		
		public FixedPanel( LayoutManager lm, int width, int height, boolean hHold, boolean vHold )
		{
			super(lm);
			this.width = width;
			this.height = height;
			this.hHold = hHold;
			this.vHold = vHold;
		}
		
		public Dimension getPreferredSize()
		{
			Dimension dim = super.getPreferredSize();
			
			if( hHold )
				dim.width = width;
			else
				dim.width = Math.max(width,dim.width);
			
			if( vHold )
				dim.height = height;
			else
				dim.height = Math.max(height,dim.height);
				
			return dim;
		}
		
		public Dimension getMinimumSize()
		{
			return new Dimension(width,height);
		}
		
		public void setBounds( int x, int y, int w, int h )
		{
			if( hHold )
			{
				x += (w-width)/2;
				w = width;
			}
			
			if( vHold )
			{
				y += (h-height)/2;
				h = height;
			}
			
			super.setBounds( x, y, w, h );
		}
		
	}
	
	public void actionPerformed( ActionEvent e )
	{
		String cmd = e.getActionCommand();
		
		debug("Differ.actionPerformed got "+cmd);
		
		if( cmd.equals(RESET) )
			resetApplet();
		
		broadcast( e );
	}
	
	// reset the applet
	
	public void resetApplet()
	{
		debug("Resetting the applet");
		
		if( configPicker != null )
		{
			String s = configPicker.getSelectedItem();
			int len = configurations.length;
			
			for( int i=0; i<len; ++i )
			{
				if( configurations[i].getName().equals(s) )
				{
					configurations[i].set();
					break;
				}
			}
		}

		info.clearSlider();
		
		broadcast( new ActionEvent(this,0,RESET) );
		
		debug("Color report - "+info.colorReport());
	}

	// handle the action listener for detecting state changes.
	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}
	
	public void broadcast( ActionEvent e )
	{
		if( listeners != null )
		{
			listeners.actionPerformed(e);
		}
	}

	class ConfigurationChanger implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			try
			{
				Choice ch = (Choice) e.getItemSelectable();
				String s = ch.getSelectedItem();
				
				int i;
				int len = configurations.length;
				
				for( i=0; i<len; ++i )
				{
					Configuration conf = configurations[i];
					if( s.equals(conf.getName()) )
					{
						conf.set();
						break;
					}
				}
			}
			
			catch( Exception ex )
			{
				System.err.println("Paranoia pays off with "+e.getItemSelectable()+" -- "+ex);
			}
		}
	}

	class Popup extends PaddedPanel
	{
		RawLabel line1, line2;
		
		public Popup()
		{
			super( null, 5 );
			enableEvents(AWTEvent.MOUSE_EVENT_MASK);
			line1 = new RawLabel();
			line2 = new RawLabel();
			Font f = new Font("SansSerif",Font.PLAIN,12);
			line1.setFont(f);
			line2.setFont(f);
		}
		
		public void processMouseEvent( MouseEvent e )
		{
			if( e.getID() == MouseEvent.MOUSE_RELEASED )
				info.clearSlider();
				
			super.processMouseEvent(e);
		}
		
		public void paint( Graphics g )
		{
			Insets insets = getInsets();
			Dimension lsize = line1.getPreferredSize();
			int lh = lsize.height + 2;
			
			g.setColor( getForeground() );
			
			Dimension size = getSize();
			int w = size.width - insets.left - insets.right;
			int h = size.height - insets.top - insets.bottom;
			int x = insets.left;
			int y = insets.top;
			
			int lwidth;
			int vwidth;
			
			final String label1 = "~!y~! = ";
			final String label2 = "~!I/I~v0~v~! = ";
			String label1a = DoubleFormat.format(graph.getSliderDistance()*1E2)+" cm";
			String label2a = DoubleFormat.format(graph.getIntensity());
			final String label1b = "-9.99 cm";
			final String label2b = "9.99x10-9";
			
			line1.setText(label1);
			line2.setText(label2);
			
			int l1w = line1.getMinimumSize().width;
			int l2w = line2.getMinimumSize().width;
			lwidth = Math.max( l1w, l2w );
						
			line1.setText(label1+label1b);
			line2.setText(label2+label2b);
			
			vwidth = Math.max( line1.getMinimumSize().width+lwidth-l1w,
							   line2.getMinimumSize().width+lwidth-l2w );

			int xoffs = (w-vwidth)/2;
			
			line1.setText(label1+label1a);
			line2.setText(label2+label2a);
			
			line1.paint( g, x+xoffs+lwidth-l1w, y, false );
			y += lh;
			line2.paint( g, x+xoffs+lwidth-l2w, y, false );
			y += lh+insets.bottom;

			g.drawRect( xoffs, 0, vwidth+insets.left+insets.right-1, y-1 );
			
		}
	}
	
	class Configuration
	{
		String label;
		int options;
		int defaultWavelength;
		int defaultSpacing;
		int defaultN;
		int defaultDistance;
		int defaultWidth;
		boolean isSmall;
		
		public Configuration( String label, int options,
				int defaultWavelength,
				int defaultSpacing,
				int defaultN,
				int defaultDistance,
				int defaultWidth )
		{
			this.label = label;
			this.options = options;
			this.defaultWavelength = defaultWavelength;
			this.defaultSpacing = defaultSpacing;
			this.defaultN = defaultN;
			this.defaultDistance = defaultDistance;
			this.defaultWidth = defaultWidth;
		}
		
		public int getOptions()
		{
			return options;
		}
		
		public String getName()
		{
			return label;
		}
		
		int countBits( int bitter )
		{
			int count = 0;
			
			while( bitter != 0 )
			{
				count += (bitter&1);
				bitter >>= 1;
			}
			
			return count;
		}
		
		public void set()
		{
			info.clearSlider();
			info.setWavelength(defaultWavelength);
			info.setSlitSpacing(defaultSpacing);
			info.setNumberOfSlits(defaultN);
			info.setDistanceToScreen(defaultDistance);
			info.setSlitWidth(defaultWidth);
			info.setOptions( options );
			
			sliderPanel.removeAll();
			
			boolean tall = info.getVersion() != 1;
			int rows = version1Rows;
			boolean wide = (countBits(options&SLIDERS) <= rows);
			int cols = wide ? 2 : 1;
			
			sliderPanel.setLayout( new GridLayout(rows,cols) );
			
			if( (options & WAVELENGTH) != 0 )
				sliderPanel.add( new Adjuster(info,"wavelength",wide) );
			
			if( (options & NUMSLITS) != 0 )
				sliderPanel.add( new Adjuster(info,"numberOfSlits",wide) );
			
			if( (options & SLITSPACING) != 0 )
				sliderPanel.add( new Adjuster(info,"slitSpacing",wide) );
			
			if( (options & SLITWIDTH) != 0 )
				sliderPanel.add( new Adjuster(info,"slitWidth",wide) );
			
			if( (options & LINESPCM) != 0 )
				sliderPanel.add( new Adjuster(info,"linesPerCM",wide) );

			if( (options & DISTANCE) != 0 )
				sliderPanel.add( new Adjuster(info,"distanceToScreen",wide) );
			
			noteIcon.setVisible((options & FAKENOTES) != 0);
			noteIcon.getParent().validate();
			
			sliderPanel.validate();
		}
	}
	
	static void debug( String s )
	{
		if( Utility.debug )
			System.out.println("Differ:: "+s);
	}
}
