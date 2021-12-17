// Decompiled by Jad v1.5.5.3. Copyright 1997-98 Pavel Kouznetsov.
// Jad home page:      http://web.unicom.com.cy/~kpd/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StringUtil.java

package Graph;

import java.awt.*;

public abstract class StringUtil
{

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
        if(just == 2 || just == 5 || just == 6)
            x = p.x;
        else
        if(just == 1 || just == 3 || just == 4)
            x = p.x - txtWidth;
        else
            x = p.x - txtWidth / 2;
        int y;
        if(just == 4 || just == 6 || just == 8)
            y = p.y;
        else
        if(just == 3 || just == 5 || just == 7)
            y = p.y + txtHeight;
        else
            y = p.y + txtHeight / 2;
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

    public static String valueOfWithPrecision(double d, int p)
    {
        String orig = String.valueOf(d);
        int eIndex = orig.indexOf(101);
        if(eIndex == -1)
            eIndex = orig.indexOf(69);
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

    public static final int CENTER = 0;
    public static final int RIGHT = 1;
    public static final int LEFT = 2;
    public static final int TOP_RIGHT = 3;
    public static final int BOTTOM_RIGHT = 4;
    public static final int TOP_LEFT = 5;
    public static final int BOTTOM_LEFT = 6;
    public static final int TOP_CENTER = 7;
    public static final int BOTTOM_CENTER = 8;
}
