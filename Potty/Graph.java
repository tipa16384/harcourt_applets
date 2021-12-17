// Decompiled by Jad v1.5.5.3. Copyright 1997-98 Pavel Kouznetsov.
// Jad home page:      http://web.unicom.com.cy/~kpd/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Graph.java

package Graph;

import java.awt.*;
import java.util.Vector;
import RawLabel;

// Referenced classes of package ckc.awt:
//            DataPoint, DataSet, StringUtil

public class Graph extends Component implements DataListener
{
	static final boolean debug = false;
			//Here are the internal variables
			// use setDefaults() to set these to your favorite default values
			// most are publics so the values can change to how each graph presents itself
		public	boolean xLabelCentered;		//xlabel==false will print off right side of graph (taking up no bottom space)
		public	boolean yLabelCentered;		//ylabel not done yet
	    public	boolean xMaxMinNum;			//will show origin and max numbers of x axis
	    public	boolean yMaxMinNum;			//will show origin and max numbers of y axis
		public	Color	axisColor;
		public	boolean	autorange;			//whether we allow autoranging (broken)
	    public	boolean xMajNum;
	    public	boolean xMinNum;
	    public	boolean yMajNum;
	    public	boolean yMinNum;
	    public	boolean xMajLines;
	    public	boolean yMajLines;
	    public	boolean	box_graph;			//True==full rectangle graph box, False==L shaped axis' only
		public	boolean	full_ticks;			//True==above+below axis ticks, False==below only
		public	boolean	xZeroLine;			//True==draw full graph line at value zero, False==ticks only
		public	boolean	yZeroLine;			//True==draw full graph line at value zero, False==ticks only

	    public	int xBorder, defxBorder;
	    public	int yBorder, defyBorder;
	    public	int gWidth;
	    public	int gHeight;
	    public	int pWidth;
	    public	int pHeight;
	    public	int eastBorder, defeastBorder;
	    public	int northBorder, defnorthBorder;
	    public	double xOrigin;
	    public	double yOrigin;
	    public	double xMax;
	    public	double yMax;
	    public	int xPrec;
	    public	int yPrec;
	    public	int	xMag;					//the magnitude of the units
	    public	int	yMag;
	    public	Font labelFont;
	    public	Font numFont;
	    public	Vector dataSets;
	    public  RawLabel specialTextPopup = null;	//special rawlabel that will appear when data matches

	    RawLabel	xLabel;
	    RawLabel	yLabel;
	    //String	xLabel;
	    //String	yLabel;
	    double	xMajTicks;
	    double	xMinTicks;
	    double	yMajTicks;
	    double	yMinTicks;
	    int		numDataPoints;
	    boolean changed;
	    Thread	me;
	    
    public Graph()
    {
        setDefaults();
        dataSets = new Vector();
        me = null;
    }

		/*
		 *  Set all the default values for any new graph object
		 *    you should change all these to fit you default fancy.
		 *  each graph can/will have their individual traits changed by sets or publics
		 */
    private void setDefaults()
    {
        defxBorder = defyBorder = 7;
        defeastBorder = defnorthBorder = 4;
        setHLabel( "X" );
        setVLabel( "Y" );
        xOrigin = yOrigin = 0.0D;
        xMax = yMax = 100D;
        xMajTicks = 10D;
        xMinTicks = 5D;
        yMajTicks = 10D;
        yMinTicks = 5D;
        xMajLines = yMajLines = xMajNum = yMajNum = false;
        xMinNum = yMinNum = false;
        xPrec = yPrec = 1;
        labelFont = new Font("SanSerif", Font.BOLD, 11);
        numFont = new Font("SanSerif", Font.PLAIN, 10);
        gHeight = gWidth = -1;
        pHeight = pWidth = 4 * xBorder;

		box_graph = false;			//only show the XY axis
		full_ticks = false;			//only show axis ticks outside of graph
		xLabelCentered = false;		//xlabel==false will print off right side of graph (taking up no bottom space)
		yLabelCentered = false;		//ylabel not done yet
	    xMaxMinNum = true;			//will show origin and max numbers of x axis
	    yMaxMinNum = true;			//will show origin and max numbers of y axis
		axisColor = new Color(102,102,102);
		autorange = false;			//whether we allow autoranging (broken)

        changed = true;
    }

