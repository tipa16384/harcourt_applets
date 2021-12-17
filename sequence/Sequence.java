/*
	Trivial applet that displays a string - 4/96 PNL
*/

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.URL;
import java.util.Vector;

public class Sequence extends Applet
{
	List list = new List();
	Vector order = new Vector();
	
	Button delete, submit, cancel;
	
	String doneAction;
	
	static String [] imageFiles =
	{
		"arrowup.gif",
		"arrowup-down.gif",
		"arrowdown.gif",
		"arrowdown-down.gif"
	};
	
	static final int iArrowUp = 0;
	static final int iArrowUpDown = 1;
	static final int iArrowDown = 2;
	static final int iArrowDownDown = 3;
	
	Image [] images = new Image[imageFiles.length];
	
	URL docbase, cosebase;
	
	public Insets getInsets()
	{
		Insets insets = super.getInsets();
		insets.top += 10;
		insets.bottom += 10;
		insets.left += 10;
		insets.right += 10;
		
		return insets;
	}
	
	public void init()
	{
		docbase = getDocumentBase();
		System.out.println("Document base is "+docbase);
		cosebase = getCodeBase();
		System.out.println("Code base is "+cosebase);
		
		setBackground( Color.white );
		
		setLayout( new BorderLayout(10,10) );
		
		// load the images
		getImages();
		
		// make and add list
		add( list, BorderLayout.CENTER );
		addModule(getParameter("modules"));
		
		// make and add control panel
		Panel controls = new Panel( new BorderLayout() );
		add( controls, BorderLayout.EAST );
		
		// make and add panel to hold things near the bottom
		Panel bottomControls = new Panel( new BorderLayout() );
		controls.add( bottomControls, BorderLayout.SOUTH );
		
		// make and add delete button
		String deleteLabel = getParameter("delete");
		if( deleteLabel != null && deleteLabel.length() > 0 )
		{
			bottomControls.add( new DeleteButton(deleteLabel), BorderLayout.NORTH );
		}
		
		// make and add ok/cancel buttons
		Panel okCancelPanel = new Panel();
		bottomControls.add( okCancelPanel, BorderLayout.SOUTH );
		
		doneAction = getParameter("doneaction");
		if( doneAction != null && doneAction.length() > 0 )
		{
			submit = new Button("Submit changes");
			submit.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						try
						{
							String cmds = doneAction + "&seq=";
							
							int len = order.size();
							for( int i=0; i<len; ++i )
							{
								if( i > 0 ) cmds += ',';
								cmds += ((Integer)order.elementAt(i)).toString();
							}
							
							System.out.println("Submitting command "+cmds);
							
							URL destURL = new URL( cosebase, cmds );
							
							System.out.println("gonna send it to "+cmds);
							
							AppletContext cntxt = getAppletContext();
							cntxt.showDocument( destURL, "f2b" );
						}

						catch( Exception ee )
						{
							System.out.println("Bombed out of submit with "+ee);
						}
					}
				} );
	
			okCancelPanel.add( submit );
		}
				
		cancel = new Button("Cancel");
		//okCancelPanel.add( cancel );
		
		// make and add reorder panel
		String reorderLabel = getParameter("reorder");
		if( reorderLabel != null && reorderLabel.length() > 0 )
		{
			Panel reorder = new Panel( new BorderLayout(4,4) );
			controls.add( reorder, BorderLayout.NORTH );
			reorder.add( new Label(reorderLabel), BorderLayout.CENTER );
			
			// make an add the arrow panel
			Panel arrowPanel = new Panel( new BorderLayout(2,2) );
			reorder.add( arrowPanel, BorderLayout.WEST );
			
			Component upArrow, downArrow;
			
			upArrow = new ArrowControl( iArrowUp, true );
			arrowPanel.add( upArrow, BorderLayout.NORTH );
			downArrow = new ArrowControl( iArrowDown, false );
			arrowPanel.add( downArrow, BorderLayout.SOUTH );
		}
	}
	
	void getImages()
	{
		final int len = imageFiles.length;
		MediaTracker mt = new MediaTracker(this);
		
		showStatus("loading images");
		
		try
		{
			for( int i=0; i<len; ++i )
			{
				Image im = getImage(cosebase,imageFiles[i]);
				images[i] = im;
				mt.addImage(im,0);
			}

			mt.waitForAll();

			showStatus("image loading done");
		}
		
		catch( Exception e )
		{
			System.out.println("Caught "+e);
			showStatus("Caught "+e);
		}
	}
	
	void addModule( String modlist )
	{
		if( modlist != null && modlist.length() > 0 )
		{
			int sci = modlist.indexOf(';');
			String modname;
			
			if( sci < 0 )
			{
				modname = modlist;
				modlist = null;
			}
			
			else
			{
				modname = modlist.substring(0,sci);
				modlist = modlist.substring(sci+1);
			}
			
			if( modname != null && modname.length() > 0 )
			{
				list.add( modname );
				order.addElement( new Integer(order.size()) );
			}
			
			//System.out.println("modname="+modname+" modlist="+modlist);
			addModule( modlist );
		}
	}
	
	class DeleteButton extends Button
					   implements ActionListener
	{
		public DeleteButton( String label )
		{
			super(label);
			addActionListener( this );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			int sci = list.getSelectedIndex();
			if( sci >= 0 )
			{
				list.remove(sci);
				order.removeElementAt(sci);
			}
		}
	}

	class ArrowControl extends Component
	{
		Image upImage;
		Image downImage;
		boolean isUp = true;
		boolean upper;
		
		public ArrowControl( int imageIndex, boolean upper )
		{
			this.upper = upper;
			upImage = images[imageIndex];
			downImage = images[imageIndex+1];
			enableEvents( AWTEvent.MOUSE_EVENT_MASK );
		}
		
		protected void processMouseEvent( MouseEvent e )
		{
			switch( e.getID() )
			{
				case MouseEvent.MOUSE_PRESSED:
					isUp = false;
					repaint();
					
					if( upper )
						swapUp();
					else
						swapDown();
					
					break;
				
				case MouseEvent.MOUSE_RELEASED:
					isUp = true;
					repaint();
					break;
			}
			
			super.processMouseEvent( e );
		}
		
		void swapUp()
		{
			int sel = list.getSelectedIndex();
			
			if( sel > 0 )
			{
				String s = list.getSelectedItem();
				list.remove( sel );
				list.add( s, sel-1 );
				list.select( sel-1 );
				
				Object o = order.elementAt(sel);
				order.removeElementAt(sel);
				order.insertElementAt( o, sel-1 );
			}
		}
		
		void swapDown()
		{
			int sel = list.getSelectedIndex();
			int len = list.getItemCount();
			
			if( sel >= 0 && sel < (len-1) )
			{
				String s = list.getSelectedItem();
				list.remove( sel );
				list.add( s, sel+1 );
				list.select( sel+1 );
				
				Object o = order.elementAt(sel);
				order.removeElementAt( sel );
				order.insertElementAt( o, sel+1 );
			}
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(upImage.getWidth(this),upImage.getHeight(this));
		}
		
		public void paint( Graphics g )
		{
			g.drawImage( isUp ? upImage : downImage, 0, 0, this );
		}
	}
}
