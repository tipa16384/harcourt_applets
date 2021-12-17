import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.Vector;
import java.beans.*;

public final class GraphInfo 
{
	final public static int MIN_SLITS = 1;
	final public static int MAX_SLITS = 25;
	final public static int MIN_DISTANCE = 100;
	final public static int MAX_DISTANCE = 200;
	final public static int MIN_SPACING = 10;
	final public static int MAX_SPACING = 100;
	final public static int MIN_WIDTH = 5;
	final public static int MAX_WIDTH = 100;

	final public static int MIN_WAVELENGTH = 400;
	final public static int MAX_WAVELENGTH = 675;
	
	private int numberOfSlits = (MAX_SLITS+MIN_SLITS)/2;
	private int distanceToScreen = (MIN_DISTANCE+MAX_DISTANCE)/2;
	private int slitSpacing = (MIN_SPACING+MAX_SPACING)/2;
	private int slitWidth = (MIN_WIDTH+MAX_WIDTH)/2;
	private int wavelength = (MIN_WAVELENGTH+MAX_WAVELENGTH)/2;
	private int sliderPosition = -1;
	private int options = 0;
	
	// "true" if the current configuration is a diffraction grating
	boolean grating = false;

	static Color [] colors=null;
	
	static final Color [] spectrum =
		{
			//new Color(255,0,255),
			new Color(128,0,255),
			new Color(0,128,255),
			new Color(0,255,0),
			new Color(255,255,0),
			new Color(255,128,0),
			new Color(255,0,0),
			new Color(255,0,0)
		};
		
	PropertyChangeSupport pcs;
	
	// color for the force vectors
	static public final Color FORCE_VECTOR_COLOR = new Color(0,51,204);
	
	// color for the negative charge
	static public final Color NEGATIVE_COLOR = new Color(51,102,255);
	
	// color for the positive charge
	static public final Color POSITIVE_COLOR = new Color(255,51,51);
	
	// color for the field lines
	static public final Color FIELD_COLOR = new Color(204,0,0);
	
	// color for the control backdrop
	static public final Color CONTROL_COLOR = new Color(255,255,206);
	
	// color of the "halo" around the selected charge
	static public final Color HIGHLIGHT_COLOR = new Color(255,204,51);
	
	// color of the grid lines (the 'rules') in the graph
	static public final Color GRID_COLOR = new Color(204,255,255);
	
	// color of the axes in the graph
	static public final Color AXIS_COLOR = new Color(204,204,204);
	
	// color of the horizontal line below the graph
	static public final Color SEPARATE_COLOR = new Color(153,153,153);
	
	// date/version information for the applet
	static public final String dateInfo = "08.11.1999";

	// quickness to update (in ms)
	static public final int RESPONSIVENESS = 250;
		
	static public final String [][] paramList = {
		{ "model",	"int",		"configuration selection (0-2)" },
		{ "double", "boolean",	"select double-slit model" },
		{ "single",	"boolean",	"select single-slit model" },
		{ "grating","boolean",	"select diffraction grating" },
		{ "derive",	"boolean",	"select single-slit derivation" },
		{ "mondo",	"boolean",	"select D&I model" },
		{ "version","int",		"version (1-3) (overrides selections)" }
		};
	
	public int model = 0;
	public int version = 0;
	public boolean [] selection = { true, true, true, true, true };
	
	// Coulomb's constant
	public static final double Ke = 8.9875E9;
	
	// charge on an electron, in Coulombs.
	public static final double C = -1.60217733E-19;
	
	// space permittivity constant (epsilon-0)
	public static final double e0 = 8.854187817E-12;
	
	Main applet = null;
	
	public GraphInfo()
	{
		pcs = new PropertyChangeSupport(this);
	}

	public String colorReport()
	{
		String s;
		int n = 0;

		if( colors == null )
			s = "color table doesn't exist";
		else
		{
			for( int i=0; i<colors.length; ++i )
				if( colors[i] != null )
					++n;
			s = "color table has "+n+"/"+colors.length+" entries";
		}
		
		return s;
	}

	public Color findColor( double I )
	{
		return findColor(findColorRGB(I));
	}
	
