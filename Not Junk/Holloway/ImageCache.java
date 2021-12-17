/*
	ImageCache
	Copyright 1997 Steve Klingsporn
*/

import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.ImageProducer;
import java.util.Hashtable;
import java.net.URL;
import java.net.MalformedURLException;

/**
	A simple Image loading and cacheing class.  Good
	for loading local images, bad for Applet usage.
**/
public class ImageCache
{	
	protected Hashtable				_cache = null;
	protected static ImageCache		_instance = null;
	
		
	/**
		Constructor
	**/
	protected ImageCache()
	{
		_cache = new Hashtable();
	}
	

	/**
		Returns the image cache
	**/
	public static final ImageCache getImageCache()
	{
		if (ImageCache._instance == null)
			ImageCache._instance = new ImageCache();
		
		return _instance;
	}
	
	
	/**
		Clears the static image cache
	**/
	public final void clear()
	{
		_cache = null;
		_cache = new Hashtable();
	}
		
	
	/**
		Attempts to return an Image, and puts it in the cache
		or uses it if it is in the cache (all those 2-letter words
		together look funny).
	**/
	public final Image getImage(String path)
	{
		URL				imageURL = null;
		ImageProducer	imageSource = null;
		Image			image = null;
		MediaTracker	watchdog = null;
		Panel			tempPanel = null;
						
		//  Make sure things are cool...
		if (path == null)
			return null;
										
		//  Try fetching the image (by pathname) from the Hashtable first		
		
		if (_cache.containsKey(path))
			return (Image)_cache.get(path);
		else
		{
			//  Create the URL to fetch with
			try
			{
				imageURL = new URL("file:" + path);
			}
			catch (MalformedURLException exception)
			{
				System.out.println("### ImageCache.getImage: Malformed URL exception.");
				return null;
			}
			//  Try to get the contents of the image
			try
			{
				imageSource = (ImageProducer)imageURL.getContent();
			}
			catch (Exception exception)
			{
				System.out.println("### ImageCache.getImage: Exception loading image."
							       + exception.toString());
				return null;
			}
			
			//  Hack of all hacks; sucks, but works...
			
			tempPanel = new Panel();
			image = tempPanel.createImage(imageSource);
			
			if (image != null)
			{
				//  Wait until the image is actually loaded before adding it
				//  to the cache and returning it.
				
				watchdog = new MediaTracker(tempPanel);
				watchdog.addImage(image, 0);
				try
				{
					watchdog.waitForID(0);
				}
				catch (InterruptedException exception)
				{
					System.out.println("### ImageCache.getImage: Image fetch interrupted.");
					return null;
				}
								
				_cache.put(path, image);	
				return image;
			}
			else
				return null;
		}
	}	
}
	
	
