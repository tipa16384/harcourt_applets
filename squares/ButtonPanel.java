import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import java.util.*;

public class ButtonPanel extends TitledPanel
						 implements PropertyChangeListener
{
	Squares squares;
	GraphInfo info;

	Button estimate;
	Button residuals;
	Button compare;
	Button done;
	
	StageButton estimateButton;
	StageButton residualsButton;
	StageButton compareButton;
	StageButton doneButton;
	
	public ButtonPanel( Squares squares, GraphInfo info )
	{
		super("Commands",new GridLayout(0,1,10,10));
		this.squares = squares;
		this.info = info;
		
		info.addPropertyChangeListener( this );
		
		setBackground( info.CONTROL_COLOR );
		
		estimate = new Button("Estimate");
		residuals = new Button("Residuals");
		compare = new Button("Compare");
		done = new Button("Done");
		
		/*
		add( new StageButton("Test","test",info) );
		
		addEntry( estimate );
		addEntry( residuals );
		addEntry( compare );
		addEntry( done );
		*/
		
		estimateButton = new StageButton("Estimate",info.make_estimate,info);
		residualsButton = new StageButton("Residuals",info.calc_residuals,info);
		compareButton = new StageButton("RMSE",info.make_comparison,info);
		doneButton = new StageButton("Done",info.all_done,info);
		
		add( estimateButton );
		add( residualsButton );
		add( compareButton );
		add( doneButton );
		
		doneButton.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					allDone();
				}
			} );
		
		residualsButton.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					calcResiduals();
				}
			} );
		
		estimateButton.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					makeEstimate();
				}
			} );
		
		compareButton.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					makeComparison();
				}
			} );
	}
	
	public void propertyChange( PropertyChangeEvent pce )
	{
		String pname = pce.getPropertyName();
		
		if( pname.equals(info.reset_applet) )
		{
			estimateButton.setEnabled(false);
			residualsButton.setEnabled(false);
			compareButton.setEnabled(false);
			doneButton.setEnabled(false);
		}
		
		if( pname.equals(info.enter_values) )
		{
			estimateButton.setEnabled(false);
			residualsButton.setEnabled(false);
			compareButton.setEnabled(false);
			doneButton.setEnabled(false);
		}
		
		if( pname.equals(info.make_estimate) )
		{
			estimateButton.setEnabled(true);
			residualsButton.setEnabled(false);
			compareButton.setEnabled(false);
			doneButton.setEnabled(false);
		}		
		
		if( pname.equals(info.calc_residuals) )
		{
			estimateButton.setEnabled(false);
			residualsButton.setEnabled(true);
			compareButton.setEnabled(false);
			doneButton.setEnabled(false);
		}		
		
		if( pname.equals(info.make_comparison) )
		{
			estimateButton.setEnabled(false);
			residualsButton.setEnabled(false);
			compareButton.setEnabled(true);
			doneButton.setEnabled(false);
		}		

		if( pname.equals(info.all_done) )
		{
			estimateButton.setEnabled(false);
			residualsButton.setEnabled(false);
			compareButton.setEnabled(false);
			doneButton.setEnabled(true);
		}		
	}

	public Insets getInsets()
	{
		Insets insets = super.getInsets();
		
		insets.top += 8;
		insets.bottom += 8;
		insets.left += 10;
		insets.right += 10;
		
		return insets;
	}

	void allDone()
	{
		info.firePropertyChange(info.all_done,0,1);
	}
	
	void calcResiduals()
	{
		info.firePropertyChange(info.calc_residuals,0,1);
	}
	
	void makeEstimate()
	{
		info.firePropertyChange(info.enter_values,0,1);
		info.firePropertyChange(info.make_estimate,0,1);
	}
	
	void makeComparison()
	{
		info.firePropertyChange(info.make_comparison,0,1);
	}
	
	void addEntry( Component c )
	{
		Panel p = new Panel();
		p.add( c );
		add( p );
	}
}
