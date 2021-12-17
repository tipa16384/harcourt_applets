package Graph;

// Source File Name:   StringUtil.java

import java.awt.*;

public abstract class StringUtil
{
					//The XY point given will be used for justifying 
					// just like a phone keypad (or your keyboard's numpad)
							//BTW 0==centered
    public static final int BOTTOM_LEFT = 1;
    public static final int BOTTOM_CENTER = 2;
    public static final int BOTTOM_RIGHT = 3;
    public static final int LEFT = 4;
    public static final int CENTER = 5;
    public static final int RIGHT = 6;
    public static final int TOP_LEFT = 7;
    public static final int TOP_CENTER = 8;
    public static final int TOP_RIGHT = 9;
    
    public static boolean noPostE = false;

    public static void drawString(String s, Graphics g, Point p, int just)
    {
        drawString(s, g, p, just, null, null);
    }

    public static void drawString(String s, Graphics g, Point p, int just, Color fg, Color bg)
    {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        int txtWidth = fm.stringWidth(s);
        int txtHeight = fm.getHeight();
        int x;
        if(just == LEFT || just == BOTTOM_LEFT || just == TOP_LEFT)
            x = p.x;
        else
        if(just == RIGHT || just == BOTTOM_RIGHT || just == TOP_RIGHT)
            x = p.x - txtWidth;
        else								//any of the centered X
            x = p.x - txtWidth / 2;
        int y;
        if(just == BOTTOM_LEFT || just == BOTTOM_CENTER || just == BOTTOM_RIGHT)
            y = p.y;
        else
        if(just == TOP_LEFT || just == TOP_CENTER || just == TOP_RIGHT)
            y = p.y + txtHeight;
        else								//any of the middle Y
            y = p.y + txtHeight / 2;

        if(fg != null)
            g.setColor(fg);
        if(bg != null)
        {
            if(fg == null)
                fg = g.getColor();
            g.setColor(bg);
            g.fillRect(x - 3, y - fm.getAscent(), txtWidth + 6, txtHeight);
            g.setColor(fg);
        }
        g.drawString(s, x, y);
    }

    public static String engValueOfWithPrecision(double d, int p)
    {
    	return( engValueOfWithPrecision( d, p, 0 ) );
    }

    
    public static String engValueOfWithPrecision(double d, int p, int magnitude)
    {
		boolean old_noPostE = noPostE;
		//noPostE = true;
		if( magnitude != 0 )
			d = d / Math.pow(10,magnitude);
		//System.out.println("engineering value of org=="+d);
		String s = valueOfWithPrecision(d, p);
		noPostE = old_noPostE;
		return( s );
    }

    public static String valueOfWithPrecision(double d, int p)
    {
        String orig = String.valueOf((float)d);
        int eIndex = orig.indexOf('e');
        if(eIndex == -1)
            eIndex = orig.indexOf('E');
        String origPreE;
        String origPostE;
        if(eIndex == -1)
        {
            origPreE = orig;
            origPostE = "";
        }
        else
        {
            try
            {
                origPreE = orig.substring(0, eIndex);
                origPostE = orig.substring(eIndex, orig.length());
            }
            catch(StringIndexOutOfBoundsException ex)
            {
                origPreE = orig;
                origPostE = "";
            }
        }
        if( noPostE )				//caller doesn't want E-9 string
            origPostE = "";
        int i = origPreE.indexOf(".");
        String result;
        if(i == -1)
        {
            result = new String(origPreE);
            i = origPreE.length();
        }
        else
        {
            result = origPreE.substring(0, i);
        }
        //System.out.println("origPreE=="+origPreE+" origPostE=="+origPostE);
        if(p == 0)
            return result + origPostE;
        i++;
        result = result.concat(".");
        for(int j = 0; j < p;)
        {
            if(i >= origPreE.length())
                result = result.concat("0");
            else
                result = result.concat(origPreE.substring(i, i + 1));
            j++;
            i++;
        }

        return result + origPostE;
    }

    public static String verticalizeText(String s)
    {
        String vertStr = "";
        for(int index = 0; index < s.length(); index++)
            vertStr = vertStr + s.charAt(index) + "\n";

        return vertStr;
    }

    public StringUtil()
    {
    }

}
