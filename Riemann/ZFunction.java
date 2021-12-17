// ZFunction Class
// Written by Bill Ziemer and Brendon Cheves
// Based upon the Java Polygon class

package cool_utils;	// our cool utilities

// java packages
import java.applet.*;
import java.awt.*;
import java.lang.Math;

import expr.*;	// expr package Copyright 1996 by Darius Bacon

public class ZFunction
{
	public double XSCALE = 520.0;
	public double YSCALE = 270.0;
	
	// for scaling
	public double yMin,yMax;
	public double xMin = 0, xMax = 1;
	
	Double doubleCheck = new Double(1.0);
	
	//  offscreen images
	//Image p = null;
	//Graphics g;

	/**
     * The total number of points.
     */
     public int npoints = 0;

    /**
     * The array of x coordinates.
     */
    public int[] xpoints;

    /**
     * The array of y coordinates.
     */
    public int[] ypoints;
    
    /*
     * Bounds of the polygon.
     */
    Rectangle bounds = null;

 	public Expr expr;
	public Variable xvar;
	public double realArea, areaSum, fixedpt;
	
	int iterate, endit;
 
 	
	public ZFunction()
	{
		realArea = 0.0;
		areaSum = 0.0;
		endit=4000;
		xpoints = new int[4];
		ypoints = new int[4];
		// set up the parsing variables
		xvar = Variable.make("x");
		Variable.make("pi").set_value (Math.PI);	// pi is not in the parsing classes
	}


	/*
	 * Parse the function
	*/	 
	public void parse( String f )
 	{
	   	try
	   	{
	   		expr = Parser.parse(f);
	   		determineYMinYMax();
	   		//return expr;
	   	}
		catch (Syntax_error se)
		{
			System.err.println ("Syntax error: " + se);
			return;
		}
	}
	
    /**
     * Appends a point to a function.  If an
     * operation that calculates the bounding box has already been
     * performed, this method updates the bounds accordingly.

     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public void addPoint(int x, int y)
    {
		if (npoints == xpoints.length)
		{
		    int tmp[];

		    tmp = new int[npoints * 2];
		    System.arraycopy(xpoints, 0, tmp, 0, npoints);
		    xpoints = tmp;

		    tmp = new int[npoints * 2];
		    System.arraycopy(ypoints, 0, tmp, 0, npoints);
		    ypoints = tmp;
		}
		xpoints[npoints] = x;
		ypoints[npoints] = y;
		npoints++;

		if (bounds != null)
		{
		    updateBounds(x, y);
		}
	}

    /*
     * Calculate the bounding box of the points passed to the constructor.
     * Sets 'bounds' to the result.
     */
     
    void calculateBounds(int xpoints[], int ypoints[], int npoints)
    {
		int boundsMinX = Integer.MAX_VALUE;
		int boundsMinY = Integer.MAX_VALUE;
		int boundsMaxX = Integer.MIN_VALUE;
		int boundsMaxY = Integer.MIN_VALUE;
		
		for (int i = 0; i < npoints; i++)
		{
		    int x = xpoints[i];
		    boundsMinX = Math.min(boundsMinX, x);
		    boundsMaxX = Math.max(boundsMaxX, x);
		    int y = ypoints[i];
		    boundsMinY = Math.min(boundsMinY, y);
		    boundsMaxY = Math.max(boundsMaxY, y);
		}
	
		bounds = new Rectangle(boundsMinX, boundsMinY,
			       boundsMaxX - boundsMinX,
			       boundsMaxY - boundsMinY);
    }
    
    

    /*
     * Update the bounding box to fit the point x, y.
     */
    void updateBounds(int x, int y)
    {
	    
		bounds.x = Math.min(bounds.x, x);
		bounds.width = Math.max(bounds.width, x - bounds.x);
		bounds.y = Math.min(bounds.y, y);
		bounds.height = Math.max(bounds.height, y - bounds.y);
		
    }	

    /**
     * Determines the area spanned by this Polygon.
     * @return a Rectangle defining the bounds of the Polygon.
     */
    public Rectangle getBoundingBox()
    {
    	
    	bounds.reshape((int)xMin,(int)yMax,(int)(xMax - xMin), (int)(yMax - yMin));
    	
		//if (bounds == null)
		//{
		 //   calculateBounds(xpoints, ypoints, npoints);
		//}
		return bounds;
    }    

