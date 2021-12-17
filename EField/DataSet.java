package Graph;

/**
*/

import java.awt.Color;
import java.util.Vector;
import	java.util.EventListener;

// Referenced classes of package:
//            DataPoint

public class DataSet extends Vector
{
				//The types (Styles) of graphs drawn
    public static final int DOT = 0;
    public static final int CIRCLE = 1;
    public static final int TRIANGLE = 2;
    public static final int LINE = 3;
    public static final int FILL = 4;

    public int symType = 3;
    public int symSize = 2;
    public Color symColor = Color.black;

	Vector	listeners = new Vector();			//array of those listening to me
	Vector	eventQueue = new Vector();			//queue of events to tell others to process
	boolean	processing = false;					//whether we are currently having events handled
    
    public DataSet()
    {
        symColor = Color.black;
        symType = 0;
        symSize = 2;
    }

    public void setGraphDisplay(int type, int size, Color c)
    {
        symType = type;
        symSize = size;
        symColor = c;
    }

    public void addDataPoint(double x, double y)
    {
        addElement(new DataPoint(x, y));
        sendDataEvent( DataEvent.UPDATE );
    }

    public void addDataPoint(DataPoint dp)
    {
        addElement(dp);
        sendDataEvent( DataEvent.UPDATE );
    }
    
    public void setType( int type )
    {
    	symType = type;
    }

    public int getType()
    {
    	return( symType );
    }

    public void setSymSize( int size )
    {
    	symSize = size;
    }

    public int getSymSize()
    {
    	return( symSize );
    }

    public void setColor( Color c )
    {
    	symColor = c;
    }

    public Color getColor()
    {
    	return( symColor );
    }


		/** send an event to all the waiting listeners 
		 *	that announces that the DataSet has been changed
		 */
 
	public void sendDataEvent( int id )
	{
		sendDataEvent( id, false );
	}

	public void sendDataEvent( int id, boolean first )
	{
		sendDataEvent( new DataEvent(id,this,null), first );
	}

	public void sendDataEvent( DataEvent e )
	{
		sendDataEvent( e, false );
	}

					//high priority events can be inserted first at top of queue
	public void sendDataEvent( DataEvent e, boolean first )
	{
		if( listeners.size() == 0 )			//check for anyone listening
			return;

		boolean shouldWeProcess = false;

		synchronized( eventQueue )			//enQ this event
		{
			if( first )
				eventQueue.insertElementAt( e, 0 );
			else
				eventQueue.addElement( e );
			shouldWeProcess = !processing;
			processing = true;
		}
		if( shouldWeProcess )
		{
			doEventLoop: for(;;)
			{
				synchronized( eventQueue )
				{
					if( eventQueue.size() == 0 )
					{
						processing = false;
						break doEventLoop;
					}
					e = (DataEvent)eventQueue.firstElement();
					eventQueue.removeElement( e );
				}
				Vector v;					//keep a local copy
				int len;					// so we can run unsync
				synchronized( listeners )
				{
					v = (Vector)listeners.clone();
					len = v.size();
				}
				for( int i = 0; i < len; ++i )
				{
					((DataListener)v.elementAt(i)).processDataEvent( e );
				}
			}
		}
	}

	public void addDataListener( DataListener dl )
	{
		if( !listeners.contains(dl) )
			listeners.addElement( dl );
	}

	public void removeDataListener( DataListener dl )
	{
		if( listeners.contains(dl) )
			listeners.removeElement( dl );
	}

}


public class DataEvent extends Object
{
	static public final int
		UPDATE = 0,
		LASTENTRY = 1;
	
	static private String[] names = {
		"UPDATE"
		};
		
	private int id;							// the specific event
	private Object arg1;					// random object.
	private Object arg2;					// another random object.

	public DataEvent( int id, Object obj1, Object obj2 )
	{
		this.id = id;
		this.arg1 = obj1;
		this.arg2 = obj2;
	}
	
	public DataEvent( int id )
	{
		this( id, new Object(), new Object() );
	}
	
	public int getID()
	{
		return id;
	}
	
	public Object getArgument1()
	{
		return arg1;
	}
	
	public Object getArgument2()
	{
		return arg2;
	}
	
	private String idString()
	{
		if( id >= 0 && id < LASTENTRY )
			return names[id];
		else
			return "UNKNOWN("+id+")";
	}
	
	public String toString()
	{
		return getClass().getName()+"["+idString()+"]";
	}
}



/**
 * The listener interface for receiving action events. 
 *
 * @version 1.6 11/23/96
 * @author Carl Quinn
 */
public interface DataListener extends EventListener
{

    /**
     * Invoked when an action occurs.
     */
    public void processDataEvent( DataEvent e );

}
