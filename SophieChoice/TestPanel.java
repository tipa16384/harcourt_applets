import java.awt.*;
import java.awt.event.*;

public class TestPanel extends Panel implements ItemListener
{
	String [] english = { "english", "\u00D7one", "two", "three", "four", "five" };
	String [] deutsch = { "deutsch", "\u03B1ein", "zwei", "drei", "vier", "fuenf" };
	String [] espanol = { "espanol", "\u03BBuno", "dos", "tres", "quato", "cinque" };
	String [] nihongo = { "nihongo", "\u03A9ichi", "ni", "san", "yon", "go" };
	
	boolean horiz = false;
	
	public TestPanel()
	{
		super( new GridLayout(2,2) );
		
		add(creation(english));
		add(creation(deutsch));
		add(creation(espanol));
		add(creation(nihongo));
	}

	private SophieChoice creation( String [] slist )
	{
		SophieChoice sc = new SophieChoice(slist[0]);
		sc.setShowLabel(true);
		sc.setLabelOnTop( horiz );
		horiz = !horiz;
		sc.addItemListener(this);
		
		int len = slist.length;
		int i;
		
		for( i=1; i<len; ++i )
			sc.add( slist[i] );
		
		return sc;
	}
	
	public void itemStateChanged( ItemEvent e )
	{
		System.out.println(e.getSource()+" chose "+e.getItem());
	}
}