	public void plot(Graphics g)
	{
		int y1, y2, j;

      	g.setColor(Color.blue);
		
		xvar.set_value(xPixelToReal(20.0));
		y1 = yRealToPixel(expr.value());
		
		for(int i=xRealToPixel(xMin)-20; i < xRealToPixel(xMax)+20; i++)
		{
			xvar.set_value(xPixelToReal(i+1));
			y2 = yRealToPixel(expr.value());
		//	System.out.println("g.drawLine(" + i + "," + y1 + "," + (i + 1) + "," + y2 + ");");

			// Bug in drawLine that freaks when argument outside drawing area, yields spurious vertical lines
			if( (yRealToPixel(yMax)-20 <= y1) && (y1 <= yRealToPixel(yMin)+20)) 
				{
					g.drawLine(i, y1, i+1, y2);
				}
			
			y1 = y2;
		}
	}
	
	// input real value, output pixel in interval [0, YSCALE]
	public int yRealToPixel(double yReal)
	{
		return (int)((yReal - yMax) * YSCALE / (yMin - yMax) + 10);
	}

	public int yRealToPixel(int yReal)
	{
		return (int)((yReal - yMax) * YSCALE / (yMin - yMax) + 10);
	}


	// input pixel, output real in interval [yMin, yMax]
	public double yPixelToReal(double yPixel)
	{
		return ((yPixel - 10) / YSCALE * (yMin - yMax) + yMax);
	}

	public double yPixelToReal(int yPixel)
	{
		return ((yPixel - 10) / YSCALE * (yMin - yMax) + yMax);
	}



	// input pixel, output real value in interval [xMIn, xMax]
	public double xPixelToReal(double xPixel)
	{
		return ((xPixel - 40.0) * (xMax - xMin) / XSCALE + xMin);
		
	}

	public double xPixelToReal(int xPixel)
	{
		return ((xPixel - 40.0) * (xMax - xMin) / XSCALE + xMin);
		
	}


	// input real, output pixel in interval [0, XSCALE]
	public int xRealToPixel(double xReal)
	{
		return (int)((xReal - xMin) / (xMax - xMin) * XSCALE + 40);
		
	}

	public int xRealToPixel(int xReal)
	{
		return (int)((xReal - xMin) / (xMax - xMin) * XSCALE + 40);
		
	}


	public void determineYMinYMax()
	{
		
		yMin = doubleCheck.POSITIVE_INFINITY;
		yMax = doubleCheck.NEGATIVE_INFINITY;
		
		for(int i=xRealToPixel(xMin);i<xRealToPixel(xMax);i++)
		{
		
			xvar.set_value(xPixelToReal(i));
			
			if(expr.value() > yMax)
			{
				yMax = expr.value();
			}
			
			if(expr.value() < yMin)
			{
				yMin = expr.value();
			}
		}
		
		if(yMin > 0)
		{
			yMin = 0;
		}
	}
	
	
	public double gaussianQuad()
	{
		double  y1, y2, y3, y4, y5;
		xvar.set_value(.5*.9061798459+.5);
   		y1 = expr.value();
   		xvar.set_value(-.5*.9061798459+.5);
   		y2 = expr.value();
   		xvar.set_value(.5*.5384693101+.5);
   		y3 = expr.value();
   		xvar.set_value(-.5*.5384693101+.5);
   		y4 = expr.value();
   		xvar.set_value(.5);
   		y5 = expr.value();
   		return .5*(.236926885*(y1+y2)
   		        +.4786286705*(y3+y4)
   		        +.5688888889*y5);
	}
	
	public double area(double a, double b, double err)
	{
		realArea = 0.0;
		iterate = 0;
		AdaptiveQuad(a,b,err);
		if (iterate >= endit) System.out.println("Error " + err + " not acheived after "+ iterate+" iterations.");
		return realArea;
	}
	
	public double area(double a, double b, double err, int ending)
	{					
		realArea = 0;
		iterate = 0;
		endit = ending;
		AdaptiveQuad(a,b,err);
		return realArea;
	}
	
