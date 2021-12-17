import java.awt.*;
import util.DoubleBufferPanel;

public class TitledPanel extends DoubleBufferPanel
{
	public static final int STANDARD = 0;
	public static final int OUTLINE = 1;
	public static final int TRANSPARENT = 2;
	public static final int INVISIBLE = 3;
	
	Font headerFont;
	FontMetrics fm;
	int style;
	int just = Label.LEFT;
	
	Color headerBackgroundColor = Color.black;
	Color headerForegroundColor = Color.white;

	public TitledPanel( String name )
	{
		this( name, new FlowLayout() );
	}	

	public TitledPanel( String name, LayoutManager layout )
	{
		super(layout);
		setName( name );
		setHeaderFont( GraphInfo.fontBiggerBold );
		setStyle(STANDARD);
	}
	
	public void setHeaderFont( Font font )
	{
		headerFont = font;
		fm = getFontMetrics(headerFont);
		invalidate();
		doLayout();
		validate();
		repaint();
	}
	
	public void setHeaderBackground( Color color )
	{
		headerBackgroundColor = color;
		repaint();
	}

	public void setHeaderForeground( Color color )
	{
		headerForegroundColor = color;
		repaint();
	}

	public void setStyle( int style )
	{
		this.style = style;
		
		switch( style )
		{
			default:
			case INVISIBLE: setJustification( Label.CENTER ); break;
			case STANDARD: setJustification( Label.LEFT ); break;
			case OUTLINE: setJustification( Label.CENTER ); break;
			case TRANSPARENT: setJustification( Label.LEFT ); break;
		}
		
		repaint();
	}

	public void setJustification( int just )
	{
		this.just = just;
		repaint();
	}

	public Dimension getPreferredSize()
	{
		Dimension dim = super.getPreferredSize();
		
		Insets insets = getInsets();
		
		int width = fm.stringWidth(getName())+insets.left+insets.right;
		
		dim.width = Math.max(width,dim.width);
		
		return dim;
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		Insets insets = getInsets();
		Dimension dim = getSize();
		
		String name = getName();
		int hh = fm.getHeight()+2;
		int xoffs;
		int sw = fm.stringWidth( name );
		
		switch( just )
		{
			default:
			case Label.LEFT:
				xoffs = 5;
				break;
			
			case Label.RIGHT:
				xoffs = dim.width - 5 - sw;
				break;
			
			case Label.CENTER:
				xoffs = (dim.width-sw)/2;
				break;
		}
		
		switch( style )
		{
			default:
			case STANDARD:
				{
					g.setColor( headerBackgroundColor );
					g.drawRect( 0, 0, dim.width-1, dim.height-1 );
					g.fillRect( 0, 0, dim.width-1, hh-1 );
					g.setColor( headerForegroundColor );
					g.setFont( headerFont );
					g.drawString( name, xoffs, fm.getAscent() );
				}
				break;
			
			case INVISIBLE:
				{
					g.setColor( headerBackgroundColor );
					g.fillRect( 0, 0, dim.width-1, hh-1 );
					g.setColor( headerForegroundColor );
					g.setFont( headerFont );
					g.drawString( name, xoffs, fm.getAscent() );
				}
				break;
			
			case TRANSPARENT:
				{
					g.setColor( headerBackgroundColor );
					g.drawRect( 0, 0, dim.width-1, dim.height-1 );
					g.drawLine( 0, hh-1, dim.width-1, hh-1 );
					g.setColor( getForeground() );
					g.setFont( headerFont );
					g.drawString( name, xoffs, fm.getAscent() );
				}
				break;
			
			case OUTLINE:
				{
					g.setColor( getForeground() );
					g.drawRect( 0, hh/2, dim.width-1, dim.height-1-hh/2 );
					g.setColor( getBackground() );
					g.setFont( headerFont );
					g.fillRect( xoffs-2, 0, sw+4, hh );
					g.setColor( getForeground() );
					g.drawString( name, xoffs, fm.getAscent() );
				}
				break;
		}
		
	}
	
	public Insets getInsets()
	{
		return new Insets(getFontMetrics(headerFont).getHeight()+2,
			7,2,7);
	}
}