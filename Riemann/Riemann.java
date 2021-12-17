// Riemann Sum Applet
// Concept by Bill Ziemer
// Programming by Brendon Cheves & Bill Ziemer
// e-mail: brendon@csulb.edu  wziemer@csulb.edu

// Copyright 1996 by California State University, Long Beach
// expr package Copyright 1996 by Darius Bacon

// Created 4/19/96
// Last update 10/16/96


// java packages
import java.applet.*;
import java.awt.*;
import java.lang.Math;


// our packages
import expr.*;			// f(x) parser by Darius Bacon
import cool_utils.*;	// cool utilities we made
import util.*;

public class Riemann extends Applet
{
	static final boolean DEBUG = false;

	Color current_color = Color.black;

	Button helpButton, aboutButton, plusdx, minusdx, okBtn;

	Label sliderLabel,areaSumLabel, realAreaLabel,errorLabel;

	int	dx = 80;

	double	theSum, theIntegral, areaSum, realArea, approxErr, error;
   		
    CardLayout
    	theCards;
    	
    String	theTopStyle = "Constant, Random point";
    
    Canvas	theCanvas;
    
    Panel	theSliderPanel, theFeedbackPanel, okBtnPanel,
    		mainCd, aboutCd, helpCd, theAboutPanel, theHelpPanel;
    
    Component theDrawingPanel, dxPanel;
    	
	MultiLineLabel aboutLabel;
	
	Dimension d;
	
	// EZGridLayout manager stuff
	EZGridSettings gridSettings;
	EZGridLayout EZGL;

