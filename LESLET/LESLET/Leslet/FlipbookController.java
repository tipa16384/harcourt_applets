public interface FlipbookController
{
	public int getCurrentStep();
	
	public void setTotalSteps( int ts );
	
	public int getTotalSteps();

	public void stepForward();
	
	public void stepBackward();
	
	public void gotoStep( int step );
	
} // end class FlipbookController
