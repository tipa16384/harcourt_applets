import java.util.*;
import java.io.*;

public class Textbook extends ParsedClass
{
	Vector sections = new Vector();

	public void setSection( Section object )
	{
		debug("adding section "+object+" to the Textbook");
		sections.addElement(object);
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
