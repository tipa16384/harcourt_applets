#include <iostream>

// declares these two value-returning functions.
int total(int,int);
float percent(int,int);

using namespace std;

// prints a report on the numbers and species
// of turtles found in a New Hampshire lake.

int main( void )
{	
	int painted = 306;	// number of painted turtles
	int mud = 418;		// number of mud turtles

	// total population is the sum of all turtles
	int pop = total(painted,mud);

	cout << "\n\n\n";

	// print a report on the number and percentage
	// of the turtle population.
	cout << " painted turtles: " << painted <<
		" (" << percent(painted,pop) << "%)" << endl;
	
	cout << "     mud turtles: " << mud <<
		" (" << percent(mud,pop) << "%)" << endl;
	
	cout << "           total: " << pop << endl;
	
	return 0;
}

// getTotal: takes three two and returns their sum.

int total( int a, int b )
{
	return a+b;
}

// getPercent: takes two integers and returns
// their ratio as a floating point percentage

float percent( int pop, int tot )
{
	return ((float)pop * 100.0) / (float)tot;
}


