import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import java.util.*;

public class DataEntryPanel extends TitledPanel
							implements PropertyChangeListener
{
	Infect infect;
	GraphInfo info;
	Button plotButton, resetButton;
	TextComponent xText, yText;
	
	static final String entryTemplate = "9.99E99";
	
	public DataEntryPanel( Infect infect, GraphInfo info )
	{
		super("Enter Data",new BorderLayout());
		
		setStyle( TitledPanel.TRANSPARENT );
		
		Panel p, p1;
		
		p1 = new Panel( new GridLayout(0,1,0,5) );
		
		this.infect = infect;
		this.info = info;
		
		xText = new TextField(entryTemplate);
		yText = new TextField(entryTemplate);
		
		setFont( info.fontBigBold );
		
		p = new Panel( new BorderLayout() );
		p.add( xText, BorderLayout.EAST );
		p.add( new Label("X (minutes) =",Label.RIGHT), BorderLayout.CENTER );
		p1.add( p );
		
		p = new Panel( new BorderLayout() );
		p.add( yText, BorderLayout.EAST );
		p.add( new Label("Y (population size) =",Label.RIGHT), BorderLayout.CENTER );
		p1.add( p );
		
		add( p1, BorderLayout.WEST );
		
		plotButton = new Button("Plot data point");
		plotButton.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					plotPoint();
				}
			} );
			
		resetButton = new Button("Reset graph");
		resetButton.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					reset();
				}
			} );
			
		
		p1 = new Panel( new GridLayout(0,1,0,5) );
		p1.add( plotButton );
		p1.add( resetButton );
		add( p1, BorderLayout.EAST );
		
		info.addPropertyChangeListener( this );
	}
	
	void plotPoint()
	{
		TextComponent parsing = null;
		
		System.out.println("Plotting...");
		
		try
		{
			double x, y;
		
			parsing = xText;	
			x = (new Double(parsing.getText())).doubleValue();
			parsing = yText;
			y = (new Double(parsing.getText())).doubleValue();
			parsing = null;
			
			System.out.println("Point is "+x+","+y);
			
			info.firePropertyChange(info.new_point,null,new DPoint(x,y));
			
			clear();
		}
		
		catch( Exception e )
		{
			System.err.println("Error while parsing - "+e);
			
			if( parsing != null )
			{
				parsing.requestFocus();
				parsing.selectAll();
			}
		}
	}
	
	void reset()
	{
		System.out.println("Reset...");
		info.firePropertyChange(info.reset_applet,0,1);
	}

	public Insets getInsets()
	{
		Insets insets = super.getInsets();
		insets.top += 5;
		insets.bottom += 5;
		return insets;
	}
	
	public void clear()
	{
		xText.setText("");
		yText.setText("");
		xText.requestFocus();
	}

	public void propertyChange( PropertyChangeEvent pce )
	{
		String pname = pce.getPropertyName();
		
		if( pname.equals(info.reset_applet) )
		{
			clear();
		}
	}
}
