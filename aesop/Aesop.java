import java.awt.*;
import java.applet.*;
import java.beans.*;

public class Aesop extends Panel
				   implements PropertyChangeListener
{
	Main main;
	GraphInfo info;
	
	final static String raceTitle = "Race";
	final static String theoryTitle = "Theory";
	
	RaceScreen race;
	TheoryScreen theory;
	CardLayout layout;
	
	double currentTime = 0.0;
	
	public Aesop( Main main, GraphInfo info )
	{
		super( new CardLayout() );
		layout = (CardLayout) getLayout();
		
		race = new RaceScreen(main,info);
		theory = new TheoryScreen(main,info);
		
		this.main = main;
		this.info = info;

		add( race, raceTitle );
		add( theory, theoryTitle );
		
		info.addPropertyChangeListener( Aesop.this );
		info.firePropertyChange( info.reset_applet, null, null );
	}
	
	public void propertyChange( PropertyChangeEvent pce )
	{
		String prop = pce.getPropertyName();
		
		if( prop.equals(info.time_changed) )
		{
			Double newTime = (Double)pce.getNewValue();
			currentTime = newTime.doubleValue();
		}
		
		if( prop.equals(info.part_one) )
		{
			System.out.println("part one");
			layout.show(this,raceTitle);
			info.firePropertyChange( info.reset_applet, null, null );
		}
		
		if( prop.equals(info.part_two) )
		{
			System.out.println("part two");
			layout.show(this,theoryTitle);
			info.firePropertyChange( info.reset_applet, null, null );
		}
	}
}
