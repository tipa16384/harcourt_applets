// Decompiled by Jad v1.5.5.3. Copyright 1997-98 Pavel Kouznetsov.
// Jad home page:      http://web.unicom.com.cy/~kpd/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Graph.java

package Graph;

import java.awt.*;
import java.util.Vector;

// Referenced classes of package ckc.awt:
//            DataPoint, DataSet, StringUtil

public class Graph extends Panel implements DataListener
{
	    public	int xBorder;
	    public	int yBorder;
	    public	int gWidth;
	    public	int gHeight;
	    public	int pWidth;
	    public	int pHeight;
	    public	int eastBorder;
	    public	int northBorder;
	    String	xLabel;
	    String	yLabel;
	    public	double xOrigin;
	    public	double yOrigin;
	    public	double xMax;
	    public	double yMax;
	    double	xMajTicks;
	    double	xMinTicks;
	    double	yMajTicks;
	    double	yMinTicks;
	    public	boolean xMajNum;
	    public	boolean xMinNum;
	    public	boolean yMajNum;
	    public	boolean yMinNum;
	    public	boolean xMajLines;
	    public	boolean yMajLines;
	    public	int xPrec;
	    public	int yPrec;
	    public	Font labelFont;
	    public	Font numFont;
	    public	Vector dataSets;
	    int		numDataPoints;
	    boolean changed;
	    Thread	me;

    public Graph()
    {
        setDefaults();
        dataSets = new Vector();
        me = null;
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

        paint(getGraphics());
    }

    	private static boolean impainting = false;			//sync only one call needed

    public void paint(Graphics g)
    {
    	if( !impainting )
    	{
    		impainting = true;
	       	g.setColor(getBackground());
	        g.fillRect(0, 0, size().width, size().height);
	        update(g);
    		impainting = false;
        }
    }

    public void removeAllData()
    {
        dataSets.removeAllElements();
        paint(getGraphics());
    }

    public void setBounds(int x, int y)
    {
        xMax = x;
        yMax = y;
        changed = true;
    }

    private void setDefaults()
    {
        xBorder = yBorder = 10;
        eastBorder = northBorder = 10;
        xLabel = "X";
        yLabel = "Y";
        xOrigin = yOrigin = 0.0D;
        xMax = yMax = 100D;
        xMajTicks = 10D;
        xMinTicks = 5D;
        yMajTicks = 10D;
        yMinTicks = 5D;
        xMajLines = yMajLines = xMajNum = yMajNum = true;
        xMinNum = yMinNum = false;
        xPrec = yPrec = 1;
        labelFont = new Font("Geneva", 1, 12);
        numFont = new Font("Geneva", 0, 10);
        gHeight = gWidth = -1;
        pHeight = pWidth = 4 * xBorder;
        changed = true;
    }

    public void setLabelFont(Font f)
    {
        labelFont = f;
    }

    public void setHLabel(String h)
    {
        xLabel = h;
    }

    public void setVLabel(String v)
    {
        yLabel = v;		//would be nice if StringUtil.verticalizeText(v); worked but doesn't
    }

