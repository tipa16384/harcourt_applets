import java.awt.*;
import java.util.Random;

public class Damage
{
	Weapon [] weaps =
		{
			// 1H Blunt
			new Weapon("Cracked Staff",5,32),
			new Weapon("Staff",5,28),
			new Weapon("Jagged Pine Crook*",5,32),
			new Weapon("Darkwood Staff*",5,28),
			new Weapon("FS Hammer",6,30),
			new Weapon("Mace",7,37),
			new Weapon("Flail",7,36),
			new Weapon("Morning Star",8,40),
			new Weapon("FS Morning Star",8,38),
			new Weapon("Combine Morning Star*",8,38),
			new Weapon("Combine Warhammer*",6,30),
			new Weapon("Screaming Mace*",8,35),
			new Weapon("Scepter of Rahotep*",9,38),
			new Weapon("Dwarven Mace",8,38),
			new Weapon("Dwarven Warhammer",7,33),
			new Weapon("Hulking Spiked Club",6,26),
			new Weapon("Rod of Drones*",6,30),
			new Weapon("Orcish Mace",7,39),
			new Weapon("Scepter of Flame*",7,29),
			new Weapon("Enc. FS Morning Star*",7,30),
			new Weapon("Black Enameled Mace*",8,28),
			new Weapon("Tainted Battleworn MS*",7,30),
			new Weapon("Runed Battleworn MS*",7,33),
			new Weapon("Cold Iron MS*",9,35),
			new Weapon("Gem Encrusted Scepter*",7,28),
			new Weapon("Sharkbone Hammer*",8,26),
			
			// 2H Blunt
			new Weapon("Bloodforge Hammer*",13,50),
			new Weapon("Runed Totem Staff*",9,37),
			new Weapon("Wee Harvester*",10,45),
			new Weapon("Great Staff",9,38),
			new Weapon("FS Great Staff",9,36),
			new Weapon("Combine Great Staff*",9,36),
			new Weapon("Hammer",13,51),
			new Weapon("Sap Sheen Staff*",10,34),
			new Weapon("Staff of the Observers*",12,35),
			new Weapon("Enc. Tier'Dal Great Staff*",15,42),
			new Weapon("Glowing Wooden Crook*",11,35),
			new Weapon("Ogre War Maul",17,50),
			new Weapon("Treant Staff*",10,35),
			new Weapon("Runed Tier'Dal Great Staff*",16,48),
			new Weapon("Heirophant's Crook*",20,44),
			new Weapon("Staff of Earth Crafters*",21,48),
			new Weapon("Staff of Forbidden Rites*",25,50),
			
			// 1H slash
			new Weapon("Tarnished Scimitar",5,33),
			new Weapon("Scimitar",5,25),
			new Weapon("FS Scimitar",5,24),
			new Weapon("Combine Scimitar*",5,24),
			new Weapon("Well Balanced Scimitar*",5,21),
			new Weapon("Oaken Scimitar*",7,26),
			new Weapon("Silvery Scimitar*",6,22),
			new Weapon("Solvedi Scimitar*",6,22),
			new Weapon("Obsidian Scimitar*",7,27),
			new Weapon("Forged Bastard Sword",7,33),
			new Weapon("Runed Falchion*",11,34),
			new Weapon("Scimitar of Mistwalker*",10,25),
			new Weapon("Tunarian Scimitar*",8,19)
		};
		
	public static void main( String [] args )
	{
		int len = args.length;
		int i;
		
		try
		{
			int hpval = Integer.parseInt(args[0]);

			Damage dam = new Damage();
			for( i=0; i<dam.weaps.length; ++i )
			{
				Weapon w = dam.weaps[i];
				dam.calc(w.name,w.damage,w.delay,hpval);
			}
		}
		
		catch( Exception e )
		{
			System.err.println("argument error :- "+e);
		}
	}
	
	final Random rand = new Random();
	final int ntrials = 1000;
	final int ncases = 5;
	
	void calc( String name, int dam, int dly, int hp )
	{
		System.out.print(name);
		
		double fdam = (double)dam;
		
		for( int i = 0; i < ncases; ++i )
		{
			double frate = ((double)i)/((double)ncases);
			int t0 = 0;
			
			for( int j = 0; j < ntrials; ++j )
			{
				int mobhp = hp;
				int t = 0;
				
				while( mobhp > 0 )
				{
					if( rand.nextDouble() >= frate )
					{
						double dam1 = fdam*rand.nextDouble() +
									  fdam*rand.nextDouble();
						mobhp -= (int) Math.round(dam1);
					}
					
					if( mobhp > 0 )
						++t;
				}
				
				t0 += t;
			}

			double s1 = ((double)(t0*dly)) / ((double)ntrials * 10.0);

			System.out.print(","+Math.round(s1));
		}
		
		System.out.println();
	}

	class Weapon
	{
		String name;
		int damage;
		int delay;
		
		public Weapon( String name, int damage, int delay )
		{
			this.name = name;
			this.damage = damage;
			this.delay = delay;
		}
		
		public String toString()
		{
			return name+"("+damage+","+delay+")";
		}
	}
}
