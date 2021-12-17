import java.awt.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;
import util.RapidLabel;

public class Match extends Panel
					 implements PropertyChangeListener
{
	Main main;
	GraphInfo info;
	
	static public final Color backgroundColor = new Color(0xFF,0xFF,0xCC);
	
	public Match( Main main, GraphInfo info )
	{
		super( new BorderLayout(5,5) );
		
		this.main = main;
		this.info = info;

		setBackground( backgroundColor );
		setForeground( Color.black );
		setFont( info.fontPlain );
		
		add( new Instructions(), BorderLayout.NORTH );
		add( new Items( main, info ), BorderLayout.WEST );
		add( new Facts( main, info ), BorderLayout.CENTER );
				
		info.addPropertyChangeListener(this);
		
		info.firePropertyChange( info.reset_applet, 0, 1 );
	}
	
	public Color getColor()
	{
		return backgroundColor;
	}

	public void propertyChange( PropertyChangeEvent pce )
	{
		String pname = pce.getPropertyName();
		
		//System.out.println("Received event "+pname);
	}
	
	public Main getMain()
	{
		return main;
	}
	
	class Instructions extends RapidLabel
	{
		public Instructions()
		{
			setForeground( Color.black );
			setBackground( new Color(0xCC,0xCC,0x99) );
			setFont( info.fontBigBold );
			
			String inst = main.getParameter("Instructions");
			if( inst == null || inst.length() == 0 )
				inst = "**** Instructions not found! ****";
				
			setText(inst);
			setAlignment(Label.LEFT);
		}
		
		public int getTextGap()
		{
			return 2;
		}
	}
}
