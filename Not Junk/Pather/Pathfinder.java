// A->B->C->D->E->F->Apublic class Pathfinder{	final static int PATHLEN = 5;	int [] bestResult = new int[PATHLEN];	int bestCost;	int [] currentResult = new int[PATHLEN];	public void findBestPath()	{		int fact = factorial(PATHLEN);		
		bestCost = 1000;
				for( int i=0; i<fact; ++i )		{			getRoute( i, currentResult );			int curCost = cost( currentResult );			if( curCost < bestCost )			{				bestCost = curCost;				int [] t = bestResult;				bestResult = currentResult;				currentResult = t;			}		}				System.out.println("best score was "+bestCost);		System.out.println("that path was "+printRoute(bestResult));	}	
	public void findWorstPath()	{		int fact = factorial(PATHLEN);		
		bestCost = 0;
				for( int i=0; i<fact; ++i )		{			getRoute( i, currentResult );			int curCost = cost( currentResult );			if( curCost > bestCost )			{				bestCost = curCost;				int [] t = bestResult;				bestResult = currentResult;				currentResult = t;			}		}				System.out.println("worst score was "+bestCost);		System.out.println("that path was "+printRoute(bestResult));	}
	String printRoute( int [] ar )
	{
		String s = "A->";

		for( int i=0; i<PATHLEN; ++i )
			s = s + (new Character((char)('B'+ar[i]))) + "->";
		
		return s + "A";
	}
	int factorial(int f)	{		if( f <= 1 ) return 1;		else return f * factorial(f-1);	}		void getRoute( int route, int [] ar )	{		for( int i=0; i<PATHLEN; ++i )
			ar[i] = route % (PATHLEN-i);
		
				bump( ar[0], ar, 1 );				//System.out.println("route "+route+" is "+printRoute(ar));	}		void bump( int magic, int [] ar, int from )	{		if( from >= PATHLEN ) return;		bump( ar[from], ar, from+1 );		for( int i=from; i<PATHLEN; ++i )			if( ar[i] >= magic )				++ar[i];	}		int cost( int [] ar )	{		int val = cost(0,ar[0]+1) + cost(0,ar[PATHLEN-1]+1);			for( int i=0; i<PATHLEN-1; ++i )			val += cost( ar[i]+1, ar[i+1]+1 );				return val;	}	int cost( int from, int to )	{		if( from > to )			return cost( to, from );		else switch( from )		{			case 0:				switch( to )				{					case 1: return 5;					case 2: return 15;					case 3: return 3;					case 4: return 9;					case 5: return 2; 
					case 6: return 1;				}				break;						case 1:				switch( to )				{					case 2: return 6;					case 3: return 14;					case 4: return 11;					case 5: return 1;					case 6: return 30;				}				break;						case 2:				switch( to )				{					case 3: return 8;					case 4: return 4;					case 5: return 12;					case 6: return 30;				}				break;			case 3:				switch( to )				{					case 4: return 13;					case 5: return 10;					case 6: return 30;				}				break;						case 4:				switch( to )				{					case 5: return 7;					case 6: return 30;				}				break; 
			
			case 5:
				switch( to )
				{
					case 6: return 30;
				}
				break;		}		return 1000;	}	}