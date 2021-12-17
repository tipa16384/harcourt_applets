import java.awt.*;
import java.util.*;

public class BetDisplay extends Component
{
	Vector bets;
	GraphInfo info;
	
	int width, height;
	int [] columnWidths;
	
	Font font;
	FontMetrics fm;
	int fh;
	
	final static String horseTitle = "Horse";
	final static String positionTitle = "Pos";
	final static String betTitle = "Bet";
	
	public BetDisplay( GraphInfo info, Vector bets )
	{
		this.bets = bets;
		this.info = info;
		
		font = info.fontPlain;
		fm = getFontMetrics(font);
		fh = fm.getHeight();
		columnWidths = new int[4];
		
		setFont(font);
		
		calcDimensions();
	}

	void calcDimensions()
	{
		int i;
		Horse [] horses;
		
		// get the width of column 1 (horse name)
		horses = info.getHorses();
		columnWidths[0] = fm.stringWidth(horseTitle);;
		for( i=0; i<horses.length; ++i )
		{
			String s = horses[i].getName();
			columnWidths[0] = Math.max(columnWidths[0],fm.stringWidth(s));
		}
		columnWidths[0] += 6;
		
		// get the width of column 2 (win, place, show)
		columnWidths[1] = fm.stringWidth(positionTitle);
		columnWidths[1] = Math.max(columnWidths[1],fm.stringWidth(Bet.WIN.substring(0,1)));
		columnWidths[1] = Math.max(columnWidths[1],fm.stringWidth(Bet.PLACE.substring(0,1)));
		columnWidths[1] = Math.max(columnWidths[1],fm.stringWidth(Bet.SHOW.substring(0,1)));
		columnWidths[1] += 6;
		
		// get the width of column 3 (dollar amount)
		columnWidths[2] = fm.stringWidth(betTitle);
		columnWidths[2] = Math.max(columnWidths[2],fm.stringWidth(Main.formatMoney(9999)));
		columnWidths[2] += 6;
		
		// get the width of column 4 (delete button)
		columnWidths[3] = 10;
		
		width = 0;
		for( i=0; i<4; ++i )
			width += columnWidths[i];

		height = 100;
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(width,height);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		
		g.setFont( font );

		g.setColor( GraphInfo.AXIS_COLOR );
		
		int y, x, j, i;
		String s;
		
		y = fm.getAscent();
		
		g.drawLine( 0, fh, dim.width, fh );
		
		x = columnWidths[0];
		
		for( i=1; i<4; ++i )
		{
			g.drawLine( x, 0, x, dim.height );
			x += columnWidths[i];
		}
		
		x = 0;
		
		g.setColor( Color.red );
		
		for( i=0; i<4; ++i )
		{
			int cw = columnWidths[i];
			
			switch( i )
			{
				case 0: s = horseTitle; break;
				case 1: s = positionTitle; break;
				case 2: s = betTitle; break;
				default: s = ""; break;
			}
			
			g.drawString(s,x+(cw-fm.stringWidth(s))/2,y);
			
			x += cw;
		}
		
		g.setColor( Color.black );
		
		int len = bets.size();
		for( j=0; j<len; ++j )
		{
			Bet bet = (Bet) bets.elementAt(j);
			x = 0;
			for( i=0; i<4; ++i )
			{
				int cw = columnWidths[i];
				boolean rj = false;
				boolean cr = false;
				
				switch( i )
				{
					case 0: s = bet.getHorse().getName(); break;
					case 1: s = bet.getPosition().substring(0,1); cr = true; break;
					case 2: s = Main.formatMoney(bet.getAmount()); rj = true; break;
					default: s = ""; break;
				}
				
				if( rj )
					g.drawString(s,x+cw-fm.stringWidth(s)-3,(j+1)*fh+y);
				else if( cr )
					g.drawString(s,x+(cw-fm.stringWidth(s))/2,(j+1)*fh+y);
				else
					g.drawString(s,x+3,(j+1)*fh+y);
				
				x += cw;
			}
		}
	}
}
