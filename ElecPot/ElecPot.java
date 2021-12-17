//package ElecPot;

import	java.applet.Applet;
import	java.awt.*;
import	java.awt.event.*;
import	java.net.*;
import	Graph.*;
import	Line.*;

/** ElecPot -- Applet 
 *    displays Electrical Field strengths
 *  an EFPanel (for an image(2 plates)) that reports events
 *  a GraphPanel that charts the results
 *  a (popup to pick a) function to Fit/overlay onto graph
 *  some sliders to alter the function variables 
 */

public class ElecPot extends Applet
{
	public	static	Color userbackground;
		
	static	int		Current_Func = 1;				//default funtion (1=Sphere)
	Checkbox PlotPointsCB = null;
	Checkbox ViewLinesCB = null;
	Checkbox GraphFuncsCB = null;

	FixedPanel Cpnl;
	FixedPanel BottPnl;
	Picture formula;
	int	EFpsizeX = 200;
	int	EFpsizeY = 100;
	Graph	graph;
	final DataSet func_ds = new DataSet();		//add a function display DataSet
	LabeledSlider QLblSlider;
	LabeledSlider RLblSlider;
	LabeledSlider BLblSlider;
	Slider Qslider = null;
	Slider Rslider = null;
	Slider Bslider = null;
	static	EFPanel EFp = null;
	static	Panel	examplePnl = null;
	static	Picture pict = null;

