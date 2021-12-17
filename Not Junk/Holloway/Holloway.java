import java.io.*; 
import java.util.Date;

public class Holloway
{
	static String HOLLOWAYDB = "holloway.db";
	static String MAIN = "index.html";
	static String COUNTRY = ".html";
	static String about = "<p>While spending a moment or three looking for the name Holloway "+
		"on the web (and being amazed at the number of us out there!), I thought I'd make a "+
		"page of links to what I found... and then that grew into this behemoth, my "+
		"<b>Holloways on the Web</b> pages.</p>\n"+
		"<p>If you'd like to list yourself on this page, <a href=\"#hollowform\">fill out this form</a> "+
		"and I'll make sure it gets in the next update. If you'd like to remove or change information "+
		"about yourself, write me at <a href=\"mailto:brendah@cybergal.com\">brendah@cybergal.com</a>.</p>";
		
	static String apology = "<p>I'm slowly bringing the Holloways on the Web pages up, " +
		"this time without using CGI programs to dynamically show stuff. So I apologize for " +
		"the crude appearance of these pages, but at least they <i>are</i> up again! Thanks " +
		"for your patience.</p><p>The current page can take awhile to load...</p>";

	public static void main( String args[] )
	{                      
		generateMain();
    }

    static void generateMain()
    {
 		FileInputStream in;
        FileOutputStream out;
        PrintWriter p = null;
        boolean hasState = true;
			String country = "", state = "", city = "";
			String countryFile = "";

			boolean inTable = false;
			int colcount = 0;
			final int maxcol = 3;



 		try
 		{
 			String oldState = "Flopersbee";
 			String oldCountry = "Fishmongeria";

        	out = new FileOutputStream(MAIN);
 			in = new FileInputStream(HOLLOWAYDB);
            p = new PrintWriter(out);

            p.println("<HTML><HEAD><TITLE>Holloways on the Web</TITLE></HEAD><BODY>");

            p.println("<p><center><table width=\"100%\" border=4><tr>"+
            	"<th align=center bgcolor=\"#CCFFCC\"><font size=7>Holloways on the Web</font></th></tr></table></center></p>");

			p.println(about);
            p.println(apology);

			p.println("<p align=center><b>This page last updated "+(new Date())+".</b></p>");

            p.println("<p><center><table width=\"90%\" border=0>");

			boolean colorme = true;

 			for(;; colorme = !colorme )
 			{
 				Record r = new Record();

 				r.read(in);

 				String s;
 				String notes = r.getNotes();
 				country = r.getCountry();
 				state = r.getState();
                city = r.getCity();

                String td = colorme ? "valign=top bgcolor=\"#CCFFCC\""
                					 : "valign=top";
                String tds = "<td " + td + ">";
                String etd = "</td>";

 				p.print("<tr>");

 				p.print(tds+r.getName()+etd+tds);

                if( city.length() > 0 && state.length() > 0 )
                	p.print(city+", "+state);
                else if( city.length() > 0 )
                	p.print(city);
                else if( state.length() > 0 )
                	p.print(state);
                else
                	p.print("&nbsp;");
                p.print(etd+tds);
                if( country.length() > 0 )
                	p.print(country);
                else
                	p.print("&nbsp;");

                p.print(etd);


                p.print(tds+r.getPage()+etd);

                if( notes.length() > 0 )
                	p.print("</tr><tr><td "+td+" colspan=4><font size=-1>"+notes+"</font>"+etd);

 				p.println("</tr>");
 			}
 		}

 		catch( EOFException e )
 		{
 			//System.out.println("Reached end of file.");
 		}

 		catch( Throwable e )
 		{
 			System.err.println("Caught "+e+" while reading.");
 		}

		finally
		{
			if( p != null )
			{
				if( inTable )
				{
					if( colcount > 0 )
					{
						for( ; colcount < maxcol; ++colcount )
							p.print("<td></td>");
						p.println("</tr>");
					}
				}

				p.println("</table></center>"+hollowform+postscript+"</body></html>");
				p.flush();
			}
		}

	}

