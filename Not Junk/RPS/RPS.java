import java.util.Random;public class RPS extends Object{	Random r;	static final int reps = 100000;		public RPS()	{		r = new Random();	}		public int getRandom()	{		int wins = 0;		for( int i=0; i<reps; ++i )		{			int mine = getThrow();			int yours = getThrow();			if( (mine == (yours+1)) || (mine==0 && yours==2) )				++wins;		}				return (wins*100)/reps;	}		public int getFavorite()	{		int wins = 0;
		int mine = getThrow();
				for( int i=0; i<reps; ++i )		{			int yours = getThrow();			if( (mine == (yours+1)) || (mine==0 && yours==2) )				++wins;		}				return (wins*100)/reps;	}		public int getOpponent()	{		int wins = 0;
		int mine = getThrow();
				for( int i=0; i<reps; ++i )		{						int yours = getThrow();			if( (mine == (yours+1)) || (mine==0 && yours==2) )				++wins;
			mine = yours;		}				return (wins*100)/reps;	}		public int getTooLate()	{		int wins = 0;
		int mine = getThrow();
				for( int i=0; i<reps; ++i )		{						int yours = getThrow();			if( (mine == (yours+1)) || (mine==0 && yours==2) )				++wins;
			mine = yours+1;
			if( mine > 2 ) mine = 0;		}				return (wins*100)/reps;	}}