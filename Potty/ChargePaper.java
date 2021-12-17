import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class ChargePaper extends GraphPaper implements ActionListener
{
	Example state = null;
	int mode = -1;
	
	final Point Op = new Point(125,50);
		
	ActionListener listeners = null;

	public ChargePaper()
	{
		addMouseListener( new MouseAdapter()
			{
				public void mouseReleased( MouseEvent e )
				{
					sendPoint( e.getX(), e.getY() );
				}
			} );
	}
	
	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}
	
	void sendPoint( int x, int y )
	{
		if( listeners != null && state != null )
		{
			double pot;
			
			pot = potAt(x,y);
			
			//System.out.println("potAt("+x+","+y+") is " + pot);
			
			double dx = (double)(x-Op.x);
			double dy = (double)(y-Op.y);
			
			DPoint d = new DPoint(state.calcX(dx,dy),pot,0);
			d.special = state.significantPoint(x,y) ? 1 : 0;
			
			dy = -dy;		//reverse the Y direction, so that up screen coords are positive increases
			
			if( state instanceof PerpendicularDipole )
				d.x = dy;
			//else if( state instanceof AxisDipole )
			else if( state instanceof ParallelPlates )
			{			//the point needs reversing,
						// because the positive plate is now on top
				d.x = ((ParallelPlates)state).mungeX(((ParallelPlates)state).R + dy);
			}
			
			ActionEvent e = new ActionEvent(d,0,ElecPot.POINT);
			
			listeners.actionPerformed( e );
		}
	}
	
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		if( state != null )
			state.paint(g);
	}

	public Dimension getMinimumSize()
	{
		return new Dimension(Example.Op.x*2,Example.Op.y*2);
	}
	
	public Dimension getPreferredSize()
	{
		return getMinimumSize();
	}
	
	public Example getState()
	{
		return state;
	}
	
	public void setState( Example state )
	{
		//System.out.println("ChargePaper.setState("+state+")");
		
		this.state = state;
		
		removeAll();
	}

	public void setExample( int ex )
	{
		System.out.println("ChargePaper.setExample("+ex+")");
		
		if( state != null )
		{
			state.setExample( this, ex, new DPoint(0.0,0.0,0.0) );
			arrange(state.getCharge() * GraphInfo.Ke * -GraphInfo.C);
			repaint();
		}
	}
	
	public void arrange( double maxCharge )
	{
		int len = components.size();
		int pos=0, neg=0;
		
		for( int i=0; i<len; ++i )
		{
			Object o = components.elementAt(i);
			if( o instanceof Charge )
			{
				Charge ch = (Charge) o;
				double charge = ch.getCharge();
				if( charge < 0 ) neg++;
				else pos++;
			}
		}
		
		//System.out.println(pos+" positive and "+neg+" negative charges. at maxCharge "+maxCharge);
		
		for( int i=0; i<len; ++i )
		{
			Object o = components.elementAt(i);
			if( o instanceof Charge )
			{
				Charge ch = (Charge) o;
				double charge = ch.getCharge();
				if( charge < 0 ) ch.setCharge(-maxCharge/(double)neg);
				else ch.setCharge(maxCharge/(double)pos);
			}
		}
		
		super.arrange();
	}
	
	DPoint dorigin = null;
		
	private double potAt(int x,int y)
	{
		Dimension size = getSize();
		dorigin = DPoint.setDPoint(dorigin,x-size.width/2,y-size.height/2,scale);
		
		if( state != null )
			return state.calcDpot(this,dorigin);
		else
			return 0.0;
	}
	
	public void setMode( int mode )
	{
		//System.out.println("ChargePaper.setMode("+mode+")");
		
		this.mode = mode;
		updateBackdrop();
	}
	
	public void actionPerformed( ActionEvent e )
	{
		//System.out.println("ChargePaper received "+e);
		
		String s = e.getActionCommand();
		
		if( s.equals(ElecPot.STATECHANGED) )
			setState( (Example) e.getSource() );
		else if( s.equals(ElecPot.MODECHANGED) )
			setMode( e.getID() );
		else if( s.equals(ElecPot.EXAMPLE) )
			setExample( e.getID() );
	}

	public boolean showGrid(){ return false; }
	public boolean showAxes(){ return false; }
	public boolean showEquipotential()
	{
		return (mode == ElecPot.VIEW_MODE) || 
			(mode == ElecPot.GRAPH_MODE && !GraphInfo.PE2);
	}
	public boolean okayToPlot(){ return false; }
}
