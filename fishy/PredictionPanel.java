import java.awt.*;
import java.awt.event.*;

public class PredictionPanel extends TitledPanel
							 implements ActionListener
{
	FontMetrics fm;
	Fishy fishy;
	GraphInfo info;
	
	int maxStringWidth = 0;
	
	public PredictionPanel( Fishy fishy, GraphInfo info )
	{
		super( "Step 3: Predict", new BorderLayout() );
		
		this.fishy = fishy;
		this.info = info;
		
		setForeground( Color.black );
		setBackground( Color.white );
		setFont( info.fontBigPlain );
		
		fm = getFontMetrics( info.fontPlain );
		
		Button predictButton = new Button("Predict");
		predictButton.addActionListener( this );
		
		Panel p = new Panel();
		p.add( predictButton );
		add( p, BorderLayout.SOUTH );
		
		int len = Fish.getNumTypes();
		int i;
		
		p = new Panel( new GridLayout(len,1,5,5) );
		add( p, BorderLayout.CENTER );
		
		for( i=0; i<len; ++i )
		{
			String s = Fish.getName(i);
			maxStringWidth = Math.max(maxStringWidth,fm.stringWidth(s));
		}
		
		add( new TrackerLabels(), BorderLayout.NORTH );
		
		for( i=0; i<len; ++i )
			p.add( new FishTracker(i) );
	}
	
	boolean match = true;
	
	public void actionPerformed( ActionEvent e )
	{
		match = true;
		setValues( fishy.getFishCounts() );
		
		fishy.setMessage( match
				? Fishy.winna
				: Fishy.losa );
	}
	
	void setValues( int [] fishCounts )
	{
		setValues( this, fishCounts );
	}
	
	void setValues( Container cont, int [] fishCounts )
	{
		int len = cont.getComponentCount();
		int i;
		
		for( i=0; i<len; ++i )
		{
			Component c = cont.getComponent(i);
			
			if( c instanceof FishTracker )
			{
				((FishTracker)c).setActual(fishCounts);
				
				try
				{
					int a, b;
					
					a = ((FishTracker)c).getPrediction();
					b = ((FishTracker)c).getActual();
					
					match = match && (a == b);
				}
				
				catch( Exception e )
				{
					match = false;
				}
			}
			
			else if( c instanceof Container )
			{
				setValues( (Container)c, fishCounts );
			}
		}
	}
	
	public void clear()
	{
		clear(this);
	}
	
	public void clear( Container cont )
	{
		int len = cont.getComponentCount();
		int i;
		
		for( i=0; i<len; ++i )
		{
			Component c = cont.getComponent(i);
			
			if( c instanceof TextComponent )
			{
				((TextComponent)c).setText("");
			}
			
			else if( c instanceof Container )
			{
				clear( (Container)c );
			}
		}
	}

	class TrackerLabels extends FishTracker
	{
		public TrackerLabels()
		{
			super(-1);
		
			add( new XLabel("Guess") );
			add( new XLabel("Actual") );
		}
	}
	
	class FishTracker extends Panel
	{
		int which;
		TextField prediction;
		TextField actual;
		
		public FishTracker( int which )
		{
			super( new GridLayout(1,3) );
			
			Panel p;
			
			setFont( info.fontPlain );
			
			this.which = which;
			
			prediction = new TextField(2);
			actual = new TextField(2);
			actual.setEditable(false);
			
			add( new FishIcon(which) );
			
			if( which >= 0 )
			{
				Color color = Fish.getBGColor(which);
				
				p = new Panel();
				p.add( prediction );
				//prediction.setForeground(color);
				add( p );
				
				p = new Panel();
				p.add( actual );
				add( p );
				
				//setBackground( color );
			}
		}

		public Insets getInsets()
		{
			if( which >= 0 )
				return new Insets(1,1,1,1);
			else
				return super.getInsets();
		}
		
		public void paint( Graphics g )
		{
			super.paint( g );
			
			if( which >= 0 )
			{
				Dimension dim = getSize();
				g.setColor( Fish.getColor(which) );
				g.drawRect( 0, 0, dim.width-1, dim.height-1 );
			}
		}

		public int getPrediction()
		{
			int guess = -1;
			
			if( which >= 0 )
			{
				try
				{
					String s = prediction.getText();
					guess = Integer.parseInt(s);
				}
				
				catch( Exception e )
				{
				}
			}
						
			return guess;
		}

		public int getActual()
		{
			int guess = -1;
			
			if( which >= 0 )
			{
				try
				{
					String s = actual.getText();
					guess = Integer.parseInt(s);
				}
				
				catch( Exception e )
				{
				}
			}
						
			return guess;
		}

		public void setActual( int [] fishCounts )
		{
			if( which >= 0 )
				actual.setText( Integer.toString(fishCounts[which]) );
		}
	}
	
	class FishIcon extends Component
	{
		int which;
		Image image;
		String label;
		
		public FishIcon( int which )
		{
			this.which = which;
			
			if( which >= 0 )
			{
				image = Utility.getImage(fishy,Fish.getFileName(which));
				label = Fish.getName(which);
			}
			
			else
			{
				image = null;
				label = null;
			}
		}
		
		public Dimension getPreferredSize()
		{
			Dimension dim;
			
			if( image != null )
				dim = new Dimension(image.getWidth(fishy),image.getHeight(fishy));
			else
				dim = new Dimension();
			
			dim.height += fm.getHeight();
			dim.width = Math.max(dim.width,maxStringWidth);
			
			return dim;
		}
		
		public void paint( Graphics g )
		{
			if( image != null && label != null )
			{
				Dimension dim = getSize();
				
				int fh = fm.getHeight();
				int sw = fm.stringWidth(label);
				
				g.setColor( Fish.getColor(which) );
				g.fillRect( 0, 0, dim.width-1, fh );
				
				g.setColor( getForeground() );
				g.drawString( label, (dim.width-sw)/2, fm.getAscent() );
				g.drawImage( image, (dim.width-image.getWidth(fishy))/2, fh, this );
			}
		}
	}
}
