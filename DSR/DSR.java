import java.io.*;
import java.util.*;

public class DSR
{
	private static final String defaultFile = "eqlog_43_Espranza.txt";
	private static final boolean debug = true;
	private static final boolean table = false;
	
	private static final String months[] = { "Jan", "Feb", "Mar", "Apr", "May",
									  "Jun", "Jul", "Aug", "Sep", "Oct",
									  "Nov", "Dec" };

	String filename;

	Vector zones = new Vector();
	Vector csr = new Vector();
	Vector users = new Vector();
	
	// extra notes
	
	Vector bugs = new Vector();
	Vector badnames = new Vector();
	Vector notes = new Vector();
	Vector issues = new Vector();
	Vector gms = new Vector();
	Vector weddings = new Vector();
	Vector harass = new Vector();
	Vector exploit = new Vector();
	Vector names = new Vector();
	Vector feedback = new Vector();
	Vector events = new Vector();
	Vector warns = new Vector();
	Vector kills = new Vector();
		
	User whoami = null;
	
	int pets = 0;
	int resolved = 0;
	int emergency = 0;
	int urgent = 0;
	
	boolean inpet = false;
	int urgency = 0;
	int unavail = 0;
	
	static final boolean simulate = true;
	
	Date startDate = null;
	Date currentDate = null;
	
	public static void main(String args[])
	{
		DSR dsr;
		
		if( args.length == 0 )
		{
			dsr = new DSR(defaultFile);
			dsr.process();
		}
		
		else
		{
			for( int i=0; i<args.length; ++i )
			{
				dsr = new DSR(args[i]);
				dsr.process();
			}
		}
		
		ChartMaker chart;
		
		chart = new ChartMaker();
		chart.generate();
	}

	public DSR( String filename )
	{
		this.filename = filename;
	}

	public void analyze( String s )
	{
		if( s != null && s.length() > 0 )
		{
			if( s.charAt(0) == '[' )
			{
				Date date = getDate(s);
				
				if( startDate == null )
					startDate = date;
				
				currentDate = date;
				
				s = stripDate(s);
				int slen = s.length();
				String temp;
				
				if( s.startsWith("You have entered ") )
				{
					visitedZone( s.substring(17,slen-1) );
				}
				
				else if( (temp=matchMid(s,"Killing")) != null )
				{
					addNote( kills, "Killed "+temp );
				}
				
				else if( (temp=matchEnd(s,"saved.")) != null )
				{
					identity(temp);
				}
				
				else if( (temp=matchEnd(s,"GMSAYS")) != null )
				{
					//debug(temp+" is tagged as a GM by virtue of GMSAY");
					addUser( users, new User(temp,User.GM) );
				}

				else if( s.startsWith("You GMSAY") ||
						 s.startsWith("You say to your guild") ||
						 s.startsWith("You tell your party") )
				{
					if( (temp=matchMid(s,"<note bug>")) != null )
					{
						addNote( bugs, temp );
					}
					
					else if( (temp=matchMid(s,"<note badname>")) != null )
					{
						addNote( badnames, temp );
					}
					
					else if( (temp=matchMid(s,"<note>")) != null )
					{
						addNote( notes, temp );
					}
					
					else if( (temp=matchMid(s,"<note name>")) != null )
					{
						addNote( names, temp );
					}
					
					else if( (temp=matchMid(s,"<note issue>")) != null )
					{
						addNote( issues, temp );
					}
					
					else if( (temp=matchMid(s,"<note gm>")) != null )
					{
						addNote( gms, temp );
					}
					
					else if( (temp=matchMid(s,"<note warn>")) != null )
					{
						addNote( warns, temp );
					}
					
					else if( (temp=matchMid(s,"<note warning>")) != null )
					{
						addNote( warns, temp );
					}
					
					else if( (temp=matchMid(s,"<note wedding>")) != null )
					{
						addNote( weddings, temp );
					}
					
					else if( (temp=matchMid(s,"<note harass>")) != null )
					{
						addNote( issues, temp );
					}
					
					else if( (temp=matchMid(s,"<note exploit>")) != null )
					{
						addNote( issues, temp );
					}
					
					else if( (temp=matchMid(s,"<note names>")) != null )
					{
						addNote( issues, temp );
					}
					
					else if( (temp=matchMid(s,"<note feedback>")) != null )
					{
						addNote( issues, temp );
					}
					
					else if( (temp=matchMid(s,"<note events>")) != null )
					{
						addNote( events, temp );
					}
					
					else if( (temp=matchMid(s,"<note event>")) != null )
					{
						addNote( events, temp );
					}
					
					else if( (temp=matchMid(s,"<note quest>")) != null )
					{
						addNote( events, temp );
					}
				}
								
				else if( (temp=matchEnd(s,"tells you")) != null )
				{
					addUser( users, new User(temp,false) );
				}
				
				else if( s.indexOf("**") == 0 )
				{
				}
				
				else if( s.indexOf("*WARNING*") == 0 )
				{
				}
				
				else if( s.charAt(0) == '*' || s.charAt(0) == '[' )
				{
					addUser( users, new User(s) );
				}
				
				else if( s.startsWith("RETRIEVING PETITION") )
				{
					++pets;
					inpet = true;
					urgency = unavail = 0;
				}
				
				else if( s.startsWith("DELETING THIS PETITION") )
				{
					++resolved;
					inpet = false;
				}
				
				else if( s.startsWith("CHECKING IN") )
				{
					++resolved;
					if( urgency == 1 ) ++urgent;
					else if( urgency == 2 ) ++emergency;
					inpet = false;
				}
				
				else if( s.startsWith("STATUS UPGRADED TO URGENT") )
				{
					urgency = 1;
				}
				
				else if( s.startsWith("STATUS UPGRADED TO EMERGENCY") )
				{
					urgency = 2;
				}
				
				else if( s.startsWith("STATUS DOWNGRADED TO URGENT") )
				{
					urgency = 1;
				}
				
				else if( s.startsWith("STATUS DOWNGRADED TO NORMAL") )
				{
					urgency = 0;
				}
				
				else if( s.startsWith("DELETING THIS") )
				{
					++resolved;
				}
			}
			
			else
			{
				debug("Unrecognized line \""+s+"\"");
			}
		}
	}
	
