/*
import java.util.Random;


        
		int x, y;
		
		for( y=0; y<Strategy.HOWMANY; ++y )
		{
			for( x=0; x<Strategy.HOWMANY; ++x )
			{   
				Strategy xs, ys;

				xs = new Strategy(x);
				ys = new Strategy(y);
				
				int wins = ys.versus(xs);
				System.out.println(ys +
								   " vs. " +
								   xs +
								   " = " +
								   wins + "% wins");
			}
		}

	}