	void AdaptiveQuad(double a, double b, double err)
	{
		double y1, y2, y3, y4, y5, Simp1, Simp2;
		
		xvar.set_value(a);
		y1 = expr.value();
		
		xvar.set_value( (a+b)/2.0 );
		y3 = expr.value();
		
		xvar.set_value(b);
		y5 = expr.value();
		
		xvar.set_value( (3*a+b)/4.0 );
		y2 = expr.value();
		 
		xvar.set_value( (a + 3*b)/4.0 );
		y4 = expr.value();
		
		Simp1 = (b-a)/6.0 * (y1 + 4*y3 + y5);
		Simp2 = (b-a)/12.0 * (y1 + 4*y2 + 2*y3 + 4*y4 + y5);
		
		
		if ((((Simp1 - Simp2) > 15.0*err) || ((Simp2 -Simp1) > 15.0*err)) & iterate < endit)
		{
			AdaptiveQuad(a,(a+b)/2.0,err/2.0);
			AdaptiveQuad((a+b)/2.0,b,err/2.0);
			iterate += 1;
		} 
		else
		{
			realArea += Simp2;
		}
	}

	public double fixedPoint( double guess, double error)
	{
		double p_new, p_old, leftendpt, rightendpt;
		int iterate, tmp;
		
		// check tp see if there really is a fixed point
		tmp = fixedPointExists(guess);
		
		iterate = 0;
		p_old = guess;
		leftendpt = guess;
		rightendpt = guess;

		if( tmp != 0 ) // one exists
		{
			
			do
			{
				xvar.set_value(p_old);
				p_new = expr.value();
				if(p_new < leftendpt)
				{
					leftendpt = p_new;
				}
				else
				{
				 	if(rightendpt < p_new){ rightendpt = p_new; }
				}
				if(Math.abs(p_new-p_old) <= error)
				{
					break;
				}
				
				p_old = p_new;
				iterate++;
				//System.out.println("Left end point: " + leftendpt + "  Right end point: " + rightendpt);
			}
			while (iterate < 50 && (rightendpt - leftendpt < 10));
			
			establishScale(.9*leftendpt, 1.1*rightendpt);
			yMin = xMin;
			yMax = xMax;
			
			if((iterate == 50) || (rightendpt - leftendpt >= 10)) // iteration fails to find it
			{
				System.out.println("Accuracy "+error+" not reached in 50 iterations");
				bisection(guess, guess+.1*tmp, error);
				p_new = fixedpt;
			}
			
		}
		else // no fixed point exists
		{
			do
			{
				xvar.set_value(p_old);
				p_new = expr.value();
				if(p_new < leftendpt)
				{
					leftendpt = p_new;
				}
				else
				{
				 	if(rightendpt < p_new){ rightendpt = p_new; }
				}
				
				if(Math.abs(p_new-p_old) <= error)
				{
					break;
				}
				
				p_old = p_new;
				iterate++;
			}
			while (iterate < 25 && (rightendpt - leftendpt < 10));
			
			p_new = doubleCheck.POSITIVE_INFINITY;
						//	System.out.println("in to establish with failed");

			if(rightendpt - leftendpt >= 10)
			{
				establishScale(guess-5, guess+5);
			}
			else
			{
				establishScale(.9*leftendpt, 1.1*rightendpt);
			}
				//System.out.println("No fixed point within 10 of your initial guess");
		}
			
		return p_new;

	}
	
	// Return 0 if no fixed point, + if fixed point to the right of guess, - if to the left.
	private int fixedPointExists(double guess)
	{
		double ystart, y, diff;
		int i=1,ans;
		
		xvar.set_value(guess);
		if(expr.value() - guess == Math.abs(expr.value()-guess))
		{
			ystart = 1;
		}
		else
		{
			ystart = -1;
		}
		
		do
		{
			xvar.set_value(guess + .1*i);
			if( expr.value() - (guess + .1*i)== Math.abs(expr.value() - (guess + .1*i)))
			{
				y = 1;
			}
			else
			{
				y = -1;
			}
			
			i++;
		}
		while( ystart - y == 0 && i < 100);
		if(ystart - y != 0)
		{
			ans = i; // found a fixed point to the right, i tenth's over
			//System.out.println("found a fixed point to the right");
		}
		else // check to the left
		{
			i = 1;
			do
			{
				xvar.set_value(guess - .1*i);
				if( expr.value() - (guess - .1*i)== Math.abs(expr.value() - (guess - .1*i)))
				{
					y = 1;
				}
				else
				{
					y = -1;
				}
				
				i++;
			}
			while( ystart - y == 0 && i < 100);
			if(ystart - y != 0)
			{
				ans = -i; // found a fixed point to the left, i tenth's over
							//System.out.println("found a fixed point to the left");

			}
			else
			{
				ans = 0;
							//System.out.println("Didn't find a fixed point");

			}
		}
		return ans;
	}
	
