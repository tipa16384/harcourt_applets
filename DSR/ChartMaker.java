import java.io.*;
import java.util.*;

public class ChartMaker
{
	final static String chartFile = "chart.txt";

	final static int reso = 5;
	final static int maxlevel = 60;
	final static int divs = maxlevel/reso;
	
	final static String equiv[][] = {
		{ "Monk", "Disciple", "Master", "Grandmaster" },
		{ "Rogue", "Rake", "Assassin", "Blackguard" },
		{ "Enchanter", "Illusionist", "Beguiler", "Phantasmist" },
		{ "Shaman", "Mystic", "Oracle", "Luminary" },
		{ "Ranger", "Pathfinder", "Outrider", "Warder" },
		{ "Druid", "Wanderer", "Preserver", "Hierophant" },
		{ "Magician", "Elementalist", "Conjurer", "Arch Mage" },
		{ "Wizard", "Channeler", "Sorcerer", "Evoker" },
		{ "Cleric", "Vicar", "Templar", "High Priest" },
		{ "Bard", "Troubador", "Troubadour", "Minstrel", "Virtuoso" },
		{ "Paladin", "Cavalier", "Knight", "Crusader" },
		{ "Warrior", "Champion", "Myrmidon", "Warlord" },
		{ "Shadow Knight", "Reaver", "Revenant", "Grave Lord" },
		{ "Necromancer", "Heretic", "Defiler", "Warlock" }
		};
	
	Vector users = new Vector();
	
	public ChartMaker()
	{
		User.readUsers(users);
	}
	
	public void generate()
	{
		Vector race = new Vector();
		Vector job = new Vector();
		
		int len = users.size();
		int i;
		
		for( i=0; i<len; ++i )
		{
			User u = (User) users.elementAt(i);

			if( u.rank != null ) continue;			

			String lRace = u.race;
			String lJob = u.profession;

			if( lRace != null )
			{			
				if( !race.contains(lRace) )
					race.addElement(lRace);
			}
			
			if( lJob != null )
			{
				lJob = convert(lJob);	
				if( !job.contains(lJob) )
					job.addElement(lJob);
			}
		}
		
		int nJob = job.size();
		int nRace = race.size();

		int grid[][] = new int[nJob][nRace];
		int levelsByJob[][] = new int[nJob][divs];
		
		System.out.println("Races="+nRace+" Classes="+nJob);

		for( i=0; i<len; ++i )
		{
			User u = (User) users.elementAt(i);

			if( u.rank != null ) continue;			

			int ijob, irace;
						
			String lRace = u.race;
			String lJob = u.profession;
			
			if( lJob != null ) lJob = convert(lJob);
			
			ijob = job.indexOf(lJob);
			irace = race.indexOf(lRace);

			if( ijob >= 0 && irace >= 0 )
			{
				++grid[ijob][irace];
				int lvl = u.level;
				int ilvl = (lvl-1)/reso;
				++levelsByJob[ijob][ilvl];
			}
		}
		
		try
		{
			PrintStream ps = new PrintStream( new FileOutputStream(chartFile) );
			
			for( i=0; i<divs; ++i )
			{
				ps.print(","+(i*reso+1)+" to "+((i+1)*reso));
			}
			
			ps.println();
			
			for( i=0; i<nJob; ++i )
			{
				ps.print(job.elementAt(i));
				int classTotal=0;
				for( int j=0; j<divs; ++j )
				{
					int val = levelsByJob[i][j];
					ps.print(","+val);
					classTotal += val;
				}

				ps.println();
			}

			ps.close();
		}
		
		catch( Exception e )
		{
			System.err.println("While doing chart - "+e);
		}
	}
	
	String convert( String s )
	{
		int nclass = equiv.length;
		
		for( int i=0; i<nclass; ++i )
		{
			int el = equiv[i].length;
			
			for( int j=0; j<el; ++j )
			{
				String s1 = equiv[i][j];
				
				if( s1 != null && s.equals(s1) )
					return equiv[i][0];
			}
		}

		return s;
	}
}
