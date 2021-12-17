import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import java.util.*;

public class MessagePanel extends Panel
						  implements PropertyChangeListener
{
	Squares squares;
	GraphInfo info;
	Message message;

	public MessagePanel( Squares squares, GraphInfo info )
	{
		super( new BorderLayout() );
		this.squares = squares;
		this.info = info;
		
		info.addPropertyChangeListener( this );
		
		message = new Message();
		add( message, BorderLayout.CENTER );
	}		

	public void propertyChange( PropertyChangeEvent pce )
	{
		String pname = pce.getPropertyName();
		
		if( pname.equals(info.reset_applet) )
		{
			message.setText("Enter data points and click \"Enter\"");
		}
		
		if( pname.equals(info.enter_values) )
		{
			message.setText("Click \"Estimate\" to display and adjust estimated fit.");
		}
		
		if( pname.equals(info.make_estimate) ||
		    pname.equals(info.new_estimate) )
		{
			message.setText("Click \"Residuals\" to display residuals for the estimate.");
		}
		
		if( pname.equals(info.calc_residuals) )
		{
			message.setText("Click \"Compare\" to display the RMSEs.");
		}
		
		if( pname.equals(info.make_comparison) )
		{
			message.setText("Click \"Done\" to show the line of best fit.");
		}
		
		if( pname.equals(info.all_done) )
		{
			message.setText("Click \"Reset\" to enter another set of values.");
		}
	}
}
