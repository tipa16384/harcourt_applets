import java.awt.*;

public class Spacer extends Component
{
	int width;
	
	public Spacer( int width )
	{
		this.width = width;
	}
	
	public Dimension getPreferredSize()
	{
		Dimension dim = super.getPreferredSize();
		dim.width = width;
		return dim;
	}
}