	void identity( String s )
	{
		if( whoami == null )
		{
			whoami = addUser( users, new User(s,User.GM) );
			debug("I am \""+whoami+"\"");
		}
	}
	
	void addNote( Vector list, String noot )
	{
		list.addElement(noot);
	}
	
	String matchMid( String s, String tmpl )
	{
		int idx;
		
		if( (idx=s.indexOf(tmpl)) != -1 )
		{
			String ts = s.substring( idx+tmpl.length()+1, s.length()-1 );
			debug("Extracted \""+ts+"\"");
			return ts;
		}
		
		else return null;
	}
	
	String matchEnd( String s, String tmpl )
	{
		int lastidx;
		
		if( (lastidx=s.indexOf(tmpl)) != -1 )
		{
			return s.substring(0,lastidx-1);
		}
		
		else
		{
			return null;
		}
	}
	
	User addUser( Vector uList, User u )
	{
		User ret = null;
		
		if( !u.name.equals("ANONYMOUS") )
		{
			if( uList.contains(u) )
			{
				int i = uList.indexOf(u);
				User u0 = (User) uList.elementAt(i);
				u0.merge(u);
				ret = u0;
			}
			
			else
			{
				//debug("new user "+u);
				uList.addElement(u);
				ret = u;
			}
		}
		
		return ret;
	}

	void visitedZone( String zone )
	{
		if( !zones.contains(zone) )
		{
			//debug("visited "+zone);
			zones.addElement(zone);
		}
	}
	
