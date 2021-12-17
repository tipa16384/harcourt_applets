import java.awt.*;
import java.applet.*;

public class Horsey extends Panel
{
	Main main;
	GraphInfo info;
	
	Content content;
	
	public Horsey( Main main, GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.main = main;
		this.info = info;
		
		//testGauss();
		
		Component header = new Header(main,info);
		add( header, BorderLayout.NORTH );
		
		Component footer = new Footer(main,info);
		add( footer, BorderLayout.SOUTH );

		content = new Content(main,info);
		add( content, BorderLayout.CENTER );

		invalidate();
		doLayout();
		repaint();

		main.reset();
	}
	
	// run the race
	public void doRace()
	{
		//System.out.println("Running a race");
		content.doRace();
	}

	void testGauss()
	{
		Gauss gauss = new Gauss( 5.0, 10.0 );
		for( int i=0; i<20; ++i )
			System.out.println( gauss.getValue() );
	}
}
