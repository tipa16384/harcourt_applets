/*
	Trivial applet that displays a string - 4/96 PNL
*/

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.Random;
import util.DoubleBufferPanel;

public class ShipOfFools extends Applet
{
	String lab;
	Random ran;
	Component background;
	boolean switchState = false;
	
	public void start()
	{
		Panel p;
		Component c;
		Dimension dim;
		Dimension size = getSize();
		
		Utility.setApplet(this);
		
		setLayout( null );

		p = new DoubleBufferPanel(null);
		p.setLocation(0,0);
		p.setSize(size.width,size.height);
		add( p );
		
		p.add( background = new Background() );
		p.add( c = new RollOne() );
		
		dim = c.getPreferredSize();
		c.setSize(dim.width,dim.height);
		c.setLocation( (size.width-dim.width)/2, size.height-dim.height-10 );
		
		p.add( c = new Swap() );
		
		dim = c.getPreferredSize();
		c.setSize(dim.width,dim.height);
		c.setLocation( size.width-dim.width-10, size.height-dim.height-10 );

		lab = "Insert Witty Name Here";

		ran = new Random();
		rollOne();
	}

	class Swap extends Button
	{
		public Swap()
		{
			super("Swap");
			
			addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					switchState = !switchState;
					rollOne();
				}
			} );
		}
	}

	class RollOne extends Button
	{
		public RollOne()
		{
			super("Choose another name");

			addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					rollOne();
				}
			} );
		}
	}

	class Background extends Component
	{
		Image image;
	
		public Background()
		{
			setSize(640,480);
			setLocation(0,0);
			setFont( new Font("Arial",Font.PLAIN,24) );
			setForeground( new Color(0,150,255) );
			
			image = Utility.getImage(this,"Jenerik.jpg");
		}	

		public void paint( Graphics g )
		{
			g.drawImage( image, 0, 0, this );
			g.setColor( getForeground() );
			g.setFont( getFont() );
			
			FontMetrics fm = getFontMetrics(getFont());
			int sw = fm.stringWidth(lab);
			g.drawString( lab, 330-sw/2, 60 );
		}
	}

	public void rollOne()
	{
		int a = Math.abs(ran.nextInt()) % group.length;
		int b = Math.abs(ran.nextInt()) % fools.length;
		
		if( !switchState )
		{
			String s = fools[b];
			
			int len = s.length();
			if( len > 0 )
			{
				char ch = s.charAt(len-1);
				if( ch == '\'' )
					s = s.substring(0,len-1);
				else if( len > 1 )
				{
					ch = s.charAt(len-2);
					if( ch == '\'' )
						s = s.substring(0,len-2);
				}
			}
			
			lab = "Jenerik <"+group[a]+" of "+s+">";
		}
		else
			lab = "Jenerik <"+fools[b]+" "+group[a]+">";
			
		background.repaint();
	}
	
	static String [] group =
	{
		"Assemblage",
		"Assembly",
		"Avatars",
		"Body",
		"Company",
		"Coalition",
		"Conclave",
		"Conference",
		"Congregation",
		"Congress",
		"Convention",
		"Convocation",
		"Crowd",
		"Gathering",
		"Meeting",
		"Muster",
		"Troop",
		"Circle",
		"Crowd",
		"Array",
		"Band",
		"Batch",
		"Bevy",
		"Bunch",
		"Bundle",
		"Cluster",
		"Followers",
		"Ship",
		"Collection",
		"Party",
		"Brothers",
		"Sisters",
		"Scum",
		"Friends",
		"People",
		"Critters",
		"Legend",
		"Peak",
		"Flock",
		"Minions",
		"Defenders",
		"Fellowship",
		"Knights",
		"Legion",
		"League",
		"Lords",
		"Ladies",
		"Children",
		"Order",
		"United",
		"United Coalition",
		"Protectors",
		"Guardians",
		"Tales",
		"Talons",
		"Death",
		"Lives",
		"End",
		"Memory",
		"Temptation",
		"Stragglers",
		"Story",
		"Dreamers"
	};
	
	static String [] fools =
	{
		"Fate",
		"Fate\'s",
		"Seagulls\'",
		"Gnomes\'",
		"The Clock",
		"Seekers\'",
		"Norrath",
		"The Chosen",
		"Velious\'",
		"Kunark\'s",
		"Odus\'",
		"Antonica\'s",
		"Faydwer\'s",
		"Fools\'",
		"The Dead",
		"Friends\'",
		"The Faithful",
		"The Loyal",
		"The Dead",
		"Dragons\'",
		"The Lost",
		"Discord\'s",
		"Order\'s",
		"Friends\'",
		"Crusaders\'",
		"Rage",
		"Obsidian",
		"Justice\'s",
		"Honour\'s",
		"The League",
		"Phat Lewt",
		"Posterity\'s",
		"The Moon",
		"The Sun",
		"The Gods\'",
		"Erollisi\'s",
		"Hate\'s",
		"Love\'s",
		"Fear\'s"
	};
}
