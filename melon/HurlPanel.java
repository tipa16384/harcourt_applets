import java.awt.*;
import java.awt.event.*;

public class HurlPanel extends Panel
{
	Melon melon;
	GraphInfo info;
	Button resetButton;
	Button hurlButton;
	VelocityPanel velPanel;
	FinishPanel finPanel;
	
	public HurlPanel( Melon melon, GraphInfo info )
	{
		this.melon = melon;
		this.info = info;
		
		resetButton = new Button("Reset");
		resetButton.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					reset();
				}
			} );
		
		hurlButton = new Button("Hurl");
		hurlButton.addActionListener( new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					hurl();
				}
			} );
		
		velPanel = new VelocityPanel();
		add( velPanel, BorderLayout.WEST );
		
		Panel p = new Panel();
		p.setForeground( Color.black );
		p.add( hurlButton );
		p.add( resetButton );
		
		add( p, BorderLayout.CENTER );
		
		finPanel = new FinishPanel();
		add( finPanel, BorderLayout.EAST );
	}
	
	public void redrawSuccess()
	{
		finPanel.redrawSuccess();
	}
	
	public double getVelocity()
	{
		return velPanel.getVelocity();
	}
	
	void reset()
	{
		melon.reset();
	}
	
	void hurl()
	{
		melon.hurl();
	}
	
	class FinishPanel extends Panel
	{
		public FinishPanel()
		{
			super( new GridLayout(0,1) );
			int len = melon.getNumPlanets();
			int i;
			
			for( i=0; i<len; ++i )
			{
				add( new Readout(melon.getPlanet(i)) );
			}
		}

		public void redrawSuccess()
		{
			int len = getComponentCount();
			
			for( int i=0; i<len; ++i )
			{
				Component c = getComponent(i);
				c.repaint();
			}
		}
	}
	
	class Readout extends Component
	{
		final String separator = ": ";
		
		Font font;
		FontMetrics fm;
		Planet planet;
		
		public Readout( Planet planet )
		{
			this.planet = planet;
			font = info.fontPlain;
			fm = getFontMetrics(font);
			setFont(font);
		}
		
		public Dimension getPreferredSize()
		{
			Dimension dim = new Dimension();

			dim.width = getResultWidth()
					  + fm.stringWidth(planet.getName()+separator);
			dim.height = fm.getHeight();
			
			return dim;
		}
		
		int getResultWidth()
		{
			int w=0;
			String [] rs = Planet.getStatusStrings();
			
			for( int i=0; i<rs.length; ++i )
			{
				w = Math.max(w,fm.stringWidth(rs[i]));
			}

			return w;
		}
		
		public void paint( Graphics g )
		{
			Dimension dim = getSize();
			String result = planet.getStatusString();
			String name = planet.getName()+separator;
						
			int xr = dim.width-getResultWidth();
			int xn = xr-fm.stringWidth(name);
			int ya = fm.getAscent();
			
			g.setFont(font);
			g.setColor(getForeground());
			g.drawString(result,xr,ya);
			g.drawString(name,xn,ya);
		}
	}
	
	class VelocityPanel extends Panel
	{
		TextComponent tc = new TextField(5);
		
		public VelocityPanel()
		{
			add( new Label("Initial Velocity: ", Label.RIGHT ) );
			add( tc );
			add( new Label("m/s", Label.LEFT) );
			
			tc.setForeground(Color.black);
			tc.setBackground(Color.white);
		}
		
		public double getVelocity()
		{
			double vel = 0.0;
			
			try
			{
				String s = tc.getText();
				vel = Double.valueOf(s).doubleValue();
			}
			
			catch( Exception e )
			{
				vel = 0.0;
			}
			
			finally
			{
				tc.setText(Double.toString(vel));
				tc.selectAll();
				tc.requestFocus();
			}
			
			return (vel == 0.0) ? vel : -vel;
		}
	}
}
