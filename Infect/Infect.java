import java.awt.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;

public class Infect extends Panel
					 implements PropertyChangeListener
{
	Main main;
	GraphInfo info;
	
	static public final Color backgroundColor = new Color(0xCC,0xCC,0x99);
	
	public Infect( Main main, GraphInfo info )
	{
		super( new BorderLayout(1,1) );
		
		this.main = main;
		this.info = info;

		setBackground( Color.white );
		setForeground( Color.black );
		setFont( info.fontPlain );
		
		Panel p = new IndentPanel(new BorderLayout(),10,10,10,10);
		p.setBackground( backgroundColor );
		
		p.add( new DataEntryPanel(this,info), BorderLayout.CENTER );
		
		add( p, BorderLayout.NORTH );
		add( new GraphPanel(this,info), BorderLayout.CENTER );
		
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
		
		System.out.println("Received event "+pname);
	}

	public Insets getXInsets()
	{
		return new Insets(5,5,5,5);
	}
	
	public Main getMain()
	{
		return main;
	}
	
	class IndentPanel extends Panel
	{
		Insets insets;
		
		public IndentPanel( LayoutManager lm, int top, int left, int bottom, int right )
		{
			super(lm);
			insets = new Insets(top,left,bottom,right);
		}
		
		public Insets getInsets()
		{
			return insets;
		}
	}
}
