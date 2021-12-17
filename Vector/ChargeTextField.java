import java.awt.*;
import java.awt.event.*;

public class ChargeTextField extends IntegerTextField
{
	Coulomb1 appl;
	boolean radius;
	boolean isFixed;
	
	public ChargeTextField( Coulomb1 appl, boolean radius )
	{
		this(appl,radius,true);
	}
	
	public ChargeTextField( Coulomb1 appl, boolean radius, boolean isFixed )
	{
		super(radius?"2":"1");
		low = radius ? 2 : 1;
		high = radius ? 10 : 5;
		increment = radius ? 2 : 1;
		this.appl = appl;
		this.radius = radius;
		this.isFixed = isFixed;
		updateValue();
	}
	
	public void increment()
	{
		super.increment();
		updateValue();
	}
	
	public void decrement()
	{
		super.decrement();
		updateValue();
	}
	
	public void updateValue()
	{
		if( appl != null )
		{
			Charge charge = isFixed ?
								appl.getFixedCharge() :
								appl.getTestCharge();
			
			if( radius )
			{
				charge.setRadius( getValue()/increment );
				appl.recalc();
			}
			
			else
			{
				double sign = (charge.getCharge()>0) ? 1.0 : -1.0;
				charge.setCharge( getValue()*sign );
				appl.recalc();
			}
		}
	}
	
	public Dimension getMinimumSize()
	{
		FontMetrics fm = getFontMetrics(getFont());

		return new Dimension(fm.getMaxAdvance(),fm.getHeight());
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
}