    public void addDataSet(DataSet ds)
    {
        dataSets.addElement(ds);
        ds.addDataListener( this );
    }

    public void clear()
    {
        int num = dataSets.size();
        for(int i = 0; i < num; i++)
        {
            DataSet ds = (DataSet)dataSets.elementAt(i);
            ds.removeAllElements();
        }

        if( specialTextPopup != null )
        	specialTextPopup = null;

        paint(getGraphics());
    }

    	private static boolean impainting = false;			//sync only one call needed

    public void paint(Graphics g)
    {
    	if( !impainting )
    	{
    		impainting = true;
        	changed = false;
			
			if( g != null )
			{
		       	g.setColor(getBackground());
		        g.fillRect(0, 0, size().width, size().height);
		        update(g);
			}
			
    		impainting = false;
        }
        if( changed )
        {
        	if( g != null )
        		paint( g );
        }
    }

    public void removeAllData()
    {
        dataSets.removeAllElements();
        //paint(getGraphics());
    }

    public void setBounds(int x, int y)
    {
        xMax = x;
        yMax = y;
        changed = true;
    }

    public void setLabelFont(Font f)
    {
        labelFont = f;
    }

    public void setHLabel(String h)
    {
        //xLabel = h;
        //setFont(labelFont);			//set our font before RawLabel creates
        xLabel = new RawLabel( h, Label.CENTER );
        xLabel.setFont( labelFont );
    }

    public void setVLabel(String v)
    {
        //yLabel = v;		//would be nice if StringUtil.verticalizeText(v); worked but doesn't
        //setFont(labelFont);			//set our font before RawLabel creates
        debug("setFont(labelFont)");
        yLabel = new RawLabel( v, Label.CENTER );
        yLabel.setFont( labelFont );
    }

    public void setLabels(String x, String y)
    {
        //xLabel = x;
        //yLabel = y;		//would be nice if StringUtil.verticalizeText(y); worked but doesn't
        setHLabel( x );
        setVLabel( y );
    }

    public void setNumFont(Font f)
    {
        numFont = f;
    }

    public void setSize(int w, int h)
    {
        pWidth = w;
        pHeight = h;
        changed = true;
    }

    public void setXAxis(double min, double max, double majorTicSpace, double minorTicSpace)
    {
        xOrigin = min;
        xMax = max;
        xMajTicks = majorTicSpace;
        xMinTicks = minorTicSpace;
    }

    public void setYAxis(double min, double max, double majorTicSpace, double minorTicSpace)
    {
        yOrigin = min;
        yMax = max;
        yMajTicks = majorTicSpace;
        yMinTicks = minorTicSpace;
    }

    public void setXAxis(double min, double max, double majorTicSpace, double minorTicSpace, int mag)
    {
        xOrigin = min;
        xMax = max;
        xMajTicks = majorTicSpace;
        xMinTicks = minorTicSpace;
        xMag = mag;
    }

    public void setYAxis(double min, double max, double majorTicSpace, double minorTicSpace, int mag)
    {
        yOrigin = min;
        yMax = max;
        yMajTicks = majorTicSpace;
        yMinTicks = minorTicSpace;
        yMag = mag;
    }

    public void autoXAxis( double min, double max )
    {
    		double dt;
        if( xMajTicks > 0.0D )
        {
        	dt = Math.round((xMax - xOrigin) / xMajTicks);
        	if( dt != 0.0D )
	        	xMajTicks = (max - min) / dt;
        }
        if( xMinTicks > 0.0D )
        {
        	dt = Math.round((xMax - xOrigin) / xMinTicks);
        	if( dt != 0.0D )
	        	xMinTicks = (max - min) / dt;
        }
        xOrigin = min;
        xMax = max;
        changed = true;				//tell paint to do it again.
    }

