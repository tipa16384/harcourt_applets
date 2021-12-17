import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.*;
import java.net.URL;
import java.applet.*;
import java.beans.*;

import util.DoubleBufferPanel;

public class Prilosec extends Panel
{
	Main applet;
	Color purple = new Color(54,5,102);
	
	Vector vGlossary;
	final static String glossaryFile = "glossary.txt";
	
	String sampleText = "Do you ever wonder why, sometimes in the H. pylori, "+
		"your ulcer suddenly feels like a parietal cell? So have we all, "+
		"duodenum.";
	
	Glossary glossary;
	Content content;
	Dialog popup = null;
	RapidLabel popupText = null;
	Label popupTitle = null;
	RapidLabel captionText = null;
	
	final static int glossWidth = 220;
	
	Font popupFont = new Font("SansSerif",Font.PLAIN,12);
	Font popupTitleFont = new Font("SansSerif",Font.BOLD,14);
	Font popupTitleItalicFont = new Font("SansSerif",Font.BOLD+Font.ITALIC,14);
	
	URL docbase;
	
	// initializer -- start off with a BorderLayout.
	
	public Prilosec( Main applet )
	{
		super( new BorderLayout() );

		docbase = applet.getDocumentBase();

		setBackground( new Color(255,255,225) );
		setFont( popupFont );
		
		readGlossary();
		
		Utility.setApplet(applet);
		this.applet = applet;
		
		glossary = new Glossary();
		content = new Content();
		
		add( glossary, BorderLayout.WEST );
		add( content, BorderLayout.CENTER );
		
		Component frame = applet.getParent();
		
		while( (frame != null) && !(frame instanceof Frame) )
			frame = frame.getParent();
		
		if( frame != null )
		{
			System.out.println("making popup window");
			popup = new Dialog((Frame)frame,"Glossary",false);
			popup.setForeground( Color.yellow );
			popup.setBackground(purple);
			popup.setResizable(false);
			popup.setSize(glossWidth+20,glossWidth+20);
			popupTitle = new Label("dummy");
			popupTitle.setFont(popupTitleFont);
			popupText = new RapidLabel("dummy",Label.LEFT,glossWidth);
			popupText.setFont( popupFont );
			
			Panel p = new FixedPanel( new BorderLayout(), glossWidth, glossWidth, true, false );
			p.add( popupText, BorderLayout.CENTER );
			p.add( popupTitle, BorderLayout.NORTH );
			popup.add( p, BorderLayout.CENTER );
			
			popup.addWindowListener( new WindowAdapter()
				{
					public void windowClosing( WindowEvent e )
					{
						popup.hide();
					}
				} );
		}
		
		setCaption(sampleText);
	}

	void setCaption( String s )
	{
		glossary.highlight(s);
		captionText.setText(s);
	}

	void readGlossary()
	{
		System.out.println("Reading glossary");
		
		InputStreamReader is;
		char [] buffer = new char[256];
		boolean bol = true;
		DictionaryEntry de = null;
		StringBuffer sbt=null, sbd=null;
		
		vGlossary = new Vector();
		
		try
		{
			URL gurl = new URL(docbase,glossaryFile);
			is = new InputStreamReader(gurl.openStream());
			int len;
			boolean appendToTerm = false;
			
			while( (len=is.read(buffer)) > 0 )
			{
				for( int i=0; i<len; ++i )
				{
					char ch = buffer[i];
					
					if( ch == '\n' || ch == '\r' )
					{
						bol = true;
					}
					else if( bol )
					{
						if( ch == '%' )
						{
							if( sbt != null && sbd != null )
							{
								String term = new String(sbt);
								String def = new String(sbd);
								//System.out.println("term="+term+" ("+sbt+")");
								
								de = new DictionaryEntry(term,def);
								vGlossary.addElement(de);
							}
						}
						
						else if( ch == '*' )
						{
							sbt = new StringBuffer();
							sbd = new StringBuffer();
							appendToTerm = true;
						}
						
						else if( ch == '-' )
						{
							if( appendToTerm )
							{
								appendToTerm = false;
							}
							
							else sbd.append(' ');
						}
						
						bol = false;
					}

					else
					{
						if( appendToTerm )
							sbt.append(ch);
						else
							sbd.append(ch);
					}
				}
			}
		}
		
		catch( Exception ex )
		{
			System.err.println("While reading glossary: "+ex);
		}
	}
	
