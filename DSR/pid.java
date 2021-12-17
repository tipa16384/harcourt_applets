import java.io.*;
import java.util.*;

public class pid
{

	public static void main(String args[])
	{
		pid dumbo;
		
		if( args.length == 0 )
		{
			System.err.println("No username!");
		}
		
		else
		{
			dumbo = new pid();
			
			for( int i=0; i<args.length; ++i )
			{
				dumbo.lookup(args[i]);
			}
		}
	}

	Vector users = new Vector();

	public pid()
	{
		User.readUsers(users);
	}

	void report( User u )
	{
		int len = users.size();
		int i;
		
		for( i=0; i<len; ++i )
		{
			User u1 = (User) users.elementAt(i);
			
			if( u.equalsGlobal(u1) )
				System.out.println(u1);
		}
	}

	void lookup( String username )
	{
		//System.out.println("Looking up "+username);
		
		int len = users.size();
		int i;
		boolean found = false;
		
		for( i=0; i<len; ++i )
		{
			User u = (User) users.elementAt(i);
			
			if( u.equals(username) )
			{
				report(u);
				found = true;
			}
		}
		
		if( !found )
		{
			System.out.println(username+" not found.");
		}
	}
}
