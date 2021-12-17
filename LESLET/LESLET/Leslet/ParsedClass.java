import java.beans.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.applet.Applet;

public class ParsedClass extends Container
{
	String characters = null;
	static final String imagesFolder = "images";
	static File prefix = null;
	static final String prefixFileName = "prefix.ini";
	static final String presinfoName = "presinfo.html";
	static final String navbarName = "navbar.html";
	static final String buttonName = "buttons.html";
	
	static public final Color red = new Color(204,0,0);
	static public final Color gray = new Color(204,204,204);
	static public final Color orange = new Color(204,153,102);
	
	// Constants for the player layout

	static public final int navBarWidth = 20;
	static public final int navButtonWidth = navBarWidth;
	static public final int navButtonHeight = 13;
	static public final Dimension pageFlipSize = new Dimension(navBarWidth,15);
	static public final int gutterSize = 2;
	static public final int buttonHeight = 13;
	static public final int buttonWidth = 73;
	static public final int buttonSep = 2;
	static public final int presInfoHeight = 52;
	static public final int presInfoWidth = 223;
	static public final int screenInfoHeight = 65;
	static public final int screenInfoWidth = 418;
	static public final int headerHeight = presInfoHeight+buttonHeight;
	static public final int headerWidth = presInfoWidth+screenInfoWidth;
    static public final int mirrorOffset = 4;
    static public final int mirrorGrid = 5;

	static public Applet applet = null;

	static String screenName( int num )
	{
		return "screen"+num+".html";
	}
	
	static String screenInfoName( int num )
	{
		return "scr"+num+"info.html";
	}
	
	static String subscreenName( int num, int snum )
	{
		return "screen"+num+'x'+snum+".html";
	}
	
    public static String getURL( File file )
    {
       	String s;
       	String surl;
       	
       	if( file.isAbsolute() )
       	{
	       	if ( System.getProperty("os.name").startsWith("M") )
	       	{
	       		//System.out.println("went thru Mac File2URL");
	       		s = "file:"+file.getAbsolutePath();
	       	}
	       	else
	       	{
	       		s = "file:///"+file.getAbsolutePath();
	       	}
		}
		
		else
		{
			s = file.getPath();
		}
		
		s = s.replace(File.separatorChar,'/');
		
       	URL url = null;

       	try
      	{
           	url = new URL(s);
           	surl = url.toString();
           	debug("using absolute href "+surl,null);
        }

        catch( MalformedURLException mue )
        {
           	debug("Using relative href "+s,null);
			surl = s;
        }

        return surl;
    }

	public File getFile( String name )
	{
		File f = new File(imagesFolder,name);
		
		if( f.exists() ) return f;
		
		//debug("file "+f+" doesn't exist. let's go searching...");
		
		File rfile;
		
		rfile = readPrefix();
		if( rfile == null )
		{
			debug("prefix.ini missing or damaged");
			return null;
		}
		
		f = new File(rfile,name);
		if( f.exists() && !f.isDirectory() )
		{
			debug("found "+f);
			return f;
		}
		
		debug(f+" doesn't exist.");
		return null;
	}
	
	synchronized File readPrefix()
	{
		if( prefix != null )
			return prefix;
			
		try
		{
			FileInputStream fis = new FileInputStream(prefixFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object o = ois.readObject();
			if( o == null || o instanceof String )
			{
				if( o != null )
					prefix = new File((String)o);
				else
					prefix = null;

				debug("prefix is "+prefix);
				return prefix;
			}
		}
		
		catch( FileNotFoundException e )
		{
			debug( prefixFileName+" not found.");
		}
		
		catch( Exception e )
		{
			debug("readPrefix - "+e);
		}

		return null;
	}
	
	public void finish()
	{
	}
	
	public void setAttribute( String name, String type, String value )
	{
		try
		{
			Field f = getClass().getField(name);
			if( f != null )
			{
				f.set(this,value);
				//debug("setAttribute - field "+name+" set to "+value);
			}
		}
		
		catch( NoSuchFieldException e )
		{
			/* field doesn't exist */
			debug("setAttribute - field "+name+" does not exist in "+getClass().getName());
		}
		
		catch( IllegalArgumentException e )
		{
			debug("setAttribute - field "+name+" is not a string.");
		}
		
		catch( Exception e )
		{
			debug("setAttribute - "+e);
		}
	}
	
	public void appendCharacters( String s )
	{
		if( characters == null )
			characters = s;
		else
			characters = characters + " " + s;
		//debug("data for "+getClass().getName()+" is "+characters);
	}
	
	void doScript( PrintWriter p )
	{
	}
	
	void framesetOpen( PrintWriter p, boolean rows, String dim )
	{
		String rowcol = rows ? "ROWS" : "COLS";
		p.println("<FRAMESET "+rowcol+"=\""+dim+"\" border=0 frameborder=0>");
//		p.println("<FRAMESET "+rowcol+"=\""+dim+"\">");
	}
	
	void framesetClose( PrintWriter p )
	{
		p.println("</FRAMESET>");
	}
	
	void frame( PrintWriter p, String source, String name )
	{
		p.println("<FRAME SRC=\""+source+"\" NAME=\""+name+"\" marginwidth=0 marginheight=0 frameborder=0 border=0 noresize scrolling=\"no\">");
	}
	
	void noframes( PrintWriter p, String message )
	{
		p.println("<NOFRAMES>");
		p.println(message);
		p.println("</NOFRAMES>");
	}
	
	void scriptOpen( PrintWriter p )
	{
		p.println("<SCRIPT>");
		p.println("<!-- support non-scripting browsers (but, why?)");
		doScript(p);
	}

	void scriptClose( PrintWriter p )
	{
		p.println("// End the hiding here. -->");
		p.println("</SCRIPT>");
	}
	
	void doStyle( PrintWriter p )
	{
	}
	
	void styleOpen( PrintWriter p )
	{
		p.println("<STYLE type=\"text/css\">");
		doStyle(p);
	}
		
	void styleClose( PrintWriter p )
	{
		p.println("</STYLE>");
	}
		
	void htmlOpen( PrintWriter p )
	{
		p.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Frameset//EN\" \"http://www.w3.org/TR/REC-html40/frameset.dtd\">");
		p.println();
		p.println("<!-- Automagically Generated by the MakeDHTML lesson converter.");
		p.println("     Copyright (c) 1999 by Archipelago Productions");
		p.println("     Written by Brenda Holloway (brendah@mbay.net)");
		p.println("  -->");
		p.println();
		p.println("<HTML>");
	}
	
	void htmlClose( PrintWriter p )
	{
		p.println("</HTML>");
	}
	
	void htmlHeader( PrintWriter p, String title )
	{
		p.println("<HEAD>");
		if( title != null )
			p.println("<TITLE>"+title+"</TITLE>");
		p.println("<META http-equiv=\"Content-Script-Type\" content=\"JavaScript\">");
		p.println("</HEAD>");
	}

	void htmlBodyOpen( PrintWriter p )
	{
		htmlBodyOpen(p,null);
	}
	
	void htmlBodyOpen( PrintWriter p, String styles )
	{
		p.print("<BODY");
		if( styles != null )
			p.print(" style=\""+styles+"\"");
		p.println(">");
	}
	
	void htmlBodyClose( PrintWriter p )
	{
		p.println("</BODY>");
	}
	
	static boolean debug = true;
	
	void debug( String s )
	{
		debug( s, this );
	}
	
	static synchronized void debug( String s, Object o )
	{
		if( debug )
		{
			if( o == null )
				System.err.println("debug:: "+s);
			else
				System.err.println(o.getClass().getName()+":: "+s);
		}
	}
}