	//Use old tried and true bisection method to get fixed point
	private void bisection(double a, double b, double err)
	{
		double ya, yb, ymid;
		
		xvar.set_value( a );
		ya = expr.value() - a;
		
		xvar.set_value( b );
		yb = expr.value() - b;

		xvar.set_value( (a+b)/2 );
		ymid = expr.value()-(a+b)/2 ;
		
		if( b-a <= err)
		{
			fixedpt = a;
		}
		else
		{ 
			if(   (ya > 0) && (ymid < 0) 
			   || (ya < 0) && (ymid > 0) )
			{
				bisection(a, (a+b)/2, err);
			}
			else
			{
				bisection( (a+b)/2, b, err);
			}
		} 
		
	}

	public void drawRectangles(Graphics g,int dx, double r)
	{
		int x1,y1,x2,y2,x3,y3,w=0,h=0,delta,tempy1,tempy2, tempy3;
		int[] xPoints = new int[5];
		int[] yPoints = new int[5];
		   		
 		y2 = yRealToPixel(0);
 		areaSum = 0;
 		
		for(x1 = xRealToPixel(xMin),x2 = x1 + dx; x2 <= xRealToPixel(xMax); x2 += dx )
		{
			 
	    	g.setColor(Color.orange);

			xvar.set_value(r*xPixelToReal(x1)+(1-r)*xPixelToReal(x2));

			y1 = (int)yRealToPixel(expr.value());
			
			areaSum += dx / XSCALE * expr.value();
			
			if(y2<y1)	// below x axis
			{
				h = y1 - y2;
				y1 = yRealToPixel(0);
			}
			else
			{
				h = y2 - y1;
			}

			w = x2 - x1;
			
			g.fill3DRect(x1,y1,w,h,true);
			g.setColor(Color.darkGray);
			g.drawRect(x1,y1,w,h);
			 
			x1 = x2;
		}
			
	

		// guarantee the very xMax rect
		xvar.set_value(r*xPixelToReal(x1)+(1-r)*xMax);
		y1 = (int)yRealToPixel(expr.value());
		

		if(y2<y1)
		{
			h = y1 - y2;
			y1 = yRealToPixel(0);
		}
		else
		{
			h = y2 - y1;
		}
		w = xRealToPixel(xMax) - x1;
	
		areaSum += w / XSCALE * expr.value();
	
		
		// fill the rectangles
     	g.setColor(Color.orange);
		g.fill3DRect(x1,y1,w,h,true);
		// draw a black border
		g.setColor(Color.darkGray);
		g.drawRect(x1,y1,w,h);

	}

