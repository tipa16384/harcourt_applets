package Wired;

import java.awt.Panel;
import java.awt.Insets;
import java.awt.LayoutManager;

// padding panel

public class PaddedPanel extends Panel
{
	Insets insets;
	
	public PaddedPanel(int pad)
	{
		this.insets = new Insets(pad,pad,pad,pad);
	}
	
	public PaddedPanel( LayoutManager layout, int pad )
	{
		super(layout);
		this.insets = new Insets(pad,pad,pad,pad);
	}
	
	public PaddedPanel(int top, int left, int bottom, int right)
	{
		this.insets = new Insets(top,left,bottom,right);
	}
	
	public PaddedPanel( LayoutManager layout, int top, int left, int bottom, int right )
	{
		super(layout);
		this.insets = new Insets(top,left,bottom,right);
	}
	
	public Insets getInsets()
	{
		return insets;
	}
}

