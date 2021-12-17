import java.awt.*;

// "t" is in microseconds

public interface CircuitElement
{
	static public final int VOLTAGE = 0;
	static public final int CURRENT = 1;
	static public final int INDUCTANCE = 2;
	
	double getValue( int selector, double tau );
	double getPhase( long t0 );
	double getTau();

	boolean showTrace( int sel );

	Color getColor();
	//Panel getPanel();

	public String getMeterLabel();
	public String getMeterUnits();
}
