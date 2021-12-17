import java.util.*;
import java.io.*;

public class Page extends ParsedClass
{
	public String title = "untitled page";
	
	public Page()
	{
		setLayout( new TwoDLayout(0,10) );
	}
	
	public void setFlipbook( Flipbook object )
	{
		debug("adding flipbook "+object+" to page");
		add( object );
	}

	public void setTextbook( Textbook object )
	{
		debug("adding textbook "+object+" to page");
		add( object );
	}

	public void setPicture( Picture object )
	{
		debug("adding image "+object+" to page");
		add( object );
	}

	public void setLink( Link object )
	{
		debug("adding link "+object+" to page");
		add( object );
	}

	public void setParagraph( Paragraph object )
	{
		debug("adding paragraph "+object+" to page");
		add( object );
	}

	public void setQuiz( Quiz object )
	{
		debug("adding quiz "+object+" to page");
		add( object );
	}
	
	public String toString()
	{
		return getClass().getName()+"["+title+",bounds="+getBounds()+"]";
	}
	
	public String getTitle()
	{
		return title;
	}
}