	public void drawRectangles(Graphics g,int dx)
	{
		int x1,y1,x2,y2,x3,y3,w=0,h=0,delta,tempy1,tempy2, tempy3;
		int[] xPoints = new int[5];
		int[] yPoints = new int[5];
		double r; 
		   		
 		y2 = yRealToPixel(0);
 		areaSum = 0;
 		
		for(x1 = xRealToPixel(xMin),x2 = x1 + dx; x2 <= xRealToPixel(xMax); x2 += dx )
		{
			 
	    	g.setColor(Color.orange);
	    	
	    	r = Math.random();
			xvar.set_value(r*xPixelToReal(x1)+(1-r)*xPixelToReal(x2));

			y1 = (int)yRealToPixel(expr.value());
			
			areaSum += dx / XSCALE * expr.value();
			
			if(y2<y1)	// below x axis
			{
				h = y1 - y2;
				y1 = yRealToPixel(0);
			}
			else
			{
				h = y2 - y1;
			}

			w = x2 - x1;
			
			g.fill3DRect(x1,y1,w,h,true);
			g.setColor(Color.darkGray);
			g.drawRect(x1,y1,w,h);
			 
			x1 = x2;
		}
			
	

		// guarantee the very xMax rect
		r = Math.random();
		xvar.set_value(r*xPixelToReal(x1)+(1-r)*xMax);
		y1 = (int)yRealToPixel(expr.value());
		

		if(y2<y1)
		{
			h = y1 - y2;
			y1 = yRealToPixel(0);
		}
		else
		{
			h = y2 - y1;
		}
		w = xRealToPixel(xMax) - x1;
	
		areaSum += w / XSCALE * expr.value();
	
		
		// fill the rectangles
     	g.setColor(Color.orange);
		g.fill3DRect(x1,y1,w,h,true);
		// draw a black border
		g.setColor(Color.darkGray);
		g.drawRect(x1,y1,w,h);

	}
	public void drawTrapezoids(Graphics g,int dx)
	{
		int x1,y1,x2,y2,x3,y3,w=0,h=0,delta,tempy1,tempy2, tempy3;
		int[] xPoints = new int[5];
		int[] yPoints = new int[5];
		double realy1,realy2;
		
 		areaSum = 0.0;
		
		for(x1 = xRealToPixel(xMin),x2 = x1 + dx; x2 <= xRealToPixel(xMax); x2 += dx )
		{
			xvar.set_value(xPixelToReal(x1));
			realy1 = expr.value();
			y1 = (int)yRealToPixel(expr.value());

			xvar.set_value(xPixelToReal(x2));
			realy2 = expr.value();
			y2 = (int)yRealToPixel(expr.value());
			
			areaSum += (dx / XSCALE) * ((realy1 + realy2) / 2.0);
			
			xPoints[0] = x1;
			xPoints[1] = x2;
			xPoints[2] = x2;
			xPoints[3] = x1;

			yPoints[0] = y1;
			yPoints[1] = y2;
			yPoints[2] = yRealToPixel(0);
			yPoints[3] = yRealToPixel(0);
			
			g.setColor(Color.darkGray);
			
			g.drawLine(x1,y1,x2,y2);
			g.drawLine(x2,y2,x2,yRealToPixel(0));

			xPoints[0] = x1+2;
			xPoints[1] = x2-2;
			xPoints[2] = x2-2;
			xPoints[3] = x1+2; 
			
			yPoints[0] = y1+2;
			yPoints[1] = y2+2;
			yPoints[2] = yRealToPixel(0)-2;
			yPoints[3] = yRealToPixel(0)-2;

			g.setColor(Color.yellow);
			g.fillPolygon(xPoints,yPoints,4);
			
			x1 = x2;
		}
		
		// guarantee xMax trapezoid
		if(x1 < xRealToPixel(xMax))
		{
			xvar.set_value(xPixelToReal(x1));
			realy1 = expr.value();
			tempy1 = (int)yRealToPixel(expr.value());

			xvar.set_value(xPixelToReal(560));
			realy2 = expr.value();
			tempy2 = (int)yRealToPixel(expr.value());

			w = xRealToPixel(xMax) - x1;

			areaSum += (w / XSCALE) * ((realy1 + realy2) / 2.0);
		
			xPoints[0] = x1;
			xPoints[1] = xRealToPixel(xMax);
			xPoints[2] = xRealToPixel(xMax);
			xPoints[3] = x1;

			yPoints[0] = tempy1;
			yPoints[1] = tempy2;
			yPoints[2] = yRealToPixel(0);
			yPoints[3] = yRealToPixel(0);
			
			g.setColor(Color.darkGray);
			
			g.drawLine(x1,tempy1,xRealToPixel(xMax),tempy2);
			g.drawLine(560,tempy2,xRealToPixel(xMax),yRealToPixel(0));
			

			xPoints[0] = x1+2;
			xPoints[1] = xRealToPixel(xMax)-2;
			xPoints[2] = xRealToPixel(xMax)-2;
			xPoints[3] = x1+2;


			g.setColor(Color.yellow);
			g.fillPolygon(xPoints,yPoints,4);
		}

	}


