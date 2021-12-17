import java.awt.*;
import java.applet.*;

public class Helena extends Panel
{
	Main main;
	GraphInfo info;
	
	public Helena( Main main, GraphInfo info )
	{
		super( new BorderLayout() );
		
		ControlPanel controls = new ControlPanel(main,info);
		GraphPanel graphs = new GraphPanel(main,info);
		
		this.main = main;
		this.info = info;

		controls.addPointEventListener( graphs );
		
		add( controls, BorderLayout.NORTH );
		add( graphs, BorderLayout.CENTER );
		
		main.reset();
	}
}
