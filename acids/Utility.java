import java.awt.*;
import java.net.URL;
import java.io.*;
import java.applet.*;
import java.lang.reflect.*;

public abstract class Utility
{
	static Applet applet = null;
	final static boolean debugText = false;
	
	public static double parseDouble( String s )
	{
		double val = 0.0;
		boolean negative = false;
		
		if( s != null && s.length() > 0 )
		{
			char [] chars = s.toCharArray();
			boolean foundDot = false;
			
			int i;
			int len = chars.length;
			double power = 1.0;
			
			scanner: for( i=0; i<len; ++i )
			{
				char ch = chars[i];
				
				if( i == 0 )
				{
					if( ch == '-' )
					{
						negative = true;
						continue scanner;
					}
					
					if( ch == '+' )
						continue scanner;
				}
				
				if( ch == '.' )
				{
					if( foundDot )
						break scanner;
					else
					{
						foundDot = true;
						continue scanner;
					}
				}
				
				double dval = (double)Character.digit(ch,10);
				
				if( dval < 0.0 )
					break scanner;
				
				if( foundDot )
				{
					power /= 10.0;
					val += power * dval;
				}
				
				else
				{
					val = val * 10.0 + dval;
				}
			}
		}
		
		return (negative && val != 0.0) ? -val : val;
	}
	
	public static String capital( String s )
	{
		if( s == null || s.length() == 0 )
			return s;
		
		char [] chars = s.toCharArray();
		char ch = chars[0];
		
		if( Character.isLowerCase(ch) )
		{
			chars[0] = Character.toUpperCase(ch);
			s = new String(chars);
		}
		
		return s;
	}
	
	public static Method findMethod( Object o, String name, Class [] params )
	{
		Method man = null;
		
		if( o != null && name != null )
		{
			try
			{
				man = o.getClass().getMethod(name,params);
			}
			
			catch( NoSuchMethodException nsme )
			{
				System.err.println("Utility:: Couldn't find method "+name+"("+params+")");
			}
			
			catch( SecurityException se )
			{
				System.err.println("Utility:: Security Exception...");
			}
			
			catch( Exception e )
			{
				System.err.println("Utility:: "+e);
			}
		}
		
		return man;
	}

	public static void setValue( Object target, Method msetter, double val )
	{
		if( msetter != null )
		{
			try
			{
				Double dont = new Double(val);
				Object [] params = { dont };
				msetter.invoke(target,params);
			}
			
			catch( Exception e )
			{
				System.err.println("NumedicField.setValue:: "+e);
			}
		}
	}

	public static double getValue( Object target, Method mgetter )
	{
		double val = 0;
		
		if( mgetter != null )
		{
			try
			{
				Double dont = (Double) mgetter.invoke(target,null);
				if( dont != null )
					val = dont.doubleValue();
			}
			
			catch( Exception e )
			{
				System.err.println("Utility.getValue:: "+e);
			}
		}
		
		return val;
	}
	
	public static Applet getApplet()
	{
		return applet;
	}
	
	public static void setApplet( Applet app )
	{
		applet = app;
	}
	
	public static Image getImage( Component base, String name )
	{
		debug("looking for "+name);
		
						//The following is going way overboard!!!
						// It gets all browsers to work (load the correct image)
						//  BEWARE The order is important/critical
		Image image = null;
		int	attempt = 0;			//for diag
		URL url = null;
		
		// ATTEMPT #1
		// ICE==security crash, NetScape==null

		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the class loader to construct the URL");
			
			try
			{
				ClassLoader loader = base.getClass().getClassLoader();
				
				debug("??? loader is "+loader);
				
				url = loader.getResource(name);
				
				if( url != null && applet != null )
					image = applet.getImage(url);
				
				debug("--- returned URL="+url+" and image="+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	

		// ATTEMPT #2
		// ?
		
		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the loader to get a resource as a stream");
			
			try
			{
				debug("             does not compute");
				
				Class cla = base.getClass();
				
				debug("             class is "+cla+" and 'name' is "+name);
				
				InputStream is = cla.getResourceAsStream(name);
				
				debug("            is (InputStream) is "+is);
				
				byte [] but = new byte[is.available()];
				
				debug("            but["+is.available()+"] is "+but);
				
				is.read(but);
				
				debug("            data read");
				
				image = base.getToolkit().createImage(but);

				debug("--- returned image "+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	

		// ATTEMPT #3
		// 

		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the class to construct the URL");
			
			try
			{
				url = base.getClass().getResource(name);
				
				if( url != null && applet != null )
					image = applet.getImage(url);
				
				debug("--- returned URL="+url+" and image="+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	

		// ATTEMPT #4
		// ICE==security crash, NetScape==null
		
		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the class loader to get a system resource");
			
			try
			{
				url = base.getClass().getClassLoader().getSystemResource(name);
				
				if( url != null && applet != null )
					image = applet.getImage(url);
					
				debug("--- returned URL="+url+" and image="+image);
			}
			
			catch( Exception e )
			{
				debug("--- threw "+e);
			}
		}	

		// ATTEMPT #5
		// works for ICE
		
		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the codebase to form the URL");
			
			if( applet == null )
			{
				debug("--- applet is null");
			}
			
			else
			{
				try
				{
					url = new URL(applet.getCodeBase(),name);
					
					if( url != null )
						image = applet.getImage(url);
						
					debug("--- returned URL="+url+" and image="+image);
				}
				
				catch( Exception e )
				{
					debug("--- threw "+e);
				}
			}
		}

		// ATTEMPT #6
		// works for ICE
		
		if( image == null )
		{
			attempt++;
			debug("attempt #"+attempt+" - use the documentbase to form the URL");
			
			if( applet == null )
			{
				debug("--- applet is null");
			}
			
			else
			{
				try
				{
					url = new URL(applet.getDocumentBase(),name);
					
					if( url != null )
						image = applet.getImage(url);
						
					debug("--- returned URL "+url);
				}
				
				catch( Exception e )
				{
					debug("--- threw "+e);
				}
			}
		}

	/*
		s = url.toString();
		try { url = new URL( s ); }
		catch ( MalformedURLException me )
			{ System.out.println("MalformedURLException "+me ); }
	*/
		if( image == null )
			debug("*** couldn't construct an image for "+name+" (even though the URL was "+url+")");
		else
			debug("*** success with attempt "+attempt+" produces "+image+" from URL "+url);
		
		return image;
	}

	private static void debug( String s )
	{
		if( debugText )
			System.out.println("Utility:: "+s);
	}
}