	// Our function class, based on the Java Polygon class
	ZFunction theFunction;

	
    public void init()
    {
     	resize(640,420);
   	   	
   	   	error = 1E-10;
   	   	
  	   	theFunction = new ZFunction();
   	   	
    	// create a card layout
    	theCards = new CardLayout(10,10);
		this.setLayout(theCards);
 		
 		// create the main card
 		mainCd = new Panel();
 		this.add("main",mainCd);
 		mainCd.setLayout(new BorderLayout(10,10));
 		
 		// create the about card
 		aboutCd = new Panel();
 		this.add("about",aboutCd);
  		aboutCd.setLayout(new BorderLayout(10,10));
		
 		// create the help card
 		helpCd = new Panel();
 		this.add("help",helpCd);
  		helpCd.setLayout(new BorderLayout(10,10));
  

    	// create the drawing panel and add it to the main cd
   		theDrawingPanel = new DrawPanel();
   		Panel p = new DoubleBufferPanel( new BorderLayout() );
   		p.add( theDrawingPanel, BorderLayout.CENTER );
   		mainCd.add( p, BorderLayout.CENTER );

		  		  	
	  	// create the EZGridLayout
  		EZGL = new EZGridLayout(12,2);
	
		
		// create the feedback panel and add it to the main cd
 		theFeedbackPanel = new Panel();
		mainCd.add(BorderLayout.SOUTH,theFeedbackPanel);
 		theFeedbackPanel.setLayout(EZGL);
 		 
 		minusdx = new Button("-");
		gridSettings = new EZGridSettings(minusdx,1,1,1,1);
		theFeedbackPanel.add(minusdx);
 		EZGL.addLayoutInfo(gridSettings);


  		dxPanel = new DXDisplay();
 		gridSettings = new EZGridSettings(dxPanel,2,1,2,1);
		theFeedbackPanel.add(dxPanel);
		EZGL.addLayoutInfo(gridSettings);

 		plusdx = new Button("+");
		gridSettings = new EZGridSettings(plusdx,4,1,1,1);
		theFeedbackPanel.add(plusdx);
		EZGL.addLayoutInfo(gridSettings);
				 				
		sliderLabel = new Label();
		sliderLabel.setText("dx = " + String.valueOf(round(dx/theFunction.XSCALE)));
		gridSettings = new EZGridSettings(sliderLabel,5,1,2,1);
		theFeedbackPanel.add(sliderLabel);
		EZGL.addLayoutInfo(gridSettings);

		areaSumLabel = new Label();
		gridSettings = new EZGridSettings(areaSumLabel,7,1,3,1);
		theFeedbackPanel.add(areaSumLabel);
		EZGL.addLayoutInfo(gridSettings);

		realAreaLabel = new Label();
		gridSettings = new EZGridSettings(realAreaLabel,10,1,3,1);
		theFeedbackPanel.add(realAreaLabel);
		EZGL.addLayoutInfo(gridSettings);
		
		errorLabel = new Label();
		gridSettings = new EZGridSettings(errorLabel,7,2,3,1);
		theFeedbackPanel.add(errorLabel);
		EZGL.addLayoutInfo(gridSettings);
		
		//helpButton = new Button("Help");
		//gridSettings = new EZGridSettings(helpButton,1,2,1,1);
		//theFeedbackPanel.add(helpButton);
		//EZGL.addLayoutInfo(gridSettings);

		aboutButton = new Button("About...");
		gridSettings = new EZGridSettings(aboutButton,2,2,1,1);
		theFeedbackPanel.add(aboutButton);
		EZGL.addLayoutInfo(gridSettings);
		
		theFeedbackPanel.resize(preferredSize());
		
		// create the about panel and add it to the about cd
		theAboutPanel = new Panel();
		aboutCd.add(BorderLayout.CENTER,theAboutPanel);
		theAboutPanel.setLayout(new FlowLayout());
		
		// create the OK button panel and add it to the about cd
		okBtnPanel = new Panel();
		aboutCd.add(BorderLayout.SOUTH,okBtnPanel);
		
		// add the stuff to the about panel
		Font theFont = new Font("TimesRoman", Font.BOLD, 18);
		aboutLabel = new MultiLineLabel(
			"Riemann Sum Applet\nCopyright 1996 by California State University, Long Beach\nWritten by Bill Ziemer and Brendon Cheves\nFunction Parsing Copyright 1996 by Darius Bacon\nDebugged by Brenda Holloway",
			10, 10, MultiLineLabel.CENTER);
		aboutLabel.setFont(theFont);
		theAboutPanel.add(aboutLabel);
		
		// add the OK button
		okBtn = new Button("OK");
		okBtnPanel.add(okBtn);

		// initialize the offscreen stuff
		d = theDrawingPanel.size();

		// parse the default function
		theFunction.parse("log(7 * x + 7) + cos(7 * x)");

		//store the real answer for the area
		realArea = theFunction.area(0.0,1.0,error);
        
		// finally, show the main card
		theCards.show(this,"main");
    }
    
    double round( double d )
    {
    	return Math.rint( d*1000.0 )/1000.0;
    }
    
    class DrawPanel extends /*DoubleBufferPanel*/ Component
    {
	    public void paint(Graphics g)
	    {
	    	// who cares about g, we know where we want to draw
			g.clearRect(0,0,600,500);

		    if(theTopStyle.equals("Constant, Random point"))
		    {
				theFunction.drawRectangles(g,dx);	    	
		    }
		    if(theTopStyle.equals("Constant, Left point"))
		    {
				theFunction.drawRectangles(g,dx,1.0);
		    }
			if(theTopStyle.equals("Constant, Mid-point"))
			{
				theFunction.drawRectangles(g,dx,0.5);
			}
		    if(theTopStyle.equals("Linear"))
		    {
				theFunction.drawTrapezoids(g,dx);
			}
		    if(theTopStyle.equals("Quadratic"))
		    {
				theFunction.drawQuadratics(g,dx);
			}
			
			theFunction.plot(g);
			
			drawAxis(g);
	 		drawLimits(g);
			drawLabels(g);
		}
    }
     