	public void init()
  	{
  			
		System.out.println("Starting Applet");

						//Get/Set all the parameters for this Applet.
						//  Set all parameters to user/html/prs or default values
						// Some lower level objects may ask for parameters too.
		this.setForeground( getColorParameter("foreground",new Color(0,0,0)) );
		
		this.setBackground( getColorParameter("background",new Color(255,255,255)) );
		
		userbackground = getColorParameter("userbackground",new Color(255,255,206));
		//System.out.println("Background=="+background);


						//Layout the Look 
						// by placing smaller panels onto applet panel
    	setLayout(new BorderLayout());

				//add a Top Title Panel with a reset button.
		Panel TitlePnl = new Panel( new BorderLayout() );
		add( "North", TitlePnl );

    			//Add the pieces (more panels or components)
    			// to the Title large panel

					//Add title and date strings
			Label TitleLabel = new Label( "Electric Potential  3.08.99" );
			TitlePnl.add( "West", (Component)TitleLabel );
	
			Panel ButtonPnl = new Panel( new BorderLayout() );
					//Add a NEW (set new var) button
			Button nb = new Button("New");
			nb.setBackground( Color.red );
			nb.setForeground( Color.white );
			nb.setSize(50,20);
			nb.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						graph.clear();
					}
				} );
			ButtonPnl.add( "West", nb );
		
					//Add a reset button
			Button b = new Button("Clear");
			b.setBackground( Color.red );
			b.setForeground( Color.white );
			b.setSize(50,20);
			b.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						graph.clear();
					}
				} );
			ButtonPnl.add( "East", b );
			TitlePnl.add( "East", ButtonPnl );
		
    	
				//add a West Instructions and function selecter
				// Presently there are 9 rows of 1 col items to put here
		Panel WestPnl = new Panel( new GridBagLayout() );
		if( userbackground != null ) WestPnl.setBackground(userbackground);
		add( "West", WestPnl );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		//gbc.insets = new Insets(5,5,5,5);

    			//Add the pieces (more panels or components) to the West Panel

					//Add a "Choose an example" panel
			Panel ChoosePnl = new Panel( new GridLayout(2,1));
					//Add the "Choose an example" label
				Label ChooseExpLabel = new Label( "Choose an example" );
				ChoosePnl.add( (Component)ChooseExpLabel );
						//Add an example chooser
				final Choice exp_choice = new Choice( );
				exp_choice.addItem( "Charged Sphere" );
				exp_choice.addItem( "Infinite Line of Charge" );
				exp_choice.addItem( "Parallel Plates" );
				exp_choice.addItem( "Coaxial Cylinder" );
				exp_choice.addItem( "Dipole (perpendicular bisector)" );
				exp_choice.addItem( "Dipole (along axis)" );
				exp_choice.addItem( "Charged Ring" );
				exp_choice.addItem( "Charged Disk" );
				exp_choice.addItemListener( new ItemListener()
					{
						public void itemStateChanged( ItemEvent e )
						{
							switch_example( exp_choice.getSelectedIndex() + 1 );
						}
					} );
			ChoosePnl.add( exp_choice );
			gbc.gridy = 0;
			WestPnl.add( ChoosePnl, gbc );
			
					//Add the "Step One" panel
			Panel Step1Pnl = new Panel( new GridLayout(2,1));
						//Add the "Step One" label
				Label StepOneLabel = new Label( "Step One" );
				Step1Pnl.add( (Component)StepOneLabel );
						//And add a simple line below
				Line SeparatorLine = new Line( 1 );
			Step1Pnl.add( SeparatorLine );
			gbc.gridy = 8;
			WestPnl.add( Step1Pnl, gbc ); 

					//Put all the checkboxs (to follow) into a CheckboxGroup
			CheckboxGroup cbg = new CheckboxGroup();
				
					//Add the 'plot points' checkbox, title and instructions text
			String [] t1 = {
				"Instructions for clicking on the diagram",
				"to plot points, how many, and in what",
				"areas."
				};		
			CheckboxPanel cbp = new CheckboxPanel("Plot Points",true,t1,cbg);
			PlotPointsCB = cbp.getCheckbox();
			PlotPointsCB.addItemListener( new StepRadioListener() );
			gbc.gridy = 9;
			gbc.anchor = GridBagConstraints.WEST;
			WestPnl.add( cbp, gbc );
	
					//Add the 'View Equipotential Lines' checkbox, title and instructions text
			String [] t2 = {
				"Instructions for plotting the",
				"equipotential and what to look for."
				};		
			cbp = new CheckboxPanel("View Equipotential Lines",false,t2,cbg);
			ViewLinesCB = cbp.getCheckbox();
			ViewLinesCB.addItemListener( new StepRadioListener() );
			gbc.gridy = 10;
			gbc.anchor = GridBagConstraints.WEST;
			WestPnl.add( cbp, gbc );
	
					//Add the "Step Two" panel
			Panel Step2Pnl = new Panel( new GridLayout(2,1));
						//Add the "Step Two" label
				Label StepTwoLabel = new Label( "Step Two" );
				Step2Pnl.add( (Component)StepTwoLabel );
						//And add a simple line below
				SeparatorLine = new Line( 1 );
			Step2Pnl.add( SeparatorLine );
			gbc.gridy = 15;
			WestPnl.add( Step2Pnl, gbc ); 
	
					//Add the 'Graph Function' checkbox, title and instructions text
			String [] t3 = {
				"Instructions for modifying parameters",
				"to match plotted curve to function curve."
				};		
			cbp = new CheckboxPanel("Graph Function",false,t3,cbg);
			GraphFuncsCB = cbp.getCheckbox();
			GraphFuncsCB.addItemListener( new StepRadioListener() );
			gbc.gridy = 16;
			gbc.anchor = GridBagConstraints.WEST;
			WestPnl.add( cbp, gbc );


				//add a Center/East main work/activity panel
				// for EFpanel, 3D image, plot_chart, Function Equation and varibles sliders
		Panel WorkPnl = new Panel( new BorderLayout() );
		add( "Center", WorkPnl );

    			//Add the pieces (more panels or components) to the West Panel
	
	    			//Add the North click-in panel
	    			// this is a drawing of 'current_func'
	    			//  and an image of the 3D space it comes from with XYZ axis
	    	examplePnl = new Panel( new BorderLayout() );
				String imageName = this.getStringParm("EFimage"+Current_Func,"apart"+Current_Func+".jpg");
				pict = new Picture( imageName );
				examplePnl.add( "East", pict );

				EFp = new EFPanel(EFpsizeX, EFpsizeY);
				EFp.setBounds(5,5,EFpsizeX, EFpsizeY);
				examplePnl.add( "West", EFp );
			WorkPnl.add( "North", examplePnl );
			
			Panel GraphPnl = new Panel( new BorderLayout() );
			SeparatorLine = new Line( 1 );
			GraphPnl.add( "North", SeparatorLine );
	        graph = new Graph();
	        graph.setLayout(null);
	        graph.setBounds(10, 215, 430, 275);
	        graph.setName("ElecPot test");
	        graph.setLabels("r", "V");
	        graph.setXAxis(0.0D, 80.0D, 20.0D, 5.0D);
	        graph.setYAxis(0.0D, 25.0D, 10.0D, 5.0D);
	        graph.addDataSet( EFp.ds );					//add both datasets to our graph
	        graph.addDataSet( EFp.ds2 );
			GraphPnl.add("Center", graph );
			WorkPnl.add("Center", GraphPnl );
	
			func_ds.setSymSize( 1 );
			func_ds.setType( DataSet.LINE );
			graph.addDataSet( func_ds );			// for plot curve fitting
			
					//add a Bottom Panel for formulas and control sliders
			validate();
			BottPnl = new FixedPanel( new BorderLayout(), WorkPnl.getSize().width, 80 );
			SeparatorLine = new Line( 1 );
			BottPnl.add( "North", SeparatorLine );
			WorkPnl.add( "South", BottPnl );
			System.out.println("BottPnl = new FixedPanel( "+WorkPnl.getSize().width+", 80 );");
	
					//Add some controls to the control panel
			imageName = "formula"+Current_Func+".gif";
			//imageName = "formula.gif";
			formula = new Picture( imageName );
			BottPnl.add( "West", formula );

					//Add some controls to the control panel
			//Cpnl = new FixedPanel( new GridBagLayout(), WorkPnl.getSize().width - formula.getPreferredSize().width, formula.getPreferredSize().height );
			//Cpnl = new FixedPanel( new GridBagLayout(), WorkPnl.getSize().width - formula.getPreferredSize().width, 80 );
			Cpnl = new FixedPanel( new GridBagLayout(), 250, 80 );
			if( userbackground != null )
				Cpnl.setBackground(userbackground);
			GridBagConstraints gc = new GridBagConstraints();
			gc.fill = GridBagConstraints.BOTH;
			gc.insets = new Insets( 5, 5, 5, 5 );
			
			//plot_function(); don't plot until Cpnl is showing
			QLblSlider = new LabeledSlider( "Q", 1, 20, 7 );
			Qslider = QLblSlider.getSlider();
			Qslider.addDataListener( new DataListener()
				{
					public void processDataEvent( DataEvent e )
					{
						plot_function();
					}
				} );
			gc.gridx = gc.gridy = 0;
			Cpnl.add( QLblSlider, gc );
			//Cpnl.add( "Center", QLblSlider );
			
			RLblSlider = new LabeledSlider( "R", 1, 55, 35 );
			Rslider = RLblSlider.getSlider();
			Rslider.addDataListener( new DataListener()
				{
					public void processDataEvent( DataEvent e )
					{
						plot_function();
					}
				} );
			gc.gridx = 0;
			gc.gridy = 1;
			Cpnl.add( RLblSlider, gc );
						//Add a third slider for Coax(f3) ONLY
			BLblSlider = new LabeledSlider( "b", 1, 55, 35 );
			Bslider = RLblSlider.getSlider();
			Bslider.addDataListener( new DataListener()
				{
					public void processDataEvent( DataEvent e )
					{
						plot_function();
					}
				} );
			gc.gridx = 0;
			gc.gridy = 2;
			Cpnl.add( BLblSlider, gc );
			if( Current_Func != 4 )
				BLblSlider.hide();

			BottPnl.add( "Center", Cpnl );
			validate();
			//BottPnl.doLayout();
			if( (GraphFuncsCB != null) && (GraphFuncsCB.getState() == false) )
			{
				Cpnl.hide();
				formula.hide();
			}
		
		validate();
	}


	public void switch_example( int example )
	{
		if( Current_Func == example )
			return;
		Current_Func = example;
		if( EFp != null )
			EFp.setFunction( Current_Func );

					//adjust graph size to optimal viewing of values
					// Very Probable needs adjusting
		BLblSlider.hide();
		Dimension EFsize = EFp.getPreferredSize();
		if( Current_Func == 1 )
		{
	        graph.setLabels("r", "U");
	        graph.setXAxis(0.0D, 80.0D, 20.0D, 5.0D);
	        graph.setYAxis(0.0D, 25.0D, 10.0D, 5.0D);
		}
		else if( Current_Func == 2 )
		{
	        graph.setLabels("r", "U");
	        graph.setXAxis(0.0D, 80.0D, 0.0D, 0.0D);
	        graph.setYAxis(0.0D, 25.0D, 0.0D, 0.0D);
		}
		else if( Current_Func == 3 )
		{
	        graph.setLabels("x", "U");
	        graph.setXAxis(0.0D, 80.0D, 20.0D, 5.0D);
	        graph.setYAxis(0.0D, 50.0D, 10.0D, 5.0D);
		}
		else if( Current_Func == 4 )
		{
			BLblSlider.show();
	        graph.setLabels("r", "U");
	        graph.setXAxis(0.0D, 80.0D, 20.0D, 5.0D);
	        graph.setYAxis(0.0D, 25.0D, 10.0D, 5.0D);
		}
		else if( Current_Func == 5 )
		{
	        graph.setLabels("x", "U");
	        graph.setXAxis(0.0D, 80.0D, 20.0D, 5.0D);
	        graph.setYAxis(0.0D, 25.0D, 10.0D, 5.0D);
		}
		else if( Current_Func == 6 )
		{
	        graph.setLabels("x", "U");
	        graph.setXAxis(0.0D, 80.0D, 20.0D, 5.0D);
	        graph.setYAxis(0.0D, 25.0D, 10.0D, 5.0D);
		}
		else if( Current_Func == 7 )
		{
	        graph.setLabels("x", "U");
	        graph.setXAxis(0.0D, 80.0D, 20.0D, 5.0D);
	        graph.setYAxis(0.0D, 25.0D, 10.0D, 5.0D);
		}
		else if( Current_Func == 8 )
		{
	        graph.setLabels("x", "U");
	        graph.setXAxis(0.0D, 80.0D, 20.0D, 5.0D);
	        graph.setYAxis(0.0D, 25.0D, 10.0D, 5.0D);
		}
		graph.repaint();

		String imageName = this.getStringParm("EFimage"+Current_Func,"apart"+Current_Func+".jpg");
		if( pict != null );
			examplePnl.remove( pict );
		pict = new Picture( imageName );
		if( pict != null );
			examplePnl.add( "East", pict );

					//Change the formula equation picture
		imageName = "formula"+Current_Func+".gif";
		if( formula != null )
			BottPnl.remove( formula );
		formula = new Picture( imageName );
		if( formula != null )
			BottPnl.add( "West", formula );

		//validate();
		this.getParent().validate();
	}



	private	static int lastQ = 0;
	private	static int lastR = 0;

	private void plot_function()
	{
		int	pts = 100;				//number of points this will plot

		if( (Qslider == null) || (Rslider == null) )
			return;		
		int Q = Qslider.getValue();
		int R = Rslider.getValue();
		if( (Q == lastQ) && (R == lastR) )
			return;
		double p = 0.1;
		//func_ds.removeAllElements();			//not really needed everytime
		while( func_ds.size() < pts )			//init a vector full (of x=cnt, y=near zeroes)
			func_ds.addDataPoint( func_ds.size(), 0.1 );
		for( int i = 0; i < pts ; i++ )			//Blotz beware graph=100 so don't change this 100 until div 100 added
		{						//try not to do 100 paint()s
			if( i < R )
				p = (i * ((double)Q / (10.0 * (R*R*R)))) * 10000.0;
			else
				p = ((double)Q / (10.0 * (i*i))) * 10000.0;

			((DataPoint)func_ds.elementAt( i )).y = p;
		}
		func_ds.removeElementAt( pts-1 );					//force one paint, by
		func_ds.addDataPoint( pts, p );	// the listener to addDataPoint()
		lastQ = Q;
		lastR = R;
	}

	private boolean getBooleanParm( String name, boolean def_bool )
	{
		String value = this.getParameter( name );
		if( (value == null) || (value.length() == 0) )
			return( def_bool );
		
		if( value.equalsIgnoreCase("true") )
			return true;
		else if( value.equalsIgnoreCase("false") )
			return false;
		if( value.equalsIgnoreCase("on") )
			return true;
		else if( value.equalsIgnoreCase("off") )
			return false;
		else
			return def_bool;
	}
	
	private String getStringParm( String name, String def_string )
	{
		String value = this.getParameter( name );
		
		return (value == null) ? def_string : value;
	}

	private int getIntParm( String name, int def_int )
	{
		String value = this.getParameter( name );

		if( (value == null) || (value.length() == 0) )
			return( def_int );
		
		try
		{
			return( Integer.parseInt(value) );
		}
		
		catch( Exception e )
		{
			return( def_int );
		}
	}	
	
	private double getDoubleParm( String name, double def_double )
	{
		String value = this.getParameter( name );

		if( (value == null) || (value.length() == 0) )
			return( def_double );
		
		try
		{
			Double d = new Double(value);
			return( d.doubleValue() );
		}
		
		catch( Exception e )
		{
			return( def_double );
		}
	}	
	
		//Read any user desired Color parameters

	protected Color getColorParameter( String name, Color def_color )
	{
		//System.out.println("getColorParameter");
		Color col = getColorParameter( name );
		//System.out.println("getColorParameter found "+col);
		if( col == null )
			col = def_color;
		//System.out.println("getColorParameter returning "+col);
		return( col );
	}
		
	protected Color getColorParameter( String name )
	{
		String value = this.getParameter( name );
		if( (value == null) || (value.length() == 0) )
			return( null );
		try
		{ return( new Color(Integer.parseInt(value,16)) ); }
		catch(Exception e)
			{ return(null); }
	}

		//Return information suitable for display in an About box.
	public String getAppletInfo()
	{
		return( "ElecPot v0.91 by Archipelago Productions and Riddle" );
	}

		//Return information about the supported Applet parameters
		// Web browsers and Applet viewers should display this info.
		//  These are the parameter values the user can set
	public String[][] getParameterInfo()
	{
		return( info );
	}

		/** The info that getParameterInfo() will return
		*   an array of arrays of strings describing each parameter
		*   format: parameter name, parameter type, parameter description
		*/
	private String[][] info = 
	{
		{"foreground", "hexadecimal color value", "set foreground color"},
		{"background", "hexadecimal color value", "set background color"},
		{"userbackground", "hexadecimal color value", "set user input area background color"},
		{"EFimage1..N"	 , "Example Image Filename String"	, "3D Image to load example 1..N"}
	};

			
	class CheckboxPanel extends Panel
	{
		Checkbox cb;
		
		public CheckboxPanel(String label,boolean state,
							String [] text)
		{
			super( new BorderLayout() );
			
			cb = new Checkbox(label,state);
			add( cb, BorderLayout.NORTH );
			add( new TextPanel(text), BorderLayout.CENTER );
		}
		
		public CheckboxPanel(String label,boolean state,
							String [] text, CheckboxGroup group)
		{
			super( new BorderLayout() );
			
			cb = new Checkbox(label,state,group);
			add( cb, BorderLayout.NORTH );
			add( new TextPanel(text), BorderLayout.CENTER );
		}
		
		public Checkbox getCheckbox()
		{
			return cb;
		}
	}
	
	class TextPanel extends Component
	{
		String [] text;
		FontMetrics fm;
		final int xOffset = 22;
		
		public TextPanel( String [] text )
		{
			this.text = text;
			Font f = new Font("SansSerif",Font.PLAIN,9);
			setFont( f );
			fm = getFontMetrics( f );
		}
		
		public Dimension getMinimumSize()
		{
			int h = text.length * fm.getHeight();
			int w = 0;
			
			for( int i=0; i<text.length; ++i )
			{
				w = Math.max(w,fm.stringWidth(text[i]));
			}
			
			return new Dimension(w+xOffset,h);
		}
		
		public Dimension getPreferredSize()
		{
			return getMinimumSize();
		}
		
		public void paint( Graphics g )
		{
			g.setColor( Color.black );
			g.setFont( getFont() );
			for( int i=0; i<text.length; ++i )
			{
				int y = i*fm.getHeight()+fm.getAscent()-1;
				g.drawString(text[i],xOffset,y);
			}
		}
	}

    class StepRadioListener implements ItemListener
    {

        public void itemStateChanged(ItemEvent event)
        {
            Object object = event.getSource();
            //if(object == GraphFuncsCB)
            {
                if( PlotPointsCB.getState() == true )
                {
                	EFp.enable();
                	System.out.println("PlotPointsCBItem to showing");
                }
                else
                {
                	EFp.disable();
                	System.out.println("PlotPointsCBItem to hiding");
                }

                if( ViewLinesCB.getState() == true )
                {
                	//SomePnl.show();
                	System.out.println("ViewLinesCBItem to showing");
                }
                else
                {
                	//SomePnl.hide();
                	System.out.println("ViewLinesCBItem to hiding");
                }

                if( GraphFuncsCB.getState() == true )
                {
					plot_function();
                	System.out.println("GraphFuncItem to showing");
					formula.show();
                	Cpnl.show();
                }
                else
                {
                	Cpnl.hide();
					formula.hide();
                	System.out.println("GraphFuncItem to hiding");
                }
                BottPnl.getParent().doLayout();
            }
        }

        StepRadioListener()
        {
        }
    }


	class FixedPanel extends Panel
	{
		int width, height;
		
		public FixedPanel( LayoutManager lm, int w, int h )
		{
			super(lm);
			width = w;
			height = h;
		}
		
		public void setBounds( int x, int y, int w, int h )
		{
			if( w != width )
			{
				x += (w-width)/2;
				w = width;
			}

			if( h != height )
			{
				y += (h-height)/2;
				h = height;
			}
			
			super.setBounds(x,y,w,h);
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(width,height);
		}
		
		public Dimension getMinimumSize()
		{
			return new Dimension(width,height);
		}
	}

}
