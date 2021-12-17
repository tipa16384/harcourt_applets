import java.io.*;
import java.util.*;

public class Skillz
{
	private static final String defaultFile = "eqlog_43_Espranza.txt";
	private static final boolean debug = false;

	private static final String months[] = { "Jan", "Feb", "Mar", "Apr", "May",
									  "Jun", "Jul", "Aug", "Sep", "Oct",
									  "Nov", "Dec" };

	private static final String nullString = "null";

	String filename;

	Date startDate, currentDate;
	
	static Vector skills = new Vector();
	static Vector characters = new Vector();
		
	// extra notes
	
	String whoami = null;
	Character thisChar = new Character( nullString );
	
	public static void main(String args[])
	{
		Skillz dsr;
		
		if( args.length == 0 )
		{
			dsr = new Skillz(defaultFile);
			dsr.process();
		}
		
		else
		{
			for( int i=0; i<args.length; ++i )
			{
				dsr = new Skillz(args[i]);
				dsr.process();
			}
		}

		makeDSR();
	}

	public Skillz( String filename )
	{
		this.filename = filename;
	}

	void processSkill( String s )
	{
		int exidx = s.indexOf('!');
		
		if( exidx < 0 ) return;
		
		String skill = s.substring(0,exidx);

		int opnidx = s.indexOf('(');
		int clsidx = s.indexOf(')');
		
		if( opnidx < 0 || clsidx < 0 ) return;
		
		String value = s.substring(opnidx+1,clsidx);
		
		processSkill( skill, value );	
	}
	
	void processSkill( String skill, String value )
	{
		debug("Skill is "+skill+", value="+value);

		int sidx = skills.indexOf(skill);

		if( sidx < 0 )
		{
			sidx = skills.size();
			skills.addElement(skill);
		}

		thisChar.setSkill(sidx,value);
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
				
				else if( (temp=matchMid(s,"You have become better at")) != null )
				{
					processSkill(temp);
				}
				
				else if( (temp=matchEnd(s,"saved.")) != null )
				{
					identity(temp);
				}
				
				else if( (temp=matchMid(s,"You backstab")) != null )
				{
					processBackstab(temp);
				}
			}
			
