import java.awt.*;
import java.awt.event.*;

//import util.GenericIcon;

public class PicturePanel extends PaddedPanel implements ActionListener
{
	Example state = null;
	ChargePaper graph = null;
	Component ThreeD = null;
	ActionListener listeners = null;
	int mode = -1;
	
	public PicturePanel()
	{
		super( new FixedLayoutManager(), 0, 0, 0, 0 );
		
		graph = new ChargePaper();
		add( graph, new Rectangle(0,0,250,100) );
		graph.addActionListener( this );
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		g.setColor( new Color(153,153,153) );
		Dimension czr = getSize();
		g.drawLine(10,czr.height-1,czr.width-10,czr.height-1);
	}
	
	public void addActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.add(listeners,l);
	}
	
	public void removeActionListener( ActionListener l )
	{
		listeners = AWTEventMulticaster.remove(listeners,l);
	}
	
	private void pointReceived( ActionEvent e )
	{
		if( listeners != null && mode == ElecPot.PLOT_MODE )
			listeners.actionPerformed( e );
	}
	
	public void setState( Example state )
	{
		//System.out.println("PicturePanel.setState("+state+")");
		
		this.state = state;
		
		if( ThreeD != null )
			remove(ThreeD);
		
		//ThreeD = new GenericIcon(getToolkit().getImage(state.get3D()),null,"3d");
		ThreeD = new GenericIcon(state.get3Dimage(),null,"3d");
		
		add( ThreeD, new Rectangle(260,0,90,100) );

		if( graph != null )
			graph.setState(state);
		
		state.setMode(mode);
			
		validate();
	}
	
	public void setMode( int mode )
	{
		//System.out.println("PicturePanel.setMode("+mode+")");
		
		if( graph != null )
			graph.setMode(mode);
		
		if( state != null )
			state.setMode(mode);
			
		this.mode = mode;
	}
	
	public void setExample( int ex )
	{
		//System.out.println("PicturePanel.setExample("+ex+")");
		
		if( graph != null )
			graph.setExample(ex);
	}
	
	public void actionPerformed( ActionEvent e )
	{
		//System.out.println("PicturePanel received "+e);
		
		String s = e.getActionCommand();
		
		if( s.equals(ElecPot.STATECHANGED) )
			setState( (Example) e.getSource() );
		else if( s.equals(ElecPot.MODECHANGED) )
			setMode( e.getID() );
		else if( s.equals(ElecPot.POINT) )
			pointReceived( e );
		else if( s.equals(ElecPot.EXAMPLE) )
			setExample( e.getID() );
	}
}
