package Wired;

import java.beans.*;
import java.awt.event.*;

public interface TimeSource
{
	long getTime();
	
	void addActionListener( ActionListener pcl );
	void removeActionListener( ActionListener pcl );
}
