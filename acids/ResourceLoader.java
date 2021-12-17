/*
File: ResourceLoader.java

Author: Matthew Cornell, Umass, cornell@cs.umass.edu

Purpose: Defines ResourceLoader, a utility that helps access images and
	text files that are stord in JARs. Note: These methods use
	getResourceAsStream() instead of getResource() because Netscape has a
	bug in the latter method. Note that this class does NOT work for images
	that aren't stored in a JAR.

History: ResourceLoader used to be in the ckc.util package, but that required
	all methods to take an Applet arg that we could call getClass() on. This
	was because getResourceAsStream() returned null for ResourceLoader.class
	when it was in its own package. But in the default package it works fine.
	WHY!?
	

Status: Implemented.
DO: When getResource() fixed, add getResourceURL(), which enables simpler
	creation of Images and playing of sounds. (Use Image Applet.getImage(URL)
	and void Applet.play(URL).)

Changes:
12/22/97	Created.
3/3/98		Moved to ckc.util package.
			Hmm: Doesn't seem to be working. Changed to accept Applet arg.
			Moved back to default package, and removed Applet arg.

*/

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.io.IOException;


/*
ResourceLoader is a utility class that helps with loading files with
required resources (i.e. text, images, etc.) from .jar files in a way that
works with Netscape.
*/
public class ResourceLoader {


	/*
	Top-level class method that returns an Image corresponding to
	resFileName. Returns null if one couldn't be loaded.
	*/
	public static Image getImage(String resFileName) {
		Image image;				// returned image. set belows
		InputStream in = getInputStream(resFileName);
		if(in == null) return null;
		// Continue.
		Toolkit tk = Toolkit.getDefaultToolkit();
		try {
			int bytesAvailable = in.available();
			if(bytesAvailable == 0) return null;
			// Continue
			byte[] imageData = new byte[bytesAvailable];
			in.read(imageData);
			image = tk.createImage(imageData);
			return image;
		} catch(IOException e) {
			return null;
		}
	}


	/*
	Top-level class method that returns an InputStream for resFileName.
	Returns null if one couldn't be created.
	*/
	public static InputStream getInputStream(String resFileName) {
		Class myClass = ResourceLoader.class;
		InputStream inputStream = myClass.getResourceAsStream(resFileName);	// null if not found
		return inputStream;
	}


	public static InputStream test(String resFileName) {
		Class myClass = ResourceLoader.class;
		InputStream inputStream = myClass.getResourceAsStream(resFileName);	// null if not found
		return inputStream;
	}
}
