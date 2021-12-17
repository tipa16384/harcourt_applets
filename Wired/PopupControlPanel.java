package Wired;

import java.awt.*;
import java.awt.event.*;
import util.RapidLabel;

public class PopupControlPanel extends PaddedPanel
							   implements ActionListener
{
	// in ohms
	static final String [] ohmList =
		{
			"10",
			"25",
			"50",
			"75",
			"100",
			"120",
			"150",
			"200"
		};
	
	// in microfarads - mult is 1E-6
	static final String [] faradList =
		{
			"5",
			"10",
			"50",
			"60",
			"80",
			"100",
			"250",
			"1000"
		};
	
	// in milliHenris - mult is 1E-3
	static final String [] henriList =
		{
			"50",
			"100",
			"120",
			"160",
			"200",
			"250",
			"385",
			"400"
		};

	public PopupControlPanel( Wired actionSource )
	{
		super( new ColumnLayout(10,5), 0, 10, 0, 0 );
		setFont( actionSource.getFont() );
		setBackground( GraphInfo.CONTROL_COLOR );
		actionSource.addActionListener( this );
	}
	
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand().equals(Wired.CIRCUIT) )
		{
			Circuit c = (Circuit) e.getSource();
			
			removeAll();
			
			Panel p;
			Component comp;
			RawLabel rl;
			
			// labels
			
			p = new Panel(null);
			rl = new RawLabel("AC Source");
			rl.setVerticalAlignment( Label.RIGHT );
			p.add( rl );
			
			comp = new RapidLabel("Display on Graph",Label.CENTER,55,true);
			comp.setFont( getFont() );
			p.add( comp );
			add( p );
			
			// power
			
			{
				p = new Panel(null);
				NumedicField f;
				IncrementTool it;
				RawCheckbox eddie = c.power.getCheckbox();
				
				Font font = eddie.getFont();
				font = new Font(font.getName(), font.getStyle(),
					14 );
				eddie.setFont(font);
				
				f = new NumedicField(c.power,"amplitude");
				it = new IncrementTool(f);
				p.add( new PoopUp(f,it,"Amplitude"," V") );
				p.add( eddie );
				add(p);
				
				p = new Panel(null);
				f = new NumedicField(c.power,"frequency");
				it = new IncrementTool(f);
				p.add( new PoopUp(f,it,"Frequency"," Hz") );
				add(p);
			}
			
			// blank space
			
			add( new PaddedPanel(null,10,0,0,0) );
			
			// resistor
			
			if( c.hasResistor )
			{
				NumedicChoice f;
				
				p = new Panel(null);
				f = new NumedicChoice(c.resistor,"ohm",ohmList,1);
				p.add( new PoopUp(f,null,"Resistance"," ~W") );
				p.add( c.resistor.getCheckbox() );
				add(p);
			}
			
			// coil
			
			if( c.hasCoil )
			{
				NumedicChoice f;
				
				p = new Panel(null);
				f = new NumedicChoice(c.coil,"henri",henriList,1);
				p.add( new PoopUp(f,null,"Inductance"," mH") );
				p.add( c.coil.getCheckbox() );
				add(p);
			}
			
			// capacitor
			
			if( c.hasCapacitor )
			{
				NumedicChoice f;
				
				p = new Panel(null);
				f = new NumedicChoice(c.capacitor,"farad",faradList,1);
				p.add( new PoopUp(f,null,"Capacitance"," ~mF") );
				p.add( c.capacitor.getCheckbox() );
				add(p);
			}
			
			validate();
		}
	}
}
