/*	Trivial application that displays a string - 4/96 PNL*/
import java.util.Random;
public class Game {    private static Random r;
	public static void main(String args[]) {        r = new Random();
        
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

	}	public static int getThrow()	{		int i = r.nextInt();		if( i < 0 ) i = -i;		i %= 3;		return i;	}	public static int getStrategy()	{		int i = r.nextInt();		if( i < 0 ) i = -i;		i %= Strategy.HOWMANY-1;		return i;	}	}