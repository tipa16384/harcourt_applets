import java.awt.*;
import java.applet.*;
import util.DoubleBufferPanel;

public class Tangent extends DoubleBufferPanel
{
	Main main;
	GraphInfo info;
	
	public Tangent( Main main, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.main = main;
		this.info = info;
		
		add( new Graph(info), BorderLayout.CENTER );
		add( new Controls(info), BorderLayout.SOUTH );
		
		invalidate();
		doLayout();
		repaint();

		main.reset();
	}
}
