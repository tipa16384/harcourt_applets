package Wired;

import java.awt.*;

public class InvisiPanel extends Container
{
	public InvisiPanel()
	{
		this( new FlowLayout() );
	}
	
	public InvisiPanel( LayoutManager layout )
	{
		setLayout( layout );
	}
}
