import java.util.*;
import java.io.*;

public class Image extends ParsedClass
{
	public String align = "none";
	public String caption = null;
	public String src = null;
	
	public String toString()
	{
		return getClass().getName()+"(align="+align+",src="+src+")";
	}
}