    public void autoYAxis( double min, double max )
    {
    		double dt;
        if( yMajTicks > 0.0D )
        {
        	dt = Math.round((yMax - yOrigin) / yMajTicks);
        	if( dt != 0.0D )
	        	yMajTicks = (max - min) / dt;
        }
        if( yMinTicks > 0.0D )
        {
        	dt = Math.round((yMax - yOrigin) / yMinTicks);
        	if( dt != 0.0D )
	        	yMinTicks = (max - min) / dt;
        }
        yOrigin = min;
        yMax = max;
        changed = true;				//tell paint to do it again.
    }

    public void setXMajTicks(double value)
    {
        xMajTicks = value;
    }

    public void setXMinTicks(double value)
    {
        xMinTicks = value;
    }

        static Dimension strSize = null;		//Beware this var is used as boolean for 'needs to be erased'
       	static int sX = 0;						// and these vars have to live between state changes
       	static int sY = 0;

    public void update(Graphics g)
    {
        int triX[] = new int[4];
        int triY[] = new int[4];
        if( (gHeight < 0) || (pHeight != size().height) || (pWidth != size().width) )
        {
        	xBorder = defxBorder;
        	yBorder = defyBorder;
        	eastBorder = defeastBorder;
        	northBorder = defnorthBorder;
        	FontMetrics fm = g.getFontMetrics(labelFont);
            pHeight = size().height;
            pWidth = size().width;
            if(pHeight < xBorder || pWidth < yBorder)
                return;
            if(xLabel != null)
            	if(xLabelCentered)
		            xBorder = fm.getHeight() + 5;
		    	else
		            //eastBorder += fm.stringWidth(xLabel) + 8;
	                eastBorder = xLabel.getPreferredSize().width + 8;
            if(yLabel != null)
            	if(yLabelCentered)
	                //yBorder = fm.stringWidth(yLabel) + 8;
	                yBorder = yLabel.getPreferredSize().width + 8;
		    	else
		            northBorder += fm.getHeight() + 4;
            fm = g.getFontMetrics(numFont);
            if(xMajNum || xMinNum || xMaxMinNum)
                xBorder += fm.getHeight() + 5;
            if(yMajNum || yMinNum || yMaxMinNum)
            {
                yBorder += fm.stringWidth(StringUtil.engValueOfWithPrecision(yMax, yPrec, yMag)) + 5;
            }
            gHeight = pHeight - xBorder - northBorder;
            gWidth = pWidth - yBorder - eastBorder;
        }
        double xFactor = (double)gWidth / (xMax - xOrigin);
        double yFactor = (double)gHeight / (yMax - yOrigin);
        g.setColor(Color.black);
        Color oldColor = g.getColor();
        g.setColor(axisColor);
        int width = size().width - eastBorder - yBorder;
        int height = size().height - northBorder - xBorder;

		if( box_graph )
			g.drawRect(yBorder, northBorder, width, height);
		else
		{
			g.drawLine(yBorder-2, northBorder+height, yBorder+width, northBorder+height);
			g.drawLine(yBorder, northBorder, yBorder, northBorder+height+2);
		}
        g.setColor(oldColor);
        g.setFont(numFont);
        if(yMax > 0.0D && yOrigin < 0.0D)
        {
            double h = yFactor * yMax;
            if( yZeroLine )
	            g.drawLine(yBorder, (int)Math.round(northBorder + h), yBorder + width, (int)Math.round(northBorder + h));
	        else
	        {
	            g.drawLine(yBorder-2, (int)Math.round(northBorder + h), yBorder + (full_ticks ? 2 : 0), (int)Math.round(northBorder + h));
	            if( box_graph )
		            g.drawLine(size().width - eastBorder - (full_ticks ? 2 : 0), (int)Math.round(northBorder + h), size().width - eastBorder + 2, (int)Math.round(northBorder + h));
	        }
            if( (yMajTicks > 0.0D) || yMaxMinNum )
            {
                StringUtil.drawString("0.0", g, new Point(yBorder-5, (int)Math.round(northBorder + h)), StringUtil.RIGHT);
		        //g.setFont(numFont);
            }
        }
        if(xMax > 0.0D && xOrigin < 0.0D)
        {
            double w = Math.abs(xFactor * xOrigin);
            if( xZeroLine )
	            g.drawLine((int)Math.round((double)yBorder + w), northBorder + height, (int)Math.round((double)yBorder + w), northBorder);
	        else
	        {
	            g.drawLine((int)Math.round((double)yBorder + w), northBorder + height + 2, (int)Math.round((double)yBorder + w), northBorder + height - (full_ticks ? 2 : 0));
	            if( box_graph )
		            g.drawLine((int)Math.round((double)yBorder + w), northBorder - 2, (int)Math.round((double)yBorder + w), northBorder + (full_ticks ? 2 : 0));
	        }
            if( (xMajTicks > 0.0D) || xMaxMinNum )
                StringUtil.drawString("0.0", g, new Point((int)Math.round((double)yBorder + w), (gHeight + northBorder + 6)), StringUtil.CENTER);
        }
        if(yMaxMinNum)
        {
            StringUtil.drawString(StringUtil.engValueOfWithPrecision(yOrigin, ((Math.abs(yOrigin) > 100) ? 0 : yPrec), yMag), g, new Point(yBorder - 5, (gHeight + northBorder)), StringUtil.BOTTOM_RIGHT);
            StringUtil.drawString(StringUtil.engValueOfWithPrecision(yMax, ((yMax > 100) ? 0 : yPrec), yMag), g, new Point(yBorder - 5, northBorder), StringUtil.RIGHT);
           	g.drawLine(yBorder - 2, northBorder, yBorder + (full_ticks ? 2 : 0), northBorder);
        }
        if(yMajTicks > 0.0D)
        {
            for(double dy = yMajTicks; dy <= yMax - yOrigin; dy += yMajTicks)
            {
                if(dy < yMax - yOrigin)
                {
                	if( yMajLines )
	                    g.drawLine(yBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), (size().width - eastBorder) + 2, (gHeight + northBorder) - (int)(yFactor * dy));
	                    //yBorder + 2, (gHeight + northBorder) - (int)(yFactor * dy));
	                    //g.drawLine(size().width - eastBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), (size().width - eastBorder) + 2, (gHeight + northBorder) - (int)(yFactor * dy));
                	else
                	{
	                    if( full_ticks )
	                    	g.drawLine(yBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), yBorder + 2, (gHeight + northBorder) - (int)(yFactor * dy));
	                    else
	                    	g.drawLine(yBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), yBorder, (gHeight + northBorder) - (int)(yFactor * dy));
	                    if( box_graph )
	                    	g.drawLine(size().width - eastBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), (size().width - eastBorder) + 2, (gHeight + northBorder) - (int)(yFactor * dy));
                    }
                }
                if(yMajNum)
                    StringUtil.drawString(StringUtil.engValueOfWithPrecision(dy + yOrigin, yPrec, yMag), g, new Point(yBorder - 5, (gHeight + northBorder) - (int)(yFactor * dy)), StringUtil.RIGHT);
                //debug("in yMajTicks  yMajNum=="+yMajNum);
            }
        }
        if(yMinTicks > 0.0D)
        {
            for(double dy = yMinTicks; dy <= yMax - yOrigin; dy += yMinTicks)
            {
                if(dy < yMax - yOrigin)
                {
                    g.drawLine(yBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), yBorder + 2, (gHeight + northBorder) - (int)(yFactor * dy));
                    if( box_graph )
	                    g.drawLine(size().width - eastBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), (size().width - eastBorder) + 2, (gHeight + northBorder) - (int)(yFactor * dy));
                }
                if(yMinNum)
                    StringUtil.drawString(StringUtil.engValueOfWithPrecision(dy + yOrigin, yPrec, yMag), g, new Point(yBorder - 5, gHeight - (int)(yFactor * dy)), StringUtil.RIGHT);
            }

        }
        if(yLabel != null)
        {
        	int labX;
        	int labY;
            FontMetrics fm = g.getFontMetrics(numFont);
            if( yLabelCentered )
            {
	            labY = gHeight / 2;
    	        labX = yBorder - 5;
	            if(yMajNum || yMinNum)
    	            labX -= fm.stringWidth("0000") + 5;
    	    }
            else
            {
	            //labY = northBorder - 4;
	            labY = northBorder - fm.getHeight() - 4;
            	labX = yBorder - (yLabel.getPreferredSize().width / 2);	//center this string
	        }
            g.setFont(labelFont);
            //StringUtil.drawString(yLabel, g, new Point(labX, labY), StringUtil.BOTTOM_CENTER);
            yLabel.paint( g, labX, labY, false );
        }
        g.setFont(numFont);
        if(xMaxMinNum)
        {
            StringUtil.drawString(StringUtil.engValueOfWithPrecision(xOrigin, xPrec, xMag), g, new Point(yBorder, (gHeight + northBorder + 6)), StringUtil.CENTER);
            StringUtil.drawString(StringUtil.engValueOfWithPrecision(xMax, xPrec, xMag), g, new Point(yBorder + gWidth + 5, (gHeight + northBorder + 6)), StringUtil.RIGHT);
           	g.drawLine(yBorder + gWidth, (gHeight + northBorder) + 2, yBorder + gWidth, gHeight + northBorder - (full_ticks ? 2 : 0));
        }
        if(xMajTicks > 0.0D)
        {
            for(double dx = xMajTicks; dx <= xMax - xOrigin; dx += xMajTicks)
            {
                if(dx < xMax - xOrigin)
                	if( xMajLines )
                    	g.drawLine(yBorder + (int)(xFactor * dx), northBorder, yBorder + (int)(xFactor * dx), gHeight + northBorder + 2);
                	else
                    	g.drawLine(yBorder + (int)(xFactor * dx), (gHeight + northBorder) - 2, yBorder + (int)(xFactor * dx), gHeight + northBorder + 2);
                if(xMajNum)
                    StringUtil.drawString(StringUtil.engValueOfWithPrecision(dx + xOrigin, xPrec, xMag), g, new Point(yBorder + (int)(xFactor * dx), gHeight + northBorder + 5), StringUtil.TOP_CENTER);
            }

        }
        if(xMinTicks > 0.0D)
        {
            for(double dx = xMinTicks; dx <= xMax - xOrigin; dx += xMinTicks)
            {
                if(dx < xMax - xOrigin)
                    g.drawLine(yBorder + (int)(xFactor * dx), (gHeight + northBorder) - 2, yBorder + (int)(xFactor * dx), gHeight + northBorder + 2);
                if(xMinNum)
                    StringUtil.drawString(StringUtil.engValueOfWithPrecision(dx + xOrigin, xPrec, xMag), g, new Point(yBorder + (int)(xFactor * dx), gHeight + northBorder + 5), StringUtil.TOP_CENTER);
            }

        }
        if(xLabel != null)
        {
        	int labX;
        	int labY;
            FontMetrics fm = g.getFontMetrics(numFont);
            if( xLabelCentered )
            {
            	labX = yBorder + gWidth / 2;
	            labY = gHeight + northBorder;
	        }
            else
            {
            	labX = yBorder + gWidth + 5;
	            //labY = gHeight + northBorder;
	            labY = gHeight + northBorder - fm.getAscent();
	        }
            if(xMajNum || xMinNum)
                labY += fm.getHeight() + 5;
            g.setFont(labelFont);
            //StringUtil.drawString(xLabel, g, new Point(labX, labY), StringUtil.BOTTOM_LEFT);
            xLabel.paint( g, labX, labY, false );
        }
        int lastX = 0;
        int lastY = 0;
        int numDataSets = dataSets.size();
        for(int i = 0; i < numDataSets; i++)
        {
            DataSet ds = (DataSet)dataSets.elementAt(i);
            g.setColor(ds.symColor);
            int numDataPoints = ds.size();
            boolean prevPoint = false;
            for(int j = 0; j < numDataPoints; j++)
            {
                double dx = ((DataPoint)ds.elementAt(j)).x;
                double dy = ((DataPoint)ds.elementAt(j)).y;
                //debug("dx="+dx+" dy="+dy);
                //was if(dx <= xMax && dy <= yMax)  //was, but looks better to max/min values at top/bottom
                if(dx > xMax) {
                	if( autorange )
                		autoXAxis( xOrigin, dx );
                	else
                		dx = xMax;
                } else if( dx < xOrigin) {
                	if( autorange )
                		autoXAxis( dx, xMax );
                	else
	                	dx = xOrigin;
                } else if(dy > yMax) {
                	if( autorange )
                		autoXAxis( yOrigin, dy );
                	else
	               		dy = yMax;
                } else if( dy < yOrigin) {
                	if( autorange )
                		autoXAxis( dy, yMax );
                	else
	                	dy = yOrigin;
                }
                //debug(" now dx="+dx+" dy="+dy+" for xOrigin="+xOrigin+" xMax="+xMax);

                int x = yBorder + (int)((dx - xOrigin) * xFactor);
                int y = (gHeight + northBorder) - (int)((dy - yOrigin) * yFactor);
                if(ds.symType == 0)
                    g.fillOval(x - ds.symSize, y - ds.symSize, ds.symSize * 2, ds.symSize * 2);
                else
                if(ds.symType == 1)
                    g.drawOval(x - ds.symSize, y - ds.symSize, ds.symSize * 2, ds.symSize * 2);
                else
                if(ds.symType == 2)
                {
                    triX[0] = x;
                    triY[0] = y - ds.symSize;
                    triX[1] = x - ds.symSize;
                    triY[1] = y + ds.symSize;
                    triX[2] = x + ds.symSize;
                    triY[2] = y + ds.symSize;
                    g.drawPolygon(triX, triY, 3);
                    g.fillPolygon(triX, triY, 3);
                }
                else
                if(ds.symType == 3)
                {
                    if(prevPoint)
                        g.drawLine(lastX, lastY, x, y);
                    lastX = x;
                    lastY = y;
                    prevPoint = true;
                }
                else
                if(ds.symType == 4)
                {
                    triX[0] = x - ds.symSize;
                    triY[0] = y;
                    triX[1] = x + ds.symSize;
                    triY[1] = y;
                    triX[2] = x + ds.symSize;
                    triY[2] = (int)gHeight + northBorder -1;
                    triX[3] = x - ds.symSize;
                    triY[3] = gHeight + northBorder -1;
                    g.drawPolygon(triX, triY, 4);
                    g.fillPolygon(triX, triY, 4);
                }
            }

        }
        if( specialTextPopup != null )
        {
            Font font = new Font("SanSerif", Font.PLAIN, 9);
            specialTextPopup.setFont(font);
            FontMetrics fm = specialTextPopup.getFontMetrics(font);
            strSize = specialTextPopup.getMinimumSize();
            if( xLabelCentered )
            {
            	sX = yBorder + ((gWidth - strSize.width) / 2);
	            sY = northBorder;; // - fm.getHeight() - 4;
	        }
            else
            {
            	sX = yBorder + gWidth - strSize.width;
	            sY = northBorder; // - fm.getHeight() - 4;
	        }
	        //for a box around text g.setColor(new Color(204,204,204));
	        //for thicker box		g.drawRect( sX-1, sY-1, strSize.width+2, strSize.height+2 );
	        //for a box around text	g.drawRect( sX-2, sY-2, strSize.width+4, strSize.height+4 );
	        specialTextPopup.setBackground(Color.white);
	        g.setColor( Color.white );
	        g.fillRect( sX+3, sY+3, strSize.width-6, strSize.height-4 );
	        //g.setColor(new Color(153,153,153));
	        specialTextPopup.setForeground(new Color(153,153,153));
            specialTextPopup.paint( g, sX, sY, false );
		}
		else if( strSize != null )
		{				//need to erase the old specialTextPopup
			oldColor = g.getColor();
			g.setColor( getBackground() );
	        g.fillRect( sX-2, sY-2, strSize.width+4, strSize.height+4 );
	        g.setColor( oldColor );
	        strSize = null;
		}
    }

	public void processDataEvent( DataEvent e )
	{
		if( e.getID() == DataEvent.UPDATE )
		{
			//debug(" processDataEvent "+e);
			//repaint();				//repaint doesn't call our paint
			paint(getGraphics());
		}
	}

	static void debug( String s )
	{
		if( debug )
		{
			System.out.println("Graph:: "+s);
		}
	}
	
}
