package COM.archi.dl.rtf;

import java.awt.*;
import java.io.Serializable;

public class StyledTextRun implements Serializable
{
	private int paragraph;
	private String text;
	private Font font;
	private Color color;
	
	public StyledTextRun( int paragraphNumber, String txt, Font f, Color c )
	{
		paragraph = paragraphNumber;
		text = txt;
		font = f;
		color = c;
	}
	
	public int getParagraph()
	{
		return paragraph;
	}
	
	public String getText()
	{
		return text;
	}
		
	public Font getFont()
	{
		return font;
	}
	
	public Color getColor()
	{
		return color;
	}	
	


} // end class StyledTextRun