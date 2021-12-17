import java.io.*;
import java.util.Vector;

public class Record
{
	static Vector data = new Vector();
	static StringBuffer sb = new StringBuffer();
	String [] fields;

    static final int FIRST=0;
    static final int MIDDLE=1;
    static final int LAST=2;
    static final int CITY=3;
    static final int STATE=4;
    static final int COUNTRY=5;
    static final int EMAIL=6;
    static final int HOMEPAGE=7;
    static final int PAGENAME=8;
    static final int ORGPAGE=9;
    static final int ORG=10;
    static final int NOTES=11;
    static final int COMMENTS=12;
    static final int NEW=13;

	public void read( InputStream in ) throws EOFException, IOException
	{
    	int ich;

    	readMe:

    	for(;;)
    	{
    		ich = in.read();

    		switch( ich )
    		{
                case -1: 
                	throw new EOFException();

                case '\t':
                	nextField();
                	break;

                case '\n':
                case '\r':
                	if( data.size() > 0 )
                	{
                		nextField();
                		break readMe;
                	}
                	
                	else break;

                default:
                	sb.append((char)ich);
                	break;
    		}
    	}

    	int count = data.size();
    	
    	//System.err.println("field count="+count);

    	int i;
    	fields = new String[count];

    	for( i=0; i<count; ++i ) 
    	{
    		fields[i] = (String) data.elementAt(i);
    		//System.err.println("fields["+i+"]=\""+fields[i]+"\"");
    	}

    	data.setSize(0);
	}

	private void nextField()
	{
		data.addElement( new String(sb) );
		sb.setLength(0);
	}
    
    public String getOrg()
    {
    	String pageName;

    	if( fields[ORG].length() > 0 && fields[ORGPAGE].length() > 0 )
			pageName = "<a href=\""+fields[ORGPAGE]+"\">"+fields[ORG]+"</a>";
		else if( fields[ORG].length() > 0 )
			pageName = fields[ORG];
		else if( fields[ORGPAGE].length() > 0 )
			pageName = "<a href=\""+fields[ORGPAGE]+"\">organization</a>";
		else
			pageName = "&nbsp;";
		
		return pageName;
    }

	public String getName()
	{
		String name;

		if( fields[MIDDLE].length() != 0 )
			name = fields[FIRST]+" "+fields[MIDDLE]+" "+fields[LAST];
		else
			name = fields[FIRST]+" "+fields[LAST];

/*		if( fields[HOMEPAGE].length() != 0 )
			name = "<A HREF=\""+fields[HOMEPAGE]+"\">"+name+"</A>";
		else */ if( fields[EMAIL].length() != 0 )
			name = "<A HREF=\"mailto:"+fields[EMAIL]+"\">"+name+"</A>";

		return name;
	}
    
    public String getPage()
    {                          
    	String pageName;

    	if( fields[PAGENAME].length() > 0 && fields[HOMEPAGE].length() > 0 )
			pageName = "<a href=\""+fields[HOMEPAGE]+"\">"+fields[PAGENAME]+"</a>";
		else if( fields[PAGENAME].length() > 0 )
			pageName = fields[PAGENAME];
		else if( fields[HOMEPAGE].length() > 0 )
			pageName = "<a href=\""+fields[HOMEPAGE]+"\">home page</a>";  
		else
			pageName = getOrg();
		
		return pageName;
    }                                                             
    
    public String getMail()
    {
    	return (fields[EMAIL] == null) ? "" : fields[EMAIL];
    }

	public String getCity()
	{
		return (fields[CITY] == null) ? "" : fields[CITY];
	}

	public String getCountry()
	{
		return (fields[COUNTRY] == null) ? "" : fields[COUNTRY];
	}

	public String getState()
	{
		return (fields[STATE] == null) ? "" : fields[STATE];
	}

	public String getNotes()
	{
		String notes = fields[NOTES];
        String comments = fields[COMMENTS];
        String s = "";
        boolean qnotes, qcomments;

        qnotes = notes.length() != 0;
        qcomments = comments.length() != 0;

		if( qnotes || qcomments )
		{
			if( qnotes )
				s = notes;
			if( qcomments )
			{
				if( s.length() != 0 )
					s = s + "\n<P>\n";
				else
					s = "";
				s = s + "<I>" + comments + "</I>";
			}
		}
		
		return s;
	}
	
	public boolean isNew()
	{
		return fields[NEW].equals("Yes");
	} 

	public boolean sameCountry( Record r )
	{                                                   
		if( r == null )
			return false;
		else
			return fields[COUNTRY].equals(r.fields[COUNTRY]);
	}

	public boolean sameState( Record r )
	{
		if( r == null )
			return false;
		else
			return sameCountry(r) && (fields[STATE].equals(r.fields[STATE]));
	}

}