	public int findBestColor( int r, int g, int b )
	{
		r = Math.max(0,Math.min(255,r));
		g = Math.max(0,Math.min(255,g));
		b = Math.max(0,Math.min(255,b));
		return findBestColor((r<<16)+(g<<8)+b);
	}
	
	public int findBestColor( int rgb )
	{
		int ri = (((rgb>>16)&0xFF)*6)/256;
		int gi = (((rgb>>8)&0xFF)*6)/256;
		int bi = (((rgb)&0xFF)*6)/256;
		return ((ri*0x33)<<16)+
			   ((gi*0x33)<<8)+
			   (bi*0x33);
	}
	
	public Color findColor( int rgb )
	{
		int ri = (((rgb>>16)&0xFF)*6)/256;
		int gi = (((rgb>>8)&0xFF)*6)/256;
		int bi = (((rgb)&0xFF)*6)/256;
		int index = ri*36+gi*6+bi;
		
		if( colors == null )
			colors = new Color[216];
		
		if( colors[index] == null )
			colors[index] = new Color(ri*0x33,gi*0x33,bi*0x33);
		
		return colors[index];
	}
	
	public int findColorRGB( double I )
	{
		int color = (int)(I * 255.0);

		int r, g, b;
		Color col;
		long min = (long)getMinWavelength();
		long ang = (long)getWavelength()-min;
		long rng = (long)(getMaxWavelength()-min)/5;
		int colidx = (int)(ang/rng);
		long colfrc = ang%rng;
		
		// the dark colors are indistuingishable from black; the
		// light colors are all clumped together. Sounds like a
		// problem with gamma correction to me!
		// let's try this gamma correction equation I found		
		
		I = Math.pow(I,0.43);
		
		long pct = (long)(I*100.0);
		
		Color from = spectrum[colidx];
		Color to = spectrum[colidx+1];
		
		r = interpolate(pct,colfrc,rng,(long)from.getRed(),(long)to.getRed());
		g = interpolate(pct,colfrc,rng,(long)from.getGreen(),(long)to.getGreen());
		b = interpolate(pct,colfrc,rng,(long)from.getBlue(),(long)to.getBlue());
		
		return ((r&0xFF)<<16) +
			   ((g&0xFF)<<8) +
			   ((b&0xFF));
	}

	int interpolate( long pct, long val, long max, long from, long to )
	{
		long y = from + (val*(to-from))/max;
		return (int)((y*pct)/(100));
	}

	public void setApplet( Main applet )
	{
		this.applet = applet;
	}
	
	public Main getApplet()
	{
		return applet;
	}

	public void setOptions( int options )
	{
		this.options = options;
	}
	
	public int getOptions()
	{
		return options;
	}

	public int getNumberOfSlits()
	{
		return numberOfSlits;
	}
	
	public int getMinNumberOfSlits()
	{
		return MIN_SLITS;
	}
	
	public int getMaxNumberOfSlits()
	{
		return MAX_SLITS;
	}
	
	public void setNumberOfSlits( int slits )
	{
		if( slits == 0 )
			slits = 2;
			
		int slots = Math.min(MAX_SLITS,Math.max(MIN_SLITS,slits));
		
		if( slots != numberOfSlits )
		{
			int old = numberOfSlits;
			numberOfSlits = slots;
			pcs.firePropertyChange(
				"numberOfSlits",
				new Integer(old),
				new Integer(slots));
		}
	}
	
	public String getNumberOfSlitsText()
	{
		return "Number of Slits";
	}
	
	public String getNumberOfSlitsUnits()
	{
		return null;
	}
	
	public String getNumberOfSlitsVar()
	{
		return Differ.imageString(Differ.I_N);
	}
	
	public int getDistanceToScreen()
	{
		return distanceToScreen;
	}
	
	public int getMinDistanceToScreen()
	{
		return MIN_DISTANCE;
	}
	
	public int getMaxDistanceToScreen()
	{
		return MAX_DISTANCE;
	}
	
