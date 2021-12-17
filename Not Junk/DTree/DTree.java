package DTree;

import java.io.*;     
import java.util.Vector;

public class DTree
{
	static boolean relative = true;

	public static void main( String args[] )
	{
		System.err.println("DTree version 1.1 by Brenda Holloway\n");

		try
		{
			int i, count = args.length;
			Vector fn = new Vector();

			// look for switches

            for( i = 0; i < count; ++i )
            {
            	String arg = args[i];
            	char ch;

            	if( (ch = arg.charAt(0)) == '-' || ch == '/' || ch == '+' )
            	{
            		String ss = arg.substring(1);                         
            		
            		if( "absolute".startsWith(ss) )
            			relative = false;
            		else if( "relative".startsWith(ss) ) 
            			relative = true;
            		else
            			throw new UsageError("Unrecognized switch \""+arg+"\"");
            	}
            	
            	else
            	{
            		File f = new File(arg);

            		if( !f.exists() )
            			throw new UsageError("Unrecognized file or directory name \""+arg+"\"");
            		
            		fn.addElement(f);
            	}
            }

			if( (count = fn.size()) == 0 )
				makeTree( new File(System.getProperty("user.dir")), 0 );

			else for( i=0; i<count; ++i )                                    
				makeTree( (File) fn.elementAt(i), 0 );

		}

		catch( UsageError e )
		{
			System.err.println("Usage Error - "+e.getMessage());
			System.err.println();
			System.err.println("Usage: DTree <directory1> [<directory2>...] [-relative | -absolute]");
		}
		
		catch( Throwable e )
		{
			System.err.println("Caught "+e);
		}

    }  
    
    static void makeTree( File path, int level ) throws UsageError
    {
    	int i;

		for( i=0; i<level; ++i )
			System.out.print("    ");
		System.out.print( relative ? path.getPath() : path.getAbsolutePath() );
		
		if( path.isDirectory() )
		{
			if( !path.canRead() )
			{
				System.out.println("  [locked]");
			}

			else
			{
				System.out.println();
				String [] flist = path.list();
				for( i=0; i<flist.length; ++i )
					makeTree( new File(path,flist[i]), level+1 );
			}
		} 
		
		else System.out.println();
    }


}
