import java.awt.*;
import java.util.*;

public class BetEntry extends Panel
{
	GraphInfo info;
	
	public BetEntry( GraphInfo info )
	{
		super( new BorderLayout() );
		
		this.info = info;
		setFont( info.fontPlain );
		
		add( new Button("BET"), BorderLayout.EAST );
		
		Panel p, p1;
		Choice c;
		int i;
		
		Horse [] horses = info.getHorses();
		c = new Choice();
		
		for( i=0; i<horses.length; ++i )
		{
			c.add( horses[i].getName() );
		}
		
		p = new Panel( new BorderLayout() );
		add( p, BorderLayout.CENTER );
		
		p.add( c, BorderLayout.NORTH );
		
		p1 = new Panel( new BorderLayout() );
		p.add( p1, BorderLayout.SOUTH );
		
		c = new Choice();
		c.add( Bet.WIN );
		c.add( Bet.PLACE );
		c.add( Bet.SHOW );
		p1.add( c, BorderLayout.EAST );
		
		p1.add( new TextField(6), BorderLayout.CENTER );
		p1.add( new Label("$"), BorderLayout.WEST );
	}
}
