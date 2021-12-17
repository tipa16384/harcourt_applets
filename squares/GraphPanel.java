import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import java.util.*;
import util.DoubleBufferPanel;

public class GraphPanel extends TitledPanel
						implements PropertyChangeListener
{
	Squares squares;
	GraphInfo info;
	ResidualList residualList;
	RMSEValues rmseValues;
	EstimateWiggler wiggler;
	
	Graph graph;
	
	boolean adjusted = false;
	boolean showResiduals = false;
	boolean showRMSE = false;
	double residuals[];
	
	double rmse[] = new double[2];
	static final int estimate = 0;
	static final int actual = 1;
		
	int estimatedLineIndex = -1;
	double estimatedSlope;
	double estimatedOffset;
	
	int actualLineIndex = -1;
	double actualSlope;
	double actualOffset;
	
	public GraphPanel( Squares squares, GraphInfo info )
	{
		super("Graph",new BorderLayout(5,5));
		this.squares = squares;
		this.info = info;
		
		setBackground( info.CONTROL_COLOR );
		
		graph = new Graph("y", "x",
					100.0, 100.0,
					-100.0, -100.0,
					10.0, 10.0);
		
		graph.hideTitle();
		
		wiggler = new EstimateWiggler();
		
		add( graph, BorderLayout.CENTER );
		
		residualList = new ResidualList();
		rmseValues = new RMSEValues();
		
		Panel p = new MarginPanel( new GridLayout(0,1,5,5), new Insets(5,0,5,0) );
		p.add( residualList, BorderLayout.NORTH );
		p.add( rmseValues, BorderLayout.SOUTH );
		
		add( p, BorderLayout.EAST );
		
		info.addPropertyChangeListener(this);
	}
	
	class MarginPanel extends Panel
	{
		Insets insets;
		
		public MarginPanel( LayoutManager layout, Insets insets )
		{
			super( layout );
			this.insets = insets;
		}
		
		public Insets getInsets()
		{
			return insets;
		}
	}
	
	class EstimateWiggler extends MouseAdapter
						  implements MouseMotionListener
	{
		boolean tracking;
		int x, y;
		
		public EstimateWiggler()
		{
			tracking = false;
		}
		
		public void mousePressed( MouseEvent e )
		{
			System.out.println("mousePressed");
			
			removeActual();
			showResiduals = false;
			showRMSE = false;
			rmseValues.repaint();
			residualList.repaint();
			info.firePropertyChange(info.new_estimate,0,1);
	
			x = e.getX();
			y = e.getY();
			tracking = true;
		}
		
		public void mouseReleased( MouseEvent e )
		{
			System.out.println("mouseReleased");
			
			tracking = false;
		}
		
		public void mouseMoved( MouseEvent e )
		{
		}
		
		public void mouseDragged( MouseEvent e )
		{
			int x0 = e.getX();
			int y0 = e.getY();
			
			double dy = graph.getY(y0) - graph.getY(y);
			double dx = graph.getX(x0) - graph.getX(x);
			boolean above = graph.getY(y0) > estimatedOffset;
			final double ratio = 0.05;
			
			double angle = Math.atan(estimatedSlope);
			
			if( above )
				angle -= ratio*dx;
			else
				angle += ratio*dx;
			
			if( Math.abs(angle) == Math.PI/2.0 )
				angle += 0.01;
			
			estimatedSlope = Math.tan(angle);
			estimatedOffset += dy;
			
			graph.repaint();
			
			x = x0;
			y = y0;
		}
	}
	
	public void propertyChange( PropertyChangeEvent pce )
	{
		String pname = pce.getPropertyName();
		
		if( pname.equals(info.new_point) )
		{
			Object o = pce.getNewValue();
			graph.addPoint( o );
		}
		
		if( pname.equals(info.reset_applet) )
		{
			graph.removeAllPoints();
			removeEstimate();

			graph.removeMouseListener( wiggler );
			graph.removeMouseMotionListener( wiggler );
		}
		
		if( pname.equals(info.enter_values) )
		{
		}
		
		if( pname.equals(info.calc_residuals) )
		{
			calcResiduals();
		}
		
		if( pname.equals(info.clear_points) )
		{
			graph.removeAllPoints();
		}
		
		if( pname.equals(info.make_estimate) )
		{
			graph.addMouseListener( wiggler );
			graph.addMouseMotionListener( wiggler );
			estimatedFit();
		}
		
		if( pname.equals(info.make_comparison) )
		{
			compare();
		}
		
		if( pname.equals(info.all_done) )
		{
			done();
		}
	}
	
	void done()
	{
		compare();
		removeActual();
		actualLineIndex = graph.addFunction(
						new LineFunction(actualSlope,actualOffset),
						info.POSITIVE_COLOR );
		showResiduals = false;
		residualList.repaint();
	}
	
	void calcResiduals()
	{
		if( estimatedLineIndex >= 0 )
		{
			Vector xv = new Vector();
			Vector yv = new Vector();
			
			graph.getPoints( xv, yv );
			
			int len = xv.size();
			int i;
			residuals = new double[len];
			
			for( i=0; i<len; ++i )
			{
				double x = ((Double)xv.elementAt(i)).doubleValue();
				double y = ((Double)yv.elementAt(i)).doubleValue();

				double y0 = x*estimatedSlope + estimatedOffset - y;
				residuals[i] = y0;
			}
		}
		
		showResiduals = true;
		residualList.repaint();
	}
	
	void compare()
	{
		calcResiduals();
		
		Vector xv = new Vector();
		Vector yv = new Vector();
		
		graph.getPoints( xv, yv );
		
		int len = xv.size();
		int i;
		double sumX = 0;
		double sumX2 = 0;
		double sumY = 0;
		double sumXY = 0;
		
		for( i=0; i<len; ++i )
		{
			double x = ((Double)xv.elementAt(i)).doubleValue();
			double y = ((Double)yv.elementAt(i)).doubleValue();
			
			sumX += x;
			sumY += y;
			sumXY += x*y;
			sumX2 += x*x;
		}
		
		actualSlope = (len*sumXY - sumX*sumY)/(len*sumX2-sumX*sumX);
		actualOffset = (sumY - actualSlope*sumX)/len;

		rmse[estimate] = rmse[actual] = 0;

		for( i=0; i<len; ++i )
		{
			double x = ((Double)xv.elementAt(i)).doubleValue();
			double y = ((Double)yv.elementAt(i)).doubleValue();
			double yEst;
			double yAct;
			double y0;
			
			yEst = estimatedSlope*x+estimatedOffset;
			yAct = actualSlope*x+actualOffset;
			
			y0 = (y-yEst);
			rmse[estimate] += y0*y0;
			
			y0 = (y-yAct);
			rmse[actual] += y0*y0;
		}
		
		rmse[estimate] = Math.sqrt(rmse[estimate]/(len-2));
		rmse[actual] = Math.sqrt(rmse[actual]/(len-2));

		showRMSE = true;
		rmseValues.repaint();
	}
	
	void removeEstimate()
	{
		graph.removeAllFunctions();
		estimatedLineIndex = actualLineIndex = -1;
		showResiduals = false;
		showRMSE = false;
		rmseValues.repaint();
		residualList.repaint();
	}
	
	void removeActual()
	{
		if( actualLineIndex >= 0 )
		{
			graph.removeFunction(actualLineIndex);
			actualLineIndex = -1;
		}
	}
	
	void estimatedFit()
	{
		removeEstimate();
		
		Vector xv = new Vector();
		Vector yv = new Vector();
		
		graph.getPoints( xv, yv );
		
		int len = xv.size();
		final int tries = 1000;
		
		if( len > 0 )
		{
			double bestA = 0;
			double bestB = 0;
			double bestDiff = 1E10;
			
			Random rand = new Random();
			
			for( int i=0; i<tries; ++i )
			{
				double A = 100.0*rand.nextDouble()-50.0;
				double B = 100.0*rand.nextDouble()-50.0;
				double diff = 0;
				
				for( int j=0; j<len; ++j )
				{
					double x = ((Double)xv.elementAt(j)).doubleValue();
					double y = ((Double)yv.elementAt(j)).doubleValue();
					double z = (y-A*x-B);
					diff += z*z;
				}
				
				if( diff < bestDiff )
				{
					bestA = A;
					bestB = B;
					bestDiff = diff;
				}
			}
			
			estimatedLineIndex = graph.addFunction( new EstimatedLineFunction(), info.NEGATIVE_COLOR );
			estimatedSlope = bestA;
			estimatedOffset = bestB;
			showResiduals = false;
			showRMSE = false;
			rmseValues.repaint();
			residualList.repaint();
		}
	}
	
	class EstimatedLineFunction implements Function
	{
		public double value( double x )
		{
			return estimatedSlope*x+estimatedOffset;
		}
	}
	
	class LineFunction implements Function
	{
		double a, b;
		
		public LineFunction( double a, double b )
		{
			this.a = a;
			this.b = b;
		}
		
		public double value( double x )
		{
			return a*x+b;
		}
	}
	
	public void paint( Graphics g )
	{
		if( !adjusted )
		{
			System.out.println("adjusting graph");
			
			adjusted = true;
			
			Dimension dim = graph.getSize();
			
			double xrange, yrange;
			double tick = 10.0;

			if( dim.width >= dim.height )
			{
				xrange = 100.0;
				yrange = ((double)dim.height*100.0)/((double)dim.width);
				yrange = Math.rint(yrange/10.0)*10.0;
			}

			else
			{
				yrange = 100.0;
				xrange = ((double)dim.width*100.0)/((double)dim.height);
				xrange = Math.rint(xrange/10.0)*10.0;
			}
			
			graph.setXMinMax(-xrange,xrange);
			graph.setYMinMax(-yrange,yrange);
			graph.setTicks(tick,tick);
		}
		
		super.paint(g);
	}

	class NarrowLabel extends Component
	{
		String text;
		String alignText;
		
		public NarrowLabel()
		{
			this("");
		}
		
		public NarrowLabel( String s )
		{
			setText(s);
		}
		
		public void setText( String s )
		{
			setText( s, s );
		}
		
		public void setText( String s, String as )
		{
			setText( s, as, true );
		}
		
		public void setText( String s, String as, boolean doRepaint )
		{
			text = s;
			alignText = as;
			if( doRepaint )
				repaint();
		}
		
		public void setAlignText( String s )
		{
			alignText = s;
			repaint();
		}
		
		public Dimension getPreferredSize()
		{
			Font font = getFont();
			FontMetrics fm = getFontMetrics(font);
			Dimension dim;
			
			dim = new Dimension( fm.stringWidth(text), fm.getHeight() );
			
			return dim;
		}
		
		public void paint( Graphics g )
		{
			Font font = getFont();
			FontMetrics fm = getFontMetrics(font);
			Dimension dim = getSize();

			g.setFont( font );
			g.setColor( getForeground() );
			int sw = fm.stringWidth(alignText);
			g.drawString( text, (dim.width-sw)/2, fm.getAscent() );
		}
	}

	class RMSEValues extends TitledPanel
	{
		public RMSEValues()
		{
			super( "RMSE", new GridLayout(0,1,2,2) );
			
			setHeaderFont( info.fontPlain );
			setStyle( TitledPanel.OUTLINE );
			
			add( new NarrowLabel("Estimated") );
			add( new RMSE(estimate) );
			add( new NarrowLabel("Actual") );
			add( new RMSE(actual) );
		}
		
		class RMSE extends NarrowLabel
		{
			int which;
			
			public RMSE( int which )
			{
				this.which = which;
			}
			
			public void paint( Graphics g )
			{
				if( showRMSE )
				{
					double dub = GraphPanel.this.rmse[which];
					dub = Math.rint(dub*10.0)/10.0;
					String s = Double.toString(dub);
					setText( s, s, false );
					super.paint(g);
				}
			}
		}
	}

	class ResidualList extends TitledPanel
	{
		Font font;
		FontMetrics fm;
	
		public ResidualList()
		{
			super( "Residuals", new GridLayout(0,1,2,2) );

			setHeaderFont( info.fontPlain );
			
			font = info.fontPlain;
			fm = getFontMetrics(font);
			setFont( font );
			setStyle( TitledPanel.OUTLINE );
			
			int len = squares.getNumPoints();
			int i;
			
			for( i=0; i<len; ++i )
			{
				add( new Residual(i) );
			}
		}
		
		class Residual extends NarrowLabel
		{
			int idx;
			final String alignText = "9: 999.9";
			
			public Residual( int idx )
			{
				this.idx = idx;
				setFont( ResidualList.this.font );
			}
			
			public void paint( Graphics g )
			{
				if( showResiduals )
				{
					double val = Math.rint(residuals[idx]*10.0)/10.0;
					setText( (idx+1)+": "+Double.toString(val), alignText, false );
					super.paint(g);
				}
			}
		}
	}
}
