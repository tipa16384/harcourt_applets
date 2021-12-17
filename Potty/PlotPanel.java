import java.awt.*;
import java.awt.event.*;
import Graph.*;

public class PlotPanel extends Panel implements ActionListener
{
	static final boolean debug = false;

	Graph graph = null;
	Example state = null;
	int mode = -1;
	DataSet ds=null, ds2=null, ds3=null;
	RawLabel resultsSign;
		
	public PlotPanel()
	{
		super( new FixedLayoutManager() );		//was new BorderLayout()
	    graph = new Graph();
	    graph.setBounds(10, 215, 430, 275);
	    graph.setName("ElecPot test");
	    graph.setLabels("r", "V");
	    graph.setXAxis(0.0D, 100.0D, 0, 0);
	    graph.setYAxis(-0.023, 0.023, 1E-10, 0.0);
		graph.xMajNum = graph.xMinNum =graph.yMajNum = graph.yMinNum = false;
		graph.yMaxMinNum = true;	//just show the org/max values
		
		add( graph, new Rectangle(0,0,179,178) );
		//add( graph );

	    ds = new DataSet();
	    ds.setGraphDisplay( DataSet.DOT, 2, Color.red);
	    //ds.addDataPoint(0,0);		//add initial start at 0,0
	    //ds.addDataPoint(100,100);
	    ds2 = new DataSet();
	    ds2.setGraphDisplay( DataSet.CIRCLE, 2, Color.red);

	    ds3 = new DataSet();
	    ds3.setGraphDisplay( DataSet.LINE, 2, Color.blue);

	    graph.addDataSet( ds );					//add both datasets to our graph
	    graph.addDataSet( ds2 );
	    graph.addDataSet( ds3 );
	    
	    if( GraphInfo.CorrectAnswerPopup )
	    	resultsSign = new RawLabel("(Data Matches)");
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(100,100);		//must stay 100 high for 3D image art
		//return new Dimension(90,90);		//must stay 90 high for 3D image art
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public void setState( Example state )
	{
		debug("setState("+state+")");
		
		if( this.state != null )
			this.state.removeActionListener(this);
		
		this.state = state;
		
		state.setupGraph( graph );
		graph.clear();
		state.setupPlot( graph, ds3 );
		debug("back from setupPlot()");
		state.addActionListener( this );
		
		//removeAll();

		repaint();
		validate();
	}
	
	public void setMode( int mode )
	{
		debug("mode set to "+mode);
		this.mode = mode;
		
		if( state != null )
			state.triggerRedraw();
	}
	
	private void plotPoint( DPoint d )
	{
		if( d.special != 0 )
		{
			if( ds != null )
				ds.addDataPoint( d.x, d.y );
		}
		
		else if( ds2 != null )
			ds2.addDataPoint( d.x, d.y );
	}
	
	private void redrawGraph( DPoint d )
	{
		if( mode == ElecPot.GRAPH_MODE && graph != null && state != null)
		{
			debug("redrawGraph - "+d);

			ds3.setImmediacy(false);
			ds3.removeAllElements();

			int min = (int) graph.xOrigin;
			int max = (int) graph.xMax;
			
			debug("min="+min+" max="+max);
			
			for( int x = min; x <= max; ++x )
			{
				double z = (double) x;
				
				ds3.addDataPoint( x,
						state.potential(z,(int)d.x,(int)d.y,(int)d.z) );
			}

							//Show the special "Data Matches" string
							// when in Graph mode
							//	   and user has plotted some/any points
							//	   and the slider values match the setExample values
							//			which are a different number for some funcs
							// by putting var resultsSign into graph.specialTextPopup
			if( (resultsSign != null)
				&& (mode == ElecPot.GRAPH_MODE)
				&& (!ds.isEmpty() || !ds2.isEmpty())
				&& (state.Q == d.x)
				&& ( (state.getID() == Example.POINTCHARGE)
					|| (state.R == d.y)
					|| ((state.getID() == Example.INFINITECOAX) && (state.A == d.y) && (state.B == d.z) ) ) )
				{
					debug("******************Data Match******************"+state.Q);
					graph.specialTextPopup = resultsSign;
				}
			else
				graph.specialTextPopup = null;
			
			ds3.setImmediacy( true );
			repaint();
		}
		
		else
		{
			ds3.removeAllElements();
			graph.specialTextPopup = null;
			repaint();
		}
	}
	
	public void actionPerformed( ActionEvent e )
	{
		String s = e.getActionCommand();
		
		if( s.equals(ElecPot.STATECHANGED) )
			setState( (Example) e.getSource() );
		else if( s.equals(ElecPot.MODECHANGED) )
			setMode( e.getID() );
		else if( s.equals(ElecPot.POINT) )
			plotPoint( (DPoint) e.getSource() );
		else if( s.equals(ElecPot.RESET) || s.equals(ElecPot.EXAMPLE) )
			graph.clear();
		else if( s.equals("redraw") )
			redrawGraph( (DPoint) e.getSource() );
	}

	static void debug( String s )
	{
		if( debug )
		{
			System.out.println("PlotPanel:: "+s);
		}
	}
}