	public Date getDate( String s )
	{
		String mstr = s.substring(5,8);
		int month = 0;
		int day = 0;
		int year = 0;
		int hour = 0;
		int min = 0;
		int sec = 0;
		
		for( month = 0; month < months.length; ++month )
		{
			if( months[month].equals(mstr) )
				break;
		}
		
		if( month == months.length )
		{
			debug("Unrecognized month "+mstr);
			month = 0;
		}
		
		int i = 9;
		int state = 0;
		int accum = 0;
		char ch;
		
		do
		{
			ch = s.charAt(i++);
			
			if( ch < '0' || ch > '9' )
			{
				switch( state++ )
				{
					case 0: day = accum; break;
					case 1: hour = accum; break;
					case 2: min = accum; break;
					case 3: sec = accum; break;
					case 4: year = accum-1900; break;
				}
				
				accum = 0;
			}
			
			else
			{
				accum = accum * 10 + (int)(ch-'0');
			}
		}
		while( ch != ']' );
		
		return new Date(year,month,day,hour,min,sec);
	}
	
	public String stripDate( String s )
	{
		s = s.substring(s.indexOf(']')+2);
		//debug("Date stripped: \""+s+"\"");
		return s;
	}

	public void process()
	{
		debug("Processing "+filename);
		
		int lines = 0;
		int state = 0;
				
		String line = null;
		
		try
		{
			debug("Opening "+filename);
			
			BufferedReader rdr = new BufferedReader(new FileReader(filename));

			debug("Reading...");
			
			do
			{
				state = 2;
				line = rdr.readLine();
				state = 0;
				
				if( line != null )
				{
					state = 1;
					analyze( line );
					state = 0;
					++lines;
				}
			}
			while( line != null );

			state = 3;
			makeDSR();
			state = 0;
		}
		
		catch( FileNotFoundException e )
		{
			debug("Line "+lines+": "+filename+" was not found ("+e+").");
		}
		
		catch( Exception e )
		{
			debug("Line "+lines+": Unrecognized error - "+e);
			
			String s;
			
			switch( state )
			{
				default: s = "doing nothing"; break;
				case 1: s = "analyzing"; break;
				case 2: s = "reading"; break;
				case 3: s = "generating DSR"; break;
			}
			
			debug("Condition: "+s);
			debug("Line=\""+line+"\"");
		}
		
		finally
		{
			debug(lines+" lines read.");
		}

		Vector oldUsers = new Vector();		

		User.readUsers( oldUsers );
		
		{
			int len = users.size();
			int i;
			
			for( i=0; i<len; ++i )
			{
				User u = (User) users.elementAt(i);
				addUser( oldUsers, u );
			}
		}
		
		User.writeUsers( oldUsers );
	}
	
	void makeDSR()
	{
		debug("makeDSR()");
			
		try
		{
			PrintStream ps = new PrintStream( new FileOutputStream("dsr.html") );

			currentDate.setTime( roundTime(currentDate.getTime()) );
			startDate.setTime( roundTime(startDate.getTime()) );
		
			makePostTitle(ps);

			dsropen(ps);

			if( whoami == null )
				whoami = new User("Espranza","Guide");

			note( ps, "+2", "b", whoami.rank + " " + whoami.name + "'s Shift Report" );

			separate(ps);
			
			listPetitions(ps);
			separate(ps);
			listHours(ps);
			separate(ps);
			listMajorIssues(ps);
			listMinorIssues(ps);
			listComments(ps);
			listZones(ps);
			separate(ps);
			listGuides(ps);

			separate(ps);

			note( ps, "-2", "i", "generated automagically with DSR Maker 0.1" );

			dsrclose(ps);

			ps.close();
		}
		
		catch( Exception e )
		{
			debug("While writing DSR: "+e);
		}
	}

	void makePostTitle( PrintStream out )
	{
		out.print("<p>"+getTime(startDate)+" - "+getTime(currentDate));
		out.print(" ("+getElapsed()+")");
		
		Vector v = new Vector();
		if( bugs.size() > 0 || exploit.size() > 0 ) v.addElement("BUG");
		if( gms.size() > 0 ) v.addElement("GM");
		if( warns.size() > 0 ) v.addElement("WN");
		if( names.size() > 0 || badnames.size() > 0 ) v.addElement("BN");
		if( events.size() > 0 ) v.addElement("Q/E");
		
		int len = v.size();
		
		if( len > 0 )
		{
			out.print(" (");
			for( int i=0; i<len; ++i )
			{
				String s = (String) v.elementAt(i);
				if( i > 0 )
					out.print(",");
				out.print(s);
			}
			out.print(")");
		}

		out.println("</p>");
	}

