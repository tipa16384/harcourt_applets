import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class GraphBackground implements ImageProducer
{
	Vector consumer = new Vector();
	GraphPaper paper;
	
	int [] hues = null;
	int [] range = null;
	
	final int lopot = -10;
	final int hipot = 10;

	static ColorModel model = null;
	
	final byte WHITE = 0;
	final byte GRID = WHITE+1;
	final byte AXES = GRID+1;
	final byte GRAY = AXES+1;
	final byte BLACK = GRAY+1;
	final byte TRANSPARENT = BLACK+1;
	final byte FIELD = TRANSPARENT+1;
	final byte HUES = FIELD+1;
		
	final int MAXHUE = HUES + hipot - lopot + 1;
	
	static byte bits[] = null;

	public GraphBackground( GraphPaper paper )
	{
		this.paper = paper;
		initializeColorModel();
	}
	
	synchronized void initializeColorModel()
	{
		if( model == null )
		{
			int i;
			
			hues = new int[MAXHUE];
			//color = info.color;
			
			for( i=0; i<MAXHUE; ++i )
				hues[i] = 0xFFFFFFFF;
			
			for( i=lopot; i<=hipot; ++i )
			{
				int re = ((i-lopot)*255)/(hipot-lopot);
				int gr = 0;
				int bl = 255 - re;
	
				hues[HUES+i-lopot] = 0xFF000000 | (re<<16) | bl;
			}
	
			hues[WHITE] = Color.white.getRGB();
			hues[GRID] = GraphInfo.GRID_COLOR.getRGB();
			hues[AXES] = GraphInfo.AXIS_COLOR.getRGB();
			hues[GRAY] = Color.lightGray.getRGB();
			hues[BLACK] = Color.black.getRGB();
			hues[FIELD] = GraphInfo.FIELD_COLOR.getRGB();
			
			byte [] r = new byte[MAXHUE];
			byte [] g = new byte[MAXHUE];
			byte [] b = new byte[MAXHUE];
			
			for( i=0; i<MAXHUE; ++i )
			{
				int col = hues[i];
				r[i] = (byte)((col >> 16) & 0xFF);
				g[i] = (byte)((col >>  8) & 0xFF);
				b[i] = (byte)(col & 0xFF);
			}
			
			model = new IndexColorModel(8,MAXHUE,r,g,b);
		}
	}
	
	// Generate
	
	class BThread extends Thread
	{
		ImageConsumer eater;
		Dimension size;
		Rectangle bitBounds = null;
		byte color = WHITE;
		int oldpot = 10000;
		DPoint dorigin = null;
		
		public BThread( ImageConsumer eater ) 
		{
			this.eater = eater;
		}
		
		public void start()
		{
			//System.out.println("starting draw thread");
			
			size = paper.getSize();

			eater.setHints(ImageConsumer.TOPDOWNLEFTRIGHT
						   | ImageConsumer.COMPLETESCANLINES
						   | ImageConsumer.SINGLEPASS
						   | ImageConsumer.SINGLEFRAME );
			eater.setColorModel( model);

			eater.setDimensions(size.width,size.height);
			
			setBits( 0, 0, size.width, size.height );
			if( bits == null )
			{
				eater.imageComplete( ImageConsumer.IMAGEERROR );
				System.out.println("GraphBackground - "+(size.width*size.height)+" baito o haotte dekimasen. Gomen ne!");
				return;
			}
			
			flushBits();
			
			drawEquipotential();
			
			flushBits();
			//bits = null;
			
			//System.out.println("finishing draw thread");
			
			eater.imageComplete( ImageConsumer.STATICIMAGEDONE );
		}
		
		synchronized void setBits( int x, int y, int w, int h )
		{
			x = 0; y = 0;
			w = size.width; h = size.height;
			
			int len = w * h;
			
			try
			{
				if( bits == null || bits.length < len )
					bits = new byte[len];
				for( int i=0; i<len; ++i )
					bits[i] = WHITE;
			}
			
			catch( OutOfMemoryError e )
			{
				System.out.println("Caught "+e+" while allocating offscreen bitmap");
				bits = null;
			}
			
			bitBounds = new Rectangle(x,y,w,h);
			
			//System.out.println("setBits("+x+","+y+","+w+","+h+")");
		}

		void flushBits()
		{
			if( bits != null )
			{
				int offs = -(bitBounds.y*bitBounds.width+bitBounds.x);
				//System.out.print("flushBits...offs="+offs+"...");
				eater.setPixels(bitBounds.x,bitBounds.y,
					bitBounds.width,bitBounds.height,
		   			model, bits, offs, bitBounds.width);
		   		//System.out.println("done");
			}
		}
		
		void drawEquipotential()
		{
			if( paper.showEquipotential() )
			{
				Example calculator = null;
				
				if( paper instanceof ChargePaper )
					calculator = ((ChargePaper)paper).getState();
				
				if( calculator == null ) return;
				
				setupFractal();
				
				final int inc = 64;
				
				for( int x=0; x<size.width; x += inc )
				{
					for( int y=0; y<size.height; y += inc )
					{
						fractal( calculator, x, y, inc, size );
					}
				}
			}
		}
		
		private void setupFractal()
		{
			oldpot = 10000;
		}
		
		private void fractal( Example calculator, int x, int y, int inc, Dimension size )
		{
			final int x1 = x-size.width/2;
			final int y1 = y-size.height/2;
			
			int pot1 = calculator.calcpot( paper, x1, y1 );
			int pot2 = calculator.calcpot( paper, x1+inc, y1 );
			int pot3 = calculator.calcpot( paper, x1+inc, y1+inc );
			int pot4 = calculator.calcpot( paper, x1, y1+inc );
			
			if( paper.showColor() )
			{
				if( (inc<=2) || (pot1 == pot2 && pot1 == pot3 && pot1 == pot4) )
				{
					if( oldpot != pot1 )
					{
						color = (byte)(HUES+(byte)(pot1-lopot));
						oldpot = pot1;
					}
					
					fillRect(x,y,inc,inc);
				}
				
				else
				{
					final int hint = inc/2;
					
					fractal( calculator, x, y, hint, size );
					fractal( calculator, x+hint, y, hint, size );
					fractal( calculator, x, y+hint, hint, size );
					fractal( calculator, x+hint, y+hint, hint, size );
				}
			}
			
			else
			{
				if( pot1 != pot2 || pot1 != pot3 || pot1 != pot4 )
				{
					if( inc <= 2 )
					{
						color = BLACK;
						
						if( ((pot1 == pot2) && (pot3 == pot4)) ||
							((pot1 == pot4) && (pot2 == pot3)) )
						{
							color = GRAY;
							fillRect(x,y,inc,inc);
						}
						
						else if( (pot2 == pot3) && (pot3 == pot4) )
						{
							color = GRAY;
							fillRect( x, y, 1, 1 );
						}
						
						else if( (pot1 == pot2) && (pot2 == pot3) )
						{
							color = GRAY;
							fillRect( x, y+1, 1, 1 );
						}
						
						else if( (pot1 == pot2) && (pot2 == pot4) )
						{
							color = GRAY;
							fillRect( x+1, y+1, 1, 1 );
						}
						
						else if( (pot1 == pot3) && (pot4 == pot3) )
						{
							color = GRAY;
							fillRect( x+1, y, 1, 1 );
						}
						
						else if( (pot1 == pot3) )
						{
							color = GRAY;
							drawLine( x, y, x+1, y+1 );
						}
						
						//g.fillRect(x,y,inc,inc);
					}
					
					else
					{
						final int hint = inc/2;
						
						fractal( calculator, x, y, hint, size );
						fractal( calculator, x+hint, y, hint, size );
						fractal( calculator, x, y+hint, hint, size );
						fractal( calculator, x+hint, y+hint, hint, size );
					}
				}
			}
		}

		private synchronized void setPixel( int x, int y )
		{
			//System.out.println("setPixel("+x+","+y+") to "+Integer.toHexString(color));
			
			if( bits != null )
			{
				x -= bitBounds.x;
				y -= bitBounds.y;
				
				if( x >= 0 && y >= 0 && y < bitBounds.height && x < bitBounds.width )
					bits[y*bitBounds.width+x] = color;
			}
		}
		
		void fillRect( int x, int y, int w, int h )
		{
			for( int x0=0; x0<w; ++x0 )
				for( int y0=0; y0<h; ++y0 )
					setPixel(x+x0,y+y0);
		}
		
		void drawLine( int x1, int y1, int x2, int y2 )
		{
			if( x1 == x2 && y1 == y2 )
			{
				setPixel(x1,y1);
			}
				
			else if( Math.abs(x1-x2) >= Math.abs(y1-y2) )
			{
				if( x1 > x2 ){ int t=x1; x1=x2; x2=t;
								   t=y1; y1=y2; y2=t; }
				
				int dy = y2-y1;
				int dx = x2-x1;
				
				for( int i=0; i<=dx; ++i )
					setPixel(x1 + i, y1 + (i*dy)/dx);
			}
			
			else
			{
				if( y1 > y2 ){ int t=x1; x1=x2; x2=t;
								   t=y1; y1=y2; y2=t; }
				
				int dy = y2-y1;
				int dx = x2-x1;
				
				for( int i=0; i<=dy; ++i )
					setPixel(x1 + (i*dx)/dy, y1+i);
			}
		}
	}
		
    /**
     * This method is used to register an ImageConsumer with the
     * ImageProducer for access to the image data during a later
     * reconstruction of the Image.  The ImageProducer may, at its
     * discretion, start delivering the image data to the consumer
     * using the ImageConsumer interface immediately, or when the
     * next available image reconstruction is triggered by a call
     * to the startProduction method.
     * @see #startProduction
     */
    public void addConsumer(ImageConsumer ic)
    {
    	//System.out.println("addConsumer("+ic+")");
    	
    	if( !isConsumer(ic) )
    	{
    		consumer.addElement(ic);
    		Thread t = new BThread(ic);
    		t.start();
    	}
    }

    /**
     * This method determines if a given ImageConsumer object
     * is currently registered with this ImageProducer as one
     * of its consumers.
     */
    public boolean isConsumer(ImageConsumer ic)
    {
    	return consumer.indexOf(ic) >= 0;
    }

    /**
     * This method removes the given ImageConsumer object
     * from the list of consumers currently registered to
     * receive image data.  It is not considered an error
     * to remove a consumer that is not currently registered.
     * The ImageProducer should stop sending data to this
     * consumer as soon as is feasible.
     */
    public void removeConsumer(ImageConsumer ic)
    {
    	//System.out.println("removeConsumer("+ic+")");
    	consumer.removeElement(ic);
    }

    /**
     * This method both registers the given ImageConsumer object
     * as a consumer and starts an immediate reconstruction of
     * the image data which will then be delivered to this
     * consumer and any other consumer which may have already
     * been registered with the producer.  This method differs
     * from the addConsumer method in that a reproduction of
     * the image data should be triggered as soon as possible.
     * @see #addConsumer
     */
    public void startProduction(ImageConsumer ic)
    {
    	//System.out.println("startProduction("+ic+")");
    	addConsumer(ic);
    }

    /**
     * This method is used by an ImageConsumer to request that
     * the ImageProducer attempt to resend the image data one
     * more time in TOPDOWNLEFTRIGHT order so that higher
     * quality conversion algorithms which depend on receiving
     * pixels in order can be used to produce a better output
     * version of the image.  The ImageProducer is free to
     * ignore this call if it cannot resend the data in that
     * order.  If the data can be resent, then the ImageProducer
     * should respond by executing the following minimum set of
     * ImageConsumer method calls:
     * <pre>
     *	ic.setHints(TOPDOWNLEFTRIGHT | < otherhints >);
     *	ic.setPixels(...);	// As many times as needed
     *	ic.imageComplete();
     * </pre>
     * @see ImageConsumer#setHints
     */
    public void requestTopDownLeftRightResend(ImageConsumer ic)
    {
    	System.out.println("requestTopDownLeftRightResend("+ic+")");
    }
}
