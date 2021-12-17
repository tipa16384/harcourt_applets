import java.awt.*;

// "t" is in microseconds

public interface CircuitElement
{
	boolean showTrace();

	Color getColor();
	
	String getMeterLabel();
	String getMeterUnits();
	double getValue( long t0 );
	double getPhase( long t0 );
}
