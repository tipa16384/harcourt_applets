import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PlanetMovie extends Component
{
	Melon melon;
	GraphInfo info;
	
	final int zoomTime = 3000;
	final int skyTime = 2000;
	final int satelliteTime = 4000;
	final int totalTime = zoomTime+skyTime+satelliteTime;
	
	final int numPlanets = 3;
	final int starfieldIndex = 3;
	final int satelliteIndex = 4;
	final int melonIndex = 5;
	
	Planet [] planets =
		{
			new Planet( "Earth", "earth.gif", "earsky.gif", 9.8, 100.0, 900.0, 100.0 ),
			new Planet( "Jupiter", "jupiter.gif", "jupsky.gif", 24.8, 800.0, 1600.0, 200.0 ),
			new Planet( "Pluto", "pluto.gif", "plusky.gif", 0.6, 50.0, 425.0, 25.0 ),
			new Planet( "Stars", "starfield.gif", null, 0.0, 0.0, 0.0, 0.0 ),
			new Planet( "UFO", "satellite.gif", null, 0.0, 0.0, 0.0, 0.0 ),
			new Planet( "Melon", "melon.gif", null, 0.0, 0.0, 0.0, 0.0 )
		};
	
	Planet planet = null;
	
	int planetSize = 10;
	int planetOffset = 0;
	int skyAmount = 0;
	int ufoAmount = 0;
	int ufoRotate = 0;
	int selectedPlanet = 0;
	
	long curtime=0;
	long hurltime = 0;
	
	boolean showGlobe = true;
	boolean showSky = true;
	boolean showSatellite = true;

	double hurlheight = 900.0;		// meters
	double hurlvelocity = 0.0;		// initial velocity meters/sec
	double hurlgravity = 9.8;		// meters/sec/sec
	double altitude = 0.0;
	boolean hurling = false;
	boolean landed = false;
	double landtime = 0.0;
	
	Font font;
	FontMetrics fm;
	
	Font infoFont;
	FontMetrics infoFontMetrics;
	
	Thread zoomThread = null;
	Thread rotateThread = null;
	
	public PlanetMovie( Melon melon, GraphInfo info )
	{
		this.melon = melon;
		this.info = info;
		
		font = info.fontBiggerBold;
		fm = getFontMetrics(font);
		
		infoFont = info.fontBigPlain;
		infoFontMetrics = getFontMetrics(infoFont);
		
		loadPlanets();
		
		setPlanet( planets[selectedPlanet] );
		
		addMouseListener( new MouseAdapter()
			{
				public void mouseClicked( MouseEvent e )
				{
					doClick( e );
				}
			} );
		
		zoomIn(0);
	}
	
	public int getNumPlanets()
	{
		return numPlanets;
	}
	
	public Planet getPlanet( int idx )
	{
		if( idx >= 0 && idx < numPlanets )
			return planets[idx];
		else
			return null;
	}
	
	public void setAltitude( double alt )
	{
		altitude = alt;
		repaint();
	}
	
	long getDelay( long time )
	{
		if( time < zoomTime )
		{
			Main main = melon.getMain();
			return main.isCrippled() ? 333 : 100;
		}
		
		else
		{
			return 20;
		}
	}
	
	void doClick( MouseEvent e )
	{
		int x = e.getX();
		int y = e.getY();
		Dimension dim = getSize();
		int fh = fm.getHeight();
		int y0 = dim.height - fh;
		
		if( y >= y0 )
		{
			if( x < 20 )
			{
				if( --selectedPlanet < 0 )
					selectedPlanet = numPlanets-1;
				
				setPlanet( planets[selectedPlanet] );
			}
			
			else if( x > (dim.width-20) )
			{
				if( ++selectedPlanet >= numPlanets )
					selectedPlanet = 0;
				
				setPlanet( planets[selectedPlanet] );
			}
		}
		
		else
		{
			zoomIn(0);
		}
	}
	
	void reset()
	{
		if( rotateThread != null )
		{
			System.out.println("Returning station to home");
			hurling = false;
		}
		
		else
		{
			System.out.println("Travelling to another planet");
			zoomIn( zoomTime );
		}
	}
	
	public void hurl( double alt, double vel )
	{
		if( !hurling && rotateThread != null )
		{
			//System.out.println("I think I'm going to hurl...");
			hurling = true;
			setLanded(false,0);
			hurltime = curtime;
			hurlheight = alt;
			hurlvelocity = vel;
			hurlgravity = getSelectedPlanet().getGravity();
		}
	}
	
	Planet getSelectedPlanet()
	{
		return planets[selectedPlanet];
	}

	void setLanded( boolean lan, double lt )
	{
		landed = lan;
		landtime = lt;
		
		if( lan )
			melon.setSuccess((Math.abs(lt-10.0)<0.1)?Planet.SUCCESS:Planet.FAILURE);
		else
			melon.setSuccess(Planet.WAITING);
	}
	
	void zoomIn( long time )
	{
		if( rotateThread != null )
		{
			rotateThread.interrupt();
		}
		
		if( zoomThread == null )
		{
			zoomThread = new ZoomThread(time);
			zoomThread.start();
		}
	}
	
	void stationKeeping()
	{
		if( rotateThread == null )
		{
			rotateThread = new RotateThread();
			rotateThread.start();
		}
	}
	
	class RotateThread extends Thread
	{
		public void run()
		{
			//System.out.println("Starting station keeping");
			
			try
			{
				long now = System.currentTimeMillis();
				for(;;)
				{
					long nower = System.currentTimeMillis();
					setTime( curtime+nower-now );
					now = nower;
					Thread.yield();
					Thread.sleep(getDelay(curtime));
				}
			}
			
			catch( Exception e )
			{
			}
			
			finally
			{
				rotateThread = null;
			}
			
			System.out.println("Ending station keeping");
		}
	}
	
	class ZoomThread extends Thread
	{
		public ZoomThread( long basetime )
		{
			setTime( basetime );
		}
		
		public void run()
		{
			//System.out.println("Zoom beginning");
			
			hurling = false;
			
			try
			{
				long now = System.currentTimeMillis();
				long tl;
				
				do
				{
					long nower = System.currentTimeMillis();
					setTime( curtime+nower-now );
					now = nower;
					Thread.yield();
					Thread.sleep(getDelay(curtime));
				}
				while( curtime < totalTime );
			}
			
			catch( Exception e )
			{
			}
			
			finally
			{
				zoomThread = null;
				stationKeeping();
			}
			
			//System.out.println("Zoom ending");
		}
	}

	void setTime( long tl )
	{
		curtime = tl;
		
		ufoRotate = (int)(tl/50);
		
		if( tl <= zoomTime )
		{
			final int min = 10;
			final int max = 1000;

			showGlobe = true;
			showSky = false;
			showSatellite = false;			
			planetSize = (int)(min + (tl*(max-min))/zoomTime);
			planetOffset = (int)((tl*2*planetSize*7)/(zoomTime*10));
		}
		
		else if( (tl-zoomTime) < skyTime )
		{
			showGlobe = false;
			showSky = true;
			showSatellite = false;
			long ct = tl-zoomTime;
			skyAmount = (int)((32*ct)/skyTime);
		}
		
		else if( (tl-zoomTime-skyTime) < satelliteTime )
		{
			showGlobe = false;
			showSky = true;
			showSatellite = true;
			long ct = tl-zoomTime-skyTime;
			ufoAmount = (int)((-300 * (satelliteTime-ct))/satelliteTime);
		}

		if( hurling && !landed )
		{
			melon.setHurlTime( (double)(curtime-hurltime)/1000.0 );
		}
		
		repaint();
	}
	
	public void setPlanet( Planet plan )
	{
		planet = plan;
		planetSize = 10;
		planetOffset = 0;
		setTime(0);
		melon.reset();
		repaint();
	}
	
	public Planet getPlanet()
	{
		return planet;
	}
	
	void loadPlanets()
	{
		try
		{
			int len = planets.length;
			int i;
			MediaTracker mt = new MediaTracker(this);
			
			for( i=0; i<len; ++i )
			{
				planets[i].loadImage(mt,this);
			}
			
			mt.waitForAll();
		}
		
		catch( Exception e )
		{
			System.err.println("Planet loading failed - "+e);
		}
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(128,200);
	}
	
	public void paint( Graphics g )
	{
		Dimension dim = getSize();
		
		g.drawImage( planets[starfieldIndex].getImage(), 0, 0, this );
		
		if( showGlobe && (planet != null) )
		{
			Image image = planet.getImage();
			if( image != null )
			{
				int iw = image.getWidth(this);
				int ih = image.getHeight(this);
				final int dh = planetSize;
				final int dw = (dh*iw)/ih;
				
				//System.out.println("planetOffset="+planetOffset);
				
				g.drawImage( image, (dim.width-dw)/2, (dim.height+planetOffset-dh)/2,
								dw, dh, this );
			}
		}

		if( showSky && (planet != null) )
		{
			Image image = planet.getSkyImage();
			if( image != null )
			{
				int y0 = dim.height - fm.getHeight() - skyAmount + 1;
				g.drawImage( image, 0, y0, this );
			}
		}

		int fh = fm.getHeight();
		int yh = dim.height - fh;
		
		if( showSatellite )
		{
			Image image;
			int ufoYOffset = 0;
			int melonYOffset = 0;
			double melonheight = 0;
			double time = 0;

			if( hurling )
			{
				time = ((double)(curtime-hurltime))/1000.0;
				melonheight = hurlheight + -hurlvelocity*time - (hurlgravity*time*time)/2;
				double d0;
				
				if( !landed && (melonheight < 0.0) )
				{
					setLanded(true,time);
				}
				
				if( landed )
				{
					melonheight = 0.0;
					time = landtime;
				}
				
				d0 = (((double)(-yh)*melonheight)/(2.0*hurlheight)) + ((double)yh)/2.0;
				
				melonYOffset = (int)d0;
				ufoYOffset = -(int)(20.0*d0);
			}
			
			image = planets[melonIndex].getImage();
			if( hurling && image != null )
			{
				int iw = image.getWidth(this);
				int ih = image.getHeight(this);
				final int numframes = 1;
				int framenum = ufoRotate%numframes;
				int frameheight = ih/numframes;
				
				int y0 = yh/2 - frameheight + melonYOffset;
				int x0 = (dim.width+ufoAmount-iw)/2;
				
				g.drawImage(image,
					x0,y0,x0+iw,y0+frameheight,
					0,framenum*frameheight,iw,(framenum+1)*frameheight,
					this);
				
				final int scopeLen = 5;
				final int scopeGap = 2;
				
				g.setColor( getForeground() );
				//g.drawLine( x0-scopeGap, y0-scopeGap, x0-scopeGap+scopeLen, y0-scopeGap );
				//g.drawLine( x0-scopeGap, y0-scopeGap, x0-scopeGap, y0-scopeGap+scopeLen );
				
				String s = ((int)Math.rint(melonheight))+"m";
				g.setFont( info.fontPlain );
				FontMetrics afm = getFontMetrics(info.fontPlain);
				int sw = afm.stringWidth(s);
				int ah = afm.getAscent();
				int ya = y0+frameheight/2+ah/2;
				g.drawString( s, dim.width-sw-scopeLen-scopeGap, ya );
				g.drawLine( dim.width, ya-ah/2, dim.width-scopeLen, ya-ah/2 );
				
				s = Math.rint(time*10.0)/10.0+"s";
				g.drawString( s, scopeLen+scopeGap, ya );
				g.drawLine( 0, ya-ah/2, scopeLen, ya-ah/2 );
			}
			
			image = planets[satelliteIndex].getImage();
			if( image != null )
			{
				int iw = image.getWidth(this);
				int ih = image.getHeight(this);
				final int numframes = 16;
				int framenum = ufoRotate%numframes;
				int frameheight = ih/numframes;
				
				int y0 = (yh-frameheight)/2 + ufoYOffset;
				int x0 = (dim.width+ufoAmount-iw)/2;
				
				g.drawImage(image,
					x0,y0,x0+iw,y0+frameheight,
					0,framenum*frameheight,iw,(framenum+1)*frameheight,
					this);
			}
		}
				
		// draw planet data
		
		if(false)
		{
			g.setColor( getForeground() );
			g.setFont( infoFont );
			
			String s;
			int y0 = infoFontMetrics.getAscent();
			int x0 = 0;
			
			s = "Gravity: "+planet.getGravity()+"m/s/s";
			
			g.drawString( s, x0, y0 );
			y0 += infoFontMetrics.getHeight();
			
			//s = "Altitude: "+altitude+"m";
			
			//g.drawString( s, x0, y0 );
			//y0 += infoFontMetrics.getHeight();
		}
		
		// draw the planet name and selection arrows
		
		{
			g.setColor( getBackground() );
			g.fillRect( 0, yh, dim.width, fh );
			
			g.setColor( getForeground() );
			g.setFont( font );
			
			String s = planet.getName();
			int tw = fm.stringWidth(s);
			g.drawString( s, (dim.width-tw)/2, yh+fm.getAscent() );

			g.setColor( getForeground() );

			int x0 = 5;
			int x9 = dim.width-5;
			
			for( int i=1; i<=(fh-6); i += 2, ++x0, --x9 )
			{
				g.drawLine(x0,yh+(fh-i)/2,x0,yh+(fh+i)/2);
				g.drawLine(x9,yh+(fh-i)/2,x9,yh+(fh+i)/2);
			}
		}
	}
}