    public void setLabels(String x, String y)
    {
        xLabel = x;
        yLabel = y;		//would be nice if StringUtil.verticalizeText(y); worked but doesn't
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

    public void setXMajTicks(double value)
    {
        xMajTicks = value;
    }

    public void setXMinTicks(double value)
    {
        xMinTicks = value;
    }

    public void update(Graphics g)
    {
        int triX[] = new int[4];
        int triY[] = new int[4];
        //if(gHeight < 0)
        {
            FontMetrics fm = g.getFontMetrics(labelFont);
            pHeight = size().height;
            pWidth = size().width;
            if(pHeight < xBorder || pWidth < yBorder)
                return;
            xBorder = fm.getHeight() + 5;
            if(yLabel != null)
                yBorder = fm.stringWidth(yLabel) + 8;
            fm = g.getFontMetrics(numFont);
            if(xMajNum || xMinNum)
                xBorder += fm.getHeight() + 5;
            if(yMajNum || yMinNum)
                yBorder += fm.stringWidth("0.00") + 5;
            gHeight = pHeight - xBorder - northBorder;
            gWidth = pWidth - yBorder - eastBorder;
        }
        double xFactor = (double)gWidth / (xMax - xOrigin);
        double yFactor = (double)gHeight / (yMax - yOrigin);
        g.setColor(Color.black);
        Color oldColor = g.getColor();
        g.setColor(Color.red);
        int width = size().width - eastBorder - yBorder;
        int height = size().height - northBorder - xBorder;
        g.drawRect(yBorder, 10, width, height);
        if(yMax > 0.0D && yOrigin < 0.0D)
        {
            double h = yFactor * yMax;
            g.drawLine(yBorder, (int)Math.round(10D + h), yBorder + width, (int)Math.round(10D + h));
        }
        if(xMax > 0.0D && xOrigin < 0.0D)
        {
            double w = Math.abs(xFactor * xOrigin);
            g.drawLine((int)Math.round((double)yBorder + w), 10 + height, (int)Math.round((double)yBorder + w), 10);
        }
        g.setColor(oldColor);
        g.setFont(numFont);
        if(yMajTicks > 0.0D)
        {
            for(double dy = yMajTicks; dy <= yMax - yOrigin; dy += yMajTicks)
            {
                if(dy < yMax - yOrigin)
                {
                	if( yMajLines )
	                    g.drawLine(yBorder - 4, (gHeight + northBorder) - (int)(yFactor * dy), (size().width - eastBorder) + 4, (gHeight + northBorder) - (int)(yFactor * dy));
	                    //yBorder + 4, (gHeight + northBorder) - (int)(yFactor * dy));
	                    //g.drawLine(size().width - eastBorder - 4, (gHeight + northBorder) - (int)(yFactor * dy), (size().width - eastBorder) + 4, (gHeight + northBorder) - (int)(yFactor * dy));
                	else
                	{
	                    g.drawLine(yBorder - 4, (gHeight + northBorder) - (int)(yFactor * dy), yBorder + 4, (gHeight + northBorder) - (int)(yFactor * dy));
	                    g.drawLine(size().width - eastBorder - 4, (gHeight + northBorder) - (int)(yFactor * dy), (size().width - eastBorder) + 4, (gHeight + northBorder) - (int)(yFactor * dy));
                    }
                }
                if(yMajNum)
                    StringUtil.drawString(StringUtil.valueOfWithPrecision(dy + yOrigin, yPrec), g, new Point(yBorder - 5, (gHeight + northBorder) - (int)(yFactor * dy)), 1);
            }

        }
        if(yMinTicks > 0.0D)
        {
            for(double dy = yMinTicks; dy <= yMax - yOrigin; dy += yMinTicks)
            {
                if(dy < yMax - yOrigin)
                {
                    g.drawLine(yBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), yBorder + 2, (gHeight + northBorder) - (int)(yFactor * dy));
                    g.drawLine(size().width - eastBorder - 2, (gHeight + northBorder) - (int)(yFactor * dy), (size().width - eastBorder) + 2, (gHeight + northBorder) - (int)(yFactor * dy));
                }
                if(yMinNum)
                    StringUtil.drawString(StringUtil.valueOfWithPrecision(dy + yOrigin, yPrec), g, new Point(yBorder - 5, gHeight - (int)(yFactor * dy)), 1);
            }

        }
        if(yLabel != null)
        {
            FontMetrics fm = g.getFontMetrics(numFont);
            int labY = gHeight / 2;
            int labX = yBorder - 5;
            if(yMajNum || yMinNum)
                labX -= fm.stringWidth("000") + 5;
            g.setFont(labelFont);
            StringUtil.drawString(yLabel, g, new Point(labX, labY), 1);
        }
        g.setFont(numFont);
        if(xMajTicks > 0.0D)
        {
            for(double dx = xMajTicks; dx <= xMax - xOrigin; dx += xMajTicks)
            {
                if(dx < xMax - xOrigin)
                	if( xMajLines )
                    	g.drawLine(yBorder + (int)(xFactor * dx), northBorder, yBorder + (int)(xFactor * dx), gHeight + northBorder + 4);
                	else
                    	g.drawLine(yBorder + (int)(xFactor * dx), (gHeight + northBorder) - 4, yBorder + (int)(xFactor * dx), gHeight + northBorder + 4);
                if(xMajNum)
                    StringUtil.drawString(StringUtil.valueOfWithPrecision(dx + xOrigin, xPrec), g, new Point(yBorder + (int)(xFactor * dx), gHeight + northBorder + 5), 7);
            }

        }
        if(xMinTicks > 0.0D)
        {
            for(double dx = xMinTicks; dx <= xMax - xOrigin; dx += xMinTicks)
            {
                if(dx < xMax - xOrigin)
                    g.drawLine(yBorder + (int)(xFactor * dx), (gHeight + northBorder) - 2, yBorder + (int)(xFactor * dx), gHeight + northBorder + 2);
                if(xMinNum)
                    StringUtil.drawString(StringUtil.valueOfWithPrecision(dx + xOrigin, xPrec), g, new Point(yBorder + (int)(xFactor * dx), gHeight + northBorder + 5), 7);
            }

        }
        if(xLabel != null)
        {
            FontMetrics fm = g.getFontMetrics(numFont);
            int labY = gHeight + northBorder;
            if(xMajNum || xMinNum)
                labY += fm.getHeight() + 5;
            int labX = yBorder + gWidth / 2;
            g.setFont(labelFont);
            StringUtil.drawString(xLabel, g, new Point(labX, labY), 7);
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
                if(dx <= xMax && dy <= yMax)
                {
                    int x = yBorder + (int)((dx - xOrigin) * xFactor);
                    int y = (gHeight + northBorder) - (int)((dy - yOrigin) * yFactor);
                    if(ds.symType == 0)
                        g.fillOval(x - ds.symSize, y - ds.symSize, ds.symSize * 2, ds.symSize * 3);
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

        }

    }

	public void processDataEvent( DataEvent e )
	{
		if( e.getID() == DataEvent.UPDATE )
		{
			//System.out.print(" processDataEvent "+e);
			//repaint();				//repaint doesn't call our paint
			paint(getGraphics());
		}
	}
}
