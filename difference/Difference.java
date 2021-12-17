import java.awt.*;
import java.applet.*;
import util.DoubleBufferPanel;

public class Difference extends DoubleBufferPanel
{
	Main main;
	GraphInfo info;
	
	public Difference( Main main, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.main = main;
		this.info = info;
		
		add( new Graph2(info), BorderLayout.CENTER );
		add( new Controls(info), BorderLayout.SOUTH );
		
		invalidate();
		doLayout();
		repaint();

		main.reset();
	}
}