	public void setDistanceToScreen( int slits )
	{
		if( slits == 0 )
			slits = (MAX_DISTANCE+MIN_DISTANCE)/2;
			
		int slots = Math.min(MAX_DISTANCE,Math.max(MIN_DISTANCE,slits));
		
		if( slots != distanceToScreen )
		{
			int old = distanceToScreen;
			distanceToScreen = slots;
			pcs.firePropertyChange(
				"distanceToScreen",
				new Integer(old),
				new Integer(slots));
		}
	}

	public String getDistanceToScreenText()
	{
		return "Distance to the Screen";
	}
	
	public String getDistanceToScreenUnits()
	{
		return Differ.imageString(Differ.I_CM);
	}
	
	public String getDistanceToScreenVar()
	{
		return Differ.imageString(Differ.I_L);
	}
	
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
    {
    	pcs.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
    {
    	pcs.removePropertyChangeListener(listener);
    }

	public int getSlitSpacing()
	{
		if( (getOptions() & Differ.FAKESPACE) != 0 )
			return getMaxSlitSpacing() / getNumberOfSlits();
		else
			return slitSpacing;
	}
	
	public int getMinSlitSpacing()
	{
		return MIN_SPACING;
	}
	
	public int getMaxSlitSpacing()
	{
		return MAX_SPACING;
	}
	
	public void setSlitSpacing( int slits )
	{
		if( slits == 0 )
			slits = (MAX_SPACING+MIN_SPACING)/2;
			
		int slots = Math.min(MAX_SPACING,Math.max(MIN_SPACING,slits));
		
		if( slots != slitSpacing )
		{
			int old = slitSpacing;
			slitSpacing = slots;
			pcs.firePropertyChange(
				"slitSpacing",
				new Integer(old),
				new Integer(slots));
		}
	}

	public String getSlitSpacingText()
	{
		return "Slit Spacing";
	}

	public String getSlitSpacingUnits()
	{
		return Differ.imageString(Differ.I_MICRO);
	}
	
	public String getSlitSpacingVar()
	{
		return Differ.imageString(Differ.I_D);
	}
	
	public int getSlitWidth()
	{
		return slitWidth;
	}
	
	public int getMinSlitWidth()
	{
		return MIN_WIDTH;
	}
	
	public int getMaxSlitWidth()
	{
		return MAX_WIDTH;
	}
	
	public void setSlitWidth( int slits )
	{
		if( slits == 0 )
			slits = (MAX_WIDTH+MIN_WIDTH)/2;
			
		int slots = Math.min(MAX_WIDTH,Math.max(MIN_WIDTH,slits));
		
		if( slots != slitWidth )
		{
			int old = slitWidth;
			slitWidth = slots;
			pcs.firePropertyChange(
				"slitWidth",
				new Integer(old),
				new Integer(slots));
		}
	}

	public String getSlitWidthText()
	{
		return "Slit Width";
	}

	public String getSlitWidthUnits()
	{
		return Differ.imageString(Differ.I_MICRO);
	}
	
	public String getSlitWidthVar()
	{
		return Differ.imageString(Differ.I_A);
	}
	
	public int getLinesPerCM()
	{
		return umToLPCM(/*getSlitWidth()+*/getSlitSpacing());
	}
	
	private int umToLPCM( int um )
	{
		return 10000/um;
	}
	
	public int getMinLinesPerCM()
	{
		return umToLPCM(MIN_WIDTH+MAX_SPACING);
	}
	
	public int getMaxLinesPerCM()
	{
		return umToLPCM(MIN_WIDTH+MIN_SPACING);
	}
	
	public void setLinesPerCM( int slits )
	{
		if( slits == 0 )
			slits = (getMinLinesPerCM()+getMaxLinesPerCM())/2;
			
		int slots = Math.min(getMaxLinesPerCM(),Math.max(getMinLinesPerCM(),slits));
		
		setSlitSpacing(umToLPCM(slots)/*-MIN_WIDTH*/);
	}

	public String getLinesPerCMText()
	{
		return "Line Density";
	}

	public String getLinesPerCMUnits()
	{
		return Differ.imageString(Differ.I_LNCM);
	}
	
	public String getLinesPerCMVar()
	{
		return Differ.imageString(Differ.I_1OVERD);
	}
	
	public int getWavelength()
	{
		return wavelength;
	}
	
	public int getMinWavelength()
	{
		return MIN_WAVELENGTH;
	}
	
	public int getMaxWavelength()
	{
		return MAX_WAVELENGTH;
	}
	
	public void setWavelength( int slits )
	{
		if( slits == 0 )
			slits = (MAX_WAVELENGTH+MIN_WAVELENGTH)/2;
			
		int slots = Math.min(MAX_WAVELENGTH,Math.max(MIN_WAVELENGTH,slits));
		
		if( slots != wavelength )
		{
			int old = wavelength;
			wavelength = slots;
			pcs.firePropertyChange(
				"wavelength",
				new Integer(old),
				new Integer(slots));
		}
	}

	public String getWavelengthText()
	{
		return "Wavelength";
	}

	public String getWavelengthUnits()
	{
		return Differ.imageString(Differ.I_NM);
	}
	
	public String getWavelengthVar()
	{
		return Differ.imageString(Differ.I_LAMBDA);
	}
	
	public void setSliderPosition( int pos )
	{
		if( pos != sliderPosition )
		{
			int old = sliderPosition;
			sliderPosition = pos;
			pcs.firePropertyChange(
				"slider",
				new Integer(old),
				new Integer(sliderPosition));
		}
	}
	
	public void clearSlider()
	{
		setSliderPosition(-1);
	}
	
	public int getSliderPosition()
	{
		return sliderPosition;
	}
	
	public int getVersion()
	{
		return version;
	}

	public void setParameters( Applet apl )
	{
		System.out.println("setting parameters for the applet");

		model = parseInt(apl,paramList[0][0],model);
		version = parseInt(apl,paramList[6][0],version);

		switch( version )
		{
			default:			// do nothing (use defaults)
				selection[0] = parseBoolean(apl,paramList[1][0],selection[0]);
				selection[1] = parseBoolean(apl,paramList[2][0],selection[1]);
				selection[2] = parseBoolean(apl,paramList[3][0],selection[2]);
				selection[3] = parseBoolean(apl,paramList[4][0],selection[3]);
				selection[4] = parseBoolean(apl,paramList[5][0],selection[4]);
				break;
				
			case 1:				// version 1
				selection[0] = selection[1] = selection[2] = true;
				selection[3] = selection[4] = false;
				break;
				
			case 2:				// version 2
				selection[0] = selection[1] = selection[2] = false;
				selection[3] = selection[4] = true;
				break;
				
			case 3:				// version 3 (everything)
				selection[0] = selection[1] = selection[2] = true;
				selection[3] = selection[4] = true;
				break;
		}
	}
	
	private String param( Applet apl, String name )
	{
		String s = apl.getParameter(name);
		if( s != null && s.length() == 0 )
			s = null;
		
		System.out.println("Value of "+name+" is "+s);
		
		return s;
	}
	
	private boolean parseBoolean( Applet apl, String name, boolean old )
	{
		String val;
		
		System.out.println("Looking for parameter "+name);
		
		val = param( apl, name );
		
		if( val == null )
			return old;
		else if( val.equalsIgnoreCase("on") )
			return true;
		else if( val.equalsIgnoreCase("off") )
			return false;
		else
			return old;
	}
	
	private String parseString( Applet apl, String name, String old )
	{
		String val = param( apl, name );
		
		return (val == null) ? old : val;
	}

	private int parseInt( Applet apl, String name, int old )
	{
		String val;
		int res = old;
		
		val = param( apl, name );
		
		if( val != null )
		{
			try
			{
				res = Integer.parseInt(val);
			}
			
			catch( Exception e )
			{
			}
		}

		return res;
	}	
	
	private double parseDouble( Applet apl, String name, double old )
	{
		String val;
		double res = old;
		
		val = param( apl, name );
		
		if( val != null )
		{
			try
			{
				Double d = new Double(val);
				res = d.doubleValue();
			}
			
			catch( Exception e )
			{
			}
		}

		return res;
	}
	
	static void yell( Component o )
	{
		System.out.println(o.getClass().getName()+"(\""+o.getName()+"\").update");
	}
}
