import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import java.util.*;

public class DataEntryPanel extends TitledPanel
{
	Squares squares;
	GraphInfo info;
	
	public DataEntryPanel( Squares squares, GraphInfo info )
	{
		super("Data Points",new GridLayout(0,1));
		this.squares = squares;
		this.info = info;
		
		setBackground( info.CONTROL_COLOR );
		
		int len = squares.getNumPoints();
		
		for( int i=0; i<len; ++i )
		{
			add( new EntryRow(i) );
		}
		
		add( new ButtonRow() );
	}
	
	class EntryRow extends Panel
				   implements PropertyChangeListener
	{
		Entry x, y;
		
		public EntryRow( int i )
		{
			x = new Entry("x"+(i+1));
			y = new Entry("y"+(i+1));
			
			add( x );
			add( y );
			
			info.addPropertyChangeListener(EntryRow.this);
		}

		public void propertyChange( PropertyChangeEvent pce )
		{
			String pname = pce.getPropertyName();
			
			if( pname.equals(info.enter_values) )
				enter();
		}
		
		void enter()
		{
			double xv = x.getValue();
			double yv = y.getValue();
			
			Object point = Graph.makePoint(xv,yv,info.POSITIVE_COLOR,xv+","+yv);
			info.firePropertyChange(info.new_point,null,point);
		}
	}
	
	class Entry extends Panel
				implements PropertyChangeListener
	{
		String label;
		TextComponent tc;
		
		public Entry( String label )
		{
			super( new BorderLayout() );
			
			this.label = label;
			
			tc = new TextField(5);
			tc.setForeground( Color.black );
			tc.setBackground( Color.white );
			
			add( new Label(label+": ",Label.RIGHT), BorderLayout.WEST );
			add( tc, BorderLayout.CENTER );
			
			info.addPropertyChangeListener(Entry.this);
		}
		
		public void propertyChange( PropertyChangeEvent pce )
		{
			String pname = pce.getPropertyName();
			
			if( pname.equals(info.reset_applet) )
				reset();
		}
		
		public double getValue()
		{
			double val = 0.0;
			
			try
			{
				String s = tc.getText();
				Double dubya = Double.valueOf(s);
				val = dubya.doubleValue();
			}
			
			catch( Exception e )
			{
				tc.setText("***");
			}
			
			return val;
		}
		
		void reset()
		{
			tc.setText("");
		}
	}
	
	class ButtonRow extends Panel
	{
		public ButtonRow()
		{
			Button enterButton = new Button("Enter");
			Button resetButton = new Button("Reset");
			
			add( enterButton );
			add( resetButton );
			
			enterButton.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						info.firePropertyChange(info.clear_points,0,1);
						info.firePropertyChange(info.enter_values,0,1);
					}
				} );
			
			resetButton.addActionListener( new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						info.firePropertyChange(info.reset_applet,0,1);
					}
				} );
		}
	}
}
