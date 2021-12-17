import java.awt.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;

public class Squares extends Panel
					 implements PropertyChangeListener
{
	Main main;
	GraphInfo info;
	
	static final int numPoints = 4;
	
	public Squares( Main main, GraphInfo info )
	{
		super( new BorderLayout(5,5) );
		
		Panel p;
		
		this.main = main;
		this.info = info;

		setBackground( new Color(204,204,255) );
		setForeground( Color.black );
		setFont( info.fontPlain );
		
		add( new GraphPanel(this,info), BorderLayout.CENTER );
		
		p = new Panel( new BorderLayout(5,5) );
		p.add( new DataEntryPanel(this,info), BorderLayout.WEST );
		p.add( new ButtonPanel(this,info), BorderLayout.CENTER );
		p.add( new MessagePanel(this,info), BorderLayout.SOUTH );
		add( p, BorderLayout.SOUTH );
		
		info.addPropertyChangeListener(this);
		
		info.firePropertyChange( info.reset_applet, 0, 1 );
	}

	public void propertyChange( PropertyChangeEvent pce )
	{
		String pname = pce.getPropertyName();
		
		System.out.println("Received event "+pname);
	}

	public static int getNumPoints()
	{
		return numPoints;
	}

	public Insets getInsets()
	{
		return new Insets(5,5,5,5);
	}
	
	public Main getMain()
	{
		return main;
	}
}
