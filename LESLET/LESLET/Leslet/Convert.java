import java.awt.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.reflect.*;
//import com.ibm.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class Convert implements DocumentHandler
{
//	String parserClass = "org.xml.sax.Parser";
	String parserClass = "com.microstar.xml.SAXDriver";
//	String parserClass = "com.ibm.xml.parsers.SAXParser";
	Parser parser;
	Vector v;
	
	public Convert()
	{
		debug("initializing convert");

		parser = null;
		v = new Vector();
		
		try
		{
			parser = ParserFactory.makeParser(parserClass);
			parser.setDocumentHandler(this);
		}
		
		catch( ClassNotFoundException e )
		{
			debug("Parser class \""+parserClass+"\" not found.");
		}
		
		catch( Exception e )
		{
			debug("While making parser - "+e);
		}

		finally
		{
			if( parser == null )
			{
				debug("Couldn't create a parser - exiting.");
				System.exit(2);
			}
		}
	}
	
	public void start( URL context, String name )
	{
		debug("parsing "+name+" in context "+context);
		
		try
		{
			URL url = new URL(context,name);
			parser.parse(new InputSource(url.openStream()));
		}
		
		catch( MalformedURLException e )
		{
			debug("that URL just isn't working out.");
		}
		
		catch( IOException e )
		{
			debug("couldn't read from that URL");
		}
		
		catch( SAXException e )
		{
			debug("SAX sez - "+e);
			
			Exception se = e.getException();
			if( se != null )
			{
				debug("Wait a second...");
//				if( se instanceof XmlException )
//				{
//					XmlException xe = (XmlException) se;
//					debug("error in line "+xe.getLine()+", column "+xe.getColumn());
//				}
//				
//				else
//				{
//					debug("embedded exception was "+e.getClass().getName());
//				}
			}
		}
		
		catch( Exception e )
		{
			debug("while parsing - "+e);
		}
	}
	
	public void setDocumentLocator( Locator locator )
	{
	}
	
	public void startDocument(  )
	{
		debug("Start Document");
	}
	
	public void endDocument(  )
	{
		debug("End Document");
		System.exit(0);
	}
	
	public void characters( char ch[], int start, int length )
	{
		String s = (new String(ch,start,length)).trim();
		
		if( s != null && s.length() > 0 )
		{
			int len = v.size();
			if( len > 0 )
			{
				ParsedClass pc = (ParsedClass) v.elementAt(len-1);
				pc.appendCharacters(s);
			}
		}
	}
	
	String capitalize( String s )
	{
		s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
		return s;
	}
	
	public void startElement( String name, AttributeList list )
	{
		ParsedClass pc;
		
		name = capitalize(name);
		
		try
		{
			Class cls = Class.forName(name);	
			pc = (ParsedClass)cls.newInstance();
			//debug("created "+pc.getClass().getName());
			
			int len = list.getLength();
			for( int i=0; i<len; ++i )
			{
				String aname = list.getName(i);
				String type = list.getType(i);
				String value = list.getValue(i);
				
				pc.setAttribute(aname,type,value);
			}
			
			v.addElement(pc);
		}
		
		catch( ClassNotFoundException cnfe )
		{
			debug("Class "+name+" not found.");
		}
		
		catch( Exception e )
		{
			debug("while parsing - "+e);
		}

	}
	
	public void endElement( String name )
	{
		try
		{
			name = capitalize(name);
			Class cls = Class.forName(name);	
			
			int len = v.size();
			if( len == 0 )
			{
				debug("fatal error - stack underflow");
				System.exit(5);
			}
			
			ParsedClass pc = (ParsedClass) v.elementAt(len-1);
			v.removeElementAt(len-1);
			
			if( len > 1 )
			{
				ParsedClass ppc = (ParsedClass) v.elementAt(len-2);
				String methodName = "set"+name;
				Class [] cargs = { pc.getClass() };
				//debug("looking for method "+methodName+" with argument "+cargs[0].getName()+" in object "+ppc);
				Method meth = ppc.getClass().getMethod(methodName,cargs);
				Object [] oargs = { pc };
				meth.invoke(ppc,oargs);
			}
			
			pc.finish();
		}
		
		catch( IllegalArgumentException e )
		{
			debug("passed a bad argument to the superclass");
		}
		
		catch( NoSuchMethodException e )
		{
			debug("no such method set"+name);
		}
		
		catch( ClassNotFoundException e )
		{
			debug("Class "+name+" not found.");
		}
		
		catch( Exception e )
		{
			debug("while ending element - "+e);
		}
	}
	
	public void ignorableWhitespace( char ch[], int start, int length )
	{
	}
	
	public void processingInstruction( String target, String data )
	{
	}
	
	static boolean debug = true;
	static void debug( String s )
	{
		if( debug )
			System.err.println("Convert:: "+s);
	}
}