			else
			{
				debug("Unrecognized line \""+s+"\"");
			}
		}
	}
	
	void processBackstab( String s )
	{
		int li = s.lastIndexOf(" points of damage");
		s = s.substring(0,li);
		
		li = s.lastIndexOf(" for ");
		
		String beast = s.substring(0,li);
		String damage = s.substring(li+5);

		processSkill( "Max Backstab", damage );
	}
	
	void identity( String s )
	{
		if( whoami == null )
		{
			whoami = s;
			debug("I am \""+whoami+"\"");

			Character iam = new Character(s);
			int i = characters.indexOf(iam);
			
			if( i >= 0 )
			{
				iam = (Character) characters.elementAt(i);
				System.out.println("Found old character "+iam);
			}
			
			else
			{
				characters.addElement(iam);
				System.out.println("Created new character "+iam);
			}
	
			iam.merge(thisChar);
			
			thisChar = iam;
		}
	}
	
	String matchMid( String s, String tmpl )
	{
		int idx;
		
		if( (idx=s.indexOf(tmpl)) != -1 )
		{
			String ts = s.substring( idx+tmpl.length()+1, s.length() );
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
	
	void visitedZone( String zone )
	{
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
		
		try
		{
			debug("Opening "+filename);
			
			BufferedReader rdr = new BufferedReader(new FileReader(filename));

			String line;
			
			debug("Reading...");
			
			do
			{
				line = rdr.readLine();
				if( line != null )
				{
					analyze( line );
					++lines;
				}
			}
			while( line != null );
		}
		
		catch( FileNotFoundException e )
		{
			debug(filename+" was not found ("+e+").");
		}
		
		catch( Exception e )
		{
			debug("Unrecognized error - "+e);
		}
		
		finally
		{
			debug(lines+" lines read.");
		}
	}
	
	static void makeDSR()
	{
		try
		{
			PrintStream ps = new PrintStream( new FileOutputStream("SkillReport.txt") );

			listSkills(ps);

			ps.close();
		}
		
		catch( Exception e )
		{
			debug("While writing DSR: "+e);
		}
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
		//ps.print("<td colspan=3 bgcolor=\"CCCCFF\" align=right>");
		ps.print("<font size=\""+size+"\"><"+style+">");
		ps.print(msg);
		ps.print("</"+style+">");
		ps.print("</font>");
		//ps.print("</td>");
		rowend(ps);
	}

	void dsropen( PrintStream ps )
	{
		//ps.println("<table border=0 cellpadding=3 cellspacing=0>");
	}	

	void dsrclose( PrintStream ps )
	{
		//ps.println("</table>");
	}	

	void bol( PrintStream out )
	{
		out.print("<br>");
	}

	void eol( PrintStream out )
	{
		out.println();
	}

	void rowbeg( PrintStream out )
	{
		bol(out);
		//out.println("<tr>");
		//out.print("<br>");
	}
	
	void rowend( PrintStream out )
	{
		//out.print("<td bgcolor=\"CCCCFF\">&nbsp;</td></tr>");
		eol(out);
	}
	
	void dataopen( PrintStream out )
	{
		//out.println("<td bgcolor=\"FFFFFF\" valign=top>");
	}
	
	void dataclose( PrintStream out )
	{
		//out.println("</td>");
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
		//out.print("<td align=right valign=top bgcolor=\"CCCCFF\"><font color=\"#151B8D\"><b>");
		out.print("<font size=\"+1\"><b>");
		
		if( s == null )
			out.print("&nbsp;");
		else
			out.print(s+":&nbsp;");
			
//		out.print("</b></font></td>");
		out.print("</b></font>");

		//dataopen(out);
		//out.print("&nbsp;");
		//dataclose(out);
	}

	static void listSkills( PrintStream out )
	{
		int i, j;
		
		int numChars = characters.size();
		
		for( i = 0; i<numChars; ++i )
		{
			Character ch = (Character) characters.elementAt(i);
			out.print(","+ch.getName());
		}
		out.println();
		
		int len = skills.size();
		for( i=0; i<len; ++i )
		{
			out.print(skills.elementAt(i));
			
			for( j=0; j<numChars; ++j )
			{
				Character ch = (Character) characters.elementAt(j);
				Integer val = ch.getSkill( i );
				
				if( val == null || val.intValue() == 0 )
					out.print(",");
				else
					out.print(","+val);
			}
			
			out.println();
		}
	}

	void listZones( PrintStream out )
	{
	}	

	String getElapsed()
	{
		long msec = currentDate.getTime() - startDate.getTime();
		int min = (int) (msec/60000);
		int hours = min/60;
		min = min % 60;

		return hours+"h "+min+"m";
	}

	long roundTime( long tim )
	{
		long min = (tim+150000)/300000;
		return min * 300000;
	}
	

	private static void debug( String s )
	{
		if( debug )
		{
			System.err.println("Skillz: "+s);
		}
	}
}

class Character
{
	final int skillSize = 100;
	
	String name;
	Vector values = new Vector(skillSize);
	
	public Character( String name )
	{
		setName( name );
		
		for( int i=0; i<skillSize; ++i )
			values.addElement(null);
	}
	
	public String toString()
	{
		return getName();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}

	public void setSkill( int si, String s )
	{
		int val = 0;
		
		try
		{
			val = Integer.parseInt(s);
		}
		
		catch( Exception e )
		{
			val = 0;
		}
		
		setSkill( si, val );	
	}
	
	public void setSkill( int si, Integer val )
	{
		if( val == null )
			setSkill( si, 0 );
		else
			setSkill( si, val.intValue() );
	}
	
	public void setSkill( int si, int val )
	{
//		System.out.println(this+": setSkill("+si+","+val+")");
		
		Integer sv = (Integer) values.elementAt(si);
		
		if( sv != null && val <= sv.intValue() )
			return;
		
		values.removeElementAt( si );
		values.insertElementAt( new Integer(val), si );
	}
	
	public Integer getSkill( int si )
	{
		return (Integer)values.elementAt( si );
	}

	public boolean equals( Object o )
	{
		//System.out.println("Asking if "+this+" equals "+o);
		
		if( o instanceof Character )
			return ((Character)o).getName().equals(getName());
		else
			return super.equals(o);
	}

	public void merge( Character z )
	{
		int len = values.size();
		
		for( int i=0; i<len; ++i )
			setSkill( i, z.getSkill(i) );
	}	
}
