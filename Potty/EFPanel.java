//package ElecPot;

import	java.awt.*;
import	java.awt.event.*;
import	java.util.Vector;
import	java.net.*;
import	Graph.*;
import	ElecPot.*;

/** A Panel
 *  that handles mouse events
 *		by computing the x,y into some value in a DataSet for charting
 *	draws itself with a border
 */

public class EFPanel extends Panel implements MouseListener, Runnable
{
		static	boolean	addBorder = false;
		static	boolean	enabled	= true;				//whether we are enabled to respond to mouse events
		public	Dimension EFsize = new Dimension(300,200);
		int		calcfunction = 1;		//which default function to calculate
		DataSet	ds;						//pink for outside of center
		DataSet	ds2;					//red for in center
  		Image	img;
		URL		codeBase;
		MediaTracker	tracker;
  		
						//first the constants of the image given us
			double	imgWidth	= 298.0;	//image size
			double	imgHeight	= 199.0;
			double	imgCenterX	= 150.0;	//center of object (spere) in image
			double	imgCenterY	= 100.0;
			double	imgRadiusX	= 75.0;		//radius of image along X axis
			double	imgRadiusY	= 66.3;		//radius of image along Y axis

	/** Constructors */
  /** Build EFPanel  no parameters gets you the default size 300x200 */
  
  public EFPanel()
  {
	this( 300, 200 );
  }

	/** Build EFPanel of given width and height  with no image given */
  public EFPanel(int width, int height)
  {
  	this( width, height, "");
  }

	/** Build EFPanel of given width and height, displaying imageName */
  public EFPanel(int width, int height, String imageName)
  {
    Panel p = new Panel();
    p.setSize( new Dimension(width,height));
    EFsize.width = width;
    EFsize.height = height;
    this.addMouseListener(this);

	codeBase = getClass().getResource(imageName);
	if( codeBase != null )
	{
		//System.out.println("codeBase is "+codeBase);
		img = getToolkit().getImage(codeBase);
	}
	//System.out.println("img is "+img);
    
    validate();
    ds = new DataSet();
    ds.setGraphDisplay( DataSet.CIRCLE, 2, Color.pink);
    //ds.addDataPoint(0,0);		//add initial start at 0,0
    //ds.addDataPoint(100,100);
    ds2 = new DataSet();
    ds2.setGraphDisplay( DataSet.CIRCLE, 2, Color.red);

	if( codeBase != null )
	{
	    tracker = new MediaTracker( this );
	    tracker.addImage( img, 10);
	    Thread runner = new Thread( this );
	    runner.start();
	}
  }

  			/** turn the painting of a border around this panel */
  public void setHasBorders( boolean condition )
  {
  	addBorder = condition;
  }
  
  public boolean getHasBorders(  )
  {
  	return ( addBorder );
  }

  public boolean isEnabled()
  {
  	return ( enabled );
  }

  public void enable()
  {
  	enabled = true;
  }

  public void disable()
  {
  	enabled = false;
  }

  
  		/** Methods that have to be in every App/Applet/Bean/Component */
  public Dimension getPreferredSize()
  {							//the preffered size is what is currently set
  	return new Dimension( EFsize );
  }
		/** @deprecated use getMinimumSize() */
  public Dimension PreferredSize()
  {
  	return new Dimension( EFsize );
  }

  public Dimension getMinimumSize()
  {
  	return new Dimension( 30, 20 );		//a guess here
  }
		/** @deprecated use getMinimumSize() */
  public Dimension MinimumSize()
  {
  	return new Dimension( 30, 20 );		//a guess here
  }

  /** Report that mouse was pressed.  (1.1)*/
  
  public void mousePressed( MouseEvent e )
  {
  	//if( enabled )
  		//calc( e.getX(), e.getY() );
  }
    
  /** Report that mouse was released.  (1.1)*/
  
  public void mouseReleased( MouseEvent e )
  {
  	if( enabled )
  		calc( e.getX(), e.getY() );
  }
  
  /** Report that mouse was clicked.  (1.1)*/
  
  public void mouseClicked( MouseEvent e )
  {
  	//if( enabled )
  		//calc( e.getX(), e.getY() );
  }
  
  /** Unused mouse events  (1.1)*/
  
  public void mouseEntered( MouseEvent e ) {}
  public void mouseExited( MouseEvent e )  {}