	String getTime( Date d )
	{
		return makeTwo(d.getHours())+":"+makeTwo(d.getMinutes());
	}
	
	String makeTwo( int x )
	{
		if( x >= 10 )
			return Integer.toString(x);
		else
			return "0"+x;
	}

	void note( PrintStream ps, String size, String style, String msg )
	{
		rowbeg(ps);
		if(table) ps.print("<td bgcolor=\"CCCCFF\">&nbsp;</td><td colspan=2 bgcolor=\"CCCCFF\" align=left>");
		ps.print("<font size=\""+size+"\"><"+style+">");
		ps.print(msg);
		ps.print("</"+style+">");
		ps.print("</font>");
		if(table) ps.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
		rowend(ps);
	}

	void dsropen( PrintStream ps )
	{
		if(table) ps.print("<table border=0 cellpadding=3 cellspacing=0>");
	}	

	void dsrclose( PrintStream ps )
	{
		if(table) ps.print("</table>");
	}	

	void bol( PrintStream out )
	{
		if( simulate )
			out.print("<br>");
	}

	void eol( PrintStream out )
	{
		if( !table )
			out.println();
	}

	void rowbeg( PrintStream out )
	{
		bol(out);
		if(table)
		{
			out.print("<tr>");
			//out.print("<br>");
		}
	}
	
	void rowend( PrintStream out )
	{
//		if(table) out.print("<td bgcolor=\"CCCCFF\">&nbsp;</td></tr>");
		if(table) out.print("</tr>");
		eol(out);
	}
	
	void dataopen( PrintStream out )
	{
		if(table) out.print("<td bgcolor=\"FFFFFF\" valign=top>");
	}
	
	void dataclose( PrintStream out )
	{
		if( table) out.print("</td>");
	}
	
	void separate( PrintStream out )
	{
		rowbeg(out);
		label(out,null);
		dataopen(out);
		out.print("&nbsp;");
		dataclose(out);
		rowend(out);
	}
	
	void label( PrintStream out, String s )
	{
		if(table)
			out.print("<td align=right valign=top bgcolor=\"CCCCFF\"><font color=\"#151B8D\"><b>");
		else
			out.print("<font size=\"+1\"><b>");
		
		if( s == null )
			out.print("&nbsp;");
		else
			out.print(s+":&nbsp;");
			
		if(table)
			out.print("</b></font></td>");
		else
			out.print("</b></font>");

		//dataopen(out);
		//out.print("&nbsp;");
		//dataclose(out);
	}

	void listComments( PrintStream out )
	{
		if( (notes.size()+gms.size()+weddings.size()+kills.size()+events.size()) > 0 )
		{
			rowbeg(out);
			label(out,"Miscellaneous");
			
			dataopen(out);
			listNotes(out,notes,"Comments");
			listNotes(out,gms,"GM Notes");
			listNotes(out,events,"Quests/Events");
			listNotes(out,weddings,"Weddings");
			listNotes(out,kills,"NPCs killed");
			
	//		out.println("<i>");
			
	//		out.println();
	//		out.println();
	//		out.println("<!-- list comments here -->");
	//		out.println();
	//		out.println();
	
	//		out.println("</i>");
			
			dataclose(out);
			
			rowend(out);
			separate(out);
		}
	}	

	void listMajorIssues( PrintStream out )
	{
		int num = bugs.size()+harass.size()+warns.size()+exploit.size()+issues.size();
		
		if( num > 0 )
		{
			rowbeg(out);
			label(out,"Major issues");
			dataopen(out);
			listNotes(out,bugs,"Bug Reports");
			listNotes(out,warns,"Warnings");
			listNotes(out,harass,"Harassment Issues");
			listNotes(out,exploit,"Exploits");
			listNotes(out,issues,"Issues");
			dataclose(out);
			rowend(out);
			separate(out);
		}
	}	

