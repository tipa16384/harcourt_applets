import java.util.*;
import java.io.*;

public class Answer extends Paragraph
{
	public Ifselect ifselect = null;
	
	public void setIfselect( Ifselect object )
	{
		debug("adding if-select "+object+" to answer");
		ifselect = object;
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
