import java.io.*;

public class Flasher
{
	byte buffer[] = new byte[128];
	
	public static void main(String args[])
	{
		Flasher fl = new Flasher();
		for( int i=0; i<args.length; ++i )
		{
			fl.process( args[i] );
		}
	}

	void process( String fn )
	{
		System.out.println("\nProcessing "+fn);
		String error = "none";
		
		Header header;
		
		try
		{
			error = "creating input stream";
			InputStream is = new FileInputStream(fn);
			
			error = "reading header";
			header = new Header(is);
			
			error = "dumping tags";
			
			while( dumpTag(is) != 0 )
				;
			
			error = "closing input stream";
			is.close();
		}
		
		catch( Exception e )
		{
			System.err.println("**** Aborted with "+e);
			System.err.println("**** crashed during: "+error);
		}
	}
	
	int dumpTag( InputStream is ) throws Exception
	{
		int tagCode = readInt(is);
		int tagID = (tagCode>>6) & 0x3FF;
		long tagLength = (long)(tagCode & 0x3F);
		
		System.out.println("\nTag ID is "+tagID);
		System.out.println("Tag length is "+tagLength);
		
		if( tagLength < 0x3F )
		{
			System.out.println("Tag data is "+dump(is,(int)tagLength));
		}
		
		else
		{
			tagLength = readLong(is);
			System.out.println("Extended tag length is "+tagLength);
			is.skip(tagLength);
		}
		
		return tagID;
	}
	
	class Header
	{
		int version;
		long flen;
		int delay;
		int fps;
		int numframes;
		
		public Header( InputStream is ) throws Exception
		{
			is.read( buffer, 0, 3 );
			
			if( buffer[0] != 'F' ||
			    buffer[1] != 'W' ||
			    buffer[2] != 'S' )
			{
				throw new Exception("Bad file signature");
			}
			
			version = (int)readByte(is);
			System.out.println("Flash version is "+version);
			
			flen = readLong(is);
			System.out.println("File is "+flen+" bytes long.");
			
			System.out.println("First coord is "+dump(is,2));
			System.out.println("Second coord is "+dump(is,2));
			System.out.println("Third coord is "+dump(is,2));
			System.out.println("Fourth coord is "+dump(is,2));
			
			System.out.println("Extra garbage is "+dump(is,1));
			
			delay = (int) readByte(is);
			System.out.println("Delay is "+delay);
			
			fps = (int) readByte(is);
			System.out.println("Frames per Second is "+fps);
			
			numframes = readInt(is);
			System.out.println("Number of frames is "+numframes);
		}
	}
	
	void readBuffer( InputStream is, int len ) throws Exception
	{
		int nread = is.read( buffer, 0, len );
		if( nread < len ) throw new Exception("out of data");
	}
	
	byte readByte( InputStream is ) throws Exception
	{
		readBuffer( is, 1 );
		return buffer[0];
	}
	
	long readLong( InputStream is ) throws Exception
	{
		readBuffer( is, 4 );
		
		long l = (((long)buffer[3]&0xFF) << 24) |
				 (((long)buffer[2]&0xFF) << 16) |
				 (((long)buffer[1]&0xFF) << 8 ) |
				 (((long)buffer[0]&0xFF));
		return l;
	}
	
	int readInt( InputStream is ) throws Exception
	{
		readBuffer( is, 2 );
		
		int i = (((int)buffer[1]&0xFF) << 8 ) |
				 (((int)buffer[0]&0xFF));
		return i;
	}
	
	String dump( InputStream is, int len ) throws Exception
	{
		readBuffer( is, len );
		
		String s = "";
		
		for( int i=0; i<len; ++i )
		{
			s = s + dumpHex(buffer[i]) + " ";
		}
		
		return s;
	}
	
	String dumpHex( byte b )
	{
		return dumpNybble( (b>>4)&0xF ) + dumpNybble( b & 0xF );
	}
	
	String dumpNybble( int b )
	{
		switch( b )
		{
			case 0: return "0";
			case 1: return "1";
			case 2: return "2";
			case 3: return "3";
			case 4: return "4";
			case 5: return "5";
			case 6: return "6";
			case 7: return "7";
			case 8: return "8";
			case 9: return "9";
			case 10: return "A";
			case 11: return "B";
			case 12: return "C";
			case 13: return "D";
			case 14: return "E";
			case 15: return "F";
			default: return "?";
		}
	}
}
