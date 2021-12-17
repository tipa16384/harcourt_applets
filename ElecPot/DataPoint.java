// Decompiled by Jad v1.5.5.3. Copyright 1997-98 Pavel Kouznetsov.
// Jad home page:      http://web.unicom.com.cy/~kpd/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DataPoint.java

package Graph;


public class DataPoint
{

    public DataPoint(double nx, double ny)
    {
        x = nx;
        y = ny;
    }

    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }

    public double x;
    public double y;
}
