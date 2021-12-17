int main()
{
	// return with no errors
	
	return 0;
}

----------------

int main()
{
	// according to a 1967 census, these were the number of people
	// who described themselves as short, tall, or just right.
	
	int tallPeople = 12306;		// number of tall people
	int shortPeople = 16092;	// number of short people
	int justRightPeople = 5;	// number of content people
	
	// return with no errors
	
	return 0;
}

-----------------

int getTotal( int, int, int );

int main()
{
	// according to a 1967 census, these were the number of people
	// who described themselves as short, tall, or just right.
	
	int tallPeople = 12306;		// number of tall people
	int shortPeople = 16092;	// number of short people
	int justRightPeople = 5;	// number of content people

	// total population is the sum of all these groups

	int totalPopulation = getTotal(tallPeople,shortPeople,justRightPeople);
	cout << "total population: " << totalPopulation << endl;
	
	// return with no errors
	
	return 0;
}

// getTotal: takes three numbers and returns their sum.

int getTotal( int a, int b, int c )
{
	return a+b+c;
}

---------------------

int getTotal( int, int, int );
int getPercent( int, int );

int main()
{
	// according to a 1967 census, these were the number of people
	// who described themselves as short, tall, or just right.
	
	int tallPeople = 12306;		// number of tall people
	int shortPeople = 16092;	// number of short people
	int justRightPeople = 5;	// number of content people

	// total population is the sum of all these groups

	int totalPopulation = getTotal(tallPeople,shortPeople,justRightPeople);
	cout << "total population: " << totalPopulation << endl;
	
	// find the percentage of tall, short, and normal people based on the
	// total population
	
	cout << "percentage of tall people: " << getPercent(tallPeople,totalPopulation) << endl;
	cout << "percentage of short people: " << getPercent(shortPeople,totalPopulation) << endl;
	cout << "percentage of just right people: " << getPercent(justRightPeople,totalPopulation) << endl;
	
	// return with no errors
	
	return 0;
}

// getTotal: takes three numbers and returns their sum.

int getTotal( int a, int b, int c )
{
	return a+b+c;
}

// getPercent: takes two numbers and returns their ratio as a percentage

int getPercent( int pop, int tot )
{
	return (pop * 100) / tot;
}
