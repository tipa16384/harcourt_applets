
/*

		  File:		TestFrame.java
	  Contains:		Test window class for AWT components
	Written by:		Steve Klingsporn <moofie@pobox.com>
			
	This is a sample program that lets you play around easily
	with the user interface components in the AWT and those derived
	thereof.  This program exercises my UI components.
	
*/

import java.awt.*;
import java.io.File;
import java.applet.*;
import java.net.URL;


/**
	Test applet
	@author Steve Klingsporn mailto:moofie@pobox.com
	@version 1.0 Thanksgiving 1997
**/
public class ListComponentDemo extends Applet
{
	Image			expanderCollapsedImage;
	Image			expanderExpandedImage;
	Image			expanderMovingImage;
	Image			packageImage;
	Image			selectedPackageImage;
	Image			classImage;
	Image			selectedClassImage;
	Image			methodImage;
	Image			selectedMethodImage;
	MediaTracker	watchdog;
	ListComponent	listComponent;
	
	public void init()
	{
		ListItem				item1, item2, item3, item4, item5;
		Dimension				size = size();
		
		setBackground(Color.white);
		setLayout(new BorderLayout());
		showStatus("Loading images...");	
		watchdog = new MediaTracker(this);
		
		expanderExpandedImage = getImage(getDocumentBase(), "images/expanded.gif");
		watchdog.addImage(expanderExpandedImage, 0);
		
		expanderCollapsedImage = getImage(getDocumentBase(), "images/collapsed.gif");
		watchdog.addImage(expanderCollapsedImage, 1);
		
		expanderMovingImage = getImage(getDocumentBase(), "images/moving.gif");
		watchdog.addImage(expanderMovingImage, 2);
		
		packageImage = getImage(getDocumentBase(), "images/package.gif");
		watchdog.addImage(packageImage, 3);
		
		selectedPackageImage = getImage(getDocumentBase(), "images/selected_package.gif");
		watchdog.addImage(selectedPackageImage, 4);
		
		classImage = getImage(getDocumentBase(), "images/class.gif");
		watchdog.addImage(classImage, 5);
		
		selectedClassImage = getImage(getDocumentBase(), "images/selected_class.gif");
		watchdog.addImage(selectedClassImage, 6);
		
		methodImage = getImage(getDocumentBase(), "images/method.gif");
		watchdog.addImage(methodImage, 7);
		
		selectedMethodImage = getImage(getDocumentBase(), "images/selected_method.gif");
		watchdog.addImage(selectedMethodImage, 8);
		
		//  Wait around until all these guys are loaded now
		
		try
		{
			watchdog.waitForAll();
		}
		catch (Exception exception)
		{
			showStatus("Error loading images...");
		}

		Box	container = new Box();

		container.setColor(new Color(80, 80, 80));
		container.setLayout(new BorderLayout());
		container.setBackground(Color.black);
		
		listComponent = new ListComponent(size.width, size.height);
		listComponent.setShadeAlternateItems(true);
		listComponent.setShadedItemColor(new Color(235, 235, 235));	
		listComponent.setExpanderIcons(expanderCollapsedImage, expanderExpandedImage, expanderMovingImage);
		addSampleItems(listComponent);
		container.add("Center", listComponent);
		add("Center", container);
	
		Panel p = new Panel();
		Checkbox t = new Checkbox("Shaded background items");
		t.setState(listComponent.getShadeAlternateItems());
		p.add(t);
		t = new Checkbox("Indented expanders");
		t.setState(listComponent.getIndentedExpanders());
		p.add(t);
		add("South", p);

	}
	
	
	/**
		Adds the sample items to the list view
	**/
	public final void addSampleItems(ListComponent listComponent)
	{
		ListItem		packageItem, subPackageItem, classItem, methodItem;
		int				i, j, k, l;
				
		showStatus("Adding 3000 items to the list...");	
		
		for (i = 0; i < 15; i++)
		{
			packageItem = new ListItem("package #" + i, packageImage, selectedPackageImage);
			listComponent.addItem(packageItem);
			
			for (j = 0; j < 5; j++)
			{
				subPackageItem = new ListItem("sub-package #" + j, packageImage, selectedPackageImage);
				packageItem.addItem(subPackageItem);
				
				for (k = 0; k < 10; k++)
				{
					classItem = new ListItem("class #" + k, classImage, selectedClassImage);
					subPackageItem.addItem(classItem);
					
					for (l = 0; l < 4; l++)
					{
						methodItem = new ListItem("method #" + l, methodImage, selectedMethodImage);
						classItem.addItem(methodItem);
					}
				}
			}
		}			
	}
	
	public boolean action(Event event, Object argument)
	{
		if (event != null && event.target instanceof Checkbox && listComponent != null)
		{
			Checkbox	target = ((Checkbox)event.target);
			
			if (target.getLabel().equals("Indented expanders"))
				listComponent.setIndentedExpanders(target.getState());
			else
				listComponent.setShadeAlternateItems(target.getState());
		}
		return true;
	}
	
} 
