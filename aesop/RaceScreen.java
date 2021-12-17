import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import util.DoubleBufferPanel;

public class RaceScreen extends Panel
{
	Main main;
	GraphInfo info;
	DFunction hareFunc;
	DFunction turtleFunc;
	
	static final String [] imageFiles =
		{
			"hare.gif",
			"sleephare.gif",
			"tortoise.gif",
		};
	
	Image [] images = new Image[imageFiles.length];
	
	static final int hareAwake = 0;
	static final int hareAsleep = 1;
	static final int tortoise = 2;
	
	public RaceScreen( Main main, GraphInfo info )
	{
		super( new BorderLayout(5,5) );
		
		this.main = main;
		this.info = info;

		setBackground( new Color(204,204,255) );
		readImages();
		
		hareFunc = new HareFunc();
		turtleFunc = new TurtleFunc();
		
		Panel p = new Panel( new BorderLayout() );
		
		p.add( new Footer(), BorderLayout.NORTH );
		p.add( new RaceMessage(), BorderLayout.SOUTH );
		add( p, BorderLayout.SOUTH );
		
		add( new ProgressPanel(), BorderLayout.WEST );
		add( new RaceTrack(), BorderLayout.CENTER );
		add( new Title("The Tortoise and the Hare"), BorderLayout.NORTH );
	}
	
	void readImages()
	{
		try
		{
			MediaTracker mt = new MediaTracker(this);
			
			for( int i=0; i<imageFiles.length; ++i )
			{
				Image image = Utility.getImage(this,imageFiles[i]);
				images[i] = image;
				mt.addImage(image,0);
			}
			
			mt.waitForAll();
		}
		
		catch( Exception e )
		{
			System.err.println("An error occurred while loading images - "+e);
		}
	}

	public Insets getInsets()
	{
		return new Insets(5,5,0,5);
	}
	
	class RaceMessage extends Message
					 implements PropertyChangeListener
	{
		boolean canMoveOn = false;

		public RaceMessage()
		{
			enableEvents( AWTEvent.MOUSE_EVENT_MASK );	
			info.addPropertyChangeListener( this );
		}
	
		protected void processMouseEvent( MouseEvent e )
		{
			if( e.getID() == MouseEvent.MOUSE_CLICKED && canMoveOn )
			{
				info.firePropertyChange(info.part_two,2.0,16.0);
			}
		}

		public void propertyChange( PropertyChangeEvent pce )
		{
			String prop = pce.getPropertyName();
			canMoveOn = false;
			
			if( prop.equals(info.reset_applet) )
			{
				setString( "Press START/STOP to start the race!" );
			}
			
			if( prop.equals(info.start) )
			{
				setString( "Press START/STOP to pause the race!" );
			}
			
			if( prop.equals(info.stop) )
			{
				setString( "Press START/STOP to continue the race!" );
			}
			
			if( prop.equals(info.end) )
			{
				setString( "It's a tie!    Press RESET to race again." );
			}
			
			if( prop.equals(info.wrong) )
			{
				setString( "The velocities are not equal at the given time." );
			}
			
			if( prop.equals(info.right) )
			{
				setString( "Correct! Click here to explore the Mean Value Theorem." );
				canMoveOn = true;
			}
		}
	}

