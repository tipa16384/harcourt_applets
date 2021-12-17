import java.awt.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.ibm.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class Atlas implements DocumentHandler
{
//	String parserClass = "com.microstar.xml.SAXDriver";
	String parserClass = "com.ibm.xml.parsers.SAXParser";
	Parser parser;
	Vector v;
	static Vector zones = new Vector();
	Vector bestroute = null;
	final boolean shortest = false;
	final boolean safest = true;
			
	static public void main( String [] args )
	{
		debug("Welcome to Atlas!");
		
		int numargs = args.length;
		int i;
		
		if( numargs == 0 )
		{
			System.out.println("pass the files to parse as command line arguments.");
			System.exit(1);
		}
		
		Atlas atlas = new Atlas();
		
		for( i=0; i<numargs; ++i )
			atlas.start(args[i]);

		atlas.bestroute = null;
		atlas.findPath("Rivervale","Qeynos");
		
		if( atlas.bestroute == null )
			debug("No route found!");
		else
		{
			int len = atlas.bestroute.size();
			for( i=0; i<len; ++i )
				System.out.println(atlas.bestroute.elementAt(i));
		}
	}

	Zone findZone( String zname )
	{
		int len = zones.size();
		int i;
		Zone zone = null;
				
		for( i = 0; i < len && zone == null; ++i )
		{
			Zone z = (Zone) zones.elementAt(i);
			if( z.name.equals(zname) )
			{
				//debug("found zone "+z);
				zone = z;
			}
		}

		return zone;
	}
	
	void findPath( String from, String to )
	{
		debug("Looking for path from "+from+" to "+to);
		Zone fz = null, tz = null;
		int len = zones.size();
		int i;
		
		fz = findZone(from);
		tz = findZone(to);
		
		debug("beginning zone is "+fz);
		debug("end zone is "+tz);
		
		if( fz == null || tz == null )
		{
			debug("one or more invalid zones");
			System.exit(1);
		}
		
		for( i = 0; i < len; ++i )
		{
			Zone z = (Zone) zones.elementAt(i);
			z.setTime(9999);
		}
		
		Vector route = new Vector();
		findRoute(fz,tz,route);
	}
	
	void findRoute( Zone fz, Zone tz, Vector route )
	{
		int rlen = route.size();
		int t = 0;
		int i;
		
		if( safest )
		{
			t = fz.getDanger();
			for( i=0; i<rlen; ++i )
			{
				t = Math.max(t,((Zone)route.elementAt(i)).getDanger());
			}
			
			t *= rlen + 1;
		}
		
		else if( shortest )
		{
			t = rlen+1;
		}
		
		else
		{
			t = fz.getDanger();
			for( i=0; i<rlen; ++i )
			{
				t += ((Zone)route.elementAt(i)).getDanger();
			}
		}
		
		if( fz.getTime() <= t )
			return;
		
		fz.setTime(t);

		route.addElement(fz);
		
		if( fz == tz )
		{
			bestroute = (Vector) route.clone();
		}

		else
		{		
			Vector v = fz.adjacent;
			int alen = v.size();
			for( i=0; i<alen; ++i )
			{
				Adjacent adj = (Adjacent) v.elementAt(i);
				Zone nz = findZone(adj.name);
				if( nz == null )
				{
					debug("no zone named "+adj.name);
				}
				else
				{
					findRoute( nz, tz, route );
				}
			}
		}
				
		route.removeElement(fz);
	}
	
	public Atlas()
	{
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
	
	public void start( String name )
	{
		debug("parsing "+name);
		
		try
		{
			parser.parse(new InputSource(new FileInputStream(name)));
		}
		
		catch( FileNotFoundException e )
		{
			debug("input file \""+name+"\" not found.");
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
		debug(zones.size()+" zones compiled.");
		//System.exit(0);
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
			System.err.println("Atlas:: "+s);
	}
}

class Map extends ParsedClass
{
	public void setZone( Zone zon )
	{
		Atlas.zones.addElement( zon );
	}
}

class Zone extends ParsedClass
{
	public Vector adjacent = new Vector();
	public String name = "unnamed zone";
	public String danger = "1";
	transient int time = 0;
		
	public void setAdjacent( Adjacent adj )
	{
		adjacent.addElement(adj);
	}
	
	public void setName( String name )
	{
		this.name = name;
	}

	public void setTime( int t )
	{
		time = t;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public int getDanger()
	{
		try
		{
			return Integer.parseInt(danger);
		}
		
		catch( Exception e )
		{
			return 1;
		}
	}

	public String toString()
	{
		return name;
	}
}

class Adjacent extends ParsedClass
{
	public String name = "unnamed adjacency";
	public String at = "0,0";
		
	public void setName( String name )
	{
		this.name = name;
	}
}
