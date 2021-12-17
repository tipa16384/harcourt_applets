import java.util.Random;

public class Strategy extends Object
{
	int strategy;
	int vstrategy;
	int scratch;  
	int opponent;

	static final int reps = 100000;
	static final int vareps = 250;
	static final int RANDOM = 0;
	static final int FAVORITE = 1;
	static final int OPPONENT = 2;
	static final int TOOLATE = 3; 
	static final int INCREMENT = 4;
	static final int VARIABLE = 5;
	static public final int HOWMANY = 6;

		strategy = which; 
		vstrategy = Game.getStrategy();       
		scratch = Game.getThrow(); 
		opponent = scratch;
	public String toString()
	{
		String s = "WHOOPS!";
		
		switch( strategy )
		{
			case RANDOM:
				s = "Random";
				break;
			
			case FAVORITE:
				s = "Favorite["+scratch+"]";
				break;
			
			case OPPONENT:
				s = "Copy Opponent";
				break;
			
			case TOOLATE:
				s = "Win Last";
				break;
			
			case INCREMENT:
				s = "Increment";
				break;
			
			case VARIABLE:
				s = "Variable";
				break;
		}

		return s;
	}
    
    public int versus( Strategy s )
    {
		int wins = 0; 
		int ywins = 0;

			else if( (yours == (mine+1)) || (yours==0 && mine==2) )
			{    
				++ywins;
			}
			theyHad( yours );
			s.theyHad( mine );
		}

	void theyHad( int theirs )
	{
		opponent = theirs;
	}

    private int nextThrow( int mine, int yours, int ours )
    {
    	if( ours > 0 && (ours % vareps) == 0 && mine < yours)
    	{ 
    		switch( strategy )
    		{
    			case VARIABLE:
					if( ((mine*100)/ours) < 15 )
						vstrategy = RANDOM;
					else
						vstrategy = Game.getStrategy();
					break;
				
				case FAVORITE:
				case INCREMENT:
                    scratch = Game.getThrow();
                    break;
			}
		}

    	int fingers = 99;
        int tstrategy = (strategy == VARIABLE) ? vstrategy : strategy;

		switch( tstrategy )
		{
			case RANDOM:
				fingers = Game.getThrow();
				break;

			case FAVORITE:
				fingers = scratch;
				break;

			case OPPONENT:
				fingers = opponent;
				break;
			
			case TOOLATE:
				fingers = opponent+1;
				break;
			
			case INCREMENT:
				fingers = scratch+1;
				break;
		}
        
        if( fingers > 2 ) fingers = 0;
        
        scratch = fingers;

    	return fingers;
    }
}