	public void drawQuadratics(Graphics g,int dx)
	{
		int x1,y1,x2,y2,x3,y3,w=0,h=0,delta,tempy1,tempy2, tempy3;
		int[] xPoints = new int[5];
		int[] yPoints = new int[5];
		double realy1,realy2,realy3;
		
  		areaSum = 0;
		
		for(x1 = xRealToPixel(xMin),x2 = x1 + dx; x2 <= xRealToPixel(xMax); x2 += dx )
		{

			if( x1+2*dx <= xRealToPixel(xMax) ) // only enter when x3 <= 560
			{

				xvar.set_value(xPixelToReal(x1));
				realy1 = expr.value();
				y1 = (int)yRealToPixel(expr.value());

				xvar.set_value(xPixelToReal(x2));
				realy2 = expr.value();
				y2 = (int)yRealToPixel(expr.value());
				
				x3 = x1 + 2*dx;
				xvar.set_value(xPixelToReal(x3));
				realy3 = expr.value();
				y3 = (int)yRealToPixel(expr.value());
				
				areaSum += (dx / XSCALE) * ((realy1 + 4 * realy2 + realy3) / 3.0);
		 		
		 		if(dx>5)
		 		{
					g.setColor(Color.black);
					tempy1 = y1;
					
					for(int i= x1 ; i < x3 ; i++)
					{
						// plotting quadratic top
						tempy2 = y3 * ((i+1-x1)*(i+1-x2))/((x3-x1)*(x3-x2)) + y2 * ((i+1-x1)*(i+1-x3))/((x2-x1)*(x2-x3)) + y1 * ((i+1-x3)*(i+1-x2))/((x1-x3)*(x1-x2));
						g.drawLine(i,tempy1,i+1,tempy2);
						tempy1 = tempy2;
			 		}
			 		
				}

				g.setColor(Color.darkGray);
				
				g.drawLine(x1,y1,x1,yRealToPixel(0));
				
				if (y2 < yRealToPixel(0) )
				{
					for(int i = y2; i < yRealToPixel(0)-2; i+=8)
					{
						g.drawLine(x2,i,x2,i+4);
					}						
				}
				else
				{
					for(int i = yRealToPixel(0); i < y2-2; i+=8)
					{
						g.drawLine(x2,i,x2,i+4);
					}						
					
				}
				
				g.drawLine(x3,y3,x3,yRealToPixel(0));
			

			}
			x1 += 2*dx;
			x2 += dx;
		}
		
		// xMax quadratic topped rect

		if(x1 > xRealToPixel(xMax))
		{
			x1 -= 2*dx;
		}

		w = (int)((xRealToPixel(xMax) - x1)/2.0);
		x2 = x1 + w;
		x3 = x2 + w;
		
		xvar.set_value(xPixelToReal(x1));
		realy1 = expr.value();
		y1 = (int)yRealToPixel(expr.value());

		xvar.set_value(xPixelToReal(x2));
		realy2 = expr.value();
		y2 = (int)yRealToPixel(expr.value());
		
		xvar.set_value(xPixelToReal(x3));
		realy3 = expr.value();
		y3 = (int)yRealToPixel(expr.value());
		
		areaSum += (w / XSCALE) * ((realy1 + 4 * realy2 + realy3) / 3.0);
		
		
		g.setColor(Color.black);
		tempy1 = y1;
		
		for(int i= x1 ; i < x3 ; i++)
		{
			// plotting quadratic top
			tempy2 = y3 * ((i+1-x1)*(i+1-x2))/((x3-x1)*(x3-x2)) + y2 * ((i+1-x1)*(i+1-x3))/((x2-x1)*(x2-x3)) + y1 * ((i+1-x3)*(i+1-x2))/((x1-x3)*(x1-x2));
			g.drawLine(i,tempy1,i+1,tempy2);
			tempy1 = tempy2;
 		}

		g.setColor(Color.darkGray);
		
		g.drawLine(x1,y1,x1,yRealToPixel(0));
		
		for(int i = Math.min(y2,yRealToPixel(0)); i < Math.max(y2,yRealToPixel(0))-2; i+=8)
		{
			g.drawLine(x2,i,x2,i+4);
		}
		
		g.drawLine(x3,y3,x3,yRealToPixel(0));
	}
		
		
	public void establishScale(double f, double l)
	{
		xMin = f;
		xMax = l;
		determineYMinYMax();
		//System.out.println("xMin="+f+"   xMax ="+l+"ymin ="+yMin+"yMax ="+yMax);
	}
	

	public double value(double x)
	{
		xvar.set_value(x);
		return expr.value();
	}
			
} // end of ZFunction Class