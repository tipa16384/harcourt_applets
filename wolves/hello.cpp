#include <iostream>
#include <cmath>
#include <iomanip>

using namespace std; 	//introduces namespace std
int main( void )
{
	const int interval = 4;
	
	float rabbits = 200;			// number of rabbits
	const float rabbitRate = 0.05;	// new rabbits per month
	const float rabbitDeath = 0.001;	// deaths per wolf per month

	float wolves = 50;			// number of wolves
	const float wolfRate = 0.0002;	// new wolves per rabbit per month
	const float wolfDeath = 0.03;	// wolf deaths per month

	cout << setw(5) << "Month" << "  "
		 << setw(7) << "Rabbits" << "  "
		 << setw(6) << "Wolves" << "  "
		 << endl;

	for( int month=0; month<=2000; ++month )
	{
		if( !(month % interval) )
		{
			cout << setw(5) << month << "  "
				 << setw(7) << int(rabbits) << "  "
				 << setw(6) << int(wolves) << "  "
				 ;
	
			int irab = int(rabbits/10);
			int iwol = int(wolves/10);
			
			for( int i=0; i<=30; ++i )
			{
				if( i==irab && i==iwol ) cout << "X";
				else if( i==irab ) cout << "R";
				else if( i==iwol ) cout << "W";
				else cout << ".";
			}
	
			cout << endl;
		}
		
		float nrabbit = float(float(rabbits) * (1.0+rabbitRate-rabbitDeath*float(wolves)));
		float nwolves = float(float(wolves) * (1.0+wolfRate*float(rabbits)-wolfDeath));

		rabbits = nrabbit;
		wolves = nwolves;
	}
}