	void showPopup( int index )
	{
		System.out.println("showPopup("+index+")");
		
		DictionaryEntry de = (DictionaryEntry)vGlossary.elementAt(index);
		String s = de.term;
		
		if( popupTitle != null )
		{
			if( s.charAt(0) == '!' )
			{
				popupTitle.setFont( popupTitleItalicFont );
				s = s.substring(1);
			}
			
			else
			{
				popupTitle.setFont( popupTitleFont );
			}

			popupTitle.setText( s );
		}
		
		if( popupText != null )
		{
			popupText.setText( de.definition );
			popupText.invalidate();
		}
		
		if( popup != null )
		{
			popup.invalidate();
			popup.validate();
			if( !popup.isShowing() )
				popup.show();
		}
	}
	
	class Glossary extends FixedPanel
	{
		Panel wordPanel;
		
		public Glossary()
		{
			super( new BorderLayout(), 108, 100, true, false );
			
			setForeground( Color.white );
			setBackground( purple );
			
			wordPanel = new Panel( new GridLayout(0,1) );

			{
				Component c = new Label("GLOSSARY",Label.CENTER);
				c.setFont( new Font("SansSerif",Font.BOLD,14) );
				c.setForeground( Color.yellow );
				c.setBackground( null );
				wordPanel.add( c );
			}
			
			int len = vGlossary.size();
			System.out.println("adding "+len+" glossary entries.");
			
			int i;
			
			for( i=0; i<len; ++i )
			{
				DictionaryEntry de = (DictionaryEntry)vGlossary.elementAt(i);
				
				Component c = new MagicLabel(de.term,i);
				wordPanel.add( c );
				c.addMouseListener( new MouseAdapter()
					{
						public void mouseReleased( MouseEvent e )
						{
							MagicLabel ml = (MagicLabel) e.getSource();
							showPopup(ml.index);
						}
					} );
			}

			add( wordPanel, BorderLayout.NORTH );
		}
		
		public void highlight( String s )
		{
			int len = wordPanel.getComponentCount();
			int i;
			
			for( i=0; i<len; ++i )
			{
				Component c = wordPanel.getComponent(i);
				if( (c != null) && (c instanceof MagicLabel) )
				{
					((MagicLabel)c).scan(s);
				}
			}
		}
	}

	class Content extends Panel
	{
		public Content()
		{
			super( new BorderLayout() );
			
			Panel p = new AnimePanel( new BorderLayout(), 320, 240, true, true );
			Image image = Utility.getImage(this,"test.jpg");
			p.add( new GenericIcon(image,null,"animation"), BorderLayout.CENTER );
			add( p, BorderLayout.CENTER );
			p = new FixedPanel( new BorderLayout(), 100, 100, false, true );
			captionText = new RapidLabel("",Label.CENTER);
			captionText.setFont( new Font("SansSerif",Font.BOLD,18) );
			p.add( captionText, BorderLayout.CENTER );
			add( p, BorderLayout.SOUTH );
		}
	}

	class AnimePanel extends FixedPanel
	{
		final int lw = 5;
		
		public AnimePanel( LayoutManager lm, int width, int height, boolean hHold, boolean vHold )
		{
			super( lm, width, height, hHold, vHold );
			width += 2*lw;
			height += 2*lw;
		}
		
		public Insets getInsets()
		{
			return new Insets(lw,lw,lw,lw);
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			g.setColor( Color.black );
			g.fillRect(0,0,dim.width-1,dim.height-1);
						
			super.paint(g);
		}
	}

	class FixedPanel extends Panel
	{
		int width;
		int height;
		boolean hHold, vHold;
		
		public FixedPanel( LayoutManager lm, int width )
		{
			this(lm,width,width);
		}
		
		public FixedPanel( LayoutManager lm, int width, int height )
		{
			this(lm,width,height,false,false);
		}
		
		public FixedPanel( LayoutManager lm, int width, int height, boolean hHold, boolean vHold )
		{
			super(lm);
			this.width = width;
			this.height = height;
			this.hHold = hHold;
			this.vHold = vHold;
		}
		
		public Dimension getPreferredSize()
		{
			Dimension dim = super.getPreferredSize();
			
			if( hHold )
				dim.width = width;
			else
				dim.width = Math.max(width,dim.width);
			
			if( vHold )
				dim.height = height;
			else
				dim.height = Math.max(height,dim.height);
				
			return dim;
		}
		
		public Dimension getMinimumSize()
		{
			return new Dimension(width,height);
		}
		
		public void setBounds( int x, int y, int w, int h )
		{
			if( hHold )
			{
				x += (w-width)/2;
				w = width;
			}
			
			if( vHold )
			{
				y += (h-height)/2;
				h = height;
			}
			
			super.setBounds( x, y, w, h );
		}
		
	}

	class DictionaryEntry
	{
		String term, definition;
		
		public DictionaryEntry( String term, String definition )
		{
			this.term = term;
			this.definition = definition;
		}
	}
}
