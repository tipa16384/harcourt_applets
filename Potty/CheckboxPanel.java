import java.awt.*;

class CheckboxPanel extends Panel
{
	Checkbox cb;
	
	public CheckboxPanel(String label,boolean state,
						String [] text, CheckboxGroup group )
	{
		super( new BorderLayout() );
		
		cb = new Checkbox(label,state,group);
		add( cb, BorderLayout.NORTH );
		add( new TextPanel(text), BorderLayout.CENTER );
	}
	
	public Checkbox getCheckbox()
	{
		return cb;
	}
}