			/** set which example function we will: 
			 *    1. Draw some graphic in the click area
			 *	  2. Which function we calc for the plot.
			 */
	public void setFunction( int func )
	{
		calcfunction = func;
		repaint();
	}

	public int getFunction()
	{
		return( calcfunction );
	}

	public void paint( Graphics g )
	{
		super.paint(g);

		EFsize = this.getSize();
		imgWidth = EFsize.width;
		imgHeight = EFsize.height;
		imgCenterY = (imgHeight / 2);
		imgCenterX = (imgWidth / 2);
		imgRadiusX = imgRadiusY = Math.min(imgCenterX,imgCenterY) / 2;
				
					//paint the special image giving us ..or..
		//if( tracker.checkID(10, true) )
			//g.drawImage( img, 0, 0, EFsize.width, EFsize.height, this );
		//else		//draw some lines,polygons, ovals that look like the current_example
		{
			//System.out.println("Painting calc "+calcfunction);
			if( calcfunction == 1 )
			{
				imgCenterX = imgCenterY - (imgCenterY / 4);
				g.setColor( Color.red );
				g.fillOval( (int)(imgCenterX-imgRadiusX), (int)(imgCenterY-imgRadiusY), (int)imgCenterY, (int)imgCenterY );
			/*	g.setColor( Color.yellow );
				Polygon center_bar = new Polygon();
					center_bar.addPoint( 0, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY-4 );
					center_bar.addPoint( 0, (int)imgCenterY-4 );
				g.fillPolygon( center_bar );
			*/
				g.setColor( Color.black );
				double deg = Math.PI * (45.0 / 180.0); 
				double edgeX = imgCenterX + (Math.cos(deg) * imgRadiusX);
				double edgeY = imgCenterY + (Math.sin(-deg) * imgRadiusY);
				g.drawLine( (int)imgCenterX, (int)(imgCenterY), (int)edgeX, (int)edgeY );
				g.drawLine( (int)edgeX, (int)edgeY, (int)(edgeX-3), (int)edgeY );
				g.drawLine( (int)edgeX, (int)edgeY, (int)edgeX, (int)(edgeY+3) );
				g.drawString("R", (int)imgCenterX, (int)(edgeY) );
				g.drawString("+", (int)(imgCenterX+imgRadiusX), (int)(imgCenterY+imgRadiusY) );
			}
			else if( calcfunction == 2 )
			{
				imgRadiusX = imgRadiusY = imgRadiusX / 3;		//make small ball
			/*	g.setColor( Color.yellow );
				Polygon center_bar = new Polygon();
					center_bar.addPoint( 0, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY-4 );
					center_bar.addPoint( 0, (int)imgCenterY-4 );
				g.fillPolygon( center_bar );
			*/
				g.setColor( Color.red );
				g.fillOval( (int)(imgCenterX-(imgRadiusX/2)), (int)(imgCenterY-(imgRadiusY/2)), (int)imgRadiusX, (int)imgRadiusY );
				g.setColor( Color.black );
				g.drawString("+", (int)(imgCenterX+imgRadiusX), (int)(imgCenterY+imgRadiusY) );
			}
			else if( calcfunction == 3 )
			{
				g.setColor( Color.yellow );
				Polygon center_bar = new Polygon();
					center_bar.addPoint( 0, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY-4 );
					center_bar.addPoint( 0, (int)imgCenterY-4 );
				g.fillPolygon( center_bar );

				int plen = (int)((imgHeight / 5) * 3);			//height of plate
				int pthick = (int)imgWidth / 20;				//thickness of plate
				int pdist = pthick * 3;							//half distance between plates
				int ptop = (int)((imgHeight - plen) / 2);						//where the top of plate draws (y)
				g.setColor( Color.blue );
				Polygon left_plate = new Polygon();
					left_plate.addPoint( (int)imgCenterX - pdist, ptop  );
					left_plate.addPoint( (int)imgCenterX - pdist, ptop+plen );
					left_plate.addPoint( (int)imgCenterX - pdist+pthick, ptop+plen );
					left_plate.addPoint( (int)imgCenterX - pdist+pthick, ptop );
				g.drawPolygon( left_plate );
				Polygon right_plate = new Polygon();
					right_plate.addPoint( (int)imgCenterX + pdist, ptop );
					right_plate.addPoint( (int)imgCenterX + pdist, ptop+plen );
					right_plate.addPoint( (int)imgCenterX + pdist+pthick, ptop+plen );
					right_plate.addPoint( (int)imgCenterX + pdist+pthick, ptop );
				g.drawPolygon( right_plate );
				g.setColor( Color.black );
				g.drawString("+", (int)imgCenterX - pdist, (int)(ptop-5) );
				g.drawString("-", (int)imgCenterX + pdist, (int)(ptop-5) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+4), (int)imgCenterX - pdist+pthick, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX + pdist, (int)(ptop+plen+4), (int)imgCenterX + pdist, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+7), (int)imgCenterX + pdist, (int)(ptop+plen+7) );
				g.drawString("D", (int)imgCenterX, (int)(ptop+plen+20) );
			}
			else if( calcfunction == 4 )
			{
				//imgCenterX = (imgWidth / 2);
			/*	g.setColor( Color.yellow );
				Polygon center_bar = new Polygon();
					center_bar.addPoint( 0, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY-4 );
					center_bar.addPoint( 0, (int)imgCenterY-4 );
				g.fillPolygon( center_bar );
			*/
				imgRadiusX = imgRadiusY = (Math.min(imgWidth,imgHeight) / 2) - 6;		//make small ball
 				g.setColor( Color.blue );
				//g.fillOval( (int)(imgCenterX-imgRadiusX), (int)(imgCenterY-imgRadiusY), (int)imgRadiusY *2, (int)imgRadiusY *2 );
				for( int i=0 ; i < 4 ; i++ )
					g.drawOval( (int)(imgCenterX-imgRadiusX)-i, (int)(imgCenterY-imgRadiusY)-i, (int)(imgRadiusY+i) *2, (int)(imgRadiusY+i) *2 );
							//do this math for 'b' line, before imgRadius changes
							//But draw the 'b'line after/above the smaller oval
				double deg = Math.PI * (45.0 / 180.0); 
				double edgeX = imgCenterX + (Math.cos(deg) * imgRadiusX);
				double edgeY = imgCenterY + (Math.sin(-deg) * imgRadiusY);

 				g.setColor( Color.red );
				imgRadiusX = imgRadiusY = imgRadiusX / 4;		//make small ball
				g.fillOval( (int)(imgCenterX-(imgRadiusX/2)), (int)(imgCenterY-(imgRadiusY/2)), (int)imgRadiusY, (int)imgRadiusY );

				g.setColor( Color.black );
							//draw 'b' the radius of big circle
				g.drawLine( (int)imgCenterX, (int)(imgCenterY), (int)edgeX, (int)edgeY );
				g.drawLine( (int)edgeX, (int)edgeY, (int)(edgeX-3), (int)edgeY );
				g.drawLine( (int)edgeX, (int)edgeY, (int)edgeX, (int)(edgeY+3) );
				g.drawString("b", (int)imgCenterX, (int)(edgeY) );
							//draw 'a' the radius of big circle
				deg = Math.PI * (135.0 / 180.0); 
				edgeX = imgCenterX + (Math.cos(deg) * imgRadiusX);
				edgeY = imgCenterY + (Math.sin(-deg) * imgRadiusY);
				g.drawLine( (int)imgCenterX, (int)(imgCenterY), (int)edgeX, (int)edgeY );
				//g.drawLine( (int)edgeX, (int)edgeY, (int)(edgeX-3), (int)edgeY );
				//g.drawLine( (int)edgeX, (int)edgeY, (int)edgeX, (int)(edgeY+3) );
				g.drawString("a", (int)imgCenterX, (int)(edgeY) );
			}
			else if( calcfunction == 5 )
			{
				g.setColor( Color.yellow );
				Polygon center_bar = new Polygon();
					center_bar.addPoint( 0, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY-4 );
					center_bar.addPoint( 0, (int)imgCenterY-4 );
				g.fillPolygon( center_bar );

				imgRadiusX = imgRadiusY = imgRadiusX / 2;		//make small ball
				int plen = (int)((imgHeight / 5) * 3);			//height of plate
				int pthick = (int)imgWidth / 20;				//thickness of plate
				int pdist = pthick * 3;							//half distance between plates
				int ptop = (int)((imgHeight - plen) / 2);						//where the top of plate draws (y)
				g.setColor( Color.red );
				g.fillOval( (int)(imgCenterX-(imgRadiusX/2)), (int)(imgCenterY+(3*imgRadiusY)), (int)imgRadiusY, (int)imgRadiusY );
				g.fillOval( (int)(imgCenterX-(imgRadiusX/2)), (int)(imgCenterY-(3*imgRadiusY)), (int)imgRadiusY, (int)imgRadiusY );

				g.setColor( Color.black );
				g.drawString("+", (int)imgCenterX - pdist, (int)(ptop-20) );
				g.drawString("-", (int)imgCenterX + pdist, (int)(ptop-20) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+4), (int)imgCenterX - pdist+pthick, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX + pdist, (int)(ptop+plen+4), (int)imgCenterX + pdist, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+7), (int)imgCenterX + pdist, (int)(ptop+plen+7) );
				g.drawString("D", (int)imgCenterX, (int)(ptop+plen+20) );
			}
			else if( calcfunction == 6 )
			{
				g.setColor( Color.yellow );
				Polygon center_bar = new Polygon();
					center_bar.addPoint( 0, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY-4 );
					center_bar.addPoint( 0, (int)imgCenterY-4 );
				g.fillPolygon( center_bar );

				imgRadiusX = imgRadiusY = imgRadiusX / 4;		//make small ball
				int plen = (int)((imgHeight / 5) * 3);			//height of plate
				int pthick = (int)imgWidth / 20;				//thickness of plate
				int pdist = pthick * 3;							//half distance between plates
				int ptop = (int)((imgHeight - plen) / 2);						//where the top of plate draws (y)

				g.setColor( Color.red );
				g.fillOval( (int)(imgCenterX-(6*(imgRadiusX/2))), (int)(imgCenterY-imgRadiusY), (int)imgRadiusY*2, (int)imgRadiusY*2 );
				g.fillOval( (int)(imgCenterX+(6*(imgRadiusX/2))), (int)(imgCenterY-imgRadiusY), (int)imgRadiusY*2, (int)imgRadiusY*2 );

				g.setColor( Color.black );
				g.drawString("+", (int)imgCenterX - pdist, (int)(ptop-20) );
				g.drawString("-", (int)imgCenterX + pdist, (int)(ptop-20) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+4), (int)imgCenterX - pdist+pthick, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX + pdist, (int)(ptop+plen+4), (int)imgCenterX + pdist, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+7), (int)imgCenterX + pdist, (int)(ptop+plen+7) );
				g.drawString("D", (int)imgCenterX, (int)(ptop+plen+20) );
			}
			else if( calcfunction == 7 )
			{
				g.setColor( Color.yellow );
				Polygon center_bar = new Polygon();
					center_bar.addPoint( 0, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY-4 );
					center_bar.addPoint( 0, (int)imgCenterY-4 );
				g.fillPolygon( center_bar );

				imgRadiusX = imgRadiusY = imgRadiusX / 4;		//make small ball
				int plen = (int)((imgHeight / 5) * 3);			//height of plate
				int pthick = (int)imgWidth / 20;				//thickness of plate
				int pdist = pthick * 3;							//half distance between plates
				int ptop = (int)((imgHeight - plen) / 2);						//where the top of plate draws (y)

				g.setColor( Color.red );
				g.fillOval( (int)(imgCenterX-(3*imgRadiusX)), (int)(imgCenterY-imgRadiusY), (int)imgRadiusY*2, (int)imgRadiusY*2 );
				g.fillOval( (int)(imgCenterX+(3*imgRadiusX)), (int)(imgCenterY-imgRadiusY), (int)imgRadiusY*2, (int)imgRadiusY*2 );

				g.setColor( Color.black );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+4), (int)imgCenterX - pdist+pthick, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX + pdist, (int)(ptop+plen+4), (int)imgCenterX + pdist, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+7), (int)imgCenterX + pdist, (int)(ptop+plen+7) );
				g.drawString("R", (int)imgCenterX, (int)(ptop+plen+20) );
			}
			else if( calcfunction == 8 )
			{
				g.setColor( Color.yellow );
				Polygon center_bar = new Polygon();
					center_bar.addPoint( 0, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY+4 );
					center_bar.addPoint( (int)imgWidth, (int)imgCenterY-4 );
					center_bar.addPoint( 0, (int)imgCenterY-4 );
				g.fillPolygon( center_bar );

				int plen = (int)imgHeight / 2;
				int phi  = (int)imgWidth / 20;
				int pdist = (int)imgWidth / 4;
				int pthick = (int)imgWidth / 20;				//thickness of plate
				int ptop = (int)((imgHeight - plen) / 2);						//where the top of plate draws (y)

				g.setColor( Color.blue );
				Polygon plate = new Polygon();
					plate.addPoint( pdist, plen/2 );
					plate.addPoint( pdist, plen+(plen/2) );
					plate.addPoint( pdist+phi, plen+(plen/2) );
					plate.addPoint( pdist+phi, plen/2 );
				g.drawPolygon( plate );

				g.setColor( Color.black );
				g.drawString("+", (int)imgCenterX - pdist, (int)(ptop-20) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+4), (int)imgCenterX - pdist+pthick, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX + pdist, (int)(ptop+plen+4), (int)imgCenterX + pdist, (int)(ptop+plen+10) );
				g.drawLine( (int)imgCenterX - pdist+pthick, (int)(ptop+plen+7), (int)imgCenterX + pdist, (int)(ptop+plen+7) );
				g.drawString("R", (int)imgCenterX, (int)(ptop+plen+20) );
			}
			else		//Oops an unknown example is being chosen, better write some code here
			{
				imgCenterX = ((imgHeight / 10) * calcfunction) - (imgCenterY / 4);
				g.setColor( Color.red );
				g.fillOval( (int)(imgCenterX-imgRadiusX), (int)(imgCenterY-imgRadiusY), (int)imgCenterY, (int)imgCenterY );
			}
		}

		if( addBorder )				//draw a border around the plates area
		{
	        g.setColor(Color.black);
	        g.drawRect(0, 0, EFsize.width - 1, EFsize.height - 1);
	        g.setColor(Color.gray);
	        g.drawRect(1, 1, EFsize.width - 3, EFsize.height - 3);
	        g.setColor(Color.black);
	        g.drawRect(2, 2, EFsize.width - 5, EFsize.height - 5);
        }
	}

				// CALCulate the current Function given an input x,y
	public void calc( int x, int y )
	{
	  	if( !enabled )
	  		return;

		DataPoint	dp;

		double	Ke = 8.9875e9;	//BLOTZ really == 8.9875e9
		
						//Convert some clicked XY to some internal standard 100x100 grid
						// The center is put at 0,0
						//

					//The above drawing sections of paint()
					//give us most of our size and locations
			double	XYfactor	= imgRadiusX / imgRadiusY;
					//XY correction factor for oval-ness of sphere
						//second the viewer/java/user has distorted/zoomed
						// the image, so translate back to image dimensions
						//  plus scale/adjust the Y axis for out-of-roundness
			double	centerX =  imgCenterX;
			double	centerY =  imgCenterY * XYfactor;
						//get the adjusted XY and radius
			double	aX =  (x * imgWidth) / this.getSize().width;
			double	aY =  ((y * imgHeight) / this.getSize().height) * XYfactor;
			double	RX =  imgRadiusX;
			double	RY =  imgRadiusY * XYfactor;
						//find some useful variables for calcs below
						//find the distance from center to point
			double dx = Math.abs(centerX - aX);
			double dy = Math.abs(centerY - aY);
			double r = Math.sqrt( (dx*dx) + (dy*dy) );
			double a = Math.atan( dy / dx );
			double pd = (r / imgWidth) * 100.0;		//adjust point to within 100% of graph
			double Rp = (((RX + RY) / 2) / imgWidth) * 100.0;	//adjust radius to within 100% of graph


		if( calcfunction == 1 )			//the inside/outside calc
		{								// inside = R source, outside = dxy3 / R2
					// the real equation distance from center (of an image)

			if( r < Math.min( RX, RY ) ) 		//do easy inside checks first
				ds2.addDataPoint( pd, pd );		// plot the inside points as x,y
			else if( r > Math.max( RX, RY ) ) 	//do easy outside checks first
				ds.addDataPoint( pd, (Rp*Rp*Rp)/(pd*pd) );	//Blotz off by
			else if( ((Math.cos(a) * r) < RX) && ((Math.sin(a) * r) < RY) ) 
				ds2.addDataPoint( pd, pd );		//probably needs *2 or div max(imgcenter, imgsize-imgcenter)
			else
				ds.addDataPoint( pd, (Rp*Rp*Rp)/(pd*pd) );	//Blotz off by
			System.out.println(" dx="+dx+" dy="+dy+" r="+r+" pd="+pd+" RX="+RX+" Rp="+Rp);
		}
		else if( calcfunction == 2 )		//Line of charge
		{
					//No plotting for inside R
			if( pd < Rp )
				return;
				// X equals (r) distance from center
				// Y equals 2Ke(theta) ln(r)
			double ddy = 2.0 * Ke * Math.log(pd);
			ds.addDataPoint( pd, ddy );
		}
		else if( calcfunction == 3 )		//Parallel Plates
		{
					// the real equation
				// Y always equals field strength (constant)
				// X equals percent vertical distance
			//double dx = 100.0 - (((double)y / this.getSize().height) * 100.0);
				//if x is in middle (40-60%) then make it dark red
			//double dd = ((double)x / this.getSize().width) * 100.0;
			if( (pd > 40.0) && (pd < 60.0) )
			{
				if( (pd > 40.0) && (pd < 60.0) )
					ds2.addDataPoint( pd, 45.0 );
				else
					ds.addDataPoint( pd, 45.0 );
			}
			else
				ds.addDataPoint( pd, 0.0 );
		}
		else if( calcfunction == 4 )		//Coaxial Cable
		{
					// the real equation
				// X equals percent vertical distance
				// Y always equals field strength (constant)
			if( (pd > 40.0) && (pd < 60.0) )
				ds2.addDataPoint( pd, 45.0 );
			else
				ds.addDataPoint( pd, 45.0 );
		}
		else if( calcfunction == 5 )		//Dipole  along perpendicular
		{
					// the real equation
				// Y always equals field strength (constant)
				// X equals percent vertical distance
			//double dx = 100.0 - (((double)y / this.getSize().height) * 100.0);
				//if x is in middle (40-60%) then make it dark red
			//double dd = ((double)x / this.getSize().width) * 100.0;
			if( (pd > 40.0) && (pd < 60.0) )
				ds2.addDataPoint( pd, 45.0 );
			else
				ds.addDataPoint( pd, 45.0 );
		}
		else if( calcfunction == 6 )		//Dipole  along axis
		{
					// the real equation
				// Y always equals field strength (constant)
				// X equals percent vertical distance
			//double dx = 100.0 - (((double)y / this.getSize().height) * 100.0);
				//if x is in middle (40-60%) then make it dark red
			//double dd = ((double)x / this.getSize().width) * 100.0;
			if( (pd > 40.0) && (pd < 60.0) )
				ds2.addDataPoint( pd, 45.0 );
			else
				ds.addDataPoint( pd, 45.0 );
		}
		else if( calcfunction == 7 )		//Charged Ring
		{
					// the real equation
				// Y always equals field strength (constant)
				// X equals percent vertical distance
			//double dx = 100.0 - (((double)y / this.getSize().height) * 100.0);
				//if x is in middle (40-60%) then make it dark red
			//double dd = ((double)x / this.getSize().width) * 100.0;
			if( (pd > 40.0) && (pd < 60.0) )
				ds2.addDataPoint( pd, 45.0 );
			else
				ds.addDataPoint( pd, 45.0 );
		}
		else if( calcfunction == 8 )		//Charged Disk
		{
					// the real equation
				// Y always equals field strength (constant)
				// X equals percent vertical distance
			//double dx = 100.0 - (((double)y / this.getSize().height) * 100.0);
				//if x is in middle (40-60%) then make it dark red
			//double dd = ((double)x / this.getSize().width) * 100.0;
			if( (pd > 40.0) && (pd < 60.0) )
				ds2.addDataPoint( pd, 45.0 );
			else
				ds.addDataPoint( pd, 45.0 );
		}
		else 		//default is a line (parallel plates)
		{
					// the real equation
				// Y always equals field strength (constant)
				// X equals percent vertical distance
			//double dx = 100.0 - (((double)y / this.getSize().height) * 100.0);
				//if x is in middle (40-60%) then make it dark red
			//double dd = ((double)x / this.getSize().width) * 100.0;
			if( (pd > 40.0) && (pd < 60.0) )
				ds2.addDataPoint( pd, 45.0 );
			else
				ds.addDataPoint( pd, 45.0 );
		}

		System.out.println( x+","+y );
		//repaint();
	}

	public void run()					//Let mediaTracker wait for image loaded
	{
		try { tracker.waitForID(10); }
		catch( InterruptedException e ) {;}
		repaint();
	}
}