	public Insets insets()
	{
		return new Insets(10,10,10,10);
	}

 	
 	// handle GUI events
    public boolean action(Event e, Object arg)
	{
		double outputdx;
		
       	if(e.target == minusdx)
      	{
       		if(dx>1)
      		{
      			dx--;
      			dxPanel.repaint();
      			theDrawingPanel.repaint();

      		}
      		return true;
      	}
      	else if(e.target == plusdx)
      	{
      		if(dx < 260)
      		{
      			dx++;
      			dxPanel.repaint();
      			theDrawingPanel.repaint();

      		}
      		return true;
      	}
 
      	else if(e.target == aboutButton)
      	{
			theCards.show(this,"about");
      		return true;
      	}
      	else if(e.target == okBtn)
      	{
 			theCards.show(this,"main");
     		return true;
      	}

 		else return super.action(e,arg);	
	}

	
	// draw the axis
	public void drawAxis(Graphics g)
	{
 	  	g.setColor(Color.black);
		
		// x axis
        g.drawLine(0,theFunction.yRealToPixel(0),580,theFunction.yRealToPixel(0));	
 
        // y axis
        g.drawLine(40,20,40,500);
        
        g.drawString("X",585,theFunction.yRealToPixel(0) + 4);
        g.drawString("Y",38,17);
 	}


	// draw all the necessary labels
	public void drawLabels(Graphics g)
	{
		double outputdx;
		
		outputdx = theFunction.xPixelToReal(dx + 40) - ((1E3 * theFunction.xPixelToReal(dx + 40)) % 1) / 1E3;
		error = 1E-10;
 
	 	sliderLabel.setText("dx = " + String.valueOf(round(outputdx)));
		areaSumLabel.setText("Area = " + String.valueOf( round(theFunction.areaSum - ((1E6 * theFunction.areaSum) % 1) / 1E6)));
		realAreaLabel.setText("Real Area = " + String.valueOf(round(realArea - ((1E6 * realArea) % 1) / 1E6)));
		errorLabel.setText("Error = " + String.valueOf( round(realArea - theFunction.areaSum )));
	}

	
	// draw limits in the proper places
	public void drawLimits(Graphics g)
	{
      	g.setColor(Color.red);
		
		// x limit
		g.drawString(".5",(int)(theFunction.XSCALE * .5) + 44,theFunction.yRealToPixel(0) + 14);
		g.drawLine((int)(theFunction.XSCALE * .5) + 40,theFunction.yRealToPixel(0) - 2,(int)(theFunction.XSCALE * .5) + 40,theFunction.yRealToPixel(0) + 2);

		g.drawString("1.0",(int)theFunction.XSCALE + 44,theFunction.yRealToPixel(0) + 14);
		g.drawLine((int)theFunction.XSCALE + 40,theFunction.yRealToPixel(0) - 2,(int)theFunction.XSCALE + 40,theFunction.yRealToPixel(0) + 2);
		
		// y limit
		theFunction.xvar.set_value(.5);
		g.drawString(String.valueOf(round(theFunction.expr.value() - ((1E2 * theFunction.expr.value()) %1) / 1E2)),5,theFunction.yRealToPixel(theFunction.expr.value()) + 3);
		g.drawLine(38,theFunction.yRealToPixel(theFunction.expr.value()),42,theFunction.yRealToPixel(theFunction.expr.value()));
		
  	}

	class DXDisplay extends Component
	{
		public Dimension getPreferredSize()
		{
			return new Dimension(88,12);
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			
			g.setColor(Color.lightGray);
			g.fillRect(0,0,dim.width,dim.height);
			
			g.setColor(Color.darkGray);
			g.fill3DRect(0,0,dx,dim.height,true);

			g.setColor(Color.black);
			g.drawRect(0,0,dim.width-1,dim.height-1);
		}
	}
 	
	// Return info for an about box browser option
	public String getAppletInfo()
	{
		return "Riemann Sum Applet by Bill Ziemer & Brendon Cheves\nmodified 1/10/2001 by Brenda Holloway";
	}


	// Called from JavaScript only
	public void drawFunction(String f)
	{
		theFunction.parse(f);
		realArea = theFunction.area(0.0,1.0,error);
		theDrawingPanel.repaint();
	}


	// Called from JavaScript only
	public void changeTops(String t)
	{
		theTopStyle = t;
		theDrawingPanel.repaint();
	}
}