	class Footer extends Panel
				 implements PropertyChangeListener, Runnable
	{
		Button startButton;
		Button stopButton;
		Button resetButton;
		TextComponent answerField;
		Label timeLabel;
		
		double currentTime = 0;
		boolean running = false;
		
		public Footer()
		{
			startButton = new Button("Start/Stop");
//			stopButton = new Button("Stop");
			resetButton = new Button("Reset");
			
			add( startButton );
//			add( stopButton );
			add( resetButton );
			
			startButton.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent ae )
					{
						if( running )
						{
							stopTimer();
						}
						
						else
						{
							startTimer();
						}
					}
				} );
			
			resetButton.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent ae )
					{
						resetTimer();
					}
				} );
			
			timeLabel = new Label("Time (c) = 19.99 minutes", Label.CENTER);
			timeLabel.setBackground( info.CONTROL_COLOR );
			add( timeLabel );
			
			Panel p = new Panel( new BorderLayout() );
			answerField = new TextField(3);
			p.add( new Label("Enter time: ", Label.RIGHT), BorderLayout.WEST );
			p.add( answerField, BorderLayout.CENTER );
			p.add( new Label(" mins"), BorderLayout.EAST );
			add( p );
			
			info.addPropertyChangeListener( Footer.this );
			
			answerField.addKeyListener( new UserEntry() );
			
			Thread t = new Thread(this);
			t.start();
			
			doLayout();
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
					tc.selectAll();
					
					try
					{
						String s = tc.getText();
					
						double val = Double.valueOf(s).doubleValue();
						
						if( val == 2.0 || val == 16.0 )
						{
							info.firePropertyChange( info.right, -1, 1 );
						}

						else
						{
							info.firePropertyChange( info.wrong, -1, 1 );
						}
					}
					
					catch( Exception ee )
					{
					}			
				}
			}
		}

		void resetTimer()
		{
			stopTimer();
			//System.out.println("Footer: Resetting timer");
			info.firePropertyChange(info.reset_applet,null,null);
		}
		
		void stopTimer()
		{
			//System.out.println("Footer: Stopping timer");
			//startButton.setLabel("Start");
			info.firePropertyChange(info.stop,-1,1);
			running = false;
		}
		
		void startTimer()
		{
			//System.out.println("Footer: Starting timer");
			//startButton.setLabel("Stop");
			info.firePropertyChange(info.start,-1,1);
			running = true;
		}

		public void propertyChange( PropertyChangeEvent pce )
		{
			String prop = pce.getPropertyName();
			
			if( prop.equals(info.reset_applet) )
			{
				//System.out.println("Footer: Applet reset");
				stopTimer();
				info.firePropertyChange( info.time_changed, -1.0, 0.0 );
				currentTime = 0.0;
			}
			
			if( prop.equals(info.time_changed) )
			{
				Double newTime = (Double)pce.getNewValue();
				double currentTime = newTime.doubleValue();
				currentTime = Math.rint(currentTime*100.0)/100.0;
				String spacing="";
				char dig0 = (char)('0'+(int)(currentTime/10.0));
				if( dig0 == '0' ){ dig0 = ' '; spacing="  "; }
				char dig1 = (char)('0'+((int)currentTime)%10);
				char dig2 = (char)('0'+((int)(currentTime*10.0))%10);
				char dig3 = (char)('0'+((int)(currentTime*100.0))%10);
				timeLabel.setText( "Time (c) = "+spacing+dig0+dig1+'.'+dig2+dig3+" minutes" );
			}
		}
		
		public void run()
		{
			final long delay = 100;			// delay...
			final double endTime = 18.0;	// 18 seconds max
			
			//System.out.println("Footer: Timer thread started");
			
			try
			{
				for(;;)
				{
					if( running )
					{
						double oldTime = currentTime;
						
						if( currentTime >= endTime )
						{
							stopTimer();
							info.firePropertyChange( info.end, -1, 1 );
							currentTime = endTime;
						}
						
						else
						{
							currentTime += ((double)delay)/1000.0;
						}
						
						info.firePropertyChange( info.time_changed, oldTime, currentTime );
					}
					
					Thread.sleep(delay);
					Thread.yield();
				}
			}
			
			catch( Exception e )
			{
			}
		}
	}
	
	class RaceTrack extends Panel
	{
		public RaceTrack()
		{
			super( new GridLayout(2,1,5,5) );
			
			add( new Racer( "Hare", true, hareFunc ) );
			add( new Racer( "Turtle", false, turtleFunc ) );
		}
	}
	
	class Racer extends DoubleBufferPanel
				implements PropertyChangeListener
	{
		String name;
		DFunction func;
		TimeDisplay speed;
		Track track;
		
		public Racer( String name, boolean isbunny, DFunction func )
		{
			super( new BorderLayout() );
			
			this.name = name;
			this.func = func;
			
			speed = new TimeDisplay();
			
			add( speed, BorderLayout.EAST );
			
			track = new Track(isbunny);
			
			add( track, BorderLayout.CENTER );
			
			info.addPropertyChangeListener( Racer.this );
		}
	
		public void propertyChange( PropertyChangeEvent pce )
		{
			String prop = pce.getPropertyName();
			
			if( prop.equals(info.time_changed) )
			{
				Double newTime = (Double)pce.getNewValue();
				double currentTime = newTime.doubleValue();
				double currentVelocity;
				currentVelocity = slope(currentTime);
				currentVelocity = Math.rint(currentVelocity*100.0);
				speed.setValue( (int) currentVelocity );
			}
		}
		
		// use linearization to find the derivative of the function at the point
		// this is close to the instantaneous velocity at time(x) in this example.
		double slope( double x )
		{
			return func.derivative(x);
		}

		class Track extends Component
					implements PropertyChangeListener
		{
			boolean isbunny;
			double percent = 0;
			double currentTime = 0;
	
			public Track( boolean isbunny )
			{
				this.isbunny = isbunny;
				setBackground( info.CONTROL_COLOR );
				setForeground( info.FORCE_VECTOR_COLOR );
				info.addPropertyChangeListener( this );
			}
			
			public void propertyChange( PropertyChangeEvent pce )
			{
				String prop = pce.getPropertyName();
				
				if( prop.equals(info.time_changed) )
				{
					Double newTime = (Double)pce.getNewValue();
					currentTime = newTime.doubleValue();
					double distance = func.value(currentTime);
					percent = distance/2.0;
					repaint();
				}
			}
			
			public void paint( Graphics g )
			{
				Dimension dim = getSize();
				Image image;
				
				if( isbunny )
				{
					if( currentTime <= 3.0 || currentTime >= 15.0 )
						image = images[hareAwake];
					else
						image = images[hareAsleep];
				}
				
				else
				{
					image = images[tortoise];
				}
				
				int markerRadius = 16;
				
				dim.width -= 5;
				
				g.setColor( getBackground() );
				g.fillRect( 0, 0, dim.width, dim.height );
				
				int x0 = markerRadius;
				int y0 = 0;
				int lineWidth = 2;
				
				dim.width -= 2*x0;
				dim.height -= 2*y0;
				
				g.setColor( getForeground() );
				g.fillRect( x0, y0-lineWidth/2+dim.height/2,
							dim.width, lineWidth );
				
				int iw = image.getWidth(this);
				int ih = image.getHeight(this);
				
				g.drawImage( image, 
							 x0+(int)Math.rint((double)dim.width*percent)-iw/2,
							 y0+dim.height/2-ih/2,
							 this );
			}
		}
	}
	
	class ProgressPanel extends DoubleBufferPanel
						implements PropertyChangeListener
	{
		Graph graph;
		
		public ProgressPanel()
		{
			super( new BorderLayout() );
			
			graph = new Graph("Distance", "Time",
								3, 20,
								0, 0,
								1, 5);
			
			graph.setRenderBounds( 0, 18 );
			graph.addFunction( hareFunc, Color.red );
			graph.addFunction( turtleFunc, Color.blue );
			graph.setBackground( info.CONTROL_COLOR );
			
			add( graph, BorderLayout.CENTER );
			
			info.addPropertyChangeListener( ProgressPanel.this );
		}
		
		public Dimension getPreferredSize()
		{
			return new Dimension(125,10);
		}
	
		public void propertyChange( PropertyChangeEvent pce )
		{
			String prop = pce.getPropertyName();
			
			if( prop.equals(info.time_changed) )
			{
				Double newTime = (Double)pce.getNewValue();
				double currentTime = newTime.doubleValue();
				graph.setRenderBounds( 0, currentTime );
				graph.repaint();
			}
		}
	}

	interface DFunction extends Function
	{
		public double derivative( double x );
	}
	
	class HareFunc implements DFunction
	{
		public double value( double x )
		{
			if( x >= 0.0 && x < 3.0 )
				return Math.pow(x/3-1,3) + 1;
			else if( x >= 3.0 && x < 15.0 )
				return 1.0;
			else
				return Math.pow(x/3-5,3) + 1;
		}
		
		public double derivative( double x )
		{
			if( x >= 0.0 && x < 3.0 )
				return (x*x/9.-2./3.*x+1.);
			else if( x >= 3.0 && x < 15.0 )
				return 0.0;
			else
				return (x*x-30.*x+225.0)/9.0;
		}
	}
	
	class TurtleFunc implements DFunction
	{
		public double value( double x )
		{
			return x/9.0;
		}
		
		public double derivative( double x )
		{
			return 1.0/9.0;
		}
	}
}
