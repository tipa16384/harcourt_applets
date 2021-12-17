import java.util.*;
import java.io.*;

public class Section extends Paragraph
{
	public String level = null;

	public String toString()
	{
		return getClass().getName()+"[chars="+characters+"]";
	}
}
