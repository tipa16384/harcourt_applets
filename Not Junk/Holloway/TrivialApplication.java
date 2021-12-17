/*
	Trivial application that displays a string - 4/96 PNL
*/

public class TrivialApplication {

	public static void main(String args[]) {
		System.out.println( "Hello World!" );

		try {
			System.in.read(); // prevent console window from going away
		} catch (java.io.IOException e) {}
	}
}
