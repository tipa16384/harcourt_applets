import java.io.*;
import java.util.*;

public class User implements Serializable
{
	private static final String userFile = "users.txt";

	public static final String GM = "Mystery CSR";

	static String [] ranks = {
		GM,
		"GM",
		"Apprentice Guide",
		"Guide",
		"Quest Troupe",
		"Senior Guide",
		"Elder Guide",
		"GM-Admin",
		"GM-Lead Admin"
		};
										  	
	public String name;
	public String rank;
	public String profession;
	public String race;
	public String pid;
	public int level;
	boolean complete;
	
	public User( String data )
	{
		this(data,true);
	}
	
	public User( String name, String rank )
	{
		this(name,false);
		this.rank = rank;
	}
	
	public User( String data, boolean full )
	{
		complete = full;
		
		if( !full )
		{
			name = data;
		}

		else
		{
			int state = 0;
			int p = 0;
			String token;
			
			while( (token=nextToken(data,p)) != null )
			{
				if( token.charAt(0) != ' ' )
				{
					switch( state )
					{
						case 0: // waiting for '['
							if( token.equals("[") )
								state = 10;
							else if( token.equals("*") )
								state = 20;
							break;
						
						case 20: // waiting for guide rank
							if( token.equals("*") )
								state = 0;
							else if( rank == null )
								rank = token;
							else
								rank += " "+token;
							break;
						
						case 10: // waiting for level OR ANON
							if( token.equals("ANON") )
								state = 30;
							else if( token.equals("ANONYMOUS") )
								state = 11;
							else
							{
								setLevel(token);
								++state;
							}
							break;
						
						case 11: // waiting for class
							if( token.equals(")") )
								;
							else if( token.equals("]") )
								state = 40;
							else if( !token.equals("Unknown") )
							{
								if( profession == null )
									profession = token;
								else
									profession += " "+token;
							}
							break;
						
						case 30: // waiting for ANON to finish
							if( token.equals("(") )
								state = 10;
							break;
						
						case 40: // getting name
							if( token.equals("(") )
							{
								name = "ANONYMOUS";
								state = 51;
							}
							
							else
							{
								name = token;
								state = 50;
							}
							
							break;
						
						case 50:	// getting race
							if( !token.equals("(") )
								state = 60;
							else
								state = 51;
							break;
						
						case 51:
							{
								if( token.equals(")") )
									state = 60;
								else if( !token.equals("Unknown") )
								{
									if( race == null )
										race = token;
									else
										race += " "+token;
								}
							}
							
							break;
						
						case 60:	// waiting for PID
							if( token.equals("PID") )
								state = 61;
							break;
						
						case 61:	// got PID
							pid = token;
							state = 70;
							break;
					}
				}
								
				p += token.length();
			}
		}
	}

	static public void readUsers( Vector users )
	{
		try
		{
			File fUser = new File(userFile);
			
			if( fUser.exists() )
			{
				FileInputStream fis = new FileInputStream(fUser);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				User u;
				
				try
				{
					while( (u = (User) ois.readObject()) != null )
					{
						users.addElement(u);
					}
				}
				
				catch( EOFException e )
				{
					//System.err.println("-- end of file -- "+users.size()+" users read.");
				}
				
				catch( Exception e )
				{
					throw e;
				}
				
				finally
				{
					ois.close();
				}
			}
			
			else
			{
			}
		}
		
		catch( Exception e )
		{
			System.err.println("readUsers - "+e);
		}
	}
	
	public static void writeUsers( Vector users )
	{
		try
		{
			File fUser = new File(userFile);
			
			if( fUser.exists() )
			{
				fUser.delete();
			}
			
			FileOutputStream fos = new FileOutputStream(fUser);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			int len = users.size();
			for( int i=0; i<len; ++i )
			{
				User u = (User) users.elementAt(i);
				oos.writeObject(u);
			}
			
			oos.close();
		}
		
		catch( Exception e )
		{
			System.err.println("writeUsers - "+e);
		}
	}

	void setLevel( String s )
	{
		try
		{
			level = Integer.parseInt(s);
		}
		
		catch( Exception e )
		{
			System.err.println("Invalid number "+s);
		}
	}

	String nextToken( String s, int p )
	{
		int len = s.length();
		
		if( p >= len )
			return null;
		
		String tok = "";
		char ch = s.charAt(p);
		
		if( ch >= '0' && ch <= '9' )
		{
			while( (p < len) )
			{
				ch = s.charAt(p++);
				if( ch >= '0' && ch <= '9' )
					tok += ch;
				else
					break;
			}
		}
		
		else if( (ch >= 'a' && ch <= 'z') ||
				 (ch >= 'A' && ch <= 'Z')   )
		{
			while( (p < len) )
			{
				ch = s.charAt(p++);
				if( (ch >= 'a' && ch <= 'z') ||
				 	(ch >= 'A' && ch <= 'Z') ||
				 	(ch >= '0' && ch <= '9') ||
				 	(ch == '-') )
					tok += ch;
				else
					break;
			}
		}
		
		else tok += ch;
		
		return tok;
	}

	public boolean equals( Object o )
	{
		if( o instanceof User )
			return equals( (User) o );
		
		else
			return super.equals(o);
	}
	
	public boolean equals( User u )
	{
		return name.equals(u.name);
	}
	
	public boolean equalsGlobal( User u )
	{
		return equals(u) ||
			( pid != null && pid.equals(u.pid) );
	}
	
	public boolean equals( String s )
	{
		return name.equalsIgnoreCase(s);
	}
	
	public void merge( User u )
	{
		if( rank == null )
			rank = u.rank;
			
		else if( u.rank != null )
		{
			int rlen = ranks.length;
			int oldRank = -1;
			int newRank = -1;
			
			for( int i=0; i<rlen; ++i )
			{
				if( rank.equals(ranks[i]) ) oldRank = i;
				if( u.rank.equals(ranks[i]) ) newRank = i;
			}
			
			if( newRank > oldRank )
			{
				System.out.println(name+" - rank "+ranks[newRank]);
				rank = u.rank;
			}
		}
		
		if( u.level > level )
		{
			if( level > 0 /*&& rank != null*/ )
				System.out.println(name+" - level "+level+"->"+u.level);
				
			level = u.level;

			profession = u.profession;
		}
		
		if( profession == null )
			profession = u.profession;
		
		if( rank != null && "Gnome".equals(race) && !"Gnome".equals(u.race) )
		{
			if( u.race != null )
			{
				System.out.println(rank+" "+name+" is really a "+u.race);
				race = u.race;
			}
		}
		
		if( race == null )
			race = u.race;
		
		if( pid == null )
			pid = u.pid;
		
		if( !complete )
			complete = u.complete;
	}
	
	public String toString()
	{
		Vector v = new Vector();
		
		if( rank != null )
			v.addElement(rank);
		if( level > 0 )
			v.addElement(Integer.toString(level));
		if( profession != null )
			v.addElement(profession);
		if( race != null )
			v.addElement(race);
		if( pid != null )
			v.addElement(pid);
					
		String s = name;
		
		int len = v.size();
		
		if( len > 0 )
		{
			s += '[';
			
			for( int i=0; i<len; ++i )
			{
				if( i > 0 )
					s += ",";
				s += (String) v.elementAt(i);
			}
			
			s += ']';
		}

		return s;
	}
	
	private void debug( String s )
	{
		System.err.println("User: "+s);
	}
}
