import java.awt.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;

public class Melon extends Panel
{
	Main main;
	GraphInfo info;
	PlanetMovie planetMovie;
	GraphPanel graphPanel;
	HurlPanel hurlPanel;
	
	double altitude;
	
	public Melon( Main main, GraphInfo info )
	{
		super( new BorderLayout(5,5) );
		
		this.main = main;
		this.info = info;

		setBackground( Color.black );
		setForeground( Color.green );
		setFont( info.fontBigPlain );
		
		planetMovie = new PlanetMovie(this,info);
		graphPanel = new GraphPanel(this,info);
		hurlPanel = new HurlPanel(this,info);
		
		Panel p;
		
		p = new DoubleBufferPanel( new BorderLayout(5,5) );
		p.add( planetMovie, BorderLayout.WEST );
		p.add( graphPanel, BorderLayout.CENTER );
		
		add( p, BorderLayout.NORTH );
		
		p = new Panel( new BorderLayout(5,5) );
		p.add( hurlPanel, BorderLayout.WEST );
		
		add( p, BorderLayout.CENTER );
		
		chooseValues();
	}

	public Insets getInsets()
	{
		return new Insets(5,5,5,5);
	}
	
	public void reset()
	{
		if( planetMovie != null )
		{
			planetMovie.reset();
		}
		
		if( graphPanel != null )
		{
			graphPanel.reset();
		}

		chooseValues();
	}
	
	public void chooseValues()
	{
		Planet planet = getPlanet();
		
		if( planet != null )
		{
			altitude = planet.rollHeight();
	
			graphPanel.setPlanet( planet );
			graphPanel.setAltitude( altitude );
			planetMovie.setAltitude( altitude );
		}
	}
	
	public Planet getPlanet()
	{
		if( planetMovie == null )
			return null;
		else
			return planetMovie.getSelectedPlanet();
	}
	
	public int getNumPlanets()
	{
		return (planetMovie == null) ? 0 : planetMovie.getNumPlanets();
	}
	
	public Planet getPlanet( int idx )
	{
		return (planetMovie == null) ? null : planetMovie.getPlanet(idx);
	}
	
	public void setSuccess( int success )
	{
		//System.out.println("success is "+success);
		Planet planet = getPlanet();
		planet.setSuccess(success);
		hurlPanel.redrawSuccess();
	}
	
	public void hurl()
	{
		double initialVelocity;

		initialVelocity = hurlPanel.getVelocity();
		planetMovie.hurl(altitude,initialVelocity);
		graphPanel.hurl(initialVelocity);
	}
	
	public void setHurlTime( double secs )
	{
		if( graphPanel != null )
		{
			graphPanel.setHurlTime( secs );
		}
	}
	
	public Main getMain()
	{
		return main;
	}
}
