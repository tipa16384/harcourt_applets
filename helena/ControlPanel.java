import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ControlPanel extends TabbedPanel
{
	Main main;
	GraphInfo info;
	Vector listeners = new Vector();
	
	final static String testMarketing = "Test Marketing";
	final static String marketModeling = "Business Modeling";
	
	public ControlPanel( Main main, GraphInfo info )
	{
		setBackground( new Color(204,204,204) );
		
		Panel p;
		Component c;
		
		this.main = main;
		this.info = info;
		
		add( new TestMarketingControls(), testMarketing );
		add( new MarketModelingControls(), marketModeling );
	}
	
	//public Dimension getPreferredSize()
	//{
//		Dimension dim = super.getPreferredSize();
//		dim.height = Math.max(dim.height,100);
//		return dim;
//	}
	
	public void addPointEventListener( PointEventListener l )
	{
		if( !listeners.contains(l) )
		{
			listeners.addElement( l );
		}
	}
	
	public void removePointEventListener( PointEventListener l )
	{
		listeners.removeElement( l );
	}
	
	public void sendPointEvent( PointEvent pe )
	{
		int len = listeners.size();
		
		for( int i=0; i<len; ++i )
		{
			PointEventListener l = (PointEventListener) listeners.elementAt(i);
			l.plot(pe);
		}
	}
			
	public void sendGraphEvent( String pe )
	{
		int len = listeners.size();
		
		for( int i=0; i<len; ++i )
		{
			PointEventListener l = (PointEventListener) listeners.elementAt(i);
			l.graph(pe);
		}
	}
			
	class TestMarketingControls extends Panel
	{
		TextComponent cents;
		
		public TestMarketingControls()
		{
			Component c;
						
			Panel p = new Panel( new BorderLayout() );

			setFont( info.fontBigBold );
			
			cents = new TextField(5);
			cents.addKeyListener( new UserEntry() );

			p.add( new Label("Enter price per cup in cents: "), BorderLayout.WEST );
			p.add( cents, BorderLayout.CENTER );

			add( p );
		}
		
		class UserEntry extends KeyAdapter
		{
			public void keyPressed(KeyEvent e)
			{
				TextComponent tc = (TextComponent)e.getSource();
				int code = e.getKeyCode();
				
				//System.out.println("Key typed is "+e.getKeyText(code));
			
				if( code == KeyEvent.VK_ENTER )
				{
					plotPoint();
				}
			}
		}

		void plotPoint()
		{
			try
			{
				String s = cents.getText();
				double val = (Double.valueOf(s)).doubleValue();
				PointEvent pe = new PointEvent(this,val,0.0);
				sendPointEvent(pe);
			}
			
			catch( Exception e )
			{
				System.err.println("Error while parsing - "+e);
			}
			
			cents.selectAll();
			cents.requestFocus();
		}
	}
	
	class MarketModelingControls extends Panel
	{
		final String captionName = "marketcaption.gif";
		Image captionImage;
		int bottomInset = 0;
		
		public MarketModelingControls()
		{
			setFont( info.fontBigBold );
			
			captionImage = Utility.getImage( this, captionName );
			if( captionImage != null )
			{
				try
				{
					MediaTracker mt = new MediaTracker(this);
					mt.addImage( captionImage, 0 );
					mt.waitForAll();
					bottomInset = captionImage.getHeight(this);
				}
				
				catch( Exception e )
				{
					captionImage = null;
				}
			}
			
			Panel p = new Panel( new BorderLayout() );
			TextComponent formula = new TextField(10);
			formula.addKeyListener( new UserEntry() );
			p.add( new Label("Enter the formula for n(x): "), BorderLayout.WEST );
			p.add( formula, BorderLayout.CENTER );
			
			add( p );
		}
		
		class UserEntry extends KeyAdapter
		{
			public void keyPressed(KeyEvent e)
			{
				TextComponent tc = (TextComponent)e.getSource();
				int code = e.getKeyCode();
				
				//System.out.println("Key typed is "+e.getKeyText(code));
			
				if( code == KeyEvent.VK_ENTER )
				{
					sendGraphEvent( tc.getText() );
					tc.selectAll();
					tc.requestFocus();
				}
			}
		}

		public Insets getInsets()
		{
			return new Insets(0,0,bottomInset,0);
		}
		
		public void paint( Graphics g )
		{
			super.paint(g);
			
			Dimension dim = getSize();
			Insets insets = getInsets();
			
			// draw caption
			if( captionImage != null )
			{
				int pw = captionImage.getWidth(this);
				g.drawImage( captionImage, (dim.width-pw)/2, dim.height-insets.bottom, this );
			}
		}
	}
}