	static String removeSpaces( String s )
	{
		StringBuffer sb = new StringBuffer();
		int i, len;
		
		len = s.length();
		for( i=0; i<len; ++i )
		{
			char ch = s.charAt(i);
			if( Character.isLetter(ch) )
				sb.append(ch);
		}                     
		
		return new String(sb);
	}

	static String hollowform =
"<a name=\"hollowform\"></a>\n<center><table border=2 cellpadding=5><tr><td align=center bgcolor=\"#CCFFCC\"><h2>Join the Holloways on the Web!</h2>"+
"<FORM METHOD=POST ACTION=\"http://www.mbay.net/cgi-bin/FormMail\">\n"+
"<input type=hidden name=\"recipient\" value=\"brendah@cybergal.com\">\n"+
"<input type=hidden name=\"subject\" value=\"Holloways on the Web\">\n"+
"\n"+
"<p>Enter your real (first and last) name:<br><input type=text name=\"realname\" size=40>\n"+
"<p>And your e-mail address:<br><input type=text name=\"email\" size=40>\n"+
"<p>Where you live (City+State/Province/Country or whatever)<br>\n"+
"<input type=text name=\"location\" size=40>\n"+
"<p>If you have a personal home page, please enter its URL here:<br>\n"+
"<input type=text name=\"homepage\" size=40>\n"+
"<p>If you work or go to school or have some other affiliation you'd like to add,\n"+
"please add it here.\n"+
"<p>Organization:<br><input type=text name=\"organization\" size=40>\n"+
"<p>Address of its web page (if any):<br><input type=text name=\"orgurl\" size=40>\n"+
"<p>Is there anything you'd like to add to your entry?\n"+
"<p>Notes:<br><textarea name=\"notes\" rows=5 cols=40></textarea>\n"+
"\n"+
"<input type=hidden name=\"title\" value=\"Thanks for Writing!\">\n"+
"<input type=hidden name=\"return_link_url\" value=\"http://www.mbay.net/~brendah/holloway/\">\n"+
"<input type=hidden name=\"return_link_title\" value=\"Back to Holloways on the Web\">\n"+
"\n"+
"<p><input type=submit value=\"Add Me!\"> <input type=reset value=\"Whoops!\">\n"+
"</form></td></tr></table></center>\n";

	static String postscript =
"<base href=\"http://www.mbay.net/~brendah/\">\n"+
"<br clear=all>\n"+
"<hr>\n"+
"<center>\n"+
"<table align=center>\n"+
"<tr><td align=center><a href=\"music/music.htm\"><img src=\"gifs/music.gif\" width=89 height=57 border=0 vspace=5 hspace=5><br>Music</a></td>\n"+
"<td align=center><a href=\"3d/3d.htm\"><img src=\"gifs/3d.gif\" width=45 height=57 border=0 vspace=5 hspace=5><br>3D</a></td>\n"+
"<td align=center><a href=\"family/family.htm\"><img src=\"gifs/family.gif\" width=55 height=57 border=0 vspace=5 hspace=5><br>Family</a></td>\n"+
"<td align=center><a href=\"projects/projects.htm\"><img src=\"gifs/projects.gif\" width=45 height=57 border=0 vspace=5 hspace=5><br>Projects</a></td>\n"+
"<td align=center><a href=\"articles/\"><img src=\"gifs/articles.gif\" width=50 height=57 border=0 vspace=5 hspace=5><br>Articles</a></td>\n"+
"<td align=center><a href=\"index.html\"><img src=\"gifs/home.gif\" width=85 height=57 border=0 vspace=5 hspace=5><br>Home</a></td>\n"+
"</table>\n"+
"</center>\n"+
"\n"+
"<p align=center>You are visitor <IMG SRC=\"/cgi-bin/nph-count?link=http://www.mbay.net/~brendah/holloways\">\n"+
"<br><font size=-1>write me at <em><a href=\"mailto:brendah@cybergal.com\">brendah@cybergal.com</a></em></font>\n"+
"</p>\n";

}
