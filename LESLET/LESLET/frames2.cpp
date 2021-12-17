int main()
{
	// according to a 1967 census, these were the number of people
	// who described themselves as short, tall, or just right.
	
	int tall = 12306;	// number of tall people
	int short = 16092;	// number of short people
	int average = 5;	// number of content people

	// total population is the sum of all these groups

	int pop = getTotal(tall,short,average);
	
	// find the percentage of tall, short,
	// and normal people based on the
	// total population
	
	cout << "percentage of tall people: "
		 << getPercent(tall,pop) << endl;
	cout << "percentage of short people: "
		 << getPercent(short,pop) << endl;
	cout << "percentage of just right people: "
		 << getPercent(average,pop) << endl;
	
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






/*

percentage of tall people: 43

28403

*/