	void listNotes( PrintStream out, Vector v, String title )
	{
		int len = v.size();
		
		if( len > 0 )
		{
			out.print("<p><b>"+title+"</b></p>");
			out.print("<ul>");
			
			for( int i=0; i<len; ++i )
			{
				out.print("<li>"+(String)v.elementAt(i)+"</li>");
			}
			
			out.print("</ul>");
		}
	}

	void listZones( PrintStream out )
	{
		rowbeg(out);
		label(out,"Zones visited");
		dataopen(out);

		int len = zones.size();
		for( int i=0; i<len; ++i )
		{
			String zone = (String) zones.elementAt(i);
			
			if( i > 0 )
			{
				if( (i+1) == len )
					out.print(" & ");
				else
					out.print(", ");
			}
			
			out.print(zone);
		}

		dataclose(out);
		rowend(out);
	}	

	void listMinorIssues( PrintStream out )
	{
		int num = badnames.size()+names.size()+feedback.size();
		
		if( num > 0 )
		{
			rowbeg(out);
			label(out,"Minor issues");
			dataopen(out);
			listNotes(out,badnames,"Bad Names");
			listNotes(out,names,"Visits from the Name Fairy");
			listNotes(out,feedback,"Feedback");
			dataclose(out);
			rowend(out);
			separate(out);
		}
	}	

	String getElapsed()
	{
		long msec = currentDate.getTime() - startDate.getTime();
		int min = (int) (msec/60000);
		int hours = min/60;
		min = min % 60;

		return hours+"h "+min+"m";
	}

	void listHours( PrintStream out )
	{
		rowbeg(out);
		label(out,"Shift time");
		
		dataopen(out);
		out.print(startDate.toLocaleString()+" to "+currentDate.toLocaleString());
		dataclose(out);
		rowend(out);
	
		rowbeg(out);
		label(out,"Number of hours worked");
		
		dataopen(out);
		out.print(getElapsed());
		dataclose(out);
		rowend(out);
	}
	
	long roundTime( long tim )
	{
		long min = (tim+150000)/300000;
		return min * 300000;
	}
	
	void listPetitions( PrintStream out )
	{
		rowbeg(out);
		label(out,"Number of petitions handled");
		dataopen(out);
		out.print(pets);
		dataclose(out);
		rowend(out);

		if( urgent != 0 )
		{
			rowbeg(out);
			label(out,"Escalated to yellow");
			dataopen(out);
			out.print(urgent);
			dataclose(out);
			rowend(out);
		}

		if( emergency != 0 )
		{
			rowbeg(out);
			label(out,"Escalated to red");
			dataopen(out);
			out.print(emergency);
			dataclose(out);
			rowend(out);
		}
	}
	
	void listGuides( PrintStream out )
	{
		Vector ranks = new Vector();
		Vector rankers = new Vector();
		
		int ulen = users.size();
		int i;
		
		for( i=0; i<ulen; ++i )
		{
			User u = (User) users.elementAt(i);
			
			if( u.rank != null )
			{
				if( !ranks.contains(u.rank) )
				{
					ranks.addElement(u.rank);
					rankers.addElement( new Vector() );
				}
				
				int ri = ranks.indexOf(u.rank);
				Vector rv = (Vector) rankers.elementAt(ri);
				rv.addElement( u );
			}
		}
		
		ulen = ranks.size();
		
		for( i=0; i<ulen; ++i )
		{
			String rank = (String) ranks.elementAt(i);
			Vector rv = (Vector) rankers.elementAt(i);
			int nr = rv.size();
			
			rowbeg(out);
			
			if( nr == 1 )
				label( out, rank );
			else
				label( out, rank+'s' );

			dataopen(out);
			
			for( int j=0; j<nr; ++j )
			{
				User u = (User) rv.elementAt(j);
				if( j > 0 )
				{
					if( (j+1) == nr )
						out.print(" & ");
					else
						out.print(", ");
				}
				
				out.print( u.name );
			}

			dataclose(out);			

			rowend(out);
		}
	}

	private static void debug( String s )
	{
		if( debug )
		{
			System.err.println("DSR: "+s);
		}
	}